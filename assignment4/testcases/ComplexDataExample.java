import java.util.*;

public class ComplexDataExample {
    public static void main(String[] args) {
        // Start measuring time
        //long startTime = System.nanoTime();

        // Test case 1: Array manipulation
        int[] numbers = {1, 2, 3, 4, 5};
        int sum = calculateSum(numbers);
        System.out.println("Sum of numbers: " + sum);

        // Test case 2: List manipulation
        List<String> fruits = new ArrayList<>();
        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add("Orange");
        displayFruits(fruits);

        // Test case 3: Map manipulation
        Map<Integer, String> students = new HashMap<>();
        students.put(1, "Alice");
        students.put(2, "Bob");
        students.put(3, "Charlie");
        printStudentNames(students);

        // End measuring time
        //long endTime = System.nanoTime();
        //long duration = (endTime - startTime) / 1000000; // Convert nanoseconds to milliseconds

        //System.out.println("Time taken: " + duration + " milliseconds");
    }

    // Method definition for test case 1
    public static int calculateSum(int[] arr) {
        int sum = 0;
        for (int num : arr) {
            sum += num;
        }
        return sum;
    }

    // Method definition for test case 2
    public static void displayFruits(List<String> list) {
        for (String fruit : list) {
            System.out.println("Fruit: " + fruit);
        }
    }

    // Method definition for test case 3
    public static void printStudentNames(Map<Integer, String> map) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.println("Student ID: " + entry.getKey() + ", Name: " + entry.getValue());
        }
    }
}
