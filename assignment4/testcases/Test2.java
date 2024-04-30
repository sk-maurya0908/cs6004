
public class Test2 {
    public static void main(String[] args) {
        
        int d = foo(10,20,0);
        //System.out.println(d);
    }
    public static int foo(int a, int b, int c){
        int x = 10;
        if (a > 5) {
            c = b;
        } else {
            c = a;
        }
        return c+5;
    }
}

