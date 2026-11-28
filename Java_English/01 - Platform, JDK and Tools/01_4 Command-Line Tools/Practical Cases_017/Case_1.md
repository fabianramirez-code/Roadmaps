# Case 1: diagnose `UnsupportedClassVersionError`

## The problem
You compile with a newer JDK and run on an older JVM (server, CI). The class cannot load:

```text
Exception in thread "main" java.lang.UnsupportedClassVersionError: Test has been compiled by a more recent version of the Java Runtime (class file version 69.0), this version of the Java Runtime only recognizes class file versions up to 61.0
```

The message contains both numbers, but `javap` can also confirm them in a `.class` or JAR you did not compile yourself.

## The rule
`major version = Java version + 44`

| Java | 8 | 11 | 17 | 21 | 25 |
|---|---|---|---|---|---|
| major | 52 | 55 | 61 | 65 | 69 |

A JVM runs classes with a `major` no greater than its own: Java 17 runs Java 8 bytecode, but Java 8 cannot run Java 17 bytecode.

## Reproduce it
With the JDKs from note 013, compile with 25 and run with 17:

```bash
# With JDK 25 active
javac -d out Test.java

# Switch to JDK 17 (method from note 013) and check
java -version

# Run the class compiled with 25
java -cp out Test
```

This produces the error above (`69.0` versus `61.0`).

## Diagnose
`-v` displays the `major version`; the filter avoids reading the entire *constant pool*:

```bash
# Individual class
javap -classpath out -v Test | Select-String "major version"

# Class inside a JAR (without source)
javap -classpath lib/app.jar -v com.example.Service | Select-String "major version"
```

Result: `major version: 69`. Compare it to `java -version` in the failing environment.

## Fix
1. **Upgrade the target JVM** to at least the class's version (ideal if you control the environment).
2. **Recompile for the target version** using `--release`:

```bash
javac --release 17 -d out Test.java
javap -classpath out -v Test | Select-String "major version"
```

Now it shows `major version: 61`. `--release` is preferable to separate `-source` and `-target`: it limits the API to that version as well as setting the bytecode (see case 5). In Maven, the equivalent property is `maven.compiler.release`.

## When to apply it
- A deployment fails with `UnsupportedClassVersionError`.
- A dependency requires newer Java: `javap` tells you the version its JAR was compiled for.
- Before publishing a library, check that it was not compiled for a version higher than promised.

# Linux/macOS Commands
```bash
javap -classpath out -v Test | grep "major version"
```

[← Back to note 017](../017%20-%2001_4.3%20javadoc,%20javap,%20jdeps.md)
