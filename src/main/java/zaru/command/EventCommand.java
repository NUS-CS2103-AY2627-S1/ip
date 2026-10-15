package zaru.command;

import java.util.Set;

import zaru.exception.ZaruException;
import zaru.task.Event;
import zaru.task.TaskList;

/** Executes the {@code event} command. */
public class EventCommand extends Command {
    private final String description;
    private final String from;
    private final String to;

    /**
     * Creates an event command.
     *
     * @param description Description of the event task.
     * @param from Event start time entered after {@code /from}.
     * @param to Event end time entered after {@code /to}.
     */
    public EventCommand(String description, String from, String to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the command word represented by this command.
     *
     * @return The {@code event} command word.
     */
    @Override
    public String getCommand() {
        return "event";
    }

    /**
     * Returns the slash-prefixed arguments accepted by an event command.
     *
     * @return A set containing {@code from} and {@code to}.
     */
    @Override
    public Set<String> getAllowedKeyedArguments() {
        return Set.of("from", "to");
    }

    /**
     * Validates and adds the event task, then returns the response message.
     *
     * @param tasks Current task list.
     * @return Task-added response message.
     * @throws ZaruException If required text or either event time is invalid.
     */
    @Override
    public String execute(TaskList tasks) throws ZaruException {
        validateDescription(description, "The description of an event cannot be empty.");
        validateNonEmpty(from, "Please provide an event start time using /from.");
        validateNonEmpty(to, "Please provide an event end time using /to.");
        return addTaskAndCreateResponse(tasks, new Event(description, from, to));
    }
}
