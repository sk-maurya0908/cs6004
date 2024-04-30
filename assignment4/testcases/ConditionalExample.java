public class ConditionalExample {
    public static void main(String[] args) {
        // Start measuring time
        //long startTime = System.nanoTime();

        int number = 10;
        String day = "Monday";
        boolean isWeekend = false;
        
        // Conditional statement 1: if-else
        if (number > 0) {
            System.out.println("Number is positive");
        } else {
            System.out.println("Number is non-positive");
        }
        
        // Conditional statement 2: switch-case
        switch (day) {
            case "Monday":
                System.out.println("Today is Monday");
                break;
            case "Tuesday":
                System.out.println("Today is Tuesday");
                break;
            case "Wednesday":
                System.out.println("Today is Wednesday");
                break;
            default:
                System.out.println("Today is not Monday, Tuesday, or Wednesday");
        }
        
        // Conditional statement 3: nested if-else
        if (number > 0) {
            if (number % 2 == 0) {
                System.out.println("Number is positive and even");
            } else {
                System.out.println("Number is positive and odd");
            }
        } else if (number < 0) {
            System.out.println("Number is negative");
        } else {
            System.out.println("Number is zero");
        }

        // Conditional statement 4: if-else with additional condition
        if (day.equals("Saturday") || day.equals("Sunday")) {
            System.out.println("It's the weekend!");
            isWeekend = true;
        } else {
            System.out.println("It's a weekday.");
            isWeekend = false;
        }

        // End measuring time
        // long endTime = System.nanoTime();
        // long duration = (endTime - startTime) / 1000000; // Convert nanoseconds to milliseconds

        // System.out.println("Time taken: " + duration + " milliseconds");
    }
}
