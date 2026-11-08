# PackGuide — Prompt maestro para reproducir la interfaz actual

> Instantánea de referencia: 8 de noviembre de 2026. Documento obtenido del código local de PackGuide, incluyendo cambios que pudieran no estar confirmados en Git. Lee primero la especificación y consulta después las fuentes completas incluidas en el anexo. El objetivo es reproducir la aplicación existente, con sus estados y medidas, sin reinterpretar su diseño.

## 1. Encargo e instrucciones de ejecución

Actúa como un desarrollador de interfaces especializado en reproducción visual exacta, Kotlin, Jetpack Compose y adaptación de interfaces entre plataformas. Implementa PackGuide en el proyecto de destino hasta dejarla ejecutable y revisable. Trabaja sobre archivos reales; una explicación o maqueta estática no completa este encargo.

La referencia es la aplicación Android cuya implementación completa se adjunta al final de este documento. Conserva su composición, jerarquía, dimensiones, textos, colores, iconos, radios, bordes, espaciados, navegación, estados, reglas de cálculo y comportamiento. No agregues decisiones estéticas propias. No reemplaces el diseño por una plantilla.

VS Code es el editor de destino, pero no define la plataforma de ejecución. Antes de implementar, inspecciona la carpeta abierta y las instrucciones del usuario:

1. Si el destino es el mismo proyecto Android, conserva Kotlin, Compose, Material 3, los recursos y las versiones originales. Cambiar de editor no exige reescribir la aplicación. Es la vía con mayor fidelidad de renderizado.
2. Si el usuario ha elegido web o el destino contiene una aplicación web, adapta el código de referencia al framework ya utilizado. Aplica las equivalencias web de este documento.
3. Si se ha elegido web y la carpeta está vacía, usa React con TypeScript y estilos explícitos, salvo preferencia expresa diferente. Mantén componentes equivalentes a los originales.
4. Si la carpeta está vacía y no se ha indicado plataforma, pregunta una sola vez si se desea Android o web. Mientras tanto puedes preparar tokens, inventario y especificaciones; no elijas silenciosamente una tecnología incompatible con lo solicitado.

No sustituyas un proyecto existente ni borres archivos ajenos para crear una demostración. Inspecciona sus instrucciones y dependencias antes de editar. Implementa por etapas comprobables y continúa hasta completar todas las pantallas y sus interacciones.

### Prioridad de las referencias

Para esta reproducción utiliza, en este orden:

1. Capturas actuales de la app y código de la misma versión, si ambos están disponibles y corresponden al mismo estado, idioma, tema y dispositivo.
2. Las fuentes completas de esta instantánea, incluidas en el anexo.
3. Las especificaciones numéricas y de comportamiento de este documento.
4. Aproximaciones medidas y documentadas únicamente cuando falte una referencia de plataforma.

Si una captura y el código discrepan, comprueba primero versión, estado y configuración; no mezcles diseños de versiones distintas. Si se pide reproducir una captura concreta, esa captura define el estado visual objetivo.

El antiguo `PROMPT.md` del proyecto contiene información histórica. Sus colores y algunas dependencias descritas no reflejan esta instantánea. No uses sus colores como referencia visual. Tampoco conviertas en componentes visibles todas las frases presentes en el diccionario de traducciones: parte de ese diccionario corresponde a interfaces anteriores que ya no se renderizan.

## 2. Qué es PackGuide y cuál es el flujo real

PackGuide organiza equipaje mediante cuatro pantallas: Escáner, Catálogo, Guía y Resumen. Antes de acceder aparece una pantalla de autenticación local con modo de registro. Ajustes existe tanto en autenticación como dentro de la aplicación.

Flujo inicial real:

`Inicio → Iniciar sesión / Crear cuenta → Escáner → Catálogo → Guía → Resumen`

Las cuatro pestañas están disponibles tras autenticarse; los botones de continuación ofrecen un recorrido adicional. Ajustes abre un diálogo y no constituye una quinta pantalla de contenido.

Estado inicial verificable:

- Tema claro e idioma español, salvo ajustes guardados previamente.
- No hay sesión autenticada al crear una nueva actividad. No se recuerda automáticamente una sesión con almacenamiento persistente.
- Tras entrar se abre Escáner.
- Solo CABINA está seleccionada y es la maleta activa.
- CABINA mide 55 × 35 × 25 cm y sus dimensiones están sin confirmar.
- No hay fotografías seleccionadas ni vistas generadas.
- El catálogo tiene 36 artículos. Los seis primeros tienen cantidades iniciales 3, 2, 1, 1, 1 y 1; los demás tienen 0.
- Hay 9 unidades seleccionadas, 0 empacadas y 6 tipos de artículos con cantidad positiva.
- Todas las zonas de los artículos comienzan en BASE.
- Volumen inicial usado: 19.2 L. Capacidad real de cálculo: 48.125 L; presentación: 48.1 L.
- Espacio estimado presentado: 28.9 L. Ocupación truncada: 39%. Progreso de empaque: 0%.
- En el indicador de diez segmentos del catálogo aparecen 3 segmentos llenos, porque se trunca `ocupación × 10`.

No reemplaces estas cantidades por un inventario de ejemplo diferente. No hagas que la aplicación comience en Catálogo ni omitas el acceso para simplificar el desarrollo final.

## 3. Identidad visual actual

Interfaz técnica, cálida y contenida: fondo marfil, superficies casi blancas, texto carbón, metadatos gris oliva, rojo apagado y verde salvia. Combina etiquetas monoespaciadas en mayúsculas con descripciones sans serif. Las tarjetas y botones tienen esquinas redondeadas. La marca visual secundaria es una matriz de puntos de 6 × 6.

No se dibuja un logotipo grande ni una portada promocional. No hay fotografías decorativas, degradados decorativos, ilustraciones de viajes, barras laterales de escritorio, tarjetas de estadísticas inventadas ni encabezados comerciales. Los recursos visuales de contenido son iconos Material Outlined, la matriz dibujada por código, las fotografías elegidas por el usuario y las composiciones locales del inventario.

### 3.1. Colores exactos del tema claro

| Token del código | Hexadecimal | Aplicación |
| --- | --- | --- |
| PrimaryBlack | #191919 | Texto principal y botón principal |
| BackgroundLight | #F3F2EE | Fondo general |
| CardSurfaceLight | #FFFEFA | Superficies y texto invertido de botones |
| AccentRedLed | #D63830 | LED, acentos y errores |
| AccentGreenLed | #54816A | Confirmaciones y estado positivo |
| SecondarySlate | #686862 | Texto secundario |
| BorderSlate | #DEDED7 | Bordes |
| SurfaceVariantLight | #EAE9E3 | Fondos internos, selección y pistas |

