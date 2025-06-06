import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

class Test{
    public static void main(String[] args ) throws UnsupportedEncodingException
    {
        System.out.println("Hello world");
        int number = 2024;
        double nubmerDouble = number;
        System.out.println("double = " + nubmerDouble);
    }
}

class Ifsample {
    public static void main(String[] args) throws UnsupportedEncodingException
    {
        System.setOut(new PrintStream(System.out, true, "cp866"));

        int x = 10;
        int y = 20;
        if(x < y) System.out.println("x меньше чем y");
        x *= 2;
        if(x == y) System.out.println("x равен y");
        x *= 2;
        if(x > y) System.out.println("x  y");
     }
}

class BoolTest {
    public static void main(String args[]) {
        boolean b;
		
        b = false;
        System.out.println("b = " + b);
        b = true;
        System.out.println("b = " + b);
        if(b) System.out.println("This code run");
        b = false;
        if(b) System.out.println("This code not run");

        //Сравнения типа boolean
        System.out.println("10 > 9 equal " + (10 > 9));
    }
}

class Conversion {
    public static void main(String args[]) {
        byte b;
        int i = 257;
        double d = 242.322;
        System.out.println("\nConversion int to byte");

        b = (byte)i;
        System.out.println("i and b " + i + " " + b);

        System.out.println("\nConverison type double to int");
        i = (int)d;
        System.out.println("i and d " + i + " " + d);

        System.out.println("\nConversion double to byte");
        b = (byte)d;
        System.out.println("b and d " + b + " " + d);
    }
}

class Promote {
    public static void main(String args[]) {
        byte b = 42;
        char c = 'c';
        short s = 1024;
        int i = 50000;
        float f = 5.57f;
        double d = .4343;
        double result = (f * b ) + ( i / c ) - ( d * s );
        System.out.println((f * b) + (i / c) - (d *s) );
        System.out.println("Result = " + result);
    }
}

class Summ {
    public static void main(String[] args) {
        int i = 10; 
        int sum = 5 * i; 
        int sigma = 0; 

        for (int j = 1; j <= 10; j++) {
            sigma += j;
        }

        sum += sigma;
        System.out.println("s = " + sum);
    }
}

class InfinitesSumWhile {
    public static void main(String[] args) {
        double sum = 0.0;
        int x = 1;
        int maxIterations = 10000; 

        while (x <= maxIterations) {
            sum += Math.sin(x) / (x * x + 5 * x);
            x++;
        }

        System.out.println("Приближённая сумма ряда: " + sum);
    }
}

class InfiniteSumDoWhile {
    public static void main(String[] args) {
        double sum = 0.0;
        int x = 1;
        int maxIterations = 10000;
        
        do {
            sum += Math.sin(x) / (x * x + 5 * x);
            x++;
        } while (x <= maxIterations);

        System.out.println("Приближённая сумма ряда: " + sum);
    }
}


class SumMultiplesOfFive {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
		System.out.print("Введите количество чисел: ");
        int count = scanner.nextInt();
        int sum = 0;
        
        System.out.println("Введите числа:");
        for (int i = 0; i < count; i++) {
            int number = scanner.nextInt();
            if (number % 5 == 0) {
                sum += number;
            }
        }

        System.out.println("Сумма чисел, кратных 5: " + sum);
        scanner.close();
    }
}
