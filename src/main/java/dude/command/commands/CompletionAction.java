package dude.command.commands;

import dude.command.core.CommandType;

/**
 * Defines the completion state and display metadata for a task-completion command.
 */
enum CompletionAction {
    /** Marks a task as completed. */
    MARK(CommandType.MARK, true, "mark task"),
    /** Marks a task as incomplete. */
    UNMARK(CommandType.UNMARK, false, "unmark task");

    private final CommandType commandType;
    private final boolean completed;
    private final String undoDescription;

    CompletionAction(CommandType commandType, boolean completed, String undoDescription) {
        this.commandType = commandType;
        this.completed = completed;
        this.undoDescription = undoDescription;
    }

    CommandType commandType() {
        return commandType;
    }

    boolean completed() {
        return completed;
    }

    String undoDescription() {
        return undoDescription;
    }
}
