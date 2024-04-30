import java.util.*;

public class Test1{
    public static void main(String[] args) {
        System.out.println("Example");

    }

    public static int exampleMethod(int abc) {
        int x = 5;
        int y = 10;
        int z = x + y; // z is used
        int a = 20;
        int b = 30; // b is never used
        return z;
    }
    
}
