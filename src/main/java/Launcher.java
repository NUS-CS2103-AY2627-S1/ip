import javafx.application.Application;

/**
 * Provides the JAR entry point without being a JavaFX {@link Application}.
 *
 * <p>Keeping the manifest entry point separate prevents the Java launcher from
 * trying to start JavaFX before the JavaFX runtime packaged in the fat JAR is
 * available on the class path.</p>
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts Mono's JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Mono.class, args);
    }
}
