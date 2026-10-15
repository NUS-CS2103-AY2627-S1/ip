package clover.command;

import java.io.IOException;

import clover.exception.CloverException;
import clover.parser.Parser;
import clover.storage.Storage;
import clover.task.TaskList;
import clover.tutoree.Tutoree;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/**
 * Represents one action that Clover can perform for the user.
 */
public abstract class Command {
    /**
     * Performs this command using Clover's current collaborators.
     */
    public abstract void execute(TaskList tasks, TutoreeList tutorees, Ui ui, Storage storage) throws CloverException;

    /**
     * Returns whether this command ends the Clover application.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns the visual style to apply to this command's response.
     */
    public CommandResponseStyle getResponseStyle() {
        return CommandResponseStyle.STANDARD;
    }

    /**
     * Validates a user-entered task number and returns its zero-based task-list index.
     *
     * @param taskNumber the one-based task number entered by the user
     * @param tasks the current task list
     * @param invalidTaskNumberMessage the message to display for an invalid task number
     * @return the validated zero-based task-list index
     * @throws CloverException if the task number does not identify a task in the list
     */
    protected int parseValidTaskIndex(String taskNumber, TaskList tasks, String invalidTaskNumberMessage)
            throws CloverException {
        if (!Parser.isValidTaskNumber(taskNumber, tasks.size())) {
            throw new CloverException(invalidTaskNumberMessage);
        }

        int taskIndex = Parser.parseTaskIndex(taskNumber);
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A validated one-based task number must map to an existing list index.";
        return taskIndex;
    }

    /** Saves the task list or reports that the requested change was not saved. */
    protected void saveTasks(TaskList tasks, Storage storage) throws CloverException {
        try {
            storage.save(tasks.asList());
        } catch (IOException | SecurityException exception) {
            throw new CloverException("The grove could not save your study quests; no change was made.");
        }
    }

    /** Saves the tutoree list or reports that the requested change was not saved. */
    protected void saveTutorees(TutoreeList tutorees, Storage storage) throws CloverException {
        try {
            storage.saveTutorees(tutorees.asList());
        } catch (IOException | SecurityException exception) {
            throw new CloverException("The grove could not save your learning companions; no change was made.");
        }
    }

    /**
     * Validates an optional tutoree name and returns its stored spelling.
     */
    protected String validateTutoreeName(String tutoreeName, TutoreeList tutorees) throws CloverException {
        if (tutoreeName == null) {
            return null;
        }
        Tutoree tutoree = tutorees.findExactName(tutoreeName)
                .orElseThrow(() -> new CloverException("No learning companion named \"" + tutoreeName
                        + "\" is in the grove. Add them before linking a quest."));
        return tutoree.getName();
    }
}
