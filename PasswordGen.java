import java.util.Random;
import java.util.Scanner;

public class PasswordGen {

	public String generatePassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
		
        return password.toString();
    }
	
    public static void main(String[] args) {
		PasswordGen gen = new PasswordGen();
		Scanner input = new Scanner(System.in);

		System.out.print("Enter lenght password: ");
		int lenght = input.nextInt();

		String password = gen.generatePassword(lenght);
		System.out.println("\nGen password: " + password);
	}
}
