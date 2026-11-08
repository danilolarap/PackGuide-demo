# PackGuide web

Reproducción en React y TypeScript de la instantánea Android del 8 de noviembre de 2026. Incluye acceso y registro local, Escáner, Catálogo, Guía, Resumen y Ajustes.

## Ejecutar en Windows

Requiere Node.js 22.12 o posterior (validado con Node 24.14).

```powershell
npm.cmd ci
npm.cmd run dev
```

Abre la dirección que imprime Vite (normalmente http://127.0.0.1:5173). `npm.cmd` permite ejecutar npm cuando PowerShell bloquea `npm.ps1`. Para servir el resultado de producción:

```powershell
npm.cmd run build
npm.cmd run preview
```

Primero pulsa **[ CREAR CUENTA ]** y registra una cuenta de este navegador. El usuario admite entre 3 y 24 caracteres; la contraseña, de 1 a 45 letras o números. No existen credenciales de demostración ni un acceso que omita la validación. Las cuentas no se sincronizan con Android.

## Comportamiento y datos

- Los 36 artículos, dimensiones, litros y cantidades iniciales se importaron del anexo Kotlin, conservado en `reference/`. Los seis primeros suman 9 unidades y 19.2 L.
- CABINA es la única maleta inicial, con 55 × 35 × 25 cm sin confirmar. Las maletas añadidas empiezan sin artículos. Cada una conserva sus dimensiones, unidades, empaque, zonas y fotografías.
- El volumen se calcula con mililitros enteros. CABINA conserva internamente 48.125 L; la presentación es 48.1 L, 28.9 L libres y 39% de ocupación inicial.
- IndexedDB almacena las cuentas con sal aleatoria de 16 bytes y PBKDF2-HMAC-SHA256, 120000 iteraciones y 256 bits. Solo tema e idioma se guardan en localStorage. La sesión, el plan y las fotos permanecen en memoria y se reinician al cerrar sesión o recargar.
- Las imágenes se procesan en el navegador, sin servicios de IA ni subida de fotografías. El resultado de Canvas mantiene el ancho de 1200 px y las zonas y coordenadas del original. Cambiar las cantidades, dimensiones, zonas o foto invalida la vista anterior; marcar unidades como empacadas no la invalida.
- La revisión admite hasta cuatro fotos, usa la primera y permite quitar y reemplazar. Los errores y la cancelación conservan las selecciones previas.

## Comprobaciones

```powershell
npm.cmd test
npm.cmd run build
# Con la vista de producción sirviendo en el puerto 5173:
node scripts/browser-check.mjs
```

Las pruebas de modelo comprueban datos iniciales, capacidad exacta, exceso, empaque por unidad, límites de cantidad, independencia de maletas, zonas, invalidación y validación de acceso. La comprobación de navegador usa Playwright con Chrome instalado en Windows; se puede indicar otro ejecutable con `PACKGUIDE_BROWSER_PATH` o usar el Chromium de Playwright. Guarda capturas de revisión en `artifacts/`.

## Fidelidad y diferencias de plataforma

Se siguieron las fuentes completas y las medidas de `reference/specification.md`. Se conservaron la paleta, los radios, la matriz 6 × 6, los iconos Material Outlined, la disposición vertical y el tracker del catálogo fuera de la lista. No se suministraron capturas ni un render Android; queda pendiente la comparación visual al mismo tamaño, densidad y escala de texto. No se afirma igualdad píxel a píxel.

El ancho máximo de revisión de 440 CSS px es una decisión de envoltura web, no una medida extraída del dispositivo original. Se verifican también viewports de 390 × 844 y 320 × 568. Se usan Roboto y Roboto Mono locales como aproximación explícita: las fuentes genéricas de Android pueden resolver de otra forma. Los campos y controles Material 3 están recreados en CSS y conservan posibles diferencias de métricas respecto de Compose. El scrim usa 32% como aproximación de plataforma pendiente de contraste.

En móviles, **TOMAR FOTO** solicita la cámara mediante el selector con `capture`. En escritorio solicita la cámara real con `getUserMedia` y obtiene una captura de referencia; si no está disponible o no tiene permiso, muestra un error y conserva **ELEGIR FOTO**. La cámara requiere HTTPS o localhost. La selección de archivos y los diálogos son los del navegador.

La app funciona localmente una vez servidos sus recursos; no se ha añadido un service worker ni se promete una recarga sin conexión al servidor.

## Archivos

- `src/App.tsx`: pantallas, navegación, estado compartido y ciclo de vida de fotografías.
- `src/model.ts`, `src/catalog.json`: inventario y cálculos.
- `src/auth.ts`: autenticación local.
- `src/composition.ts`: lectura de fotos y composición Canvas.
- `src/components.tsx`, `src/styles.css`: componentes y medidas.
- `src/i18n.tsx`, `src/translations.json`, `src/common-terms.json`: diccionario, pares de texto de previews y fallback de términos comunes del anexo.
- `scripts/import-reference.mjs`: importación reproducible de catálogo y diccionario desde Kotlin.

Material Design Icons y las fuentes conservan sus licencias en las dependencias; se incluyen copias en `public/licenses/`, que se distribuyen con la compilación.
