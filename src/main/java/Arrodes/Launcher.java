package arrodes;

import javafx.application.Application;

/** Starts Arrodes in GUI mode by default or CLI mode when requested. */
public class Launcher {
    /** Argument that explicitly selects the command-line interface. */
    private static final String CLI_ARGUMENT = "--cli";
    /** Argument that explicitly selects the graphical user interface. */
    private static final String GUI_ARGUMENT = "--gui";
    /** Message shown when the launcher receives an unsupported argument. */
    private static final String USAGE_MESSAGE = "Usage: java -jar arrodes.jar [--cli|--gui]";

    /** Prevents instantiation of this utility class. */
    private Launcher() {
    }

    /**
     * Starts Arrodes using the requested user interface.
     *
     * @param args optional interface selection argument
     */
    public static void main(String... args) {
        if (isGuiMode(args)) {
            Application.launch(Arrodes.class, args);
            return;
        }
        if (isCliMode(args)) {
            new Arrodes().run();
            return;
        }
        showUsageMessage();
    }

    /** Returns whether the arguments select the GUI, including the default case. */
    private static boolean isGuiMode(String[] args) {
        return args != null && (args.length == 0 || args.length == 1 && GUI_ARGUMENT.equals(args[0]));
    }

    /** Returns whether the arguments select the CLI. */
    private static boolean isCliMode(String[] args) {
        return args != null && args.length == 1 && CLI_ARGUMENT.equals(args[0]);
    }

    /** Displays the supported launcher arguments. */
    private static void showUsageMessage() {
        System.err.println(USAGE_MESSAGE);
    }
}