El blanco puro #FFFFFF no sustituye globalmente a #FFFEFA. El negro puro #000000 tampoco sustituye a #191919. El rojo de esta interfaz no es #FF2A2A y el verde no es #00E676.

### 3.2. Colores exactos del tema oscuro

| Propiedad semántica | Hexadecimal |
| --- | --- |
| background | #101110 |
| surface | #1B1C1A |
| surfaceVariant | #282925 |
| onBackground / onSurface / primary | #FFFEFA |
| secondary | #B2B1AA |
| outline | #8C8D86 |
| outlineVariant | #393A35 |
| onPrimary / primaryContainer | #191919 |
| tertiary / error | #D63830 |

`PackGuideColors` obtiene sus valores de `MaterialTheme.colorScheme`. Respeta esa relación. Los LED rojo y verde mantienen sus colores constantes al cambiar de tema. La composición bitmap de equipaje tiene su propia paleta fija, descrita más adelante; no la recolorees con los tokens de las tarjetas.

No actives colores dinámicos del dispositivo: el tema actual usa esquemas explícitos. En Android se adapta el color y apariencia de las barras del sistema al tema, según el comportamiento de la plataforma y edge-to-edge.

### 3.3. Tipografía completa

La fuente técnica del código es `FontFamily.Monospace`; la fuente de lectura es `FontFamily.SansSerif`. No hay archivos de fuentes personalizados en los recursos de la aplicación. Su resolución concreta depende del Android de referencia: no afirmes que una fuente web genérica coincide exactamente sin comprobarla.

| Estilo | Familia | Peso | Tamaño sp | Alto de línea sp | Tracking sp |
| --- | --- | --- | --- | --- | --- |
| displayLarge | mono | 700 | 32 | 38 | -0.5 |
| displayMedium | mono | 700 | 26 | 32 | 0 |
| displaySmall | mono | 700 | 24 | 30 | 0 |
| headlineLarge | mono | 700 | 22 | 28 | 0 |
| headlineMedium | mono | 700 | 18 | 24 | 0.25 |
| headlineSmall | mono | 700 | 16 | 22 | 0.25 |
| titleLarge | mono | 600 | 16 | 22 | 0.5 |
| titleMedium | mono | 500 | 14 | 20 | 0.5 |
| titleSmall | mono | 500 | 12 | 18 | 0.5 |
| bodyLarge | sans | 400 | 15 | 22 | 0.25 |
| bodyMedium | sans | 400 | 13 | 18 | 0.25 |
| bodySmall | sans | 400 | 11 | 16 | 0.25 |
| labelLarge | mono | 700 | 13 | 16 | 1 |
| labelMedium | mono | 500 | 11 | 14 | 1 |
| labelSmall | mono | 400 | 10 | 12 | 0.8 |

Hay overrides locales que se deben mantener: navegación a 9 sp con tracking 0; chips y badges a 11 sp; placeholder y entrada de búsqueda con estilo bodyMedium pero familia mono. Algunos `Text`, `TextButton`, `IconButton`, `Checkbox`, `FilterChip` y campos usan valores heredados de Material 3, por lo que debes consultar el código y el componente real en vez de aplicar labelSmall a todo por uniformidad.

## 4. Geometría general, unidades e insets

Todas las medidas de layout siguientes están en dp; las de texto, en sp. En Android conserva esas unidades. Un dp equivale a `density` píxeles físicos; no copies un screenshot de 1080 píxeles como si midiera 1080 dp. Las métricas sp deben observar además la escala de fuente del dispositivo.

Las pantallas principales ocupan el espacio del `Scaffold`, respetan `innerPadding` y se sitúan encima de la navegación inferior. Los encabezados quedan fuera de las listas desplazables. En Catálogo, búsqueda, filtros y tarjeta inferior permanecen fuera del scroll del inventario. Escáner, Guía y Resumen desplazan sus listas de contenido.

| Elemento | Medida |
| --- | --- |
| Margen horizontal de contenido principal | 20 |
| Padding vertical del HudHeader | 18 |
| Padding horizontal del HudHeader normal | 20 |
| Separación entre título y matriz | 12 |
| TechCard: radio / borde / padding | 18 / 1 / 16 |
| TechCard: espacio tras fila de etiqueta | 14 |
| AsciiButton: radio / altura mínima | 24 / 48 |
| AsciiButton: padding horizontal / vertical | 20 / 14 |
| Campo de búsqueda: radio / borde | 16 / 1 |
| Campo de búsqueda: padding horizontal / vertical | 12 / 10 |
| Tarjeta inferior de capacidad: radio / padding | 20 / 14 |
| Diálogo de ajustes: radio / padding | 22 / 20 |
| Barra inferior: radios superiores | 24 |

No impongas altura fija a tarjetas con textos variables. El texto largo debe envolver según la anchura y tipografía reales. La altura mínima de un botón no significa que todos los botones tengan exactamente 48 dp: el contenido y su padding pueden hacerlos más altos.

## 5. Componentes compartidos: contrato exacto

### 5.1. HudHeader

Contenedor de ancho completo con padding horizontal 20 y vertical 18. El título utiliza headlineMedium y ocupa el espacio restante. A la derecha aparece `DotMatrixMark` de 32 × 32.

Si `sysCode` o `statusText` no están vacíos se agrega una fila superior: código a la izquierda, estado LED a la derecha cuando corresponda, y 10 dp de separación antes del título. Valores predeterminados: `SYS // PACKING_OS` y `[ ONLINE ]`.

Detalle esencial: Escáner llama al encabezado con `statusText = ""` pero conserva el `sysCode` predeterminado, por lo que muestra `SYS // PACKING_OS`. Catálogo, Guía, Resumen y autenticación pasan vacíos ambos textos y no muestran esa fila. No agregues un estado ONLINE a todas las pantallas.

### 5.2. DotMatrixMark

Canvas de 32 × 32, con 36 círculos distribuidos en una matriz 6 × 6. Paso = ancho / 6. Centro de cada círculo = `((columna + 0.5) × paso, (fila + 0.5) × paso)`. Radio = paso × 0.16. Color normal = texto principal con alfa 0.18. Solo los tres puntos inferiores de la última columna son rojos: columna 5, filas 3, 4 y 5. No escribas letras, símbolos de terminal ni un icono de cuadrícula para reemplazarla.

