package dingleberry;

import dingleberry.model.Task;
import dingleberry.model.TaskList;
import dingleberry.ui.Ui;
import javafx.scene.layout.VBox;

/**
 * Renders chatbot responses as structured GUI dialog cards.
 */
final class ChatUi extends Ui {
    /** Chat log that new bot dialog rows are appended to. */
    private final VBox dialogLog;

    /**
     * Creates a GUI-backed UI that appends messages to the given dialog
     * log.
     *
     * @param chatLog the chat log to append bot messages to.
     */
    ChatUi(final VBox chatLog) {
        dialogLog = chatLog;
    }

    /**
     * Adds a calm success card confirming a task change, with the
     * heading and body kept as separate structured content.
     *
     * @param heading a short description of the change.
     * @param body the confirmation details shown below the heading.
     */
    private void appendSuccessMessage(final String heading,
            final String body) {
        dialogLog.getChildren()
                .add(DialogBox.getSuccessDialog(heading, body));
    }

    /**
     * Adds a calm information card for a neutral reply, with the
     * heading and body kept as separate structured content.
     *
     * @param heading a short description of the reply.
     * @param body the reply content shown below the heading.
     */
    private void appendInformationMessage(final String heading,
            final String body) {
        dialogLog.getChildren()
                .add(DialogBox.getInformationDialog(heading, body));
    }

    /**
     * Adds a dedicated error card with the given heading and detail
     * message, styled distinctly from an ordinary bot reply.
     *
     * @param heading a short description of the error.
     * @param message the detail message explaining what went wrong.
     */
    private void appendErrorMessage(final String heading,
            final String message) {
        dialogLog.getChildren()
                .add(DialogBox.getErrorDialog(heading, message));
    }

    @Override
    public void showWelcome() {
        final DialogBox welcomeDialog = DialogBox.getInformationDialog(
                "Welcome to Dingleberry", WELCOME_MESSAGE);
        // Keeps the ASCII-art banner's spacing aligned, unlike the
        // proportional font used by every other card.
        welcomeDialog.getStyleClass().add("welcome-dialog");
        dialogLog.getChildren().add(welcomeDialog);
    }

    @Override
    public void showLine() {
        // No separator line is needed between GUI speech bubbles.
    }

    @Override
    public void showTaskList(final TaskList tasks) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                builder.append(System.lineSeparator());
            }
            builder.append(i + 1).append(". ").append(tasks.get(i));
        }
        appendInformationMessage("Your taskies 📋", builder.toString());
    }

    @Override
    public void showTaskAdded(final Task task,
            final int totalTaskCount) {
        appendSuccessMessage("Taskie added! 🎉", task
                + System.lineSeparator() + "You now have "
                + totalTaskCount + " tasks in your list.");
    }

    @Override
    public void showTaskDeleted(final Task task,
            final int totalTaskCount) {
        appendSuccessMessage("Taskie removed! 🗑️", task
                + System.lineSeparator() + "You now have "
                + totalTaskCount + " tasks in your list.");
    }

    @Override
    public void showTaskMarked(final Task task) {
        appendSuccessMessage("Taskie complete! ✅", task.toString());
    }

    @Override
    public void showTaskUnmarked(final Task task) {
        appendSuccessMessage("Taskie reopened! 🔄", task.toString());
    }

    @Override
    public void showWrongCommandError(final String message) {
        appendErrorMessage("Dingleberry is confused! 🤔", message);
    }

    @Override
    public void showIncorrectParametersError(final String message) {
        appendErrorMessage("Oopsie, something is missing! ⚠️", message);
    }

    @Override
    public void showLoadingError(final String message) {
        appendErrorMessage("Could not load tasks",
                message + " Starting with an empty list.");
    }

    @Override
    public void showLoadingWarning(final String message) {
        appendErrorMessage("Some tasks were skipped", message);
    }

    @Override
    public void showSavingError(final String message) {
        appendErrorMessage("Could not save tasks", message);
    }

    @Override
    public void showGoodbye() {
        appendInformationMessage("Goodbye",
                "Bye, hope to see your berries again!");
    }
}
