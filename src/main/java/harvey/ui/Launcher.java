package harvey.ui;

import javafx.application.Application;
/**
 * Starts the graphical interface.
 * <p>
 * This class exists only so that the class holding {@code main} is not itself an
 * {@link Application}. Launching an {@code Application} subclass directly makes the Java
 * runtime insist that the JavaFX modules be on the module path, which they are not inside
 * a shaded JAR. Going through a plain class avoids that check, so {@code harvey.jar} runs
 * on a machine with no JavaFX installed.
 */
public class Launcher {
    /**
     * Starts the application.
     *
     * @param args passed straight on to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
