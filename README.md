# Arbarrio - Aplicación de Búsqueda de Arriendo (Android)

Arbarrio es una aplicación nativa para Android diseñada para facilitar la búsqueda de arriendos. Construida con tecnologías modernas y una arquitectura escalable, ofrece una experiencia de usuario fluida y robusta.

## 🚀 Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose (Moderno, Declarativo, Rápido)
- **Navegación**: Voyager (Navegación pragmática y tipada)
- **Inyección de Dependencias**: Koin (Ligero y potente)
- **Base de Datos Local**: Room (Persistencia offline)
- **Backend / Auth**: Firebase (Autenticación y Firestore para datos en tiempo real)
- **Carga de Imágenes**: Coil (Optimizado para Compose)

## 🏗️ Arquitectura

La aplicación sigue los principios de **Clean Architecture**, dividida en capas:
- **Data**: Implementaciones de repositorios, DAOs de Room y fuentes de datos de Firebase.
- **Domain**: Modelos de negocio, interfaces de repositorios y casos de uso.
- **UI (Presentation)**: ViewModels y Screens construidas con Compose.

## 📱 Funcionalidades Actuales

- [x] **Pantalla de Intro**: Animación de bienvenida y verificación de estado de sesión.
- [x] **Autenticación**: Login y Registro mediante Firebase Auth.
- [x] **Pantalla Principal**: Listado de propiedades en tiempo real desde Firestore.
- [x] **Búsqueda y Filtros**: Barra de búsqueda y categorías integradas.
- [x] **Detalle de Propiedad**: Vista extendida con imágenes, precio, ubicación y características técnicas.

## 🛠️ Configuración para Desarrolladores

1. Clona el repositorio.
2. Agrega tu archivo `google-services.json` en la carpeta `app/`.
3. Sincroniza con Gradle en Android Studio.
4. Asegúrate de habilitar **Authentication** (Email/Password) y **Firestore Database** en tu consola de Firebase.

---
Desarrollado con ❤️ para facilitar el acceso a la vivienda.
