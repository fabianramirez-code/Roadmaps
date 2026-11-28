# Case 2: diagnose `NoSuchMethodError` and `NoSuchFieldError`

## The problem
The code compiled, but at runtime a method or field that exists in the source fails:

```text
Exception in thread "main" java.lang.NoSuchMethodError: 'int Util.total()'
```

This is **binary incompatibility**: you compiled against one class version and run with another. Common in dependency conflicts (two libraries pull in different versions of a third).

## Why it happens
The JVM searches by **name + descriptor**, and the descriptor includes return type. `()I` means no parameters, returns `int`. If a new version returns `long`, its descriptor is `()J`: the source still compiles, but previously compiled bytecode looks for `()I`, which no longer exists. Removing a method, changing parameters or renaming it also breaks it. Fields behave similarly (`NoSuchFieldError`).

## Reproduce it
Library version 1:

```java
public class Util {
    public static int total() { return 1; }
}
```

Client:

```java
public class Client {
    public static void main(String[] args) {
        System.out.println(Util.total());
    }
}
```

```bash
javac -d lib1 Util.java
javac -cp lib1 -d out Client.java
```

Change `Util` to `public static long total() { return 1L; }` and compile it separately as the new dependency version:

```bash
javac -d lib2 Util.java
```

Run the client (compiled against v1) with library v2:

```bash
java -cp "out;lib2" Client
```

This throws `NoSuchMethodError: 'int Util.total()'`.

## Diagnose
Compare what **the caller requests** with what **the actual class offers**.

**1. What the client requests** (`-c` bytecode, `-p` includes private members):

```bash
javap -c -p -classpath out Client | Select-String "Util"
```

Output: `invokestatic ... // Method Util.total:()I`.

**2. What the loaded class offers** (`-s` prints internal descriptors):

```bash
javap -s -p -classpath lib2 Util
```

Output: `descriptor: ()J`.

`()I` versus `()J` is the cause. For fields, `-s` shows their descriptor and `-c` on the caller shows `getstatic`/`getfield` with the expected type.

## Find which JAR the JVM loads
```bash
java -verbose:class -cp "out;lib2" Client | Select-String "Util"
```

Each line identifies the class's origin (`source:`).

## Fix
1. Align versions: use the same library version when compiling and running (in Maven, `mvn dependency:tree` shows which dependency brings which version; note 11).
2. Recompile the client against the version actually used at runtime.
3. If you maintain the library: do not change return types or remove public methods; add an overload or retain the old one as `@Deprecated`.

## When to apply it
- Failure after updating a dependency despite successful compilation.
- Works locally but fails on a server with a different JAR version.
- `NoSuchMethodError`/`NoSuchFieldError` appears in a *stack trace* without recent changes to your code.

# Linux/macOS Commands
```bash
# The classpath separator is ':'
java -cp "out:lib2" Client

# Filter with grep
javap -c -p -classpath out Client | grep Util
java -verbose:class -cp "out:lib2" Client | grep Util
```

[← Back to note 017](../017%20-%2001_4.3%20javadoc,%20javap,%20jdeps.md)
