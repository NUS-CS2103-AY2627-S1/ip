package ben;

import javafx.application.Application;

/**
 * Starts the JavaFX GUI.
 * <p>
 * Level-10: when the app is packaged as a fat JAR, the JavaFX runtime
 * refuses to launch if the {@code main} class is itself a subclass of
 * {@link Application}. Putting {@code main} in this plain class, which
 * then calls {@link Application#launch}, sidesteps that restriction.
 */
public class Launcher {
    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments, passed straight through to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
