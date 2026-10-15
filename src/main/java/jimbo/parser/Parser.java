package jimbo.parser;

import jimbo.exception.JimboException;
import jimbo.task.Deadline;
import jimbo.task.Event;
import jimbo.task.Task;
import jimbo.task.TaskList;
import jimbo.task.Todo;

/**
 * Makes sense of raw user input: turns the text typed after a command word
 * (e.g. "todo", "deadline", "event") into a {@link Task}, and turns a task
 * number typed by the user into a valid 0-based index into a
 * {@link TaskList}. Jimbo's main loop still decides *which* command was
 * typed; Parser is responsible for interpreting the arguments that follow.
 */
public class Parser {
    /**
     * Parses {@code rest} (the text after the "todo" keyword) into a
     * {@link Todo}.
     *
     * @throws JimboException if the description is empty.
     */
    public Task parseTodo(String rest) throws JimboException {
        if (rest.isEmpty()) {
            throw new JimboException("A todo can't be empty — gimme something to nom on!");
        }
        return new Todo(rest);
    }

    /**
     * Parses {@code rest} (the text after the "deadline" keyword) into a
     * {@link Deadline}.
     *
     * @throws JimboException if the description or the "/by" time is missing.
     */
    public Task parseDeadline(String rest) throws JimboException {
        if (rest.isEmpty()) {
            throw new JimboException("A deadline can't be empty — tell me what needs doing!");
        }
        String[] parts = rest.split(" /by ", 2);
        if (parts.length < 2) {
            throw new JimboException("A deadline needs a \"/by\" time so I know when it goes stale — "
                    + "try \"deadline return book /by Sunday\".");
        }
        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty()) {
            throw new JimboException("A deadline can't be empty — tell me what needs doing!");
        }
        return new Deadline(description, by);
    }

    /**
     * Parses {@code rest} (the text after the "event" keyword) into an
     * {@link Event}.
     *
     * @throws JimboException if the description, the "/from" time or the
     *                        "/to" time is missing.
     */
    public Task parseEvent(String rest) throws JimboException {
        if (rest.isEmpty()) {
            throw new JimboException("An event can't be empty — tell me what's happening!");
        }
        String[] parts = rest.split(" /from ", 2);
        if (parts.length < 2) {
            throw new JimboException("An event needs a \"/from\" and \"/to\" time — try "
                    + "\"event project meeting /from 2/12/2026 1400 /to 2/12/2026 1600\".");
        }
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new JimboException("An event can't be empty — tell me what's happening!");
        }
        String[] timeParts = parts[1].split(" /to ", 2);
        if (timeParts.length < 2) {
            throw new JimboException("An event still needs a \"/to\" time — try "
                    + "\"event project meeting /from 2/12/2026 1400 /to 2/12/2026 1600\".");
        }
        String from = timeParts[0].trim();
        String to = timeParts[1].trim();
        if (from.isEmpty()) {
            throw new JimboException("Gimme a start time after \"/from\"!");
        }
        if (to.isEmpty()) {
            throw new JimboException("Gimme an end time after \"/to\"!");
        }
        return new Event(description, from, to);
    }

    /**
     * Parses {@code rest} (the text after the "find" keyword) into the
     * keyword to search for.
     *
     * @throws JimboException if the keyword is empty.
     */
    public String parseFind(String rest) throws JimboException {
        if (rest.isEmpty()) {
            throw new JimboException("Gimme a keyword to search for — try \"find book\".");
        }
        return rest;
    }

    /**
     * Parses {@code indexArg} (a 1-based task number, as typed by the user,
     * possibly with surrounding whitespace) into a valid 0-based index into
     * {@code tasks}. {@code commandName} is used to tailor the error message
     * shown when {@code indexArg} is blank, e.g. "mark" or "delete".
     *
     * @throws JimboException if the number is missing, not a valid integer,
     *                        or does not correspond to a task in the list.
     */
    public int parseTaskIndex(TaskList tasks, String indexArg, String commandName) throws JimboException {
        String trimmed = indexArg.trim();
        if (trimmed.isEmpty()) {
            throw new JimboException("Gimme a task number to " + commandName
                    + " — try \"" + commandName + " 2\".");
        }
        int index;
        try {
            index = Integer.parseInt(trimmed) - 1;
        } catch (NumberFormatException e) {
            throw new JimboException("\"" + trimmed + "\" isn't a real task number.");
        }
        if (index < 0 || index >= tasks.size()) {
            throw new JimboException("Task " + (index + 1) + " doesn't exist — "
                    + "you've only got " + tasks.size() + " task(s) right now.");
        }
        assert index >= 0 && index < tasks.size()
                : "index should be within bounds of tasks whenever this method returns normally";
        return index;
    }
}
