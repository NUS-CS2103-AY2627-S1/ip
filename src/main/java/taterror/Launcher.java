package taterror;

/**
 * Workaround entry point for running the JavaFX GUI. Some JDK/module-path setups
 * throw "JavaFX runtime components are missing" when a {@code javafx.application
 * .Application} subclass ({@link Main}) is used directly as the run/jar main
 * class; delegating through a plain class like this one avoids that check.
 * This is the class configured as the run target and jar main class.
 */
public class Launcher {
    /**
     * Delegates straight to {@link Main#main(String[])}.
     *
     * @param args forwarded to {@link Main#main(String[])}
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
