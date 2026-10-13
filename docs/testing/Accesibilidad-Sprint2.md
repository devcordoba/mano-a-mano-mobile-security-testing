# Informe de accesibilidad — Sprint 2 (ACC-01…ACC-05)

## Alcance

Pantallas principales: **Splash**, **Main**, **Login**, **Registro**, **Contacto**, **Perfil**, **Detalle de oportunidad**.

## Automatizado: Accessibility Test Framework (Espresso)

Los tests instrumentados habilitan, en `@BeforeClass`:

```java
AccessibilityChecks.enable();
```

Clases: `LoginUiTest`, `MainUiTest`, `ContactUiTest`. Cada acción de Espresso (clic, escribir) ejecuta antes los chequeos sobre la vista.

**Criterio:** 0 errores no justificados.

### Resultado de la ejecución del 13/10/2026 (Pixel 3a, API 33)

| Ejecución | Resultado | Detalle |
|-----------|-----------|---------|
| Antes de la corrección | 6 tests, 1 con fallo | `LoginUiTest#login_navegaARegistroDesdeEnlace`: `TouchTargetSizeCheck`, "This item's height is 35dp. Consider making the height of this touch target 48dp or larger" sobre `tvIrRegistroLogin` |
| Después de la corrección | 6 tests en verde | `minHeight="48dp"` y `gravity="center"` en `tvIrRegistroLogin` |

El fallo de `ContactUiTest` que apareció durante la misma sesión no fue de accesibilidad: el botón no estaba suficientemente visible (menos del 90 %) al hacer clic. Se corrigió en el test con `scrollTo()`.

**Evidencia:** capturas del reporte de `connectedDebugAndroidTest` (antes y después), guardadas en Drive y enlazadas desde el issue ACC-BUG-04.

## Accessibility Scanner (Google)

| Pantalla | Fecha | Errores / advertencias | Evidencia |
|----------|-------|------------------------|-----------|
| Login | Pendiente | Completar tras escanear en un dispositivo | `capturas/acc/login-scanner.png` |
| Main | Pendiente | | `capturas/acc/main-scanner.png` |
| Contacto | Pendiente | | `capturas/acc/contacto-scanner.png` |

Pasos: instalar [Accessibility Scanner](https://play.google.com/store/apps/details?id=com.google.android.apps.accessibility.auditor), abrir cada pantalla, ejecutar el análisis y guardar la captura.

## Revisión manual: TalkBack (checklist mínimo)

Flujo crítico: **Main (visitante) → Iniciar sesión → Login (campos) → Volver**.

| Paso | Elemento esperado | Orden lógico | OK / Bug |
|------|-------------------|--------------|----------|
| 1 | Logo con descripción | Sí | Pendiente |
| 2 | Campos usuario y contraseña anunciados | Sí | Pendiente |
| 3 | Botón ingresar | Sí | Pendiente |
| 4 | Enlace de registro | Sí | Pendiente (el área táctil ya se corrigió, ACC-BUG-04) |

Evidencia: video corto con TalkBack activo (subir a Drive y enlazar desde la Wiki).

## Hallazgos reportados como bugs

Etiquetas `bug` y `accesibilidad`. Texto de los issues en [bugs-acc-sprint2-issues.md](./bugs-acc-sprint2-issues.md).

1. **ACC-BUG-01**: área táctil de "Iniciar sesión" en Main por debajo de 48 dp. *(a confirmar con el Scanner)*
2. **ACC-BUG-02**: campo de búsqueda de Main por debajo de 48 dp. *(a confirmar con el Scanner)*
3. **ACC-BUG-03**: errores de login solo con `Toast`, sin anuncio para lectores de pantalla.
4. **ACC-BUG-04**: enlace "¿No tenés cuenta? Registrate" de Login con 35 dp de alto. **Detectado por un test automático y corregido.**

## Correcciones

| Bug | Corrección | Estado |
|-----|------------|--------|
| ACC-BUG-01 | `minHeight` y `minWidth` de 48 dp en `tvIniciarSesion` | Pendiente |
| ACC-BUG-02 | Altura mínima de 48 dp en `editBuscar` | Pendiente |
| ACC-BUG-03 | `TextInputLayout.setError` o `accessibilityLiveRegion` | Pendiente |
| ACC-BUG-04 | `minHeight="48dp"` y `gravity="center"` en `tvIrRegistroLogin` | **Corregido** (verificado con los 6 tests en verde) |

## Mejoras ya presentes

- `contentDescription` en logos e imágenes principales.
- Botones primarios de al menos 52 dp en Login y Contacto.
- `contentDescription` en el botón de ayuda de Main.
