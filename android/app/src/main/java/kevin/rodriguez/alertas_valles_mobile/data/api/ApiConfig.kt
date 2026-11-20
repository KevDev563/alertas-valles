package kevin.rodriguez.alertas_valles_mobile.data.api

object ApiConfig {
    /**
     * URL base por defecto. La URL activa se configura en la app (pantalla de login)
     * y se guarda en DataStore, para no tener que recompilar al cambiar de red/IP.
     */
    const val DEFAULT_BASE_URL: String = "http://10.0.2.2:8000/"
}
