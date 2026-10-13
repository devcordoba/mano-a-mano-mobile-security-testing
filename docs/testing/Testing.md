# Testing — Mano a Mano Mobile (Sprint 2)

Punto de entrada de calidad del proyecto. Tras el merge a `main`, copiar o enlazar este archivo desde la Wiki.

Última verificación: **13/10/2026**, emulador Pixel 3a (API 33).

## Estrategia de automatización (pirámide)

| Capa | Código ABP | Herramienta | Cantidad real | Estado |
|------|------------|-------------|---------------|--------|
| Unitaria JVM | AUT-UNIT | JUnit 4 + Mockito | 54 tests en 9 clases (unitarias y de red) | Verde |
| Integración de red | AUT-NET | MockWebServer + Retrofit | 11 tests: `AuthApiTest` (6) y `AuthApiMockWebServerTest` (5). Cubren 200, 401, 404, 500, 400 y timeout | Verde |
| API caja negra | AUT-API | Postman + Newman | Colección con 10 requests (auth, oportunidades, contacto) en `tests/api/` | Colección entregada. **Falta** adjuntar evidencia de una ejecución con Newman |
| UI instrumentada | AUT-UI | Espresso + Screen Objects | 5 tests en 3 clases (`LoginUiTest` 2, `MainUiTest` 1, `ContactUiTest` 2), todos con `AccessibilityChecks` | Verde (6/6 instrumentados, incluido `ExampleInstrumentedTest`) |
| Sistema | AUT-SYS | UI Automator | No aplica, justificado (ver abajo) | N/A |
| E2E | AUT-E2E | Appium | Solo guía en `tests/appium/README.md` | No ejecutado (deseable) |
| Accesibilidad | ACC | `AccessibilityChecks` + Accessibility Scanner + TalkBack | 1 bug detectado por test automático y corregido (ACC-BUG-04) | Scanner y TalkBack pendientes |
| CI | — | GitHub Actions | No existe `.github/workflows/` todavía | Pendiente |

### Tests unitarios y de red por clase

| Clase | Tests | Qué cubre |
|-------|------:|-----------|
| `almacenamiento/SessionManagerTest` | 9 | Guardar, leer y borrar token y datos del usuario; la sesión persiste |
| `utilidades/HttpErrorHandlerTest` | 9 | Mensajes para 400, 401, 403, 404, 500, 502/503/504 y códigos desconocidos |
| `api/AuthApiTest` | 6 | Login 200, formato del POST, 401, 404, 500 y timeout |
| `api/AuthApiMockWebServerTest` | 5 | Login 200, 401, 400, 500 y timeout |
| `utilidades/MensajeFormatoTest` | 12 | Formato de mensajes |
| `utilidades/MensajesNoLeidosTest` | 6 | Conteo de mensajes no leídos |
| `modelos/BandejaMensajesTest` | 3 | Bandeja de mensajes |
| `modelos/OportunidadTest` | 3 | Modelo de oportunidad |
| `ExampleUnitTest` | 1 | Plantilla del proyecto |
| **Total** | **54** | |

## Qué no se automatizó

- Pruebas exploratorias de usabilidad y coherencia visual (TC-14): requieren criterio humano.
- Recorrido completo con TalkBack: manual.
- Abrir correo o teléfono desde Contacto (TC-10): lo resuelve otra app, ver AUT-SYS.
- Flujos que dependen de datos reales del backend (listado, búsqueda, detalle): se prueban a mano contra el servidor desplegado.
- Botón "atrás" del dispositivo (TC-13): sin test automatizado.

## AUT-SYS: justificación de UI Automator

Ver `tests/AUT-SYS-justificacion.txt`. En resumen, el `AndroidManifest.xml` declara como único permiso `INTERNET`. La app no usa cámara, ubicación ni notificaciones, por lo que no hay diálogos de permisos del sistema para automatizar. Declara `queries` para `mailto` y `tel` solo para abrir esas apps desde Contacto.

## Cómo ejecutar

### Requisitos

- Gradle 8.0, plugin Android 8.1.2, `compileSdk`/`targetSdk` 33, `minSdk` 24. No cambiar versiones.
- Para los instrumentados: emulador **API 33** (con API 35 o 36 falla el plugin de testing de Gradle) y animaciones desactivadas.

### Unitarias y de red

```
.\gradlew testDebugUnitTest
```

Reporte: `app/build/reports/tests/testDebugUnitTest/index.html`.
Cobertura (JaCoCo): `.\gradlew createDebugUnitTestCoverageReport` → `app/build/reports/coverage/test/debug/index.html`.

### Espresso (instrumentados)

Con el emulador encendido, desactivar animaciones una sola vez por emulador:

```
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
```

Ejecutar:

```
.\gradlew connectedDebugAndroidTest
```

Reporte: `app/build/reports/androidTests/connected/debug/index.html`.
Flujos: `LoginUiTest`, `MainUiTest`, `ContactUiTest`, con Screen Objects en `app/src/androidTest/.../ui/`.

### Postman / Newman

```
cd tests/api
npm install
npm run test:ci
```

Colección: `tests/api/mano-a-mano.postman_collection.json`. Entorno: `tests/api/mano-a-mano.postman_environment.json` (`{{baseUrl}}`, sin credenciales en el código).

## Resultados de la última ejecución (13/10/2026)

| Suite | Resultado |
|-------|-----------|
| Unitarias y de red (54) | Todas en verde |
| Espresso (6 instrumentados) | 6/6 en verde, después de corregir ACC-BUG-04. Antes de la corrección fallaba 1 por `TouchTargetSizeCheck` |
| Newman | Pendiente de evidencia |

## Documentos del sprint

| Documento | Ruta |
|-----------|------|
| Planilla de test cases (versión Drive, fuente principal) | Enlace en la Wiki |
| Planilla de test cases Sprint 2 (resumen) | [Planilla-Test-Cases-Sprint2.md](./Planilla-Test-Cases-Sprint2.md) |
| Matriz de trazabilidad | [Matriz-Trazabilidad-Sprint2.md](./Matriz-Trazabilidad-Sprint2.md) |
| Informe de accesibilidad | [Accesibilidad-Sprint2.md](./Accesibilidad-Sprint2.md) |
| Bugs de accesibilidad | [bugs-acc-sprint2-issues.md](./bugs-acc-sprint2-issues.md) |
| AUT-SYS | [../../tests/AUT-SYS-justificacion.txt](../../tests/AUT-SYS-justificacion.txt) |
| Appium (deseable) | [../../tests/appium/README.md](../../tests/appium/README.md) |

## Pendiente

- Ejecutar Newman y guardar el reporte.
- Workflow de GitHub Actions (`unit-tests` como mínimo).
- Accessibility Scanner en Login, Main y Contacto, y checklist de TalkBack.
- Enlaces a la planilla y a las capturas en Drive.
