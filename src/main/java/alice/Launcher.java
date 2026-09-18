package alice;

/**
 * A plain-Java entry point that hands off to {@link Main}.
 *
 * <p>This indirection exists because of a JavaFX packaging quirk: when the
 * application is bundled into a single runnable JAR (see the
 * {@code shadowJar} task), running {@code java -jar alice.jar} with a main
 * class that itself extends {@link javafx.application.Application} makes
 * the JVM incorrectly report that "JavaFX runtime components are missing".
 * Launching from a separate class with an ordinary {@code main} method
 * avoids that check, while {@link Main} still does the real JavaFX setup.
 */
public class Launcher {
    /**
     * Starts the Alice GUI application.
     *
     * @param args command-line arguments, forwarded to {@link Main}.
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
