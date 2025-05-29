package main;

import java.util.Scanner;
import java.util.concurrent.*;

public class Vigenere {
    private final int[] keyShifts;

    // Конструктор принимает ключевое слово
    public Vigenere(String key) {
        this.keyShifts = new int[key.length()];
        for (int i = 0; i < key.length(); i++) {
            this.keyShifts[i] = Character.toUpperCase(key.charAt(i)) - 'A';
        }
    }

    // Метод шифрования
    public String encrypt(String plainText) {
        return transform(plainText, true);
    }

    // Метод дешифрования
    public String decrypt(String cipherText) {
        return transform(cipherText, false);
    }

    private String transform(String input, boolean encrypt) {
        StringBuilder output = new StringBuilder();
        int keyIndex = 0;
        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shift = keyShifts[keyIndex % keyShifts.length];
                if (!encrypt) {
                    shift = 26 - shift; // Обратный сдвиг для дешифрования
                }
                char transformed = (char) ((c - base + shift) % 26 + base);
                output.append(transformed);
                keyIndex++;
            } else {
                output.append(c); // Не-буквы остаются без изменений
            }
        }
        return output.toString();
    }

    // Вложенный класс для многопоточных задач
    private static class CipherTask implements Callable<String> {
        private final Vigenere cipher;
        private final String text;
        private final boolean encrypt;

        public CipherTask(Vigenere cipher, String text, boolean encrypt) {
            this.cipher = cipher;
            this.text = text;
            this.encrypt = encrypt;
        }

        @Override
        public String call() {
            return encrypt ? cipher.encrypt(text) : cipher.decrypt(text);
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // Создаем несколько экземпляров шифратора
        Vigenere cipher1 = new Vigenere("money");
        Vigenere cipher2 = new Vigenere("java");
        Vigenere cipher3 = new Vigenere("true");

        // Создаем ExecutorService
        ExecutorService executor = Executors.newFixedThreadPool(3);

        System.out.print("Введите первое слово которое нужно зашифровать: ");
        Future<String> future1 = executor.submit(new CipherTask(cipher1, input.next(), true));

        System.out.print("Введите второе слово которое нужно зашифровать: ");
        Future<String> future2 = executor.submit(new CipherTask(cipher2, input.next(), true));

        System.out.print("Введите третье слово которое нужно зашифровать: ");
        Future<String> future3 = executor.submit(new CipherTask(cipher3, input.next(), true));

        // Получаем результаты
        try {
            System.out.println("Результат Thread-1: " + future1.get());
            System.out.println("Результат Thread-2: " + future2.get());
            System.out.println("Результат Thread-3: " + future3.get());

            // Дешифровка
            System.out.print("Введите значения для дешифровки первого слова: ");
            Future<String> decryptFuture = executor.submit(new CipherTask(cipher1, input.next(), false));
            System.out.println("Результат дешифровки: " + decryptFuture.get());

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Ошибка выполнения задачи: " + e.getMessage());
        } finally {
            executor.shutdown(); // Завершаем работу пула потоков
        }

        input.close();
    }
}