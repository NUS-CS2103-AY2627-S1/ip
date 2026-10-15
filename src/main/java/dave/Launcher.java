package dave;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {

    /**
     * Entry point for running Dave with either CLI or GUI mode.
     *
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        if (args.length > 0 && (args[0].equalsIgnoreCase("--text")
                || args[0].equalsIgnoreCase("-cli")
                || args[0].equalsIgnoreCase("--cli"))) {
            Dave.main(args);
        } else {
            Application.launch(Main.class, args);
        }
    }
}
