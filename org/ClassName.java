public class ClassName {
	public static void main(String[] args) {
		System.out.println("Main > ");
		Thread t = new Thread("MyThread");
		t.start();
		try {
			t.join();
		} catch (Exception e) {
			System.out.println("Error: " + e.message());
		}
		System.out.println("Main f");
	}
}
