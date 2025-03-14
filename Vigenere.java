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
    private static class CipherTask implements Runnable {
        private final Vigenere cipher;
        private final String text;
        private final boolean encrypt;

        public CipherTask(Vigenere cipher, String text, boolean encrypt) {
            this.cipher = cipher;
            this.text = text;
            this.encrypt = encrypt;
        }

        @Override
        public void run() {
            String result = encrypt ? cipher.encrypt(text) : cipher.decrypt(text);
            System.out.println(Thread.currentThread().getName() + " result: " + result);
        }
    }

    public static void main(String[] args) {
        // Создаем несколько экземпляров шифратора
        Vigenere cipher1 = new Vigenere("LEMON");
        Vigenere cipher2 = new Vigenere("JAVA");
        Vigenere cipher3 = new Vigenere("THREAD");

        // Создаем потоки
        Thread thread1 = new Thread(new CipherTask(cipher1, "ATTACKATDAWN", true), "Thread-1");
        Thread thread2 = new Thread(new CipherTask(cipher2, "HELLOWORLD", true), "Thread-2");
        Thread thread3 = new Thread(new CipherTask(cipher3, "MULTITHREADING", true), "Thread-3");
		thread3.setPriority(Thread.MAX_PRIORITY);
		
        // Запускаем потоки
        thread1.start();
        thread2.start();
        thread3.start();

        // Для демонстрации дешифрования
        try {
            thread1.join();
            new Thread(new CipherTask(cipher1, "LXFOPVEFRNHR", false), "Decrypt-1").start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
