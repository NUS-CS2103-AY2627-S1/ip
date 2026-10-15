package zabud.commands.impl;

import java.util.List;

import zabud.commands.Buildable;
import zabud.commands.Command;
import zabud.commands.ParsedToken;
import zabud.commands.Validatable;
import zabud.session.Session;

/**
 * Validates and builds the fallback for unsupported command names.
 */
public final class UnknownCommand implements Validatable, Buildable {
    /**
     * Creates an unknown-command handler.
     */
    public UnknownCommand() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(List<ParsedToken> tokens, Session session) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hint() {
        return " Unknown command. Use 'help' to see available commands.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command build(List<ParsedToken> tokens, Session session) {
        throw new IllegalStateException("Unsupported commands cannot be built");
    }
}
