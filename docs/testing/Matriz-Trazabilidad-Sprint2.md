# Matriz de trazabilidad — Sprint 2

Relaciona historia de usuario, caso de prueba (planilla de test cases), test automatizado y bug asociado.
Resultados de la ejecución del **13/10/2026** (JVM local; Espresso en emulador Pixel 3a, API 33).

## Sesión, errores HTTP y red (US02 — Login y Registro)

| Historia | Caso | Test automatizado (Clase#método) | Resultado | Bug |
|----------|------|----------------------------------|-----------|-----|
| US02 | TC-15 | `SessionManagerTest#guardarToken_tieneSesionYDevuelveElMismoToken` | Verde | — |
| US02 | TC-16 | `SessionManagerTest#tokenVacio_noCuentaComoSesion` | Verde | — |
| US02 | TC-17 | `SessionManagerTest#cerrarSesion_borraTokenYDatosDelUsuario` | Verde | — |
| US02 | TC-18 | `SessionManagerTest#sesionPersiste_alCrearOtroSessionManagerConElMismoContexto` | Verde | — |
| US02 | TC-19 | `HttpErrorHandlerTest#codigo401_devuelveSesionExpirada` | Verde | — |
| Transversal | TC-20 | `HttpErrorHandlerTest#codigoDesconocido_devuelveMensajePorDefecto` | Verde | — |
| US02 | TC-21 | `AuthApiTest#login200_parseaTokenYUsuario` | Verde | — |
| US02 | TC-22 | `AuthApiTest#login_enviaPostConUsuarioYPasswordEnJson` | Verde | — |
| US02 | TC-23 | `AuthApiTest#login401_noEsExitosoYTraeMensajeDeSesionInvalida` | Verde | — |
| US02 | TC-24 | `AuthApiTest#me404_noEsExitoso` | Verde | — |
| US02 | TC-25 | `AuthApiTest#me500_noEsExitosoYUsaMensajeDeErrorDeServidor` | Verde | — |
| US02 | TC-26 | `AuthApiTest#login_siElServidorNoResponde_lanzaIOExceptionPorTimeout` | Verde | — |
| US06 | TC-27 | `AuthApiMockWebServerTest#login_cuandoCredencialesValidas_devuelve200YToken` | Verde | — |
| US06 | TC-28 | `AuthApiMockWebServerTest#login_cuandoCredencialesInvalidas_devuelve401` | Verde | — |
| US06 | TC-40 | `AuthApiMockWebServerTest#login_cuandoHayTimeout_fallaLaEjecucion` | Verde | — |

## Interfaz (Espresso)

| Historia | Caso | Test automatizado (Clase#método) | Resultado | Bug |
|----------|------|----------------------------------|-----------|-----|
| US02 | TC-02 | `LoginUiTest#login_muestraCamposPrincipales` | Verde | — |
| US02 | TC-03, TC-04 | `LoginUiTest#login_navegaARegistroDesdeEnlace` | Verde (falló antes de la corrección) | ACC-BUG-04 (corregido) |
| US03 | TC-12 | `MainUiTest#main_muestraListadoYNavegacion` | Verde | — |
| US04 | TC-09 | `ContactUiTest#contacto_muestraFormulario` | Verde (abre Contacto directo, sin pasar por la navegación) | — |
| US04 | TC-33 | `ContactUiTest#contacto_expandeFormularioYCompletaCampos` | Verde (se corrigió el test con `scrollTo()`) | — |
| US15 | ACC-TC-03 | `LoginUiTest`, `MainUiTest`, `ContactUiTest` con `AccessibilityChecks.enable()` | Verde | ACC-BUG-04 |

## API caja negra (Postman / Newman)

| Historia | Caso | Request de la colección | Resultado |
|----------|------|-------------------------|-----------|
| US06 | TC-36 | `auth/Login OK` | Pendiente de evidencia |
| US06 | TC-38 | `auth/Login payload inválido (400)` | Pendiente de evidencia |
| US06 | TC-37 | `auth/Perfil sin token (401)` | Pendiente de evidencia |
| US10 | TC-32 | `auth/Perfil autenticado GET` | Pendiente de evidencia |
| US10 | TC-39 | `auth/Perfil PATCH (actualizar nombre)` | Pendiente de evidencia |
| US08 | TC-30 | `oportunidades/Listar oportunidades` | Pendiente de evidencia |

## Cobertura por tipo

| Código | Cobertura | Detalle |
|--------|-----------|---------|
| AUT-UNIT | 54 tests JUnit | Ver `Testing.md` |
| AUT-NET | 11 tests | `AuthApiTest` (6) y `AuthApiMockWebServerTest` (5) |
| AUT-API | 10 requests | Colección en `tests/api/` |
| AUT-UI | 5 tests Espresso | 3 clases |
| AUT-SYS | N/A | `tests/AUT-SYS-justificacion.txt` |
| AUT-E2E | Deseable | `tests/appium/README.md` |

**Issues de accesibilidad:** ACC-BUG-04 (Login, creado a partir de un test automático). Completar con los números reales de ACC-BUG-01 a 03 al abrirlos en GitHub.
