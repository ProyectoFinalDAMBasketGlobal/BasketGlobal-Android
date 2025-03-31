package dam.intermodular.app.registro.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.intermodular.app.core.dataStore.DataStoreManager
import dam.intermodular.app.core.navigation.VerificationCode
import dam.intermodular.app.core.navigation.VerificationData
import dam.intermodular.app.registro.domain.RegisterRepository
import dam.intermodular.app.registro.presentation.model.RegisterModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor (
    private val dataStoreManager: DataStoreManager,
    private val registerRepository: RegisterRepository
) :ViewModel() {

    private val _email = MutableStateFlow<String>("")
    val email : StateFlow<String> = _email

    private val _password = MutableStateFlow<String>("")
    val password : StateFlow<String> = _password


    fun onRegisterChange(email: String, password: String){
        _email.value = email
        _password.value = password
    }

    fun registerUser(navigateTo: NavController) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            val user = RegisterModel(_email.value,_password.value)
            val result = registerRepository.registerUserRepository(user)
            result
                .onSuccess { response ->
                    val verificationData = VerificationData(
                        email = response.data.email,
                        emailApp = response.data.emailApp,
                        idUser = response.data.id
                    )
                    withContext(Dispatchers.Main){
                        navigateTo.navigate(VerificationCode(verificationData))
                    }
                }
                .onFailure {

                }
        }
    }

}