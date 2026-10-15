package zabud.commands.impl;

import java.time.LocalTime;
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
import zabud.tasks.Event;

/**
 * Validates and builds commands that add event tasks.
 */
public final class EventCommand implements Validatable, Buildable {
    /**
     * The user input that selects this command type.
     */
    public static final String COMMAND = "event";
    /**
     * Name of the token introducing the event start.
     */
    private static final String FROM_TOKEN = "from";
    /**
     * Name of the token introducing the event end.
     */
    private static final String TO_TOKEN = "to";
    /**
     * Requirement shown when an event's time range is invalid.
     */
    private static final String RANGE_REQUIREMENT =
            " - Event range: /to must be later than /from, and both values must include dates or both omit them.";

    /**
     * Creates an event command handler.
     */
    public EventCommand() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(List<ParsedToken> tokens, Session session) {
        if (!Validatable.hasTokenNames(tokens, Parser.DEFAULT_TOKEN, FROM_TOKEN, TO_TOKEN)
                || tokens.getFirst().value().isBlank()) {
            return false;
        }
        DateTimeToken from = new DateTimeToken(FROM_TOKEN, tokens.get(1).value());
        DateTimeToken to = new DateTimeToken(TO_TOKEN, tokens.get(2).value());
        return from.isValid() && to.isValid() && endsAfterStart(from, to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hint() {
        DateTimeToken from = new DateTimeToken(FROM_TOKEN, "");
        DateTimeToken to = new DateTimeToken(TO_TOKEN, "");
        return Validatable.formatHint(COMMAND + " " + DESCRIPTION + " " + Token.composeHints(from, to),
                DESCRIPTION_REQUIREMENT, Token.composeRequirements(from, to), RANGE_REQUIREMENT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command build(List<ParsedToken> tokens, Session session) {
        String description = tokens.getFirst().value();
        DateTimeToken from = new DateTimeToken(FROM_TOKEN, tokens.get(1).value());
        DateTimeToken to = new DateTimeToken(TO_TOKEN, tokens.get(2).value());
        return new TaskCommand(session) {
            @Override
            public void execute() {
                addTask(new Event(description, from.getDate(), from.getTime(), to.getDate(), to.getTime()));
            }
        };
    }

    /**
     * Checks that comparable event endpoints form a strictly increasing range.
     */
    private static boolean endsAfterStart(DateTimeToken from, DateTimeToken to) {
        if ((from.getDate() == null) != (to.getDate() == null)) {
            return false;
        }
        if (from.getDate() == null) {
            return to.getTime().isAfter(from.getTime());
        }
        LocalTime fromTime = from.getTime() == null ? LocalTime.MIN : from.getTime();
        LocalTime toTime = to.getTime() == null ? LocalTime.MIN : to.getTime();
        return to.getDate().atTime(toTime).isAfter(from.getDate().atTime(fromTime));
    }
}
