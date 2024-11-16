import java.util.Scanner;

class SumMultiplesOfFive {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
		System.out.print("Введите количество чисел: ");
        int count = scanner.nextInt();
        int sum = 0;
		int countSumMultiples = 0;
        
        System.out.println("Введите числа:");
        for (int i = 0; i < count; i++) {
			int number = scanner.nextInt();
			if(number >= 300){
				System.out.println("Введённое число превышает 300");
				System.exit(0);

			}
		   
			if (number % 5 == 0) {
                sum += number; 
            }

		}
        System.out.println("Сумма чисел, кратных 5: " + sum + "\nКоличество чисел кратных 5 = " + countSumMultiples);
        scanner.close();
    }
}
