public class Depura {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 15; i++) {
            Thread.sleep(500);
            System.out.println(saludar("Java"));
        }
    }

    static String saludar(String nombre) {
        return "Hola, " + nombre;
    }
}