# Rules for study notes

`Roadmaps` (`E:\All_Notes\Personal_Notes\Software\Roadmaps\Java\`) is the information source. `PRACTICAS` contains the roadmap and the `.md` study notes numbered by prefix (for example, 016), which the assistant helps write and develop topic by topic, from Java 0 through advanced topics. Each note is developed from the topics already listed in that same file.

1. Present clear information adapted to Windows 11. Write PowerShell commands for Windows 11 in `` ```bash `` blocks so comments with `#` are highlighted correctly; the examples assume they are run inside `Examples_NNN`.
2. Be concise: each fact or command appears only once per document, with a clear explanation.
3. Put Linux/macOS commands at the end, in a special section titled `# Linux/macOS Commands` (as in note 015), and show only what differs.
4. Before using an acronym, spell out its full name, for example, Java Virtual Machine (JVM); afterward you may use just the acronym.
5. When an acronym or new concept first appears, such as Technology Compatibility Kit (TCK) or Java Compatibility Kit (JCK), add a brief explanation that does not overload the note (for example, TCK: a set of tests ensuring that Java meets quality requirements) and a link to the official source or repository.
6. Be as concise as possible, without repeating words or blocks with the same information, but never ambiguous.
7. Do not generate example code or files in the repository: no example folders or generated output either. The person creates, compiles, and runs everything. Notes only provide instructions; any example must be minimal and appear in the note itself (like the `Test.java` greeting and the single basic JAR in note 015). Do not include introductory notes on creating the examples folder or `cd Examples_NNN` blocks.
8. **Very important:** every Java code block in the notes must include all imports it uses (for example, `java.util` or `java.sql`), preferably explicit ones (`import java.util.List;`); online interview compilers require them even if the IDE adds them automatically.
