package zabud.commands.impl;

import java.util.List;

import zabud.commands.Buildable;
import zabud.commands.Command;
import zabud.commands.ParsedToken;
import zabud.commands.Parser;
import zabud.commands.TaskCommand;
import zabud.commands.Token;
import zabud.commands.Validatable;
import zabud.commands.tokens.DateTimeToken;
import zabud.session.Session;
import zabud.tasks.Deadline;

/**
 * Validates and builds commands that add deadline tasks.
 */
public final class DeadlineCommand implements Validatable, Buildable {
    /**
     * The user input that selects this command type.
     */
    public static final String COMMAND = "deadline";
    /**
     * Name of the token introducing the deadline value.
     */
    private static final String BY_TOKEN = "by";

    /**
     * Creates a deadline command handler.
     */
    public DeadlineCommand() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(List<ParsedToken> tokens, Session session) {
        return Validatable.hasTokenNames(tokens, Parser.DEFAULT_TOKEN, BY_TOKEN)
                && !tokens.getFirst().value().isBlank()
                && new DateTimeToken(BY_TOKEN, tokens.get(1).value()).isValid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hint() {
        DateTimeToken by = new DateTimeToken(BY_TOKEN, "");
        return Validatable.formatHint(COMMAND + " " + DESCRIPTION + " " + Token.composeHints(by),
                DESCRIPTION_REQUIREMENT, Token.composeRequirements(by));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command build(List<ParsedToken> tokens, Session session) {
        String description = tokens.getFirst().value();
        DateTimeToken by = new DateTimeToken(BY_TOKEN, tokens.get(1).value());
        return new TaskCommand(session) {
            @Override
            public void execute() {
                addTask(new Deadline(description, by.getDate(), by.getTime()));
            }
        };
    }
}
