package dam.intermodular.app.core.dataStore

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import dam.intermodular.app.core.tinkCrypt.TinkManager
import javax.inject.Inject

class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tinkManager = TinkManager(context)

    val favoritos: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[FAVORITES_KEY] ?: emptySet()  // Si no existe, retornamos un set vacío
    }

    // Guardar los tokens, role y el idProfile en el DataStore
    suspend fun guardarTokens(accessToken: String, applicationToken: String, role: String, idProfile: String) {
        withContext(Dispatchers.IO) {
            val encryptedAccessToken = tinkManager.aead.encrypt(accessToken.toByteArray(), null)
            val encryptedApplicationToken = tinkManager.aead.encrypt(applicationToken.toByteArray(), null)

            val encryptAccessString = Base64.encodeToString(encryptedAccessToken, Base64.DEFAULT)
            val encryptApplicationString = Base64.encodeToString(encryptedApplicationToken, Base64.DEFAULT)

            context.dataStore.edit { preference ->
                preference[ACCESS_TOKEN_KEY] = encryptAccessString
                preference[APPLICATION_TOKEN_KEY] = encryptApplicationString
                preference[ACCESS_ROLE_KEY] = role
                preference[ID_PROFILE_KEY] = idProfile
            }
        }
    }

    // Obtener el AccessToken (desencriptado)
    fun getAccessToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]?.let { data ->
                val encryptedBytes = Base64.decode(data, Base64.DEFAULT)
                val decryptedBytes = tinkManager.aead.decrypt(encryptedBytes, null)
                String(decryptedBytes)
            }
        }
    }

    // Obtener el ApplicationToken (desencriptado)
    fun getApplicationToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[APPLICATION_TOKEN_KEY]?.let { data ->
                val encryptedApplication = Base64.decode(data, Base64.DEFAULT)
                val decryptedApplication = tinkManager.aead.decrypt(encryptedApplication, null)
                String(decryptedApplication)
            }
        }
    }

    // Obtener el Role
    fun getRole(): Flow<String?> {
        return context.dataStore.data.map { references ->
            references[ACCESS_ROLE_KEY]
        }
    }

    // Obtener el idProfile
    fun getIdProfile(): Flow<String?> {
        return context.dataStore.data.map { references ->
            references[ID_PROFILE_KEY]
        }
    }

    // Eliminar todos los datos de tokens y perfil
    suspend fun deleteStore() {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                preferences.remove(ACCESS_TOKEN_KEY)
                preferences.remove(APPLICATION_TOKEN_KEY)
                preferences.remove(ACCESS_ROLE_KEY)
                preferences.remove(ID_PROFILE_KEY)
            }
        }
    }

    // Función para agregar o quitar favoritos en el DataStore
    suspend fun toggleFavorite(id: String) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY] ?: emptySet()
            val newFavorites = if (id in currentFavorites) {
                currentFavorites - id
            } else {
                currentFavorites + id
            }
            preferences[FAVORITES_KEY] = newFavorites
        }
    }


    suspend fun savePuntos(puntos: Int) {
        context.dataStore.edit { preferences ->
            preferences[PUNTOS_KEY] = puntos
        }
    }


    val tarjetaPuntos: Flow<Int> = context.dataStore.data
            .map { preferences ->
                    preferences[PUNTOS_KEY] ?: 0 // Devuelve 0 si no hay puntos guardados
            }
}