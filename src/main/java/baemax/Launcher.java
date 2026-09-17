package baemax;

import javafx.application.Application;

/**
 * The GUI entry point. A separate launcher class works around a JavaFX
 * classpath issue that prevents launching {@link Main} directly.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
