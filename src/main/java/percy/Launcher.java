package percy;

import javafx.application.Application;

/**
 * A launcher class that works around a JavaFX limitation: when the {@code main}
 * class is itself an {@link Application} subclass, launching from a shaded JAR
 * fails with "JavaFX runtime components are missing". Starting from this plain
 * class instead avoids that.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
