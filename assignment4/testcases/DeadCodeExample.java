import java.util.*;

public class DeadCodeExample {
    public static void main(String[] args) {
        // Start measuring time
        //long startTime = System.nanoTime();

        // Test case 1: Variables not always used
        int a = 10;
        int b = 20;
        int c = 30;
        int result = sum(a, b);
        System.out.println("Result of sum: " + result);

        // Test case 2: Complex data structure with unused variables
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            numbers.add(i);
        }
        int sumOfList = calculateSumOfList(numbers);
        System.out.println("Sum of list: " + sumOfList);

        // End measuring time
        // long endTime = System.nanoTime();
        // long duration = (endTime - startTime) / 1000000; // Convert nanoseconds to milliseconds

        // System.out.println("Time taken: " + duration + " milliseconds");
    }

    // Method definition for test case 1
    public static int sum(int x, int y) {
        int temp = x + y;
        return temp;
    }

    // Method definition for test case 2
    public static int calculateSumOfList(List<Integer> list) {
        int sum = 0;
        for (int num : list) {
            sum += num;
        }
        return sum;
    }
}
