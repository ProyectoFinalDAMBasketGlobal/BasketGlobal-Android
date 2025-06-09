package dam.intermodular.app.core.navigation

import androidx.compose.runtime.Composable

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import dam.intermodular.app.productos.view.FavoritesScreen
import dam.intermodular.app.productos.viewModel.ProductosViewModel
import dam.intermodular.app.productos.view.MainScreen
import dam.intermodular.app.productos.view.ProductoDetailsFragment
import androidx.navigation.toRoute
import dam.intermodular.app.core.dataStore.DataStoreManager
import dam.intermodular.app.core.navigation.type.createNavType

import dam.intermodular.app.home.presentation.views.HomeScreen
import dam.intermodular.app.login.presentation.viewModel.LoginViewModel
import dam.intermodular.app.login.presentation.views.LoginScreen
import dam.intermodular.app.registro.presentation.view.RegisterScreen
import dam.intermodular.app.registro.presentation.viewModel.RegisterViewModel
import dam.intermodular.app.adquisiciones.model.Adquisicion
import dam.intermodular.app.adquisiciones.view.AdquirirProductoScreen
import dam.intermodular.app.adquisiciones.view.AdquisicionesScreen
import dam.intermodular.app.adquisiciones.view.InfoAdquisicionScreen
import dam.intermodular.app.adquisiciones.view.ModificarAdquisicionScreen
import dam.intermodular.app.login.presentation.views.ChangePasswordScreen
import dam.intermodular.app.productos.view.ProfileScreen
import dam.intermodular.app.verificationCode.presentation.view.VerificationCodeScreen
import dam.intermodular.app.verificationCode.presentation.viewModel.VerificationCodeViewModel
import dam.intermodular.app.verifyProfile.presentation.view.VerifyProfileScreen
import dam.intermodular.app.verifyProfile.presentation.viewModel.VerifyProfileViewModel
import java.net.URLDecoder
import kotlin.reflect.typeOf


