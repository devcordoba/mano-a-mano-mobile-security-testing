# Mano a Mano Mobile

Aplicación móvil nativa para Android desarrollada por estudiantes del **ISPC** para el proyecto **Mano a Mano**, una plataforma creada para conectar a personas interesadas en realizar voluntariado con organizaciones que ofrecen oportunidades de participación.

---

## Estado del Proyecto: Sprint 1

El repositorio refleja la entrega correspondiente al **Sprint 1**, centrada en la construcción de la base de la aplicación móvil, sus interfaces, identidad visual y navegación.

### Implementado

- **Splash:** pantalla inicial de la aplicación.
- **Login:** interfaz de inicio de sesión.
- **Registro:** interfaz para el registro de usuarios.
- **Catálogo de oportunidades:** pantalla principal con oportunidades representadas mediante datos de ejemplo.
- **Detalle de oportunidad:** consulta de la información correspondiente a una oportunidad seleccionada.
- **Transferencia de información:** envío de información de una oportunidad entre Activities.
- **Navegación:** navegación entre las diferentes pantallas y regreso a la pantalla anterior.
- **Contacto:** pantalla con información de contacto.
- **Identidad visual:** aplicación de logo, colores, tipografías y componentes visuales definidos para el proyecto.

### Próximos Sprints

Las funcionalidades de integración y persistencia serán desarrolladas en las siguientes etapas del proyecto, incluyendo:

- Conexión con servicio web mediante API REST.
- Obtención de datos reales.
- Persistencia de información.
- Operaciones CRUD.
- Autenticación funcional.
- Gestión de sesiones.
- JWT.
- Roles y permisos.
- Integración con el backend Django.

---

## Funcionalidades Implementadas

### Pantalla Splash

La aplicación cuenta con una pantalla inicial **Splash**, que constituye el punto de entrada al sistema y permite iniciar el flujo de navegación de la aplicación.

### Inicio de Sesión

La pantalla de **Login** permite visualizar los campos y controles necesarios para el acceso de un usuario.

En el Sprint 1 se implementó la interfaz y su navegación. La validación contra un servidor y la autenticación real serán incorporadas posteriormente.

### Registro

La aplicación dispone de una pantalla de **Registro** para representar el proceso de creación de una cuenta.

El usuario puede acceder a esta pantalla desde Login y regresar nuevamente al inicio de sesión.

### Visualización de Oportunidades

La pantalla **Main** permite visualizar oportunidades de voluntariado mediante datos de ejemplo.

Cada oportunidad presenta información que permite identificarla y seleccionarla para consultar posteriormente su detalle.

### Detalle de Oportunidad

Al seleccionar una oportunidad desde la pantalla principal, se accede a la pantalla **Detalle**.

La información de la oportunidad seleccionada es transferida entre Activities y presentada en la pantalla correspondiente.

El usuario puede regresar desde Detalle hacia Main.

### Contacto

La aplicación cuenta con una pantalla **Contacto**, donde se presenta información relacionada con los medios de contacto definidos para el proyecto.

La pantalla se encuentra integrada al flujo de navegación de la aplicación.

---

## 🛠️ Stack Tecnológico

| Componente | Detalle |
| :--- | :--- |
| **Plataforma** | Android Nativo |
| **Lenguaje** | Java |
| **Paquete Base** | `com.ispc.manoamano` |
| **IDE** | Android Studio |
| **Build** | Gradle |
| **Control de Versiones** | Git & GitHub |
| **Modelo de Branching** | Gitflow |

---

## 🚀 Compilación y Ejecución

### Requisitos

- Android Studio.
- Android SDK.
- JDK compatible con el proyecto.
- Emulador Android o dispositivo físico.

### Clonar el repositorio

```bash
git clone https://github.com/devcordoba/mano-a-mano-mobile-security-testing.git
```

### Abrir el proyecto

1. Abrir **Android Studio**.
2. Seleccionar **Open**.
3. Seleccionar la carpeta raíz del proyecto.
4. Esperar la sincronización de Gradle.
5. Conectar un emulador o dispositivo Android.
6. Ejecutar la aplicación mediante **Run**.
---

## 👥 Equipo de Desarrollo

- **Lanfranco Darel Caballero**
- **Gonzalo Quiroga**
- **Gabriel Nata**
- **Lucas Monzón**
- **Ivo Konstantinow**
- **Tomás Huespe**
- **Kevin Agustín Astrada**