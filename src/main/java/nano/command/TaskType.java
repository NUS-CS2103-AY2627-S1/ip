package nano.command;

import nano.NanoException;
import nano.task.Deadline;
import nano.task.Event;
import nano.task.Task;
import nano.task.Todo;

/** Defines how each task type is parsed from user input. */
public enum TaskType {
    TODO("todo") {
        @Override
        Task create(String input) throws NanoException {
            return new Todo(input);
        }
    },
    DEADLINE("deadline") {
        @Override
        Task create(String input) throws NanoException {
            String[] parts = input.split("/by", 2);
            return new Deadline(parts[0].trim(), parts.length > 1
                    ? parts[1].trim() : "");
        }
    },
    EVENT("event") {
        @Override
        Task create(String input) throws NanoException {
            int fromIndex = input.indexOf("/from");
            int toIndex = input.indexOf("/to");

            if (fromIndex == -1) {
                return new Event(input, "", "");
            }

            if (toIndex != -1 && toIndex < fromIndex) {
                throw new NanoException(
                        "The /from section must come before the /to section."
                );
            }

            String description = input.substring(0, fromIndex).trim();
            String from = toIndex == -1
                    ? input.substring(fromIndex + 5).trim()
                    : input.substring(fromIndex + 5, toIndex).trim();
            String to = toIndex == -1
                    ? ""
                    : input.substring(toIndex + 3).trim();

            return new Event(description, from, to);
        }
    };

    private final String keyword;

    TaskType(String keyword) {
        this.keyword = keyword;
    }

    abstract Task create(String input) throws NanoException;

    /**
     * Finds the task type represented by a command.
     *
     * @param input user input containing a task command.
     * @return the matching task type.
     * @throws NanoException if the input does not represent a supported task type.
     */
    static TaskType from(String input) throws NanoException {
        for (TaskType type : values()) {
            if (input.equals(type.keyword) || input.startsWith(type.keyword + " ")) {
                return type;
            }
        }
        throw new NanoException("Unknown task command.");
    }

    String keyword() {
        return keyword;
    }
}
