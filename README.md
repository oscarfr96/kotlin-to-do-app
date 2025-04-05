# To-Do List App

Aplicación de gestión de tareas desarrollada con **Kotlin**, **Jetpack Compose** y **Firebase**, que permite crear, organizar y dar seguimiento a tareas diarias con características avanzadas como priorización, vistas organizadas temporalmente, calendario interactivo y temas personalizables.

## Características Principales

- ✅ **Autenticación de usuarios** con Firebase Authentication.
- 📋 **Gestión completa de tareas**: creación, edición, eliminación y completado.
- 🚩 **Priorización de tareas**: Alta, Media, Baja.
- 📅 **Organización temporal**: visualización por día, futuras y pasadas.
- 📆 **Vista de calendario interactivo** con indicadores visuales.
- 🔍 **Filtros avanzados** por texto, prioridad y fecha.
- 🤚 **Interacciones gestuales**: deslizar para completar o eliminar tareas.
- 🌗 **Temas claro y oscuro** personalizables.

## Arquitectura

El proyecto sigue una arquitectura **MVVM (Model-View-ViewModel)** con **Repository Pattern**, garantizando:

- Separación clara de responsabilidades.
- Mayor facilidad para pruebas unitarias.
- Alta mantenibilidad y escalabilidad.
- Mejor rendimiento y fluidez en la experiencia del usuario.


## Tecnologías Utilizadas

- **Kotlin** 
- **Jetpack Compose** – framework declarativo para la interfaz.
- **Firebase Authentication** – autenticación segura de usuarios.
- **Firebase Firestore** – almacenamiento NoSQL en la nube.
- **Material Design 3** – sistema de diseño adaptable.
- **Flow y StateFlow** – manejo reactivo de datos.
- **Coroutines** – programación asíncrona eficiente.

## Flujo de Datos

```plaintext
Interfaz de Usuario (Compose) ↔ ViewModel ↔ Repository ↔ Firebase
```

## Instalación y Configuración

1. Clona el repositorio:

```bash
git clone <URL_REPOSITORIO>
```

2. Abre el proyecto en Android Studio.
3. Configura Firebase Authentication y Firestore desde [Firebase Console](https://console.firebase.google.com/).
4. Descarga y coloca el archivo `google-services.json` en la carpeta `/app`.
5. Sincroniza las dependencias en Android Studio.

## Licencia

Este proyecto está bajo la licencia MIT.

