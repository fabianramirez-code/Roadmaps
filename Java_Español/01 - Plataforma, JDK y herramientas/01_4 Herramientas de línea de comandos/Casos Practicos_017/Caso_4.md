# Caso 4: inspeccionar la API pública de un JAR sin código fuente

## El problema
Tienes un JAR de terceros y necesitas saber qué ofrece (si un método existe, su firma exacta, qué expone) sin fuentes ni documentación fiable. Los ejemplos usan el `commons-lang3.jar` de la nota 015.

## Paso 1: listar las clases del JAR
`javap` necesita nombres completos y no admite comodines:

```bash
# Todo el contenido (un JAR es un ZIP)
jar tf lib/commons-lang3.jar

# Solo lo que interesa
jar tf lib/commons-lang3.jar | Select-String "StringUtils"
```

Las líneas con `$` son clases internas. Convierte la ruta en nombre de clase cambiando `/` por `.` y quitando `.class`.

## Paso 2: ver la API pública
Sin `-c` ni `-v`, `javap` muestra solo firmas:

```bash
javap -classpath lib/commons-lang3.jar org.apache.commons.lang3.StringUtils
```

Salida (resumida):

```text
public class org.apache.commons.lang3.StringUtils {
  public static final java.lang.String SPACE;
  public static boolean isEmpty(java.lang.CharSequence);
  public static boolean isBlank(java.lang.CharSequence);
  ...
}
```

| Opción | Muestra |
|---|---|
| `-public` | Solo `public`, la API realmente consumible |
| `-protected` | `public` y `protected` (valor por defecto) |
| `-package` | Añade los de paquete |
| `-p` | Todo, incluido lo privado |

## Paso 3: responder preguntas concretas
**¿Existe este método en esta versión?**

```bash
javap -classpath lib/commons-lang3.jar org.apache.commons.lang3.StringUtils | Select-String "isBlank"
```

Sin salida, el método no existe en esa versión (evita errores como los del caso 2).

**¿Firma exacta, con genéricos?** La salida ya los muestra (por ejemplo `<T extends java.lang.CharSequence> T defaultIfBlank(T, T)`); con `-s` añades los descriptores internos.

**¿Qué tipo de clase es?** La cabecera indica `class`, `interface`, `abstract class` o `final class` (si es `final`, no se puede extender).

**¿Está obsoleto?** Con `-v` aparece `Deprecated: true` en el método.

**¿Con qué versión de Java se compiló?** Combina con el caso 1:

```bash
javap -classpath lib/commons-lang3.jar -v org.apache.commons.lang3.StringUtils | Select-String "major version"
```

## También sirve para el JDK
```bash
javap java.util.List
javap java.util.concurrent.ConcurrentHashMap
```

Los métodos `default` de las interfaces aparecen marcados como `default`.

## Cuándo aplicarlo
- Auditar una dependencia: qué expone y qué versión de bytecode usa.
- Comprobar si una versión del JAR tiene el método que necesitas.
- Migrar entre versiones de una librería: compara la salida de `javap` de ambas.
- Entender una librería interna sin documentación ni fuentes.

# Comandos para Linux/macOS
```bash
jar tf lib/commons-lang3.jar | grep StringUtils
javap -classpath lib/commons-lang3.jar org.apache.commons.lang3.StringUtils | grep isBlank
javap -classpath lib/commons-lang3.jar -v org.apache.commons.lang3.StringUtils | grep "major version"
```

[← Volver a la nota 017](../017%20-%2001_4.3%20javadoc,%20javap,%20jdeps.md)
