package jiji;

import javafx.application.Application;
import jiji.gui.Main;

/**
 * A launcher class to workaround classpath and module issues when launching JavaFX.
 * <p>
 * Follows the standard JavaFX packaging workaround documented in SE-EDU guides.
 */
public class Launcher {

    /**
     * Main application entry point that delegates to JavaFX Application launch
     * or CLI mode if requested.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        if (args != null && args.length > 0 && "--cli".equalsIgnoreCase(args[0])) {
            Jiji.main(args);
        } else {
            Application.launch(Main.class, args);
        }
    }
}
