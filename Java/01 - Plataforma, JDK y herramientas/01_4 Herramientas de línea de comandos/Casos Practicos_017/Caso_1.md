# Caso 1: diagnosticar `UnsupportedClassVersionError`

## El problema
Compilas con un JDK nuevo y ejecutas en una JVM más antigua (servidor, CI). La clase no carga:

```text
Exception in thread "main" java.lang.UnsupportedClassVersionError: Prueba has been compiled by a more recent version of the Java Runtime (class file version 69.0), this version of the Java Runtime only recognizes class file versions up to 61.0
```

El mensaje trae los dos números, pero con `javap` los confirmas también en un `.class` o JAR que no compilaste tú.

## La regla
`major version = versión de Java + 44`

| Java | 8 | 11 | 17 | 21 | 25 |
|---|---|---|---|---|---|
| major | 52 | 55 | 61 | 65 | 69 |

Una JVM ejecuta clases con `major` igual o menor que el suyo, nunca mayor: Java 17 ejecuta bytecode de Java 8, pero Java 8 no ejecuta el de Java 17.

## Reproducirlo
Con los JDK de la nota 013, compila con el 25 y ejecuta con el 17:

```bash
# Con JDK 25 activo
javac -d out Prueba.java

# Cambia al JDK 17 (método de la nota 013) y verifica
java -version

# Ejecuta la clase compilada con el 25
java -cp out Prueba
```

Lanza el error anterior (`69.0` frente a `61.0`).

## Diagnosticar
Con `-v` sale el `major version`; el filtro evita leer todo el *constant pool*:

```bash
# Una clase suelta
javap -classpath out -v Prueba | Select-String "major version"

# Una clase dentro de un JAR (sin código fuente)
javap -classpath lib/app.jar -v com.ejemplo.Servicio | Select-String "major version"
```

Resultado: `major version: 69`. Compáralo con `java -version` del entorno donde falla.

## Corregir
1. **Subir la JVM de destino** a una versión igual o mayor que la de la clase (ideal si controlas el entorno).
2. **Recompilar apuntando a la versión de destino** con `--release`:

```bash
javac --release 17 -d out Prueba.java
javap -classpath out -v Prueba | Select-String "major version"
```

Ahora aparece `major version: 61`. `--release` es preferible a `-source` y `-target` por separado: además de fijar el bytecode, restringe la API a la de esa versión (ver caso 5). En Maven la propiedad equivalente es `maven.compiler.release`.

## Cuándo aplicarlo
- Un despliegue falla con `UnsupportedClassVersionError`.
- Una dependencia exige un Java más nuevo: con `javap` ves a qué versión se compiló su JAR.
- Antes de publicar una librería, para comprobar que no la compilaste con una versión más alta de la prometida.

# Comandos para Linux/macOS
```bash
javap -classpath out -v Prueba | grep "major version"
```

[← Volver a la nota 017](../017%20-%2001_4.3%20javadoc,%20javap,%20jdeps.md)
