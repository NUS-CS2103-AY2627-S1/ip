package zhangwei.gui;

import javafx.application.Application;

/**
 * Starts the graphical version of ZhangWei.
 *
 * <p>This class exists only so that the class holding {@code main} is not
 * itself an {@link Application}. When it is, the Java launcher checks for the
 * JavaFX modules on the module path before running a single line, and a
 * self-contained JAR that carries JavaFX on the class path is rejected with
 * "JavaFX runtime components are missing". Going through a plain class avoids
 * that check, so the JAR runs anywhere.
 */
public class Launcher {

    /** Prevents instantiation: this class is only a home for {@code main}. */
    private Launcher() {
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command line arguments, passed on to JavaFX unchanged.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
