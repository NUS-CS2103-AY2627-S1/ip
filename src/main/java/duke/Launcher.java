package duke;

import javafx.application.Application;

/**
 * A launcher class that works around classpath issues when running the JAR.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
