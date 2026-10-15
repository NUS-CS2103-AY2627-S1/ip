package gooble.command;

/** Displays a guide to Gooble's available commands. */
public class HelpCommand extends Command {
    /** Creates a help command from complete user input. */
    public HelpCommand(String input) {
        super(input);
    }

    @Override
    public void execute(CommandContext context) {
        String topic = context.parser().argumentAfter(input, "help");
        context.ui().showHelp(topic);
    }
}