### 5.3. TechCard

Ancho completo, superficie del tema, radio 18, recorte de contenido al radio, borde 1, padding 16 y sin elevación decorativa. Borde predeterminado = border. Si se solicita exactamente primaryText, el componente reduce ese color a alfa 0.22; no dibujes un contorno carbón sólido en esas tarjetas.

Cuando existe headerTag o subTag, dibuja una fila con indicador rojo vertical de 3 × 10, radio 2, separación horizontal 8, etiqueta labelSmall secundaria a la izquierda y subetiqueta opcional a la derecha. Espacio posterior = 14. Si no existe ninguna etiqueta, omite toda esa fila y su espacio.

### 5.4. AsciiButton

Contenedor redondeado radio 24, altura mínima 48, padding horizontal 20 y vertical 14. Fila centrada, separación interna 8; icono opcional de 18; texto labelLarge. El texto es exactamente el recibido: algunos botones incluyen corchetes, otros no. No agregues corchetes automáticamente.

| Estado | Fondo | Texto |
| --- | --- | --- |
| Principal habilitado | primaryText | surface |
| Secundario habilitado | surface | primaryText |
| Acento rojo habilitado | AccentRedLed | blanco puro |
| Deshabilitado | surfaceVariant | secondaryText |

Borde 1: mismo color del fondo para principal habilitado; border en los demás casos según la lógica original. Al presionar, escala ambos ejes a 0.985 con tween de 120 ms; al soltar vuelve a 1. No hay ripple en este componente (`indication = null`). No uses sombra o desplazamiento vertical para el estado pulsado.

### 5.5. LED y badges

LED: el tamaño lógico predeterminado es 8; su Canvas ocupa tamaño × 1.8, el núcleo tiene radio tamaño / 2 y el halo radio × 1.4 del núcleo, alfa 0.12. No confundas el diámetro del núcleo con el espacio reservado. Si hay etiqueta, separación 4, labelSmall secundaria. Solo si `isPulsing` es true anima alfa 0.4→1, tween 1400 ms FastOutSlowIn, repetición inversa; el valor predeterminado es false.

TechBadge: radio 8, borde 1 transparente por defecto, padding horizontal 8 y vertical 4, texto labelSmall con tamaño 11. Fondo predeterminado primaryText, texto surface. LED opcional de tamaño lógico 6 y separación 6. Los badges de categoría sobrescriben fondo por surfaceVariant y texto por secondaryText.

DashedDivider existe como componente: grosor 1, trazo 1, hueco 5, color border. No lo agregues a pantallas que solo lo importan y no lo utilizan.

## 6. Navegación inferior

Superficie de ancho completo, color surface, esquinas superiores de radio 24 y elevación de sombra 2. Incluye el inset inferior de navegación del sistema. Fila con padding horizontal 8, vertical 8 y separación de 2.

Cinco columnas del mismo peso, en este orden:

| Elemento | Ruta | Icono exacto |
| --- | --- | --- |
| ESCÁNER | scanner | Outlined.QrCodeScanner |
| CATÁLOGO | catalog | Outlined.Inventory2 |
| GUÍA | guide | Outlined.Checklist |
| RESUMEN | summary | Outlined.Analytics |
| AJUSTES | diálogo | Outlined.Settings |

Cada columna: radio 18, padding horizontal 2 y vertical 9, contenido centrado, espacio vertical 4, icono 21, texto mono de 9 sp y tracking 0, una sola línea con ellipsis si hace falta. La columna seleccionada lleva fondo surfaceVariant, texto e icono primaryText y punto rojo de 3 dp bajo la etiqueta. Las no seleccionadas usan secondaryText y un punto transparente que conserva la geometría. Ajustes reserva un Spacer de 3 en esa posición y no se vuelve pestaña seleccionada.

Las etiquetas visibles se obtienen quitando los corchetes de los títulos de NavTab. Anima el tint de selección durante 180 ms. La transición entre pantallas es `fadeIn() togetherWith fadeOut()` de Compose; no inventes desplazamientos laterales ni duración personalizada si no se mide o resuelve desde la implementación original.

## 7. Autenticación: inicio de sesión y registro

Referencia completa: AuthScreen.kt. Esta pantalla no muestra navegación inferior.

Contenedor de pantalla: fillMaxSize, fondo background, safeDrawingPadding, imePadding, scroll vertical y padding horizontal 24 / vertical 28. Distribución vertical centrada cuando el espacio lo permite y accesible por scroll con teclado o contenido extenso.

Orden visual exacto:

1. Fila con LED rojo sin pulso, espacio 10, `PACKGUIDE // ACCESS` en labelMedium secondaryText, espacio flexible y botón de ajustes con icono Outlined.Settings.
2. Espacio 24.
3. HudHeader con `INICIAR SESIÓN` o `CREAR CUENTA`, sysCode y statusText vacíos, padding horizontal 0; mantiene su propio padding vertical 18 y la matriz de puntos.
4. Dos espacios consecutivos de 20 y 22: total 42. Conserva ese hueco del código actual.
5. Tarjeta de credenciales, superficie surface, radio 14, borde 1 y padding 20.
6. Separación 18 después de la tarjeta.
7. Fila con LED verde sin pulso, espacio 8 y `CUENTA ALMACENADA EN ESTE DISPOSITIVO`, labelSmall secundaria.

Interior de credenciales:

- `[ CREDENCIALES ]`, labelMedium principal; espacio 18.
- Campo USUARIO; espacio 12.
- Campo CONTRASEÑA.
- En registro: espacio 12 y campo CONFIRMAR CONTRASEÑA.
- Si hay error: espacio 12 y mensaje bodyMedium rojo, región de anuncio accesible polite.
- Espacio 20 y botón principal de ancho completo.
- Durante carga: espacio 12, indicador circular de 16 con grosor 2 rojo, espacio horizontal 10 y `VERIFICANDO CREDENCIALES...`, labelSmall secundaria.
- Espacio 10 y botón secundario de ancho completo para alternar modo.

Campos: OutlinedTextField Material 3, singleLine, ancho completo, radio 14, etiqueta en familia mono. Borde enfocado primaryText; sin enfoque border; contenedor sin enfoque surfaceVariant a alfa 0.35; etiqueta enfocada primaryText; cursor rojo. Las demás propiedades conservan los valores de Material 3, no un input web genérico. La etiqueta flotante, el notch del borde y sus posiciones forman parte de la reproducción.

