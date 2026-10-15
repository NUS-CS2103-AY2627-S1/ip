package stephen.helloworld;

import javafx.application.Application;

/** Launches the isolated JavaFX setup verification application. */
public class HelloWorldLauncher {
    /**
     * Starts the JavaFX runtime without making the launcher itself a JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String... args) {
        Application.launch(HelloWorldApplication.class, args);
    }
}
