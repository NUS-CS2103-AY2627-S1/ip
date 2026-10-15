package elora;

/**
 * A launcher for the JavaFX GUI, kept separate from {@link Main}. Running
 * an executable jar whose declared main class extends
 * {@code javafx.application.Application} directly can fail with a
 * "JavaFX runtime components are missing" error on some setups; going
 * through a plain class avoids that.
 */
public class Launcher {

    /**
     * Launches the JavaFX GUI.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
