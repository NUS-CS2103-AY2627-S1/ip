package am.command;

import am.task.Task;
import am.task.TaskEdit;

/**
 * Represents a command parsed from user input.
 */
public abstract sealed class Command {
    private Command() {
    }

    /** Represents blank input that requires no response or task changes. */
    public static final class EmptyCommand extends Command {
        /** Creates a command that does nothing. */
        public EmptyCommand() {
        }
    }

    /** Requests a partial edit using the user's full-list task number. */
    public static final class EditCommand extends Command {
        private final int taskNumber;
        private final TaskEdit edit;

        /** Creates an edit whose task-dependent validation is deferred to execution. */
        public EditCommand(int taskNumber, TaskEdit edit) {
            this.taskNumber = taskNumber;
            this.edit = edit;
        }

        public int getTaskNumber() {
            return taskNumber;
        }

        public TaskEdit getEdit() {
            return edit;
        }
    }

    /**
     * Requests that the application terminate.
     */
    public static final class ByeCommand extends Command {
        /**
         * Creates a command that terminates the application.
         */
        public ByeCommand() {
        }
    }

    /**
     * Requests that all tasks be displayed.
     */
    public static final class ListCommand extends Command {
        /**
         * Creates a command that displays all tasks.
         */
        public ListCommand() {
        }
    }

    /**
     * Requests that past tasks be displayed.
     */
    public static final class PastCommand extends Command {
        /**
         * Creates a command that displays past tasks.
         */
        public PastCommand() {
        }
    }

    /** Requests that tasks matching a keyword be displayed. */
    public static final class FindCommand extends Command {
        private final String keyword;

        /**
         * Creates a command that searches task names.
         *
         * @param keyword text to search for
         */
        public FindCommand(String keyword) {
            this.keyword = keyword;
        }

        /**
         * Returns the search keyword.
         *
         * @return search keyword
         */
        public String getKeyword() {
            return keyword;
        }
    }

    /**
     * Marks a task as complete.
     */
    public static final class MarkCommand extends Command {
        private final int index;

        /**
         * Creates a command targeting a task index.
         *
         * @param index zero-based task index
         */
        public MarkCommand(int index) {
            this.index = index;
        }

        /**
         * Returns the zero-based index of the task to mark.
         *
         * @return task index
         */
        public int getIndex() {
            return index;
        }
    }

    /**
     * Marks a task as incomplete.
     */
    public static final class UnmarkCommand extends Command {
        private final int index;

        /**
         * Creates a command targeting a task index.
         *
         * @param index zero-based task index
         */
        public UnmarkCommand(int index) {
            this.index = index;
        }

        /**
         * Returns the zero-based index of the task to unmark.
         *
         * @return task index
         */
        public int getIndex() {
            return index;
        }
    }

    /**
     * Adds a task to the task list.
     */
    public static final class AddTaskCommand extends Command {
        private final Task task;

        /**
         * Creates a command carrying the task to add.
         *
         * @param task task to add
         */
        public AddTaskCommand(Task task) {
            this.task = task;
        }

        /**
         * Returns the task carried by this command.
         *
         * @return task to add
         */
        public Task getTask() {
            return task;
        }
    }

    /**
     * Removes a task from the task list.
     */
    public static final class DeleteTaskCommand extends Command {
        private final int index;

        /**
         * Creates a command targeting a task index.
         *
         * @param index zero-based task index
         */
        public DeleteTaskCommand(int index) {
            this.index = index;
        }

        /**
         * Returns the zero-based index of the task to delete.
         *
         * @return task index
         */
        public int getIndex() {
            return index;
        }
    }
}
