package shelf;
public class Test3 {

    public void methodWithDeadCode() {
        int x = 5;
        int y = 10;
        int z = x + y;
        int a = z * 2;
        int b = a - x; // Dead code
        int c = b * 3;
        System.out.println("Result: " + c);
    }

    public void methodWithUnusedVariables() {
        int x = 5;
        int y = 10; // Unused variable
        int z = x + y;
        System.out.println("Result: " + z);
    }

    public void methodWithUnreachableCode() {
        int x = 5;
        if (x > 10) {
            System.out.println("x is greater than 10");
        } else {
            System.out.println("x is not greater than 10");
        }
        System.out.println("This line is unreachable");
    }

    public void methodWithComplexFlow() {
        int x = 5;
        int y = 10;
        int z = x + y;
        if (z > 15) {
            int a = z * 2;
            int b = a - x;
            System.out.println("Result: " + b);
        } else {
            System.out.println("z is not greater than 15");
        }
    }

    public static void main(String[] args) {
        Test3 obj = new Test3();
        obj.methodWithDeadCode();
        obj.methodWithUnusedVariables();
        obj.methodWithUnreachableCode();
        obj.methodWithComplexFlow();
    }
}
