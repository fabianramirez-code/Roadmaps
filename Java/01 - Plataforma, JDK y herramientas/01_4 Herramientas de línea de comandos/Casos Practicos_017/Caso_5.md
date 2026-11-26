# Caso 5: verificar que el compilador respetó `--release`

## El problema
Quieres que el código corra en una JVM concreta (por ejemplo Java 11) aunque compiles con un JDK más nuevo. `--release N` hace dos cosas: fija el `major version` del bytecode y limita la API visible a la de Java N. `-source N -target N` solo hace lo primero: compila contra la API del JDK instalado, así que un método posterior a N compila y falla en ejecución con `NoSuchMethodError` (caso 2).

## Ejemplo mínimo
`Stream.toList()` existe desde Java 16:

```java
import java.util.List;
import java.util.stream.Stream;

public class Release {
    public static void main(String[] args) {
        List<String> lista = Stream.of("a", "b").toList();
        System.out.println(lista);
    }
}
```

## 1. `--release` rechaza la API inexistente
```bash
javac --release 11 -d out Release.java
```

Falla en compilación con `cannot find symbol ... method toList()`: el error aparece en tu máquina y no en producción. Con `--release 17` compila.

## 2. Verificar el bytecode generado
```bash
javac --release 17 -d out Release.java
javap -classpath out -v Release | Select-String "major version"
```

Resultado: `major version: 61` (Java 17). Tabla de versiones en el caso 1 (`major = Java + 44`).

## 3. Comparar con `-source`/`-target`
```bash
javac -source 11 -target 11 -d out Release.java
```

Compila (con una advertencia sobre la *bootstrap classpath*) porque usa la API del JDK actual. `javap` muestra `major version: 55`, pero el bytecode referencia `Stream.toList()`, que una JVM 11 no tiene: falla en ejecución con `NoSuchMethodError`. Un `major` correcto no garantiza compatibilidad de API; solo `--release` la garantiza.

## Cuándo aplicarlo
- Antes de publicar una librería o desplegar: comprueba que el `major` coincide con la versión prometida.
- En auditorías de CI: recorre los `.class` de un JAR con `javap -v` y falla si el `major` supera el objetivo.
- Al migrar de versión: confirma que el build realmente usa `--release` (Maven: `maven.compiler.release`; Gradle: `options.release`).

# Comandos para Linux/macOS
```bash
javap -classpath out -v Release | grep "major version"
```

[← Volver a la nota 017](../017%20-%2001_4.3%20javadoc,%20javap,%20jdeps.md)
