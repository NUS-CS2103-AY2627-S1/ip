package zinc.ui;

import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/** Parses and executes commands that configure Zinc's user interface. */
public class UiCommandHandler {
    /** The UI used to store the selected background and display command messages. */
    private final Ui ui;

    /** UI subcommands indexed by their user-facing names. */
    private final Map<String, Consumer<String>> commands;

    /** Creates a handler that updates the supplied UI. */
    public UiCommandHandler(Ui ui) {
        assert ui != null : "UI command dependencies must not be null";
        this.ui = ui;
        this.commands = createCommands();
    }

    /** Parses and executes a UI subcommand. */
    public void execute(String parameters) {
        assert parameters != null : "UI command input must not be null";
        String[] commandParts = parameters.split("\\s+", 2);
        String subcommand = commandParts.length > 0 ? commandParts[0].toLowerCase(Locale.ROOT) : "";
        String arguments = commandParts.length > 1 ? commandParts[1].strip() : "";

        Consumer<String> selectedCommand = commands.get(subcommand);
        if (selectedCommand == null) {
            ui.printUiUsage();
            return;
        }
        selectedCommand.accept(arguments);
    }

    /** Creates the command registry used to dispatch UI operations. */
    private Map<String, Consumer<String>> createCommands() {
        return Map.of(
                "background", this::setBackground,
                "bg", this::setBackground);
    }

    /** Validates and stores the requested background type. */
    private void setBackground(String arguments) {
        if (arguments.isBlank() || arguments.split("\\s+").length != 1) {
            ui.printUiUsage();
            return;
        }

        BackgroundType backgroundType;
        try {
            backgroundType = BackgroundType.valueOf(arguments.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            ui.printUiUsage();
            return;
        }

        ui.setBackgroundType(backgroundType);
        ui.printBackgroundChanged(backgroundType);
    }
}
