package zabud.commands.impl;

import java.util.List;

import zabud.commands.Buildable;
import zabud.commands.Command;
import zabud.commands.ParsedToken;
import zabud.commands.Parser;
import zabud.commands.Token;
import zabud.commands.Validatable;
import zabud.commands.tokens.DateTimeToken;
import zabud.session.Session;

/**
 * Validates and builds commands that display usage guidance.
 */
public final class HelpCommand implements Validatable, Buildable {
    /**
     * The user input that selects this command type.
     */
    public static final String COMMAND = "help";

    /**
     * Creates a help command handler.
     */
    public HelpCommand() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(List<ParsedToken> tokens, Session session) {
        return Validatable.hasTokenNames(tokens, Parser.DEFAULT_TOKEN)
                && tokens.getFirst().value().isBlank();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hint() {
        return " Use 'help'.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command build(List<ParsedToken> tokens, Session session) {
        return new Command(session) {
            @Override
            public void execute() {
                printHelp();
            }
        };
    }

    /**
     * Prints all available command syntax and input requirements.
     */
    private static void printHelp() {
        DateTimeToken by = new DateTimeToken("by", "");
        DateTimeToken from = new DateTimeToken("from", "");
        DateTimeToken to = new DateTimeToken("to", "");
        System.out.println(" Available commands:");
        System.out.println("   todo " + DESCRIPTION);
        System.out.println("   deadline " + DESCRIPTION + " " + Token.composeHints(by));
        System.out.println("   event " + DESCRIPTION + " " + Token.composeHints(from, to));
        System.out.println("   list");
        System.out.println("   find " + DESCRIPTION);
        System.out.println("   mark " + TASK_NUMBER);
        System.out.println("   unmark " + TASK_NUMBER);
        System.out.println("   delete " + TASK_NUMBER);
        System.out.println("   help");
        System.out.println("   bye");
        System.out.println();
        System.out.println(" Details:");
        System.out.println(DESCRIPTION_REQUIREMENT);
        System.out.println(TASK_NUMBER_REQUIREMENT);
        System.out.println(Token.composeRequirements(by, from, to));
    }
}
