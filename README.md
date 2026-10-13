# Mano a Mano Mobile

Aplicación móvil nativa para Android desarrollada por estudiantes del **ISPC** para el proyecto **Mano a Mano**, una plataforma que conecta a personas interesadas en realizar voluntariado con organizaciones que publican oportunidades de participación.

| Tipo | Info |
|---|---|
| Organización | [devcordoba](https://github.com/devcordoba) |
| Repositorio app | [mano-a-mano-mobile-security-testing](https://github.com/devcordoba/mano-a-mano-mobile-security-testing) |
| Backend / API web | [mano-a-mano](https://github.com/devcordoba/mano-a-mano) |
| API desplegada | https://mano-a-mano-deploy.vercel.app/ |
| Wiki | [Wiki del proyecto](https://github.com/devcordoba/mano-a-mano-mobile-security-testing/wiki) |
| minSdk / targetSdk | 24 / 33 |

---

## Estado del Proyecto: Sprint 2 (Evidencia 3)

El incremento del **Sprint 2** integra la app con la **API REST** (Django), autenticación **JWT**, persistencia remota, CRUD de organización/ofertas, perfil, mensajes, contacto, validación de contraseñas, pruebas automatizadas y criterios de accesibilidad.

### Implementado

- **Splash:** pantalla inicial y entrada al flujo.
- **Login JWT:** autenticación real contra el backend; mensaje ante credenciales incorrectas; sesión local.
- **Registro:** alta de usuario con contraseña robusta (más de 8 caracteres, mayúscula, minúscula, número y símbolo) y email válido.
- **Inicio (Main):** listado de oportunidades **desde la API**, búsqueda y filtros; indicador de carga.
- **Detalle y postulación:** traspaso de datos entre Activities; postulación autenticada.
- **Perfil:** datos del usuario logueado; sin sesión redirige a Login y al volver regresa a Inicio.
- **Mensajes:** bandeja, lectura, respuesta e indicador de no leídos.
- **Contacto:** formulario validado y envío persistido en el backend; email/teléfono del proyecto.
- **Organización:** alta/edición de datos de la organización y **CRUD de ofertas** (crear, editar, pausar/activar, eliminar, imagen).
- **Roles:** experiencia distinta para visitante, voluntario y organización.
- **Seguridad en cliente:** token Bearer en requests protegidas; HTTPS hacia el deploy.
- **Ayuda / multimedia:** panel de ayuda y recursos de imagen en la app.
- **Accesibilidad básica:** botones más cómodos de tocar y mejora de foco (US15).
- **Testing:** suite unitaria JVM (JUnit); dependencias Mockito, MockWebServer, Espresso y Accessibility en el módulo `app`.

### Enlaces útiles

- Wiki Home (ceremonias Sprint 1 y 2): https://github.com/devcordoba/mano-a-mano-mobile-security-testing/wiki
- Testing: https://github.com/devcordoba/mano-a-mano-mobile-security-testing/wiki/Testing
- Ciberseguridad: https://github.com/devcordoba/mano-a-mano-mobile-security-testing/wiki/Ciberseguridad
- Accesibilidad: https://github.com/devcordoba/mano-a-mano-mobile-security-testing/wiki/Accesibilidad
- IEEE 830: https://github.com/devcordoba/mano-a-mano-mobile-security-testing/wiki/Documento-IEEE-830
- Capturas: https://drive.google.com/drive/folders/1YjRzEcMS7n03S60pWMc-MJunL5j6ramy?usp=sharing
- Milestone Sprint 2: https://github.com/devcordoba/mano-a-mano-mobile-security-testing/milestone/2

---

## Funcionalidades

### Splash

Punto de entrada. Tras unos segundos continúa hacia **Main**.

### Login y sesión

- Usuario y contraseña validados contra la API.
- Ante error: mensaje *Usuario y/o contraseña incorrecta.*
- Token JWT guardado en sesión local y enviado en `Authorization: Bearer …`.
- Cerrar sesión vuelve a modo visitante en Inicio.

### Registro

Alta de cuenta con validación de email y política de contraseña segura. Con datos válidos se puede iniciar sesión.

### Inicio – oportunidades

Lista oportunidades publicadas en el servidor (no datos de ejemplo). Incluye búsqueda/filtros e indicador de carga. Ante fallo de red la pantalla permanece usable.

### Detalle y postulación

Al tocar una oportunidad se abre el detalle con la información transferida. Con sesión se puede postular; sin sesión se solicita Login.

### Perfil

Muestra nombre, correo y rol del usuario autenticado. Sin sesión abre Login; al cancelar se regresa a Inicio sin cerrar la app.

### Mensajes

Bandeja de conversaciones, lectura del hilo, envío de respuestas y aviso de mensajes no leídos en el menú.

### Contacto

Formulario con nombre, email, asunto y mensaje. Valida campos y persiste la consulta en el backend. También ofrece email y teléfono del proyecto.

### Organización y ofertas (CRUD)

Con rol organización: guardar datos de la organización; publicar, editar, pausar/activar y eliminar ofertas (incluye imagen cuando corresponde).

### Ayuda

Panel de ayuda accesible desde la navegación, con contenido de uso de la app.

---

## Flujo de la App

```text
┌───────────────────────────────┐
│        INICIO DE LA APP       │
└───────────────┬───────────────┘
                │
                ▼
┌───────────────────────────────┐
│       SPLASH ACTIVITY         │
│         (breve espera)        │
└───────────────┬───────────────┘
                │
                ▼
┌──────────────────────────────────────────────┐
│                   MAIN                       │
│  Visitante o usuario autenticado             │
│  - Oportunidades desde API                   │
│  - Buscador / filtros                        │
│  - Menú: Inicio · Mensajes* · Perfil* · …    │
│  - Contacto · Ayuda · Iniciar sesión / Salir │
└───┬──────────┬──────────┬──────────┬─────────┘
    │          │          │          │
    │          │          │          └── Contacto
    │          │          │                 │
    │          │          │                 ▼
    │          │          │        ┌─────────────────┐
    │          │          │        │    CONTACTO     │
    │          │          │        │ Formulario API  │
    │          │          │        │ Email / Teléfono│
    │          │          │        └────────┬────────┘
    │          │          │                 │
    │          │          │                 └──► MAIN
    │          │          │
    │          │          └── Perfil*
    │          │                   │
    │          │          ┌────────┴────────┐
    │          │          │ ¿Hay sesión?    │
    │          │          └───┬─────────┬───┘
    │          │             Sí        No
    │          │              │         │
    │          │              ▼         ▼
    │          │        ┌─────────┐  ┌────────┐
    │          │        │ PERFIL  │  │ LOGIN  │
    │          │        │ datos   │  │ (atrás │
    │          │        │ rol     │  │ → MAIN)│
    │          │        │ org.**  │  └───┬────┘
    │          │        └────┬────┘      │
    │          │             │      Ingresar OK
    │          │             │           │
    │          │             └───────────┴──► MAIN auth
    │          │
    │          └── Mensajes* ──► BANDEJA ──► HILO ──► responder
    │
    ├── Seleccionar oportunidad
    │              │
    │              ▼
    │   ┌──────────────────────────────┐
    │   │     DETALLE OPORTUNIDAD      │
    │   │  datos + Postularse / Volver │
    │   └──────────┬───────────┬───────┘
    │              │           │
    │         Postularse     Volver ──► MAIN
    │              │
    │     ┌────────┴────────┐
    │     │ ¿Sesión?        │
    │     └───┬─────────┬───┘
    │        Sí        No ──► LOGIN
    │         │
    │         ▼
    │   Confirmación postulación (API)
    │
    └── Iniciar sesión / Registrarse
                   │
                   ▼
        ┌────────────────────┐
        │       LOGIN        │
        │  JWT + sesión      │
        └───┬───────────┬────┘
            │           │
       Ingresar    Registrarse
            │           │
            ▼           ▼
     MAIN auth     REGISTRO
                   (clave segura)
                        │
                        └──► LOGIN ──► MAIN auth

(*) Requiere sesión. Sin sesión → Login.
(**) Organización: panel Mi organización + CRUD de ofertas.
```

### Flujo resumido por rol

| Rol | Qué puede hacer |
|---|---|
| Visitante | Ver oportunidades, detalle, contacto, ir a login/registro |
| Voluntario | Todo lo de visitante + perfil, postularse, mensajes |
| Organización | Perfil + datos de organización + CRUD de ofertas + mensajes |

---

## Stack tecnológico

| Componente | Detalle |
|---|---|
| Plataforma | Android nativo |
| Lenguaje | Java |
| Red | Retrofit + OkHttp + Gson |
| Auth | JWT (header `Authorization: Bearer`) |
| Testing | JUnit, Mockito, MockWebServer, Espresso, Accessibility |
| Build | Gradle / Android Studio |
| Branching | Gitflow |

---

## Configuración de la API

En debug, la URL base apunta al backend desplegado:

```text
https://mano-a-mano-deploy.vercel.app/
```

---

## Compilación y ejecución

### Requisitos

- Android Studio
- Android SDK
- JDK compatible con el proyecto
- Dispositivo físico (Android 7+ / API 24+)

### Clonar

```bash
git clone https://github.com/devcordoba/mano-a-mano-mobile-security-testing.git
cd mano-a-mano-mobile-security-testing
```

### Abrir y ejecutar

1. Abrir el proyecto en Android Studio.
2. Esperar la sincronización de Gradle.
3. Conectar emulador o dispositivo.
4. Ejecutar con **Run**.

### Pruebas

```bash
# Unitarias (JVM)
./gradlew test

# Instrumentadas (emulador/dispositivo)
./gradlew connectedAndroidTest
```

---

## Equipo de desarrollo

| Nombre | Rol |
|---|---|
| Lanfranco Darel Caballero | Scrum Master / Developer |
| Gonzalo Quiroga | Product Owner / Developer |
| Gabriel Natale | Developer |
| Lucas Monzón | Developer |
| Ivo Konstantinow | Developer |
| Tomás Huespe | Developer |
| Kevin Agustín Astrada | Developer |
