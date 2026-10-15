package ralph.command;

import java.io.IOException;
import java.time.LocalDateTime;

import ralph.exception.RalphException;
import ralph.model.Task;
import ralph.model.TaskList;
import ralph.model.Todo;
import ralph.parser.Parser;
import ralph.storage.Storage;
import ralph.ui.Ui;

/**
 * Represents one user action as a command object.
 */
public interface Command {
    int MAX_TASKS = 100;

    /**
     * Executes the command performing any modifications and interacting with UI/storage as needed.
     *
     * @param tasks   the current task list
     * @param ui      the UI helper used to show messages
     * @param storage storage to persist changes when necessary
     * @return true when the application should exit after this command, false otherwise
     * @throws RalphException on validation or execution errors
     */
    boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException;

    /**
     * Helper to persist the current task list to storage, swallowing IO errors with a message.
     *
     * @param tasks   the task list to persist
     * @param storage the storage implementation used to save
     */
    static void persist(TaskList tasks, Storage storage) {
        try {
            storage.save(tasks.getAll());
        } catch (IOException e) {
            System.out.println(" Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Signals that the application should exit.
     */
    final class ByeCommand implements Command {
        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) {
            ui.showExit();
            return true;
        }
    }

    /**
     * Lists all tasks currently stored.
     */
    final class ListCommand implements Command {
        private final String sortKey;

        /**
         * Creates an unsorted list command.
         */
        public ListCommand() {
            this.sortKey = "";
        }

        /**
         * Creates a list command that requests sorting by the given key.
         *
         * @param sortKey sort criterion (e.g. "deadline", "status"); empty/blank means no sorting
         */
        public ListCommand(String sortKey) {
            this.sortKey = sortKey == null ? "" : sortKey.trim();
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) throws ralph.exception.RalphException {
            if (sortKey == null || sortKey.isBlank()) {
                ui.printTaskList(tasks.getAll());
            } else {
                ui.printTaskList(tasks.getAllSorted(sortKey));
            }
            return false;
        }
    }

    /**
     * Searches for tasks containing a keyword.
     */
    final class FindCommand implements Command {
        private final String keyword;

        /**
         * Creates a command that filters tasks by keyword.
         *
         * @param keyword the keyword to match against task descriptions
         */
        public FindCommand(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) {
            ui.showMatchingTasks(tasks.find(keyword));
            return false;
        }
    }

    /**
     * Marks a task as complete.
     */
    final class MarkCommand implements Command {
        private final int index;

        /**
         * Creates a command that marks the task at the given index.
         *
         * @param index the zero-based task index to mark as completed
         */
        public MarkCommand(int index) {
            this.index = index;
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException {
            if (index < 0 || index >= tasks.size()) {
                throw new RalphException("That task number doesn't exist.");
            }
            tasks
                .get(index)
                .markAsDone();
            ui.showMarked(tasks.get(index));
            persist(tasks, storage);
            return false;
        }
    }

    /**
     * Marks a task as not yet done.
     */
    final class UnmarkCommand implements Command {
        private final int index;

        /**
         * Creates a command that unmarks the task at the given index.
         *
         * @param index the zero-based task index to unmark
         */
        public UnmarkCommand(int index) {
            this.index = index;
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException {
            if (index < 0 || index >= tasks.size()) {
                throw new RalphException("That task number doesn't exist.");
            }
            tasks
                .get(index)
                .markAsNotDone();
            ui.showUnmarked(tasks.get(index));
            persist(tasks, storage);
            return false;
        }
    }

    /**
     * Deletes a task from the list.
     */
    final class DeleteCommand implements Command {
        private final int index;

        /**
         * Creates a command that deletes the task at the given index.
         *
         * @param index the zero-based task index to remove
         */
        public DeleteCommand(int index) {
            this.index = index;
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException {
            if (index < 0 || index >= tasks.size()) {
                throw new RalphException("That task number doesn't exist.");
            }
            Task removed = tasks.remove(index);
            ui.showRemoved(removed, tasks.size());
            persist(tasks, storage);
            return false;
        }
    }

    /**
     * Adds a to-do task.
     */
    final class AddTodoCommand implements Command {
        private final String description;

        /**
         * Creates a command that adds a to-do task.
         *
         * @param description the task description
         */
        public AddTodoCommand(String description) {
            this.description = description;
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException {
            if (tasks.size() >= MAX_TASKS) {
                throw new RalphException("My list is full — can't add more than " + MAX_TASKS + " tasks.");
            }
            Todo todo = new Todo(description);
            tasks.add(todo);
            ui.showAdded(todo, tasks.size());
            persist(tasks, storage);
            return false;
        }
    }

    /**
     * Adds a task with a deadline.
     */
    final class AddDeadlineCommand implements Command {
        private final String description;
        private final String by;

        /**
         * Creates a command that adds a deadline task.
         *
         * @param description the task description
         * @param by the due date or deadline string
         */
        public AddDeadlineCommand(String description, String by) {
            this.description = description;
            this.by = by;
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException {
            if (tasks.size() >= MAX_TASKS) {
                throw new RalphException("My list is full — can't add more than " + MAX_TASKS + " tasks.");
            }
            if (description.isEmpty()) {
                throw new RalphException("Give me what the deadline is for, please.");
            }
            if (by.isEmpty()) {
                throw new RalphException(
                    "Please include '/by' with a date/time in yyyy-MM-dd or yyyy-MM-dd HH:mm format."
                );
            }
            ralph.model.Deadline deadline = new ralph.model.Deadline(
                description,
                Parser.parseDateTime(by)
            );
            tasks.add(deadline);
            ui.showAdded(deadline, tasks.size());
            persist(tasks, storage);
            return false;
        }
    }

    /**
     * Adds an event task.
     */
    final class AddEventCommand implements Command {
        private final String description;
        private final String from;
        private final String to;

        /**
         * Creates a command that adds an event task.
         *
         * @param description the event description
         * @param from the starting date-time string
         * @param to the ending date-time string
         */
        public AddEventCommand(String description, String from, String to) {
            this.description = description;
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean execute(TaskList tasks, Ui ui, Storage storage) throws RalphException {
            if (tasks.size() >= MAX_TASKS) {
                throw new RalphException("My list is full — can't add more than " + MAX_TASKS + " tasks.");
            }
            if (description.isEmpty()) {
                throw new RalphException("Give me a short description for the event, please.");
            }
            if (from.isEmpty() || to.isEmpty()) {
                throw new RalphException("An event needs both a start and end time.");
            }
            LocalDateTime start = Parser.parseDateTime(from);
            LocalDateTime end = Parser.parseDateTime(to);
            if (!start.isBefore(end)) {
                throw new RalphException("Event start must be before the end time.");
            }
            ralph.model.Event event = new ralph.model.Event(
                description,
                start,
                end
            );
            tasks.add(event);
            ui.showAdded(event, tasks.size());
            persist(tasks, storage);
            return false;
        }
    }
}