Los campos secretos tienen control de texto `VER` / `OCULTAR`, labelSmall, a la derecha. No se usa un icono de ojo. En registro aparecen las ayudas exactas `Recomendado: letras, números, punto, guion o guion bajo.` y `1 a 45 caracteres: solo letras y números.`; en login no aparecen esas ayudas.

Textos de botones:

| Modo / estado | Botón principal | Botón secundario |
| --- | --- | --- |
| Inicio de sesión | [ ENTRAR ] | [ CREAR CUENTA ] |
| Registro | [ REGISTRAR ] | [ YA TENGO CUENTA ] |
| Validando | [ VALIDANDO... ] | Deshabilitado |
| Creando cuenta | [ CREANDO CUENTA... ] | Deshabilitado |

Usuario: trim para validar, longitud entre 3 y 24. Los caracteres sugeridos son una recomendación, no una restricción adicional implementada. Registro: contraseña no vacía, máximo 45, únicamente letras o números según `Char.isLetterOrDigit`, y confirmación coincidente. No impongas un mínimo de ocho caracteres: ese texto histórico está en traducciones pero no es la regla vigente. Al escribir se limpia el error. Al alternar modo se limpia confirmación, su visibilidad y error; se conserva usuario y contraseña.

La cuenta existe localmente con Room; contraseña derivada mediante el algoritmo del AuthRepository adjunto. La réplica debe conservar la interacción de registro/login y el manejo de errores, sin convertir el botón ENTRAR en una omisión incondicional del acceso. Si el destino web requiere un almacén diferente, implementa un equivalente local explícito y no lo describas como una cuenta sincronizada con Android. No copies contraseñas reales ni bases privadas.

## 8. Pantalla Escáner

Título `EQUIPAJE DEL VIAJE`, encabezado con fila `SYS // PACKING_OS` y sin estado ONLINE. Fondo background. Lista con márgenes horizontales 20 y separación vertical 14.

Primer bloque: `EQUIPAJE DEL VIAJE` en titleMedium, explicación bodySmall secundaria `Selecciona todas las maletas que llevarás. Debe quedar al menos una.`, y fila de tres opciones del mismo peso separadas por 4: MANO, CABINA y BODEGA. Cada opción tiene padding vertical 8, checkbox Material y texto labelMedium. Debe quedar al menos una seleccionada.

Por cada maleta seleccionada se dibuja una TechCard con encabezado `MALETA: {etiqueta}`. Dentro, una columna separada por 12 contiene:

1. Otra TechCard anidada, `EQUIPAJE`, con borde primaryText resuelto a alfa 0.22: es PhotoScanPanel. Conserva las dos capas de padding y borde; no las aplanes.
2. `MEDIDAS INTERIORES ÚTILES`, labelMedium.
3. Stepper de ALTO, ANCHO y PROF., en ese orden.
4. `CAPACIDAD INTERIOR TOTAL: {litros} L`, titleMedium.
5. Descripción `Una foto común no mide la maleta. Ajusta las medidas interiores útiles y confírmalas.`, bodySmall secundaria.
6. Fila con checkbox y `CONFIRMO LAS MEDIDAS: {etiqueta}`, labelSmall.

Stepper: fila de ancho completo, etiqueta con peso flexible titleMedium, botón IconButton con carácter `−`, valor `{entero} CM` en titleMedium y botón `+`. Rango de dimensiones 1 a 300, paso 1. Cualquier cambio desmarca la confirmación de esa maleta. Conserva las áreas táctiles Material del original.

PhotoScanPanel: texto `Toma o elige una foto de tu maleta.`, bodyMedium; foto opcional con ajuste Fit, ancho completo, altura limitada entre 140 y 260, radio 12 y fondo surfaceVariant; botón principal TOMAR FOTO con PhotoCamera; botón secundario ELEGIR FOTO con AddPhotoAlternate; indicador de carga condicional; mensaje opcional bodySmall secundario; descripción de referencia y cálculo que figura en el código. Separación entre hijos 12.

TOMAR FOTO invoca cámara del sistema y recibe una vista previa. ELEGIR FOTO abre selección de imagen. No hay un visor de cámara en vivo, marco de enfoque animado, medición LiDAR, FPS ni reconocimiento de objetos en esta pantalla actual.

Al final de la lista: texto sobre repartir artículos en Catálogo; espacio 12; botón CONTINUAR AL CATÁLOGO de ancho completo, habilitado solo si todas las maletas seleccionadas tienen medidas confirmadas; espacio final 20.

| Maleta | Alto | Ancho | Fondo | Capacidad sin redondear |
| --- | --- | --- | --- | --- |
| MANO | 45 | 35 | 20 | 31.5 L |
| CABINA | 55 | 35 | 25 | 48.125 L |
| BODEGA | 75 | 50 | 30 | 112.5 L |

Añadir una maleta no la activa automáticamente y comienza con todas sus cantidades en cero. El orden de presentación sigue MANO, CABINA, BODEGA entre las seleccionadas. Quitar una con artículos solicita confirmación con AlertDialog: título `QUITAR MALETA: {etiqueta}`, explicación literal del código, confirmar QUITAR MALETA, cancelar CANCELAR. Elimina sus asignaciones y su fotografía; no transfiere artículos a otra. Si era activa se activa la primera restante.

## 9. Pantalla Catálogo

Título `CATÁLOGO DE ÍTEMS`, sin fila SYS ni ONLINE. Debajo, contenedor con márgenes horizontales 20 y altura restante.

### 9.1. Búsqueda y categorías

Buscador surface, radio 16, borde 1 border, padding 12 horizontal / 10 vertical. Icono Outlined.Search 18, espacio 8 y entrada mono bodyMedium. Placeholder exacto `BUSCAR ÍTEM... [ _ ]`. Una línea. Sin botón de limpiar agregado.

Espacio 10; fila horizontal desplazable de categorías con separación 8: `[TODOS]`, `[ROPA]`, `[CALZADO]`, `[ASEO]`, `[TECH]`. Radio 16, borde 1, padding 12 horizontal / 7 vertical. Texto 11 con tracking heredado labelSmall. Activa: fondo primaryText, texto surface en negrita, LED rojo tamaño lógico 5 y espacio 6 antes del texto. Inactivas: fondo surface, borde border, texto primaryText normal. Espacio 10 después.

