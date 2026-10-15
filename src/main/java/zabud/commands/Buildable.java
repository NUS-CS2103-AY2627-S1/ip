package zabud.commands;

import java.util.List;

import zabud.session.Session;

/**
 * Builds an executable command from validated parser output.
 */
public interface Buildable {
    /**
     * Builds a command from values that have already passed validation.
     *
     * @param tokens validated values supplied after the command name.
     * @param session current application session.
     * @return executable command containing the validated input.
     */
    Command build(List<ParsedToken> tokens, Session session);
}
