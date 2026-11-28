# Case 4: inspect a JAR's public API without source code

## The problem
You have a third-party JAR and need to know what it offers (whether a method exists, its exact signature, what it exposes) without sources or reliable documentation. The examples use `commons-lang3.jar` from note 015.

## Step 1: list classes in the JAR
`javap` needs fully qualified names and does not accept wildcards:

```bash
# All contents (a JAR is a ZIP)
jar tf lib/commons-lang3.jar

# Only relevant entries
jar tf lib/commons-lang3.jar | Select-String "StringUtils"
```

Lines with `$` are inner classes. Convert a path to a class name by replacing `/` with `.` and removing `.class`.

## Step 2: view the public API
Without `-c` or `-v`, `javap` shows only signatures:

```bash
javap -classpath lib/commons-lang3.jar org.apache.commons.lang3.StringUtils
```

Output (abridged):

```text
public class org.apache.commons.lang3.StringUtils {
  public static final java.lang.String SPACE;
  public static boolean isEmpty(java.lang.CharSequence);
  public static boolean isBlank(java.lang.CharSequence);
  ...
}
```

| Option | Shows |
|---|---|
| `-public` | Only `public`, the usable API |
| `-protected` | `public` and `protected` (default) |
| `-package` | Adds package-private members |
| `-p` | Everything, including private members |

## Step 3: answer specific questions
**Does this method exist in this version?**

```bash
javap -classpath lib/commons-lang3.jar org.apache.commons.lang3.StringUtils | Select-String "isBlank"
```

No output means the method does not exist in that version (avoiding errors like those in case 2).

**Exact signature, including generics?** The output shows them already (for example `<T extends java.lang.CharSequence> T defaultIfBlank(T, T)`); `-s` adds internal descriptors.

**What kind of class?** The header says `class`, `interface`, `abstract class` or `final class` (`final` classes cannot be extended).

**Is it deprecated?** `-v` shows `Deprecated: true` on the method.

**Which Java version compiled it?** Combine with case 1:

```bash
javap -classpath lib/commons-lang3.jar -v org.apache.commons.lang3.StringUtils | Select-String "major version"
```

## Also works for the JDK
```bash
javap java.util.List
javap java.util.concurrent.ConcurrentHashMap
```

Interface `default` methods are marked `default`.

## When to apply it
- Audit a dependency: what it exposes and which bytecode version it uses.
- Check whether a JAR version has the method you need.
- Migrate between library versions: compare their `javap` output.
- Understand an internal library without documentation or sources.

# Linux/macOS Commands
```bash
jar tf lib/commons-lang3.jar | grep StringUtils
javap -classpath lib/commons-lang3.jar org.apache.commons.lang3.StringUtils | grep isBlank
javap -classpath lib/commons-lang3.jar -v org.apache.commons.lang3.StringUtils | grep "major version"
```

[← Back to note 017](../017%20-%2001_4.3%20javadoc,%20javap,%20jdeps.md)
