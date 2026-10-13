# Planilla de Test Cases — Sprint 2 (resumen)

La planilla completa está en Google Sheets (columnas: ID, historia, módulo, descripción, precondiciones, pasos, datos, resultado esperado/obtenido, estado, evidencia, observaciones, **Tipo de testing, Capa, ¿Automatizable?, Herramienta, ID del test**). Este archivo resume los casos agregados en el Sprint 2.

Numeración unificada con la planilla de Drive:
- **TC-01 a TC-14:** casos del Sprint 1.
- **TC-15 a TC-26:** pruebas unitarias y de red de sesión, errores HTTP y `AuthApi`.
- **TC-27 a TC-40:** casos agregados por Lanfranco (API, interfaz y seguridad), renumerados para no repetir IDs.

## TC-15 a TC-26: unitarias y de red

| ID | Caso | Tipo | Capa | Automatizable | Test | Resultado |
|----|------|------|------|---------------|------|-----------|
| TC-15 | Guardar token inicia sesión | Unitaria | Almacenamiento | Sí | `SessionManagerTest#guardarToken_tieneSesionYDevuelveElMismoToken` | Verde |
| TC-16 | Token vacío no cuenta como sesión | Unitaria (negativo) | Almacenamiento | Sí | `SessionManagerTest#tokenVacio_noCuentaComoSesion` | Verde |
| TC-17 | Cerrar sesión borra token y datos | Unitaria | Almacenamiento | Sí | `SessionManagerTest#cerrarSesion_borraTokenYDatosDelUsuario` | Verde |
| TC-18 | La sesión persiste entre instancias | Unitaria | Almacenamiento | Sí | `SessionManagerTest#sesionPersiste_alCrearOtroSessionManagerConElMismoContexto` | Verde |
| TC-19 | 401 devuelve mensaje de sesión expirada | Unitaria | Utilidades | Sí | `HttpErrorHandlerTest#codigo401_devuelveSesionExpirada` | Verde |
| TC-20 | Código desconocido devuelve mensaje por defecto | Unitaria (borde) | Utilidades | Sí | `HttpErrorHandlerTest#codigoDesconocido_devuelveMensajePorDefecto` | Verde |
| TC-21 | Login 200 se parsea a token y usuario | Integración (red simulada) | API / Modelos | Sí | `AuthApiTest#login200_parseaTokenYUsuario` | Verde |
| TC-22 | Login envía POST con usuario y contraseña en JSON | Integración | API | Sí | `AuthApiTest#login_enviaPostConUsuarioYPasswordEnJson` | Verde |
| TC-23 | Credenciales inválidas devuelven 401 | Integración (negativo) | API | Sí | `AuthApiTest#login401_noEsExitosoYTraeMensajeDeSesionInvalida` | Verde |
| TC-24 | Recurso inexistente devuelve 404 | Integración (negativo) | API | Sí | `AuthApiTest#me404_noEsExitoso` | Verde |
| TC-25 | Error de servidor devuelve 500 | Integración (negativo) | API | Sí | `AuthApiTest#me500_noEsExitosoYUsaMensajeDeErrorDeServidor` | Verde |
| TC-26 | Servidor sin respuesta produce timeout | Integración (negativo) | API | Sí | `AuthApiTest#login_siElServidorNoResponde_lanzaIOExceptionPorTimeout` | Verde |

## TC-02, 03, 04, 09, 12, 13: interfaz (Sprint 1, ahora automatizados donde corresponde)

| ID | Test de Espresso | Resultado |
|----|------------------|-----------|
| TC-02 | `LoginUiTest#login_muestraCamposPrincipales` | Verde |
| TC-03, TC-04 | `LoginUiTest#login_navegaARegistroDesdeEnlace` | Verde (tras corregir ACC-BUG-04) |
| TC-09 | `ContactUiTest#contacto_muestraFormulario` (parcial: no pasa por la navegación) | Verde |
| TC-12 | `MainUiTest#main_muestraListadoYNavegacion` | Verde |
| TC-13 | Sin test automatizado: prueba manual | Manual |

## TC-27 a TC-40: casos agregados por Lanfranco

| ID | Historia | Caso | Tipo | Capa | Automatizable | Herramienta / test | Resultado |
|----|----------|------|------|------|---------------|--------------------|-----------|
| TC-27 | US06 | Login válido devuelve 200 y token | Funcional | Integración de red | Sí | `AuthApiMockWebServerTest#login_cuandoCredencialesValidas_devuelve200YToken` | Verde |
| TC-28 | US06 | Login inválido devuelve 401 | Negativa | Integración de red | Sí | `AuthApiMockWebServerTest#login_cuandoCredencialesInvalidas_devuelve401` | Verde |
| TC-29 | US07 | Contraseña débil rechazada en el registro | Funcional | UI | Parcial | Manual (no hay test automatizado) | Pendiente |
| TC-30 | US08 | Listado de oportunidades desde la API | Funcional | API externa | Sí | Newman `Listar oportunidades` | Pendiente de evidencia |
| TC-31 | US09 | Postular requiere token | Integración | API externa | Parcial | Postman (a extender) | Manual |
| TC-32 | US10 | GET de perfil autenticado | Funcional | API externa | Sí | Newman `Perfil autenticado GET` | Pendiente de evidencia |
| TC-33 | US11 | Formulario de contacto: expande y valida campos | Funcional | UI instrumentada | Sí | `ContactUiTest#contacto_expandeFormularioYCompletaCampos` | Verde |
| TC-34 | US06 | Navegación Login → Registro | Humo | UI instrumentada | Sí | `LoginUiTest#login_navegaARegistroDesdeEnlace` | Verde |
| TC-35 | US08 | Main muestra listado y navegación | Humo | UI instrumentada | Sí | `MainUiTest#main_muestraListadoYNavegacion` | Verde |
| TC-36 | US06 | Contrato de login: 200 y token | Integración | API externa | Sí | Newman `Login OK` | Pendiente de evidencia |
| TC-37 | Seguridad | Perfil sin token devuelve 401 | Negativa | API externa | Sí | Newman `Perfil sin token (401)` | Pendiente de evidencia |
| TC-38 | Seguridad | Login con payload vacío devuelve 400 | Negativa | API externa | Sí | Newman `Login payload inválido (400)` | Pendiente de evidencia |
| TC-39 | US10 | PATCH de perfil con Bearer | Funcional | API externa | Sí | Newman `Perfil PATCH` | Pendiente de evidencia |
| TC-40 | RNF | Timeout de login | Negativa | Integración de red | Sí | `AuthApiMockWebServerTest#login_cuandoHayTimeout_fallaLaEjecucion` | Verde |

## Accesibilidad

| ID | Caso | Automatizable | Herramienta | Estado |
|----|------|---------------|-------------|--------|
| ACC-TC-01 | Accessibility Scanner en Login sin errores críticos | No | Accessibility Scanner | Pendiente de captura |
| ACC-TC-02 | TalkBack: Login → Main como visitante | No | Checklist TalkBack | Pendiente de video |
| ACC-TC-03 | `AccessibilityChecks` en los 3 flujos de Espresso | Sí | `LoginUiTest`, `MainUiTest`, `ContactUiTest` | Verde. Detectó y corrigió ACC-BUG-04 |

## Criterio de automatización

- **Sí:** login por API, listado de oportunidades, flujos de interfaz repetitivos de humo, MockWebServer.
- **No:** usabilidad subjetiva, recorrido completo con TalkBack, `mailto`/`tel` (AUT-SYS justificado).