Búsqueda por nombre o categoría, sin distinguir mayúsculas; intersección con filtro de categoría. No elimines el filtro cuando se escribe. `TODOS` está activo inicialmente. Resultado vacío: `No hay artículos para esta búsqueda.`, bodyMedium secondaryText, padding vertical 24.

### 9.2. Región desplazable de inventario

Lista con peso 1 y separación 10. Su primer elemento contiene LuggageSelector y la explicación DESTINOS DEL VIAJE, resumen de cada maleta, etiqueta de volumen y advertencia sobre referencias del catálogo. Estos textos son contenido visible y afectan la posición de la primera tarjeta: no los omitas para colocar artículos más arriba.

LuggageSelector muestra `MALETA A ORGANIZAR`, labelMedium secundaria, y una fila de FilterChip Material 3 separados por 8 para las maletas seleccionadas. No son los chips personalizados de categorías; conserva esa diferencia.

Cada uno de los 36 artículos usa CatalogInventoryCard:

1. TechCard sin fila de headerTag.
2. Fila centrada con separación 12: caja de icono 44 × 44, fondo surfaceVariant, radio 14 e icono Material de 22; columna flexible con nombre titleMedium y texto `{dimensiones} · ESTIMACIÓN POR UNIDAD: {litros} L`, bodySmall. Separación interna de columna 4.
3. Texto de volumen rectangular de referencia, bodySmall, inmediatamente después de la fila.
4. Espacio 12; badge de categoría; espacio 12.
5. `DESTINO DEL ARTÍCULO`, labelSmall secundaria; espacio 12.
6. Una fila de asignación por maleta seleccionada, fondo surfaceVariant, radio 16 y padding 12. Columna izquierda flexible con etiqueta labelMedium y litros titleLarge. A la derecha: botón menos 48 × 48, cantidad centrada con ancho mínimo 24 y botón más 48 × 48, separación 8. Cada botón tiene radio 12, fondo surface, borde 1 border e icono de 18.
7. Espacio 12 después de cada fila; luego espacio adicional 8 y total combinado de todas las maletas en bodySmall secundaria.

Menos deshabilitado en cero y con icono secondaryText a alfa 0.45. Más incrementa en uno. Reducir la cantidad limita packedQuantity al nuevo total. Cambiar cantidades en una maleta no altera las de otra. Los totales de la tarjeta combinan maletas; el tracker inferior se refiere a la activa.

### 9.3. Tarjeta inferior de capacidad

Permanece fuera de la lista, sobre la barra de navegación. Padding externo vertical 8. Radio 20, borde 1, superficie surface, padding interno 14 y elevación explícita 0. Columna con separación 10.

Orden: maleta seleccionada labelMedium; conteos labelMedium; columna separada por 6 con descripción bodySmall, volumen titleMedium en negrita y badge de balance; barra de diez segmentos; botón CREAR PLAN DE EMPACADO con icono ArrowForward.

Segmentos: diez cajas del mismo peso, separación 4, altura 5, radio 1, borde 1. Llenas primaryText; vacías surfaceVariant con borde border. Cantidad llena = truncar(ocupación × 10), limitada entre 0 y 10. Badge oscuro con LED verde si cabe y rojo si excede. El botón navega a Guía; no inventes una operación remota, spinner o animación de optimización para este paso.

## 10. Pantalla Guía

Título `GUÍA DE EMPACADO`, sin fila técnica superior. Lista con margen horizontal 20 y separación 12. Orden exacto:

1. LuggageSelector.
2. PackingSuggestionPanel, TechCard `SUGERENCIA ORIENTATIVA`.
3. TechCard `PROGRESO REAL`, con borde solicitado primaryText y resuelto a alfa 0.22.
4. PackingGuidePreviewPanel: `VISTA ORGANIZADA DE EMPAQUE`.
5. TechCard `CONSEJO DE DISTRIBUCIÓN`.
6. Título `ASIGNACIÓN EN VALIJA` titleLarge y explicación bodySmall.
7. Tarjetas de los artículos con cantidad positiva de la maleta activa, o estado vacío.
8. Botón VER RESUMEN y espacio final 16.

Sugerencia: columna separada por 10; descripción bodySmall, volumen displaySmall (rojo solo si excede), advertencia cuando no están confirmadas las dimensiones, recomendación calculada y TextButton `VER CRITERIOS Y CONSEJOS`. Al expandirse cambia a `OCULTAR DETALLES` y revela con AnimatedVisibility los cuatro párrafos exactos de la implementación. No muestres los detalles inicialmente.

Progreso: texto `{empacadas} de {seleccionadas} unidades empacadas · {porcentaje}%`, titleMedium; espacio 8; LinearProgressIndicator altura 4, primaryText, pista surfaceVariant y sin indicador de parada; espacio 12; instrucciones bodySmall. El porcentaje se calcula de unidades, no de número de tipos.

Consejo de distribución: `Coloca el calzado y los objetos pesados en la base; la ropa en el centro y lo que necesitarás durante el viaje arriba.`, bodyMedium.

Tarjeta de artículo: icono y nombre titleMedium a la izquierda, `{packedQuantity}/{quantity}` a la derecha; espacio 10; fila de dos AsciiButton con igual peso y separación 8. Primer botón secundario `ZONA: {zona} ↻`; segundo `MARCAR EMPACADO` principal o `DESMARCAR` secundario cuando se completó. El primero cicla BASE → CENTRO → ACCESO RÁPIDO → BASE. El segundo incrementa una unidad; si ya están todas, vuelve a cero. No es un booleano que marque todas las unidades desde la primera pulsación.

Si no hay artículos: `Aún no hay artículos seleccionados. Añádelos en el catálogo.` y botón IR AL CATÁLOGO. Mantén los demás paneles de la pantalla según el código.

## 11. Fotografías y vista organizada de Guía

Panel con separación 12 entre hijos. Texto sobre funcionamiento sin conexión y composición de foto original más inventario. Sin foto: mensaje y botón AÑADIR FOTO EN ESCÁNER. Con foto: etiqueta FOTO DE REFERENCIA, imagen Fit de ancho completo con altura entre 100 y 180 y TextButton CAMBIAR FOTO.

Sin artículos: mensaje de selección y botón IR AL CATÁLOGO. Botón GENERAR IMAGEN ORGANIZADA, de ancho completo, habilitado solo con foto, sin carga de foto, inventario no vacío y sin trabajo de generación en curso. Durante trabajo muestra indicador, texto `Creando vista organizada…` o `Abriendo fotos…` según el trabajo, y CANCELAR secundario.

