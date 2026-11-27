# Caso 2: diagnosticar `NoSuchMethodError` y `NoSuchFieldError`

## El problema
El código compiló, pero al ejecutarlo falla un método o campo que sí existe en el fuente:

```text
Exception in thread "main" java.lang.NoSuchMethodError: 'int Util.total()'
```

Es **incompatibilidad binaria**: compilaste contra una versión de una clase y ejecutas con otra. Típico de conflictos de dependencias (dos librerías arrastran versiones distintas de una tercera).

## Por qué ocurre
La JVM busca el método por **nombre + descriptor**, y el descriptor incluye el tipo de retorno. `()I` es: sin parámetros, devuelve `int`. Si la versión nueva devuelve `long`, su descriptor es `()J`: el fuente sigue compilando, pero el bytecode ya compilado busca `()I`, que no existe. También se rompe si se elimina el método, se cambian sus parámetros o se renombra. Con campos pasa igual (`NoSuchFieldError`).

## Reproducirlo
Versión 1 de la librería:

```java
public class Util {
    public static int total() { return 1; }
}
```

Cliente:

```java
public class Cliente {
    public static void main(String[] args) {
        System.out.println(Util.total());
    }
}
```

```bash
javac -d lib1 Util.java
javac -cp lib1 -d out Cliente.java
```

Cambia `Util` a `public static long total() { return 1L; }` y compílala aparte, como la versión nueva de la dependencia:

```bash
javac -d lib2 Util.java
```

Ejecuta el cliente (compilado contra la v1) con la librería v2:

```bash
java -cp "out;lib2" Cliente
```

Lanza `NoSuchMethodError: 'int Util.total()'`.

## Diagnosticar
Compara lo que **pide el llamador** con lo que **ofrece la clase real**.

**1. Qué pide el cliente** (`-c` bytecode, `-p` incluye privados):

```bash
javap -c -p -classpath out Cliente | Select-String "Util"
```

Salida: `invokestatic ... // Method Util.total:()I`.

**2. Qué ofrece la clase cargada** (`-s` imprime descriptores internos):

```bash
javap -s -p -classpath lib2 Util
```

Salida: `descriptor: ()J`.

`()I` frente a `()J` es la causa. Para campos, `-s` muestra su descriptor y `-c` en el llamador muestra el `getstatic`/`getfield` con el tipo esperado.

## Encontrar qué JAR carga la JVM
```bash
java -verbose:class -cp "out;lib2" Cliente | Select-String "Util"
```

Cada línea indica el origen (`source:`) de la clase.

## Corregir
1. Alinear versiones: la misma versión de la librería al compilar y al ejecutar (en Maven, `mvn dependency:tree` muestra quién arrastra qué versión; nota 11).
2. Recompilar el cliente contra la versión que realmente se ejecuta.
3. Si eres el autor de la librería: no cambies el tipo de retorno ni elimines métodos públicos; añade una sobrecarga o deja el anterior como `@Deprecated`.

## Cuándo aplicarlo
- Falla tras actualizar una dependencia aunque el proyecto compile.
- Funciona en tu máquina y falla en el servidor, que tiene otra versión de un JAR.
- Aparece `NoSuchMethodError`/`NoSuchFieldError` en un *stack trace* sin cambios recientes en tu código.

# Comandos para Linux/macOS
```bash
# El separador de classpath es ':'
java -cp "out:lib2" Cliente

# Filtrar con grep
javap -c -p -classpath out Cliente | grep Util
java -verbose:class -cp "out:lib2" Cliente | grep Util
```

[← Volver a la nota 017](../017%20-%2001_4.3%20javadoc,%20javap,%20jdeps.md)
