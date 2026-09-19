package leo;

import javafx.application.Application;

/**
 * A thin launcher for the JavaFX GUI, kept separate from {@link Main}.
 *
 * <p>When a class that directly extends {@code javafx.application.Application}
 * is run from a plain runnable jar (as {@code A-Jar} sets this project up to
 * produce), some JavaFX installations fail to detect the JavaFX runtime on
 * the classpath and refuse to start. Calling {@link Application#launch}
 * from a separate class that does <em>not</em> itself extend
 * {@code Application} avoids that check entirely - this is the workaround
 * recommended by the SE-EDU JavaFX tutorial. Launcher has no other job:
 * it just hands control straight to {@link Main}.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
