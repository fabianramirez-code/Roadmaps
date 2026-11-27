# Reglas para las notas de estudio

`Roadmaps` (`E:\All_Notes\Personal_Notes\Software\Roadmaps\Java\`) es la fuente de información. `PRACTICAS` contiene el roadmap y las notas de estudio `.md` numeradas por prefijo (por ejemplo, 016), que el asistente ayuda a redactar y desarrollar tema por tema, desde Java 0 hasta avanzado. Cada nota se desarrolla a partir de los temas ya enumerados en ese mismo archivo.

1. Presenta información clara y adaptada a Windows 11. Escribe los comandos para PowerShell en Windows 11 en bloques `` ```bash `` para que los comentarios con `#` se resalten correctamente; los ejemplos asumen que se ejecutan dentro de `Ejemplos_NNN`.
2. Sintetiza: cada dato o comando aparece una sola vez por documento, con una explicación clara.
3. Deja los comandos para Linux/macOS al final, en una sección especial titulada `# Comandos para Linux/macOS` (como en la nota 015), y muestra solo lo que difiere.
4. Antes de usar una sigla, escribe su nombre completo, por ejemplo, Java Virtual Machine (JVM); después puedes usar solo la sigla.
5. Cuando aparezca por primera vez una sigla o concepto nuevo, como Technology Compatibility Kit (TCK) o Java Compatibility Kit (JCK), añade una explicación breve que no recargue la nota (por ejemplo, TCK: conjunto de pruebas que garantiza que Java cumple los requisitos de calidad) y un enlace a la fuente o repositorio oficial.
6. Sé lo más sintético posible, sin repetir palabras ni bloques con la misma información, pero nunca ambiguo.
7. No generes código de ejemplo ni archivos en el repositorio: tampoco carpetas de ejemplos ni salidas generadas. La persona crea, compila y ejecuta todo. Las notas solo dan instrucciones; cualquier ejemplo debe ser mínimo y aparecer en la propia nota (como el saludo `Prueba.java` y el único jar básico de la nota 015). No incluyas notas introductorias sobre crear la carpeta de ejemplos ni bloques `cd Ejemplos_NNN`.
8. **Muy importante:** cada bloque de código Java de las notas debe incluir todos los imports que utiliza (por ejemplo, `java.util` o `java.sql`), preferiblemente explícitos (`import java.util.List;`); los compiladores web de las entrevistas los requieren aunque el IDE los añada automáticamente.
