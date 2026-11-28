import java.sql.Connection;
import  java.util.List;

public class Test {
    public static List<String> names (Connection connection){
        return List.of();
    }

    public static void obsolete(Thread thread){
        thread.stop();
    }

    public static void main (String[] args){
        System.out.println("Hello Java");
    }
}