package minweeder.gui;

import javafx.application.Application;

/**
 * Entry point invoked by the shadow jar. Launches {@link Main} indirectly
 * rather than extending {@link Application} itself, working around a
 * classpath issue where running a JavaFX {@code Application} class directly
 * from a fat jar fails because the JavaFX modules aren't on the module path.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
