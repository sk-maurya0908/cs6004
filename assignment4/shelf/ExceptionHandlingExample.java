package shelf;
public class ExceptionHandlingExample {
    public static void main(String[] args) {
        // Start measuring time
        long startTime = System.nanoTime();

        // Test case 1: Try-catch block with potentially thrown exception
        try {
            int result = divide(10, 0);
            System.out.println("Result of division: " + result);
        } catch (ArithmeticException e) {
            System.err.println("Error: Division by zero");
        }

        // Test case 2: Try-catch-finally block
        try {
            String str = null;
            System.out.println("Length of string: " + str.length());
        } catch (NullPointerException e) {
            System.err.println("Error: Null pointer exception");
        } finally {
            System.out.println("Finally block executed");
        }

        // Test case 3: Custom exception handling
        try {
            int[] array = {1, 2, 3};
            System.out.println("Value at index 5: " + array[5]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error: Array index out of bounds");
        }

        // Test case 4: Multiple catch blocks
        try {
            String str = "123";
            int num = Integer.parseInt(str);
            System.out.println("Parsed integer: " + num);
        } catch (NumberFormatException e) {
            System.err.println("Error: Number format exception");
        } catch (NullPointerException e) {
            System.err.println("Error: Null pointer exception");
        }

        // Test case 5: Nested try-catch blocks
        try {
            try {
                int result = divide(10, 0);
                System.out.println("Result of division: " + result);
            } catch (ArithmeticException e) {
                System.err.println("Inner Error: Division by zero");
            }
        } catch (Exception e) {
            System.err.println("Outer Error: " + e.getMessage());
        }

        // End measuring time
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // Convert nanoseconds to milliseconds

        System.out.println("Time taken: " + duration + " milliseconds");
    }

    // Method definition for test case 1
    public static int divide(int dividend, int divisor) {
        return dividend / divisor;
    }
}
