# Caso 3: ver qué genera el compilador

## Idea
`javap -c` muestra el bytecode real. Muchas construcciones de Java son azúcar sintáctico; ver su traducción explica comportamientos que parecen arbitrarios. Es tema frecuente de entrevista.

## Ejemplo mínimo
```java
public class Compilado {

    static int boxing() {
        Integer a = 127;
        Integer b = 127;
        return a + b;
    }

    static boolean comparar() {
        Integer x = 128;
        Integer y = 128;
        return x == y;
    }

    static String concatenar(String nombre, int edad) {
        return nombre + " tiene " + edad;
    }

    static Runnable lambda() {
        return () -> System.out.println("hola");
    }
}
```

```bash
javac -d out Compilado.java
javap -c -p -classpath out Compilado
```

`-p` es necesario para ver los métodos privados que genera el compilador.

## 1. *Autoboxing*: `Integer` ↔ `int`
En `boxing()` el bytecode contiene:

```text
bipush 127
invokestatic  Integer.valueOf:(I)Ljava/lang/Integer;
...
invokevirtual Integer.intValue:()I
iadd
```

- `Integer a = 127` es en realidad `Integer.valueOf(127)`.
- `a + b` llama a `intValue()` en cada operando y suma `int`.
- Si `a` fuera `null`, `intValue()` lanzaría `NullPointerException`: origen de los NPE "misteriosos" al operar con wrappers.

## 2. Por qué `==` falla con `Integer`
En `comparar()` aparece `if_acmpne`: `acmp` compara **referencias**; un `int` usaría `if_icmpne`. `Integer.valueOf` reutiliza objetos en caché solo para -128 a 127:
- `127 == 127` con `Integer` da `true` (mismo objeto).
- `128 == 128` con `Integer` da `false` (objetos distintos).

Regla: compara wrappers con `equals()`, nunca con `==`.

## 3. Concatenación de `String`
Java 8 generaba `StringBuilder`. Desde Java 9:

```text
invokedynamic #7,0  // InvokeDynamic #0:makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;
```

- El compilador delega la estrategia en la JVM (`invokedynamic`), que puede optimizarla.
- En un bucle sigue creando un `String` nuevo por iteración: para acumular usa un `StringBuilder` explícito.
- Con `-v` buscas `BootstrapMethods` para ver la receta (`\u0001 tiene \u0001`).

## 4. Lambdas
En `lambda()` aparece:

```text
invokedynamic #x,0  // InvokeDynamic #0:run:()Ljava/lang/Runnable;
```

Y con `-p` un método que no escribiste: `private static void lambda$lambda$0();`.
- El cuerpo de la lambda es un método privado sintético de la misma clase.
- No genera un `.class` aparte; una clase anónima sí (`Compilado$1.class`). Compruébalo con `dir out`.

## Otras construcciones útiles de revisar
| Construcción | Qué muestra `javap` |
|---|---|
| `switch` sobre `String` | `hashCode()` + `lookupswitch`, y luego `equals()` para resolver colisiones |
| `enum` | `extends java/lang/Enum`, métodos `values()` y `valueOf()`, campo `$VALUES` |
| `record` | `extends java/lang/Record`; `toString`, `equals`, `hashCode` generados por `invokedynamic` |
| Constructor omitido | `Compilado();` inyectado, que llama a `Object.<init>` |

## Cuándo aplicarlo
- Entrevistas: explicar con evidencia por qué `Integer == Integer` falla o qué es una lambda.
- Depurar un NPE sin referencias nulas aparentes: busca `intValue` o `longValue`.
- Entender el coste real de una construcción, como concatenar en un bucle.

# Comandos para Linux/macOS
```bash
javap -c -p -classpath out Compilado | grep -E "valueOf|intValue|acmp|invokedynamic"
```

[← Volver a la nota 017](../017%20-%2001_4.3%20javadoc,%20javap,%20jdeps.md)
