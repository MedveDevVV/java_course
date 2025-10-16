// Выводит на экран случайно сгенерированное трёхзначное натуральное число и сумму его цифр.

public class Main {
    public static void main(String[] args) {
        final int MIN = 100;
        final int MAX = 999;

        int a = (new java.util.Random()).nextInt(MAX - MIN + 1) + MIN;
        System.out.println(a);

        int sum = 0;
        for (char i : String.valueOf(a).toCharArray()){
            sum += Character.getNumericValue(i);
        }

        System.out.println(sum);
    }
}