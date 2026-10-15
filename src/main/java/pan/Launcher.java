package pan;

import javafx.application.Application;

/**
 * A tiny launcher that starts the JavaFX application.
 *
 * <p>When JavaFX is on the classpath rather than the module path - which is the
 * case for the fat JAR this project builds - starting a class that
 * {@code extends Application} directly fails with "JavaFX runtime components are
 * missing". Launching through this class, which does <em>not</em> extend
 * {@link Application}, sidesteps that. {@code build.gradle} sets
 * {@code mainClass} to {@code pan.Launcher}.
 */
public class Launcher {

    /** Hands control to the JavaFX runtime, which then calls {@link Main#start}. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}