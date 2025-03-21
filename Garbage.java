import java.util.Scanner;

public class Garbage {
    public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter value: ");
		int value = input.nextInt();
		for (int i = 14990; i < value; i++) {
			Trash[] trash3 = new Trash[i];
			Trash[] trash1 = new Trash[i * i];
			Trash[] trash2 = new Trash[i * i];
			Trash[] trash4 = new Trash[i * i];
			Trash[] trash5 = new Trash[i * i];
			System.out.println(i);
		}
	}
}

class Trash {
	double[] number = new double[1050000000];
	double[] number1 = new double[1050000000];
	double[] number2 = new double[1050000000];
	double[] number3 = new double[1000000000];
}
