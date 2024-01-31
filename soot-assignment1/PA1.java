public class PA1 {
    int f1,f2;
    public static void main(String[] args) {
        
    }

    public static void tc1printSum() {
        int a = 10, b = 20;
        int c = a+b;
        System.out.println("Test case 1! Sum is " + c);
        System.out.println(a+b);
    } 

    public static int tc2max(int a, int b, int c) {
        if(a > b){
            return a > c ? a : c;
        }
        else{
            return b > c ? b : c;
        }
    } 

    public static void tc3loop(int n) {
        int i;
        for (i = 0; i < n; i++) {
            System.out.println(i*2);
        }
        System.out.println(i*2);
    } 

    public static void tc4array(int n) {
        int arr[];
        arr = new int[n];
        for (int i : arr) {
            System.out.println(arr[i]);
        }
    } 

    public int tc5recursion(int n){
        if(n == 0)
            return 1;
        return tc5recursion(n-1) + tc5recursion(n-2);
    }
}