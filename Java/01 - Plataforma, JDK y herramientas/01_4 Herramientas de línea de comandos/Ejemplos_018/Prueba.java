import java.sql.Connection;
import  java.util.List;

public class Prueba {
    public static List<String> nombres (Connection connection){
        return List.of();
    }

    public static void obsoleto(Thread hilo){
        hilo.stop();
    }

    public static void main (String[] args){
        System.out.println("Hola Java");
    }
}