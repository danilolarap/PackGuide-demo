# PackGuide web

Demo web en React y TypeScript actualizada a la especificación Android de noviembre de 2026. Incluye acceso y registro local, Medidas, Catálogo, Guía, Resumen, vista previa de organización y Ajustes.

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
- Medidas bloquea la navegación hasta confirmar todas las maletas seleccionadas. Cambiar una dimensión o añadir una maleta reactiva el bloqueo.
- Catálogo mantiene a la vista una barra de capacidad por maleta. Guía presenta un tutorial de YouTube por maleta y casillas independientes para cada unidad empacada.
- Resumen admite hasta cuatro fotos por maleta. El informe de organización es una interfaz local: muestra la foto original de la maleta activa y un esquema independiente de todas las maletas seleccionadas, con sus medidas, zonas, unidades y progreso de 40 puntos. Abre también sin foto. La evaluación de foto con IA solo se ofrece al configurar un endpoint; únicamente envía la foto elegida cuando el usuario pulsa la acción.

## Comprobaciones

```powershell
npm.cmd test
npm.cmd run build
```

Las cinco pruebas de modelo comprueban datos iniciales, capacidad exacta, exceso, empaque por unidad, límites de cantidad, independencia de maletas, zonas y validación de acceso. Con Vite en el puerto 5173, ejecuta `node scripts/browser-check.mjs`: comprueba acceso, bloqueo de medidas, inventarios, zonas, casillas, tarjetas de Resumen, informe de tres maletas, fotos, límite de cuatro, idiomas, temas y anchos de 360–430 px. Las capturas nuevas están en `artifacts/*updated.png` y `artifacts/report-dark-en.png`.

Para comprobar el contrato de evaluación con respuestas controladas, inicia otra instancia con `$env:VITE_PACKGUIDE_REVIEW_ENDPOINT='/v1/packing-review'; npm.cmd run dev -- --port 5174` y ejecuta `node --experimental-strip-types scripts/review-check.mjs`. Solo este test intercepta el servicio: valida el envío explícito de la foto elegida, nota válida, imagen no evaluable, cuota, respuesta inválida, cancelación y consejos estables. No confirma disponibilidad de Gemini ni usa una clave real.

## Fidelidad y diferencias de plataforma

Se siguieron las fuentes completas y las medidas de `reference/specification.md` y los cambios solicitados el 11 de noviembre de 2026. Se conservaron la paleta, los radios, los motivos LED de viaje, los iconos Material Outlined, la disposición vertical y el tracker del catálogo fuera de la lista. No se suministraron capturas ni un render Android; queda pendiente la comparación visual al mismo tamaño, densidad y escala de texto. No se afirma igualdad píxel a píxel.

El ancho máximo de revisión de 440 CSS px es una decisión de envoltura web, no una medida extraída del dispositivo original. Se verifican también viewports de 390 × 844 y 320 × 568. Se usan Roboto y Roboto Mono locales como aproximación explícita: las fuentes genéricas de Android pueden resolver de otra forma. Los campos y controles Material 3 están recreados en CSS y conservan posibles diferencias de métricas respecto de Compose. El scrim usa 32% como aproximación de plataforma pendiente de contraste.

En móviles, **TOMAR FOTO** solicita la cámara mediante el selector con `capture`. En escritorio solicita la cámara real con `getUserMedia`; si no está disponible, muestra un error y conserva **AÑADIR FOTOS**. La cámara requiere HTTPS o localhost. Los tutoriales de YouTube requieren internet. La selección de archivos y los diálogos son los del navegador.

Para activar **Evaluar foto con IA**, define `VITE_PACKGUIDE_REVIEW_ENDPOINT` con la URL de un backend que implemente `POST /v1/packing-review` y acepte `{schemaVersion:1,language,photoBase64}`. El backend debe gestionar la credencial de IA; no la incluyas en Vite ni en el navegador. Sin endpoint configurado, la vista previa sigue disponible y muestra que la evaluación no está configurada. Tras una evaluación válida con nota aparecen dos consejos generales distintos de un banco bilingüe de veinte; la misma foto conserva el par durante la sesión y lo traduce al cambiar de idioma. Los consejos no se presentan como análisis de la foto.

La app funciona localmente una vez servidos sus recursos; no se ha añadido un service worker ni se promete una recarga sin conexión al servidor.

## Archivos

- `src/App.tsx`: navegación, estado compartido y ciclo de vida de fotografías.
- `src/Measurements.tsx`, `src/CatalogOverview.tsx`, `src/PackingGuide.tsx`, `src/SummaryPhotos.tsx`, `src/OrganizedPreview.tsx`, `src/TravelMotifs.tsx`, `src/packingTips.ts`, `src/updated.css`: pantallas y componentes del flujo actualizado.
- `src/model.ts`, `src/catalog.json`: inventario y cálculos.
- `src/auth.ts`: autenticación local.
- `src/composition.ts`: lectura de fotos y composición Canvas.
- `src/components.tsx`, `src/styles.css`: componentes y medidas.
- `src/i18n.tsx`, `src/translations.json`, `src/common-terms.json`: diccionario, pares de texto de previews y fallback de términos comunes del anexo.
- `public/packguide-icon.png`, `public/packguide-icon-round.png`: iconos copiados de los recursos de launcher del proyecto Android.
- `scripts/import-reference.mjs`: importación reproducible de catálogo y diccionario desde Kotlin.

Material Design Icons y las fuentes conservan sus licencias en las dependencias; se incluyen copias en `public/licenses/`, que se distribuyen con la compilación.

## Estado de publicación (11 de noviembre de 2026)

La implementación y compilación se entregan en este proyecto local. Sites devolvió `project_not_found` para el identificador existente de `.openai/hosting.json`; no se sustituyó la identidad del sitio ni se creó otra publicación. La evaluación real requiere el backend descrito arriba.
