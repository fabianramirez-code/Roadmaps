public class DebugDemo {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 15; i++) {
            Thread.sleep(500);
            System.out.println(greet("Java"));
        }
    }

    static String greet(String name) {
        return "Hello, " + name;
    }
}