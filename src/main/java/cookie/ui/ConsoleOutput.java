package cookie.ui;

/** Writes Cookie's messages to the console between separator lines. */
public class ConsoleOutput implements Output {
    /** Separates Cookie's messages in the console. */
    private static final String SEPARATOR = "____________________________________________________________";

    /** Writes one complete message to the console. */
    @Override
    public void show(String message) {
        System.out.println(SEPARATOR);
        System.out.println(message);
        System.out.println(SEPARATOR);
    }
}
