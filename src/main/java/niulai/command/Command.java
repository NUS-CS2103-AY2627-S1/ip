package niulai.command;

import niulai.NiuLaiException;
import niulai.model.TaskList;
import niulai.service.Storage;
import niulai.service.StorageException;
import niulai.service.Ui;

/**
 * Represents an executable command entered in the NiuLai command-line interface.
 */
public abstract class Command {
    /** The command keyword and matching rules used by the parser. */
    private final Type type;

    /** Creates a command backed by the specified command type. */
    protected Command(Type type) {
        this.type = type;
    }

    /**
     * Executes this command against the current application state.
     *
     * @param tasks the current task list
     * @param ui the user-interface component
     * @param storage the task storage component
     * @return the inverse action when this command changes the task list, otherwise {@code null}
     * @throws NiuLaiException if the command cannot be completed
     */
    public abstract UndoAction execute(TaskList tasks, Ui ui, Storage storage)
            throws NiuLaiException;

    /**
     * Returns whether this command asks the chatbot to undo its most recent change.
     *
     * @return whether this is an undo command
     */
    public boolean isUndo() {
        return false;
    }

    /** @return whether executing this command should end the application */
    public boolean isExit() {
        return false;
    }

    /** @return the command keyword */
    public String getKeyword() {
        return type.getKeyword();
    }

    /** @return whether the input exactly matches this command */
    public boolean matchesExactly(String input) {
        return type.matchesExactly(input);
    }

    /** @return whether the input belongs to this command */
    public boolean matches(String input) {
        return type.matches(input);
    }

    /** Creates a user-facing error while preserving specific expected storage guidance. */
    protected static NiuLaiException createStorageFailure(Exception cause) {
        String message = cause instanceof StorageException storageException
                ? storageException.getUserMessage()
                : StorageException.SAVE_FAILED_MESSAGE;
        return new NiuLaiException("NOOO!!! " + message);
    }

    /** Describes the keywords understood by the parser. */
    public enum Type {
        /** Exits the application. */
        BYE("bye"),

        /** Undoes the most recent successful state-changing command. */
        UNDO("undo"),

        /** Lists all tasks. */
        LIST("list"),

        /** Lists deadlines and events occurring on a date. */
        FIND("find"),

        /** Marks a task as completed. */
        MARK("mark"),

        /** Marks a task as pending. */
        UNMARK("unmark"),

        /** Deletes a task. */
        DELETE("delete"),

        /** Creates a basic todo task. */
        TODO("todo"),

        /** Creates a deadline task. */
        DEADLINE("deadline"),

        /** Creates an event task. */
        EVENT("event");

        private final String keyword;

        /** Creates a command type with its user-facing keyword. */
        Type(String keyword) {
            this.keyword = keyword;
        }

        /** @return the command keyword */
        public String getKeyword() {
            return keyword;
        }

        /** @return whether the input exactly matches this command */
        public boolean matchesExactly(String input) {
            return input.equals(keyword);
        }

        /** @return whether the input belongs to this command */
        public boolean matches(String input) {
            if (input.equals(keyword)) {
                return true;
            }

            if (!input.startsWith(keyword) || input.length() == keyword.length()) {
                return false;
            }

            char separator = input.charAt(keyword.length());
            return Character.isWhitespace(separator) || Character.isSpaceChar(separator);
        }
    }

    /** Reverses one successful state-changing command. */
    @FunctionalInterface
    public interface UndoAction {
        /**
         * Restores the task state before the command and persists it.
         *
         * @param tasks the current task list
         * @param storage the task storage component
         * @throws NiuLaiException if the restored list cannot be saved
         */
        void undo(TaskList tasks, Storage storage) throws NiuLaiException;
    }
}