La generación usa OfflinePackingImageService, no IA remota. Copia el resultado de Canvas o su equivalente fiel. La foto permanece sin modificar dentro de una composición con datos y un esquema. Si cambia el plan o la foto, el resultado anterior no se debe seguir mostrando como vigente; aparece el aviso del código.

Imagen generada visible: ajuste Fit, ancho completo y altura limitada entre 160 y 340; pulsar la imagen o `Ampliar vista organizada` abre diálogo con ancho de plataforma desactivado, superficie de ancho completo, 95% de altura y padding 12, imagen flexible y botón CERRAR con altura mínima 48.

### 11.1. Especificación del bitmap generado

La composición es una excepción deliberada a la paleta del resto de la UI. Tiene anchura de 1200 píxeles y altura variable. Usa fondo #F2F4F7, tinta #0F0F11, texto secundario #5E646E, rojo #D22020, tarjetas blancas, bordes #D1D5DB y cajas de artículos #F5F7F9. No uses los marfiles de las tarjetas para corregir estos colores históricos del generador: forman parte de su salida actual.

La fuente del bitmap se crea como `sans-serif` normal. Título en (48,65) a 36 px; subtítulo en (48,105) a 26. Caja de foto (48,140)-(600,470), foto centrada en (324,305), ajuste dentro de 528 × 306 sin recorte. Etiqueta de foto en (48,505), 24. Inventario empieza en x=640: título y=180; unidades y=230; volumen y=285; descripción y=325; balance y=380; dimensiones y=430.

Asa de maleta: rectángulo (480,535)-(720,575). Cuerpo: (48,565)-(1152,altura-100), radio 28. Zonas desde y=610, en orden ACCESO RÁPIDO, CENTRO, BASE. Artículos en dos columnas x=80 y x=610; cajas de 510 × 98, paso vertical 112; ilustraciones lineales por categoría y textos con truncamiento según ancho medido. El pie se dibuja en (48,altura-50), 26 px. Grosor de borde de cajas 2; ilustraciones 3.

Altura = `750 + suma por zona(56 + (62 si está vacía; si no, ceil(número de tipos / 2) × 112 + 8))`. Límite de altura 4096 y máximo 64 tipos por plan. El Canvas original completo está incluido en el anexo y prevalece para las coordenadas restantes.

## 12. Pantalla Resumen

Título `RESUMEN DEL EQUIPAJE`, sin fila técnica superior. Lista con margen horizontal 20, separación 12. Orden real:

1. TechCard TODAS LAS MALETAS: por maleta, nombre, volumen, empacados/seleccionados en bodyMedium; rojo si excede; separación 6; total global labelMedium.
2. LuggageSelector.
3. PackingReviewPreviewPanel, REFERENCIA PARA REVISAR TU MALETA.
4. TechCard MALETA SELECCIONADA.
5. TechCard CAPACIDAD ESTIMADA.
6. TechCard ESTADO DE EMPACADO.
7. Título DETALLE POR ARTÍCULO, titleLarge.
8. TechCard DISTRIBUCIÓN POR ZONA.
9. Tarjetas de artículos con cantidad positiva o mensaje `No hay artículos seleccionados.`
10. EDITAR CATÁLOGO secundario con Inventory2; espacio 8; CONTINUAR EMPACANDO principal; espacio final 16.

Aunque el título DETALLE POR ARTÍCULO aparezca antes de DISTRIBUCIÓN POR ZONA, conserva ese orden del código.

MALETA SELECCIONADA: borde primaryText a alfa 0.22, icono Outlined.Luggage rojo, etiqueta y medidas `{alto}×{ancho}×{fondo} cm` titleMedium; espacio 8; aviso sobre medidas interiores, exteriores y normas de aerolínea bodySmall secundario.

CAPACIDAD ESTIMADA: descripción bodySmall, volumen headlineMedium, ocupación bodySmall; espacio 6; barra de 4 dp con valor visual limitado entre 0 y 1, color rojo si excede o primaryText si no; espacio 10; balance. El porcentaje numérico puede exceder 100%; la barra no.

ESTADO DE EMPACADO: fila CheckCircle verde y empacados; espacio 6; RadioButtonUnchecked secundario y pendientes; espacio 6; número de tipos y unidades bodySmall.

DISTRIBUCIÓN POR ZONA usa orden BASE, CENTRO, ACCESO RÁPIDO, con unidades y litros sumados de los artículos seleccionados de la maleta activa, bodyMedium y separación 4. Este orden es diferente al dibujo del bitmap y debe mantenerse.

Detalle por artículo: icono, nombre titleMedium y contador empacados/seleccionados; espacio 4; `{zona} · {litros} L` bodySmall secundaria. No agregues controles de edición en estas tarjetas.

### 12.1. Panel de referencia para revisión

Explica que admite hasta cuatro fotos y usa la primera como referencia, con procesamiento local y sin calificación automática. Fila horizontal desplazable separada por 10; cada miniatura ocupa columna de ancho 180, imagen Fit de altura 150 y TextButton QUITAR FOTO. No conviertas las miniaturas en una cuadrícula.

Botones en columna: AÑADIR FOTOS secundario, TOMAR FOTO secundario y GENERAR REFERENCIA VISUAL principal. Los dos primeros se deshabilitan con cuatro fotos o durante trabajo. Generar requiere alguna foto, contenido seleccionado y ningún trabajo activo. Estado de carga, cancelación, errores y ampliación funcionan como en Guía. Si cambia el plan, muestra el aviso de regenerar.

## 13. Diálogo Ajustes

Referencia SettingsDialog.kt. Dialog de plataforma, superficie de ancho disponible dentro del diálogo, radio 22, borde 1 border, padding 20 y separación vertical 10. Conserva el scrim y comportamiento de cierre del Dialog original; si se adapta a web, mide la referencia en vez de inventar su opacidad.

Orden: `[ AJUSTES DE SISTEMA ]` titleMedium; `PERSONALIZA PACKGUIDE` labelSmall secundaria; Spacer 4; fila TEMA; fila IDIOMA; fila CERRAR SESIÓN solo después de autenticarse; TextButton `[ CERRAR ]` alineado al final.

Cada fila tiene fondo background, radio 2, borde 1 border, padding horizontal 12 y vertical 14, icono a la izquierda, espacio 12, columna flexible con título labelMedium y valor labelSmall separado 3, y carácter `›` a la derecha titleMedium. Toda la fila se puede pulsar. No dibujes switches o selectores desplegables: cada pulsación alterna el valor.

