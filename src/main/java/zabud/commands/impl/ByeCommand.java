package zabud.commands.impl;

import java.util.List;

import zabud.commands.Buildable;
import zabud.commands.Command;
import zabud.commands.ParsedToken;
import zabud.commands.Parser;
import zabud.commands.Validatable;
import zabud.session.Session;

/**
 * Validates and builds commands that end the current session.
 */
public final class ByeCommand implements Validatable, Buildable {
    /**
     * The user input that selects this command type.
     */
    public static final String COMMAND = "bye";

    /**
     * Creates a bye command handler.
     */
    public ByeCommand() {
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
        return " Use 'bye'.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command build(List<ParsedToken> tokens, Session session) {
        return new Command(session) {
            @Override
            public void execute() {
                System.out.println("Bye, King Solomon. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
            }

            @Override
            protected boolean exitsApplication() {
                return true;
            }
        };
    }
}
