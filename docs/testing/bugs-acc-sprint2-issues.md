# Issues de accesibilidad (GitHub)

Etiquetas de cada issue: `bug`, `accesibilidad`, `Sprint 2`.
El sprint pide que **cada integrante** aporte al menos un hallazgo de accesibilidad. Anotar abajo quién tomó cada uno.

| Issue | Pantalla | Estado | Responsable |
|-------|----------|--------|-------------|
| ACC-BUG-01 | Main | Por abrir | Sin asignar |
| ACC-BUG-02 | Main | Por abrir | Sin asignar |
| ACC-BUG-03 | Login | Por abrir | Sin asignar |
| ACC-BUG-04 | Login | **Abierto y corregido** | Gonzalo Quiroga |

---

## ACC-BUG-04 — Enlace "Registrate" en Login mide 35 dp (mínimo 48 dp)

**Pantalla:** Login (`activity_login.xml`, `@+id/tvIrRegistroLogin`)
**Pauta:** Android Accessibility, tamaño mínimo de área táctil (48×48 dp); WCAG 2.2, 2.5.8 Target Size (Minimum)
**Severidad:** Media
**Cómo se detectó:** el test `LoginUiTest#login_navegaARegistroDesdeEnlace`, con `AccessibilityChecks.enable()`, falló con `TouchTargetSizeCheck`: "This item's height is 35dp. Consider making the height of this touch target 48dp or larger".
**Corrección:** agregar `android:minHeight="48dp"` y `android:gravity="center"` al `TextView`. Con esto pasan los 6 tests instrumentados.
**Evidencia:** captura del reporte con el fallo y captura del reporte en verde (enlaces de Drive en el issue).
**Detectado por:** Gonzalo Quiroga.

---

## ACC-BUG-01 — Área táctil insuficiente en "Iniciar sesión"

**Pantalla:** Main (`activity_main.xml`, `@+id/tvIniciarSesion`)
**Pauta:** WCAG 2.2, 2.5.8 Target Size (Minimum); Android, 48×48 dp
**Severidad:** Media
**Descripción:** el control "Iniciar sesión" mide aproximadamente 32 dp de alto. TalkBack lo anuncia, pero es difícil de tocar para personas con motricidad reducida. *Confirmar la medida con Accessibility Scanner.*
**Criterio de cierre:** `minHeight` y área clickeable de al menos 48 dp sin romper el diseño.

---

## ACC-BUG-02 — Campo de búsqueda por debajo del mínimo táctil

**Pantalla:** Main (`@+id/editBuscar`, 40 dp de alto)
**Pauta:** Android Accessibility, tamaño de área táctil
**Severidad:** Baja
**Descripción:** el campo de búsqueda no alcanza los 48 dp de alto. *Confirmar con Accessibility Scanner.*
**Criterio de cierre:** altura mínima de 48 dp o relleno táctil equivalente.

---

## ACC-BUG-03 — Error de login no anunciado por TalkBack

**Pantalla:** Login (`LoginActivity`)
**Pauta:** WCAG 4.1.3 Status Messages; `accessibilityLiveRegion`
**Severidad:** Media
**Descripción:** ante credenciales incorrectas se usa un `Toast`, que TalkBack no garantiza anunciar. Una persona ciega puede no percibir el error.
**Evidencia:** video con TalkBack durante un intento de login fallido.
**Criterio de cierre:** mensaje visible en la interfaz con `liveRegion`, o error asociado al campo.