TEMA usa LightMode con CLARO o DarkMode con OSCURO; IDIOMA usa Language con ESPAÑOL o ENGLISH; cierre usa Logout, título rojo y valor SALIR DE LA CUENTA secundario. Cerrar sesión limpia fotos y vistas, vuelve al acceso y permite crear una nueva sesión de contenido. Tema e idioma sí se guardan en DataStore.

## 14. Datos de catálogo, cálculos y localización

Reproduce los 36 elementos en su orden exacto, con IDs, nombres, categorías, iconos, dimensiones y litros por unidad de `defaultCatalogItems()` en PackingState.kt. El anexo incluye los datos completos para evitar transcripción aproximada.

Capacidad = alto × ancho × profundidad / 1000, usando las medidas interiores, sin reserva adicional. Volumen utilizado = suma de cantidad × litros por unidad. Los litros por unidad son supuestos independientes; no se recalculan a partir de la caja rectangular de referencia de cada objeto. En pantalla se muestran ambas métricas, con etiquetas diferentes.

Nunca apliques el descuento del 20% que aparece en algunas frases históricas del diccionario. `PackingRules` usa suma decimal para evitar errores de exceso en ajustes exactos. En una adaptación a JavaScript utiliza aritmética decimal o unidades enteras adecuadas a la precisión de los datos; no permitas que errores binarios activen una alarma de exceso cuando el volumen coincide exactamente.

Formato de litros: una cifra decimal con punto, usando Locale.US; entre cero exclusivo y 0.05 exclusivo presenta `<0.1`. No uses coma decimal por el idioma español. Redondea para presentar, sin redondear los valores internos antes de comparar. No sustituyas 48.125 por 48.1 dentro del modelo.

Exceso cuando usado > capacidad, estrictamente. Espacio libre = max(capacidad-usado,0). Exceso = max(usado-capacidad,0). Ocupación = usado/capacidad. Porcentaje de ocupación = truncar(ocupación × 100). Progreso = división entera de empacadas × 100 / seleccionadas; cero cuando no hay unidades. No muestres NaN, Infinity ni porcentajes de ejemplo.

Español inicial e inglés alternable. Utiliza el diccionario y las funciones reales de AppText.kt y PackingText.kt. `localizedText` aplica traducción exacta y fallback de términos comunes; `previewText` tiene sus propias parejas de textos. Respeta espacios, acentos, mayúsculas, corchetes, `×`, `·`, `↻`, `−`, `›` y los distintos usos de `...` y `…`.

No reconstruyas pantallas antiguas a partir de claves como `LIDAR ACTIVE`, `PESO TOTAL: 9.8 KG`, `EFICIENCIA DE VOLUMEN: 92%`, `IATA VALID`, pasos de doblado o exportaciones. Su presencia en traducciones no demuestra que sean contenido actual.

## 15. Adaptación específica si el destino es web

Esta sección solo se aplica si se ha elegido web. El objetivo visual sigue siendo la app móvil. Mantén la composición vertical y la barra inferior también en una ventana grande; no conviertas el producto en un dashboard de escritorio por iniciativa propia.

1. Establece un viewport de comparación equivalente al ancho y alto útiles en dp del dispositivo Android de referencia. Usa 1 dp → 1 CSS px solo como escala lógica inicial, con zoom 100% y escala de fuente equivalente, y calibra contra capturas. No equipares píxeles físicos con CSS px.
2. Al comparar imágenes, normaliza la densidad y recorta las regiones de sistema que no pertenezcan a la aplicación. No dibujes una hora, señal, batería, barra de gestos o marco de teléfono falsos dentro del producto para simular Android.
3. Usa `box-sizing: border-box`, elimina márgenes nativos de página y define explícitamente tipografía y bordes de controles. No permitas que los estilos predeterminados del navegador decidan los botones, inputs o checkboxes.
4. Modela el shell como filas de contenido y navegación; usa `min-height: 0` en los hijos flex/grid que deban desplazarse. El catálogo debe conservar búsqueda y tracker fuera de la lista. Evita dos scrolls verticales de página y contenido a la vez.
5. Maneja la altura visible y los safe areas reales del navegador. El teclado no debe tapar el acceso ni sus errores. No dupliques un inset reservado ya por otro contenedor.
6. Proporciona las fuentes correctas si están disponibles y comprueba su licencia. Monospace o sans-serif del sistema de Windows no garantizan la misma anchura que Android. Ajusta familia y métricas con referencia, sin deformar todo el layout con un transform global.
7. Usa los vectores de Material Outlined correspondientes. No sustituyas automáticamente por Lucide, emojis, iconos filled o caracteres Unicode parecidos. Si extraes vectores de la dependencia original, respeta su licencia y viewBox.
8. Recrea la matriz con SVG o Canvas y las coordenadas descritas. El LED debe reservar también su halo; no basta un círculo CSS de 8 píxeles.
9. Reproduce los campos outlined de etiqueta flotante, radios y estados. Reproduce FilterChip y Checkbox Material de la referencia. No supongas que instalar una biblioteca Material web produce el mismo tamaño que Compose.
10. Imágenes con `object-fit: contain`; no recortes la maleta con cover. La composición organizada debe conservar sus coordenadas internas de 1200 píxeles y escalarse como una sola imagen al mostrarse.
11. Para tomar/elegir imágenes adapta los mecanismos del navegador. Si no hay cámara disponible, conserva un error explícito y la alternativa de elegir archivo. Nunca muestres una cámara simulada como captura real.
12. Portar comportamiento a web puede requerir equivalentes de Room, DataStore y Bitmap; mantén los límites de persistencia visibles y el funcionamiento local. No añadas autenticación remota, API de IA o claves externas para una réplica visual.
13. No uses longitudes fijas para el contenido completo ni posiciones absolutas para cada tarjeta. Reserva coordenadas absolutas para los dibujos que ya las usan: matriz, LEDs y composición bitmap.
14. Para un monitor ancho sin referencia aprobada, permite una región móvil de revisión con ancho igual al viewport objetivo, fondo exterior discreto y sin controles nuevos. Esa envoltura no debe incluirse al comparar el recorte de la app. No inventes un ancho de dispositivo y luego lo declares una medida extraída de Android.

Los renderizadores Android y navegador pueden diferir en hinting de fuente, antialiasing y controles del sistema. Trabaja para eliminar discrepancias medibles de la UI y registra las diferencias de plataforma que queden. No prometas igualdad literal de todos los píxeles únicamente por haber copiado estos valores.

