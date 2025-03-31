package dam.intermodular.app.verificationCode.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.intermodular.app.core.navigation.VerifyData
import dam.intermodular.app.core.navigation.VerifyProfile
import dam.intermodular.app.verificationCode.domain.VerificationCodeRepository
import dam.intermodular.app.verificationCode.presentation.model.VerificationCodeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VerificationCodeViewModel @Inject constructor(
    private val repositoryVerificationCode: VerificationCodeRepository
) : ViewModel() {

    private val _code = MutableStateFlow<String>("")
    val code: StateFlow<String> = _code

    fun onCodeChange(code: String){
        _code.value = code
    }

    fun sendVerificationCode(navigateTo: NavController, _id: String, emailApp: String) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            val user = VerificationCodeModel( emailApp,_code.value, _id )
            val result = repositoryVerificationCode.sendVerificationCodeRepository(user)
            result
                .onSuccess { response ->
                    val verifyData = VerifyData(
                        emailApp = response.data.emailApp,
                        idUser = response.data.idUsuario,
                    )
                    withContext(Dispatchers.Main){
                        navigateTo.navigate(VerifyProfile(verifyData))
                    }
                }
                .onFailure {  }
        }
    }
}