@Composable
fun NavigationApp(){
    val navController = rememberNavController()
    val viewModelLogin : LoginViewModel = viewModel()
    val viewModelRegister: RegisterViewModel = viewModel()
    val viewModelVerificationCode: VerificationCodeViewModel = viewModel()
    val viewModelVerifyProfile: VerifyProfileViewModel = viewModel()
    val productosViewModel: ProductosViewModel = viewModel()
    val dataStoreManager: DataStoreManager
    val authState by viewModelLogin.authState.collectAsState()
    val token by viewModelLogin.authState.collectAsState()
    val isChekingToken by viewModelLogin.isCheckingToken.collectAsState()


    NavHost(navController=navController, startDestination = "login_screen")
    {
        composable("login_screen") {
            LoginScreen(
                navigationTo = navController,
                viewModel = viewModelLogin
            )
        }
        composable<Register> {
            RegisterScreen(
                navigateTo = navController,
                viewModel = viewModelRegister
            )
        }
        composable<VerificationCode>(
            typeMap = mapOf(typeOf<VerificationData>() to createNavType<VerificationData>())
        ){ navBackStackEntry ->
            val verificationCode = navBackStackEntry.toRoute<VerificationCode>()
            VerificationCodeScreen(
                verificationData = verificationCode.verificationData,
                viewModel = viewModelVerificationCode,
                navigateTo = navController
            )
        }
        composable<VerifyProfile>(
            typeMap = mapOf(typeOf<VerifyData>() to createNavType<VerifyData>())
        ){ navBackStackEntry ->
            val verifyProfile = navBackStackEntry.toRoute<VerifyProfile>()
            VerifyProfileScreen(
                verifyData = verifyProfile.verifyData,
                viewModel = viewModelVerifyProfile,
                navigateTo = navController
            )

        }
        composable("change_password_screen") {
            ChangePasswordScreen(navController,viewModelLogin)
        }
        composable<Home>{
            HomeScreen{ name -> navController.navigate(Detail(name = name))}
        }
        composable("main_screen") {
            MainScreen(navController, productosViewModel)
        }
        composable("favorites_screen") {
            FavoritesScreen(navController, productosViewModel)
        }
        composable("profile_screen/{_id}") {
            // Aquí ya no es necesario obtener el idUsuario, ya que el ViewModel se encarga de ello
            val viewModel: VerifyProfileViewModel = hiltViewModel() // Obtén el ViewModel

            // Llamamos a ProfileScreen, sin pasar el idUsuario explícitamente
            ProfileScreen(viewModel = viewModel, navigateTo = navController)
        }
        composable("adquisiciones_screen"){
            AdquisicionesScreen(navController)
        }
        composable(
            "infoAdquisicion/{adquisicionJson}",
            arguments = listOf(navArgument("adquisicionJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val adquisicionJson = backStackEntry.arguments?.getString("adquisicionJson")
            val adquisicion = Gson().fromJson(adquisicionJson, Adquisicion::class.java)
            InfoAdquisicionScreen(navController, adquisicion)
        }

        composable(
            "modificarAdquisicion/{adquisicionJson}",
            arguments = listOf(navArgument("adquisicionJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val adquisicionJson = backStackEntry.arguments?.getString("adquisicionJson")
            val adquisicion = Gson().fromJson(adquisicionJson, Adquisicion::class.java)
            ModificarAdquisicionScreen(navController, adquisicion)
        }

        composable(
            "adquirir_producto/{roomId}/{roomName}/{roomPrice}/{stock}/{usuarioId}",
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType },
                navArgument("roomName") { type = NavType.StringType },
                navArgument("roomPrice") { type = NavType.StringType },
                navArgument("stock") { type = NavType.IntType },
                navArgument("usuarioId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val roomName = backStackEntry.arguments?.getString("roomName") ?: ""
            val roomPrice = backStackEntry.arguments?.getString("roomPrice") ?: ""
            val stock = backStackEntry.arguments?.getInt("stock") ?: 1
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""

            AdquirirProductoScreen(
                navController = navController,
                productoId = roomId,
                productoNombre = roomName,
                precio = roomPrice,
                stockProducto = stock,
                usuarioId = usuarioId
            )
        }

        composable(
            "producto_details_screen/{roomId}/{roomName}/{roomDescription}/{roomPrice}/{stock}/{roomImage}/{previousScreen}" ,
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType },
                navArgument("roomName") { type = NavType.StringType },
                navArgument("roomDescription") { type = NavType.StringType },
                navArgument("roomPrice") { type = NavType.StringType },
                navArgument("stock") { type = NavType.IntType },
                navArgument("roomImage") { type = NavType.StringType },
                navArgument("previousScreen") { type = NavType.StringType }
        )
        ) { backStackEntry ->
            
            /*
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val roomName = backStackEntry.arguments?.getString("roomName") ?: ""
            val roomDescription = backStackEntry.arguments?.getString("roomDescription") ?: ""
            val roomPrice = backStackEntry.arguments?.getString("roomPrice") ?: ""
            val stock = backStackEntry.arguments?.getInt("stock") ?: 0
            val roomImage = backStackEntry.arguments?.getString("roomImage") ?: ""
            val previousScreen = backStackEntry.arguments?.getString("previousScreen") ?: ""
            */

            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val roomName = URLDecoder.decode(backStackEntry.arguments?.getString("roomName") ?: "", "UTF-8")
            val roomDescription = URLDecoder.decode(backStackEntry.arguments?.getString("roomDescription") ?: "", "UTF-8")
            val roomPrice = URLDecoder.decode(backStackEntry.arguments?.getString("roomPrice") ?: "", "UTF-8")
            //val stock = backStackEntry.arguments?.getString("stock")?.toIntOrNull() ?: 0
            val stock = backStackEntry.arguments?.getInt("stock") ?: 0
            //val roomImage = URLDecoder.decode(backStackEntry.arguments?.getString("roomImage") ?: "", "UTF-8")
            val roomImage = backStackEntry.arguments?.getString("roomImage") ?: ""
            val previousScreen = URLDecoder.decode(backStackEntry.arguments?.getString("previousScreen") ?: "", "UTF-8")

            ProductoDetailsFragment(
                navController = navController,
                roomId = roomId,
                roomName = roomName,
                roomDescription = roomDescription,
                roomPrice = roomPrice,
                stock = stock,
                roomImage = roomImage,
                previousScreen = previousScreen
            )
        }
    }
}