## 16. Procedimiento de implementación y validación visual

### 16.1. Antes de construir

Lee este documento, el anexo y el proyecto destino. Enumera sus pantallas y componentes reales. Determina plataforma, viewport de referencia, densidad, fontScale, tema, idioma y versión. Separa hechos medidos de supuestos. Si faltan capturas, comienza con las medidas del código; el objetivo final de pixel perfect requerirá comparación visual contra un render de referencia.

No dependas de que el usuario vuelva a copiar todo el código fuente en el chat: este archivo ya contiene la instantánea completa. Si el contexto es limitado, lee el documento por secciones y consulta los bloques de código por la ruta del archivo.

### 16.2. Orden recomendado

1. Tokens y tipografía.
2. Matriz, LED, AsciiButton, TechCard, TechBadge y HudHeader.
3. Autenticación y SettingsDialog.
4. Shell y navegación inferior.
5. Modelo completo de maletas e inventario.
6. Escáner, fotos y confirmación de medidas.
7. Catálogo, filtros, cantidades y tracker.
8. Guía, zonas, progreso y composición local.
9. Resumen, revisión y ampliación.
10. Tema oscuro e inglés en todos los estados anteriores.
11. Comparación visual e iteraciones de ajuste.

### 16.3. Matriz mínima de comprobación

| Caso | Comportamiento o evidencia requerida |
| --- | --- |
| Login vacío | Posiciones y botones iniciales; sin barra inferior |
| Registro | Tres campos, ayudas, cambio de altura y scroll |
| Error de acceso | Mensaje correcto, color rojo y desplazamiento natural |
| Contraseña visible | VER / OCULTAR sin cambios de geometría no previstos |
| Ajustes en login | Tema e idioma; sin cierre de sesión |
| Ajustes tras acceso | Incluye CERRAR SESIÓN |
| Escáner inicial | CABINA, 55×35×25, sin foto, continuar deshabilitado |
| Medidas confirmadas | Continuar habilitado al confirmar todas las maletas |
| Cambio posterior de medida | Desmarca confirmación y actualiza capacidad |
| Múltiples maletas | Datos independientes y orden estable |
| Quitar maleta con contenido | Diálogo, cancelar y confirmar correctos |
| Catálogo inicial | 36 tipos, cantidades originales y tracker 19.2/48.1 |
| Búsqueda / filtro | Intersección correcta y estado sin coincidencias |
| Cantidad cero | Menos deshabilitado, no negativos |
| Empaque parcial | Contador y porcentaje derivados de unidades |
| Empaque completo | DESMARCAR y vuelta a cero al pulsar |
| Cambio de zona | Ciclo de tres zonas y actualización del resumen |
| Capacidad exacta | No aparece exceso por un error decimal |
| Exceso | Texto rojo, badge y barra correctos sin recortar el dato numérico |
| Guía sin foto / sin artículos | Acciones y botones deshabilitados correctos |
| Vista organizada | Foto intacta, datos, tres zonas y ampliación |
| Plan cambiado | Aviso de regeneración y resultado vigente correcto |
| Revisión con 4 fotos | No permite una quinta; puede quitar y reemplazar |
| Operación cancelada / foto inválida | Mensajes originales y recuperación |
| Tema oscuro | Tokens semánticos correctos; bitmap mantiene paleta propia |
| Inglés | Diccionario correcto, sin cortar controles por traducciones |
| Teclado / pantalla pequeña | Acceso desplazable, contenido visible y sin solapamientos |
| Cierre de sesión | Regreso al acceso, limpieza de fotos y vistas |

### 16.4. Comparación pixel a pixel

Cuando haya emulador/dispositivo o capturas disponibles, captura ambas implementaciones en estados equivalentes. Espera a que terminen las transiciones y cargas; usa la misma fotografía y el mismo plan. Fija idioma, tema y scroll; normaliza densidad y tamaño. Compara recortes idénticos.

Genera superposición al 50% y un mapa de diferencias si las herramientas disponibles lo permiten. Ajusta en este orden: geometría global e insets, fuente y métricas, contenedores, textos y saltos de línea, iconos, bordes, colores y estados. Verifica la posición del primer artículo, la altura del tracker y el borde superior de la barra inferior: son puntos donde suelen acumularse diferencias.

No uses un porcentaje arbitrario de similitud como prueba suficiente. Registra los desajustes concretos por pantalla y no ignores cambios visibles en espaciado o iconografía porque el fondo ocupa muchos píxeles. Si no existe referencia ejecutada o captura, entrega una implementación basada en código y declara que falta contrastarla visualmente.

Ejecuta las comprobaciones del proyecto destino apropiadas a los cambios. Para el Android original, la compilación es `gradlew.bat assembleDebug` en Windows; ejecuta las pruebas de cálculos/estado pertinentes si modificas su comportamiento. No afirmes que compilaste, abriste la app o comparaste capturas si no lo hiciste.

## 17. Entregable y condiciones de finalización

Entrega la aplicación implementada, con las seis vistas principales contando acceso y ajustes, todos sus estados, los datos completos, las fotografías y la composición local. Incluye instrucciones concretas para ejecutar el proyecto destino.

En tu informe final indica qué archivos cambiaste, qué verificaste realmente y qué diferencias visuales siguen pendientes. Si una capacidad de plataforma no puede reproducirse, conserva el flujo más fiel disponible y describe la diferencia concreta. No declares «pixel perfect» sin evidencia visual al mismo tamaño y estado.

El trabajo no termina al entregar tokens, una única pantalla, una captura estática, un plan o cuatro pestañas con contenido provisional. Continúa hasta que el producto completo sea navegable y sus cálculos estén conectados a estado compartido real.

## 18. Anexo: instantánea íntegra de las fuentes de referencia

Los bloques siguientes son referencias del proyecto Android y no una orden de copiar Kotlin dentro de un proyecto web. Conservan imports, comentarios, valores, datos y funciones originales para poder resolver cualquier detalle omitido en la descripción. Cada bloque está identificado por su ruta y una huella SHA-256 del archivo original.

Se incluyen las fuentes Kotlin de producción, configuración Gradle, manifiesto y recursos XML de producción. No se incluyen contraseñas, bases locales de cuentas, claves, `local.properties`, cachés ni artefactos de compilación. Los iconos Material vienen de las dependencias; las imágenes del lanzador son recursos binarios del repositorio y no se utilizan como ilustraciones de estas pantallas.

