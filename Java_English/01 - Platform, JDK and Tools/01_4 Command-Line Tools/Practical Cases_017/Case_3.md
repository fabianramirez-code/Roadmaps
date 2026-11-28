# Case 3: see what the compiler generates

## Idea
`javap -c` shows actual bytecode. Many Java constructs are syntactic sugar; seeing their translation explains behavior that otherwise seems arbitrary. This is a common interview topic.

## Minimal example
```java
public class Compiled {

    static int boxing() {
        Integer a = 127;
        Integer b = 127;
        return a + b;
    }

    static boolean compare() {
        Integer x = 128;
        Integer y = 128;
        return x == y;
    }

    static String concatenate(String name, int age) {
        return name + " is " + age;
    }

    static Runnable lambda() {
        return () -> System.out.println("hello");
    }
}
```

```bash
javac -d out Compiled.java
javap -c -p -classpath out Compiled
```

`-p` is needed to see compiler-generated private methods.

## 1. *Autoboxing*: `Integer` ↔ `int`
In `boxing()` the bytecode contains:

```text
bipush 127
invokestatic  Integer.valueOf:(I)Ljava/lang/Integer;
...
invokevirtual Integer.intValue:()I
iadd
```

- `Integer a = 127` is actually `Integer.valueOf(127)`.
- `a + b` calls `intValue()` on each operand and adds the `int` values.
- If `a` were `null`, `intValue()` would throw `NullPointerException`: a source of "mysterious" NPEs in wrapper operations.

## 2. Why `==` fails with `Integer`
In `compare()` you see `if_acmpne`: `acmp` compares **references**; an `int` would use `if_icmpne`. `Integer.valueOf` caches objects only from -128 to 127:
- `127 == 127` with `Integer` returns `true` (same object).
- `128 == 128` with `Integer` returns `false` (different objects).

Rule: compare wrappers with `equals()`, never `==`.

## 3. `String` concatenation
Java 8 generated `StringBuilder`. Since Java 9:

```text
invokedynamic #7,0  // InvokeDynamic #0:makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;
```

- The compiler delegates the strategy to the JVM (`invokedynamic`), which can optimize it.
- A loop still creates a new `String` per iteration: use an explicit `StringBuilder` for accumulation.
- With `-v`, look for `BootstrapMethods` to see the recipe (`\u0001 is \u0001`).

## 4. Lambdas
In `lambda()` you see:

```text
invokedynamic #x,0  // InvokeDynamic #0:run:()Ljava/lang/Runnable;
```

And with `-p`, a method you did not write: `private static void lambda$lambda$0();`.
- The lambda body is a synthetic private method of the same class.
- It does not generate another `.class`; an anonymous class does (`Compiled$1.class`). Check with `dir out`.

## Other useful constructs to inspect
| Construct | What `javap` shows |
|---|---|
| `switch` on `String` | `hashCode()` + `lookupswitch`, then `equals()` to resolve collisions |
| `enum` | `extends java/lang/Enum`, `values()` and `valueOf()` methods, `$VALUES` field |
| `record` | `extends java/lang/Record`; `toString`, `equals`, `hashCode` generated via `invokedynamic` |
| Omitted constructor | Injected `Compiled();`, which calls `Object.<init>` |

## When to apply it
- Interviews: explain with evidence why `Integer == Integer` fails or what a lambda is.
- Debug an NPE without apparent null references: look for `intValue` or `longValue`.
- Understand the actual cost of a construct, such as concatenation in a loop.

# Linux/macOS Commands
```bash
javap -c -p -classpath out Compiled | grep -E "valueOf|intValue|acmp|invokedynamic"
```

[← Back to note 017](../017%20-%2001_4.3%20javadoc,%20javap,%20jdeps.md)
