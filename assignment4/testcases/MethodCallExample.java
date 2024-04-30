public class MethodCallExample {
    public static void main(String[] args) {
        // Start measuring time
        //long startTime = System.nanoTime();

        int number = 10;
        String day = "Monday";
        
        // Test case 1: Method call with varying parameters
        boolean isPositive = checkPositive(number);
        System.out.println("Is number positive? " + isPositive);

        // Test case 2: Method call with conditions
        printDay(day);

        // Test case 3: Method call with multiple conditions
        String result = analyzeNumber(number);
        System.out.println(result);

        // End measuring time
        // long endTime = System.nanoTime();
        // long duration = (endTime - startTime) / 1000000; // Convert nanoseconds to milliseconds

        // System.out.println("Time taken: " + duration + " milliseconds");
    }

    // Method definition for test case 1
    public static boolean checkPositive(int num) {
        return num > 0;
    }

    // Method definition for test case 2
    public static void printDay(String day) {
        switch (day) {
            case "Monday":
                System.out.println("Today is Monday");
                break;
            case "Tuesday":
                System.out.println("Today is Tuesday");
                break;
            default:
                System.out.println("Other day");
        }
    }

    // Method definition for test case 3
    public static String analyzeNumber(int num) {
        if (num > 0) {
            return "Number is positive";
        } else if (num < 0) {
            return "Number is negative";
        } else {
            return "Number is zero";
        }
    }
}
