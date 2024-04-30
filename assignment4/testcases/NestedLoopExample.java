public class NestedLoopExample {
    public static void main(String[] args) {
        // Start measuring time
        //long startTime = System.nanoTime();

        int[] numbers = {1, 2, 3, 4, 5};
        
        // Outer loop
        for (int i = 0; i < numbers.length; i++) {
            System.out.println("Outer loop iteration: " + i);
            
            // Inner loop 1 - decreasing loop count
            for (int j = 5; j > 0; j--) {
                System.out.println("  Inner loop 1 iteration: " + j);
                // Some computation or code here
            }
            
            // Inner loop 2 - increasing loop count
            for (int k = 0; k < i; k++) {
                System.out.println("  Inner loop 2 iteration: " + k);
                // Some computation or code here
            }
            
            // Inner loop 3 - loop with condition based on index
            for (int m = 0; m < numbers[i]; m++) {
                System.out.println("  Inner loop 3 iteration: " + m);
                // Some computation or code here
            }
        }

        // End measuring time
        // long endTime = System.nanoTime();
        // long duration = (endTime - startTime) / 1000000; // Convert nanoseconds to milliseconds

        // System.out.println("Time taken: " + duration + " milliseconds");
    }
}
