# Arbarrio 🏙️

Aplicación Android nativa para **buscar y publicar arriendos en barrios periféricos**. Los
propietarios publican sus inmuebles con fotos y ubicación en el mapa; los inquilinos los encuentran
filtrando por categoría, los guardan en favoritos y contactan al dueño.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore%20%7C%20Storage-FFCA28?logo=firebase&logoColor=black)
![minSdk](https://img.shields.io/badge/minSdk-24-3DDC84?logo=android&logoColor=white)

---

## Tabla de contenido

- [Funcionalidades](#funcionalidades)
- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Configuración](#configuración)
- [Compilar y ejecutar](#compilar-y-ejecutar)

## Funcionalidades

- 🚀 **Intro animada** con verificación de sesión.
- 🔐 **Autenticación**: correo/contraseña, Google (Credential Manager) y Facebook, sobre Firebase Auth.
  Desbloqueo con **biometría**.
- 🏠 **Inicio**: listado de inmuebles en tiempo real desde Firestore, búsqueda y filtros por categoría.
- 🗺️ **Mapa y ubicación**: Google Maps Compose y ubicación del dispositivo para ver arriendos cercanos.
- 📄 **Detalle del inmueble**: galería de imágenes, precio, ubicación y características.
- ➕ **Publicar inmueble** con fotos (Firebase Storage) y **Mis propiedades** para administrarlas.
- ❤️ **Favoritos** guardados localmente con Room (funcionan sin conexión).
- 👤 **Perfil editable** y **Ajustes**: tema oscuro premium, idioma español/inglés por app.

## Stack tecnológico

| Área | Tecnología |
|------|------------|
| Lenguaje | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3, tipografía Inter |
| Navegación | Voyager |
| Inyección de dependencias | Koin |
| Backend | Firebase Auth, Cloud Firestore, Firebase Storage |
| Persistencia local | Room + DataStore Preferences |
| Mapas | Maps Compose + Play Services Location |
| Login social | Credential Manager (Google ID), Facebook SDK |
| Seguridad | AndroidX Biometric |
| Imágenes | Coil |

## Arquitectura

Clean Architecture en tres capas con MVVM en la presentación:

```
ui/screens (Compose + ViewModel)  ──►  domain (modelos + interfaces de repositorio)
                                              ▲
                                              │ implementa
data (Firebase, Room, DataStore, LocationService)
```

- **data**: `AuthRepositoryImpl`, `PropertyRepositoryImpl`, DAO/entidades de Room, `PreferencesManager`
  y `LocationService`.
- **domain**: `Property`, `User`, `AuthRepository`, `PropertyRepository`.
- **ui**: una pantalla + ViewModel por funcionalidad, componentes reutilizables y tema.
- **di**: `appModule.kt` registra repositorios, base de datos y ViewModels en Koin.

## Estructura del proyecto

```
app/src/main/java/com/arbarrio/app/
├── data/        local/ (Room, DataStore) · location/ · repository/
├── di/          appModule.kt
├── domain/      model/ · repository/
└── ui/          components/ · screens/ · theme/
```

## Configuración

1. Crea un proyecto en la [consola de Firebase](https://console.firebase.google.com/) con el paquete
   `com.arbarrio.app` y descarga `google-services.json` en `app/`.
2. Habilita **Authentication** (Email/Password, Google y Facebook), **Firestore** y **Storage**.
3. Registra la huella SHA-1 de tu keystore de depuración en Firebase (necesaria para Google Sign-In).
4. Configura tu API key de Google Maps y los IDs de Facebook (`facebook_app_id`,
   `facebook_client_token`) en los recursos `strings.xml`.

## Compilar y ejecutar

Requisitos: Android Studio reciente, JDK 17+, Android SDK 36.

```bash
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

Desarrollado por **Adrián Alvarez** ([@Cordobot](https://github.com/Cordobot)) con ❤️ para facilitar el acceso a la vivienda.

© 2026 Adrián Alvarez. Todos los derechos reservados.
