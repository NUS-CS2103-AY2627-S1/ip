package xiaozhi.gui;

import javafx.application.Application;

/**
 * Launches the XiaoZhi GUI.
 * <p>
 * A separate entry point from {@link Main} works around a classpath issue
 * where launching an {@code Application} subclass directly as the main
 * class can fail to find the JavaFX runtime modules.
 */
public class Launcher {

    /**
     * Starts the XiaoZhi GUI application.
     *
     * @param args Command-line arguments, passed through to {@link Application#launch}.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
