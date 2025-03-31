package dam.intermodular.app.verifyProfile.presentation.view

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import dam.intermodular.app.core.navigation.VerifyData
import dam.intermodular.app.verifyProfile.presentation.composables.ApellidoTextFiel
import dam.intermodular.app.verifyProfile.presentation.composables.CiudadTextFiel
import dam.intermodular.app.verifyProfile.presentation.composables.DatePickerField
import dam.intermodular.app.verifyProfile.presentation.composables.DniTextFiel
import dam.intermodular.app.verifyProfile.presentation.composables.GenderDropdown
import dam.intermodular.app.verifyProfile.presentation.composables.NombreTextFiel
import dam.intermodular.app.verifyProfile.presentation.viewModel.VerifyProfileViewModel

@Composable
fun  VerifyProfileScreen (verifyData: VerifyData, viewModel: VerifyProfileViewModel, navigateTo: NavController){

    val nombre by viewModel.nombre.collectAsState()
    val apellido by viewModel.apellido.collectAsState()
    val dni by viewModel.dni.collectAsState()
    val dateFormatted by viewModel.dateFormatted.collectAsState()
    val ciudad by viewModel.ciudad.collectAsState()
    val sexo by viewModel.sexo.collectAsState()
    val imageUri by viewModel.picture.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        viewModel.onPictureSelected(uri)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registro de Perfil ", fontSize = 20.sp)
        //Text(text = "Email de Aplicación: ${verifyData.emailApp}")
        Text(text = "ID Usuario a enviar: ${verifyData.idUser}")


        NombreTextFiel(nombre) { newNombre ->
            viewModel.onNombreChange(newNombre)
        }
        Spacer(modifier = Modifier.weight(1f))
        ApellidoTextFiel(apellido) { newApellido ->
            viewModel.onApellidoChange(newApellido)
        }
        Spacer(modifier = Modifier.weight(1f))
        DniTextFiel(dni) { newDni ->
            viewModel.onDniChange(newDni)
        }
        Spacer(modifier = Modifier.weight(1f))
        CiudadTextFiel(ciudad) { newCiudad ->
            viewModel.onCiudadChange(newCiudad)
        }
        Spacer(modifier = Modifier.weight(1f))
        DatePickerField(dateFormatted) { newDate ->
            viewModel.onDateChange(newDate)
        }
        Spacer(modifier = Modifier.weight(1f))
        GenderDropdown(sexo) { newSexo ->
            viewModel.onSexoChange(newSexo)
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { imagePickerLauncher.launch(arrayOf("image/*")) }) {
            Text("Seleccionar Imagen")
        }

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Imagen seleccionada",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )
        }

        // Botón para enviar el perfil
        Button(onClick = {
            Log.d("Button", "Botón clickeado") // <-- Agrega esto para verificar si el botón responde
            viewModel.sendRegisterProfile(navigateTo,verifyData.idUser) }) {
            Text(text = "Crear Perfil")
        }

    }













}