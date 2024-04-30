package shelf;
public class Test1{
    public static void main(String[] args) {
        int a = 10;
        int b = 20;
        int c = a + b;
        System.out.println(c);
    }

    public static void exampleMethod(int abc) {
        int x = 5;
        int y = 10;
        int z = x + y; // z is used
        int a = 20;
        int b = 30; // b is never used
        System.out.println(z);
    }
    
}
