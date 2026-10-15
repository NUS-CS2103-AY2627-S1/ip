package waddles;

import javafx.application.Application;

/**
 * A launcher class whose only job is to call {@link Application#launch}.
 *
 * <p>JavaFX's {@code Application} class does some classpath inspection at
 * startup that can misbehave when the application's main class itself
 * extends {@code Application} (e.g. when run from a shaded/fat jar). Using
 * a separate, unrelated class as the actual entry point avoids that problem
 * — this is a common gotcha, so it is kept as its own class rather than
 * merged into {@link Main}.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
