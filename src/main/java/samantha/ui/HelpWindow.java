package samantha.ui;

import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controls Samantha's keyboard-first standalone command guide.
 */
public class HelpWindow {
    private static final String SELECTED_COMMAND_STYLE = "command-item-selected";
    private static final String COPY_HINT = "Press c or Enter to copy the example.";
    private static final String DATE_FORMAT_MESSAGE = "Dates: d/M/yyyy or d-M-yyyy";
    private static final String DATE_TIME_FORMAT_MESSAGE =
            "Dates: d/M/yyyy or d-M-yyyy  ·  Times: HHmm (for example, 1800)";
    private static final List<CommandHelp> COMMANDS = List.of(
            new CommandHelp("help", "Show this guide.", "help", "help"),
            new CommandHelp("todo", "Add something you want to remember.",
                    "todo DESCRIPTION", "todo read chapter 5"),
            new CommandHelp("deadline", "Add a task due on a date, optionally at a time.",
                    "deadline DESCRIPTION /by DATE [TIME]",
                    "deadline submit report /by 12/9/2026 1800",
                    "DATE AND TIME FORMAT", DATE_TIME_FORMAT_MESSAGE),
            new CommandHelp("event", "Add an event with a start and end time.",
                    "event DESCRIPTION /from DATE TIME /to DATE TIME",
                    "event project meeting /from 12/9/2026 1400 /to 12/9/2026 1600",
                    "DATE AND TIME FORMAT", DATE_TIME_FORMAT_MESSAGE),
            new CommandHelp("list", "Show every task, or only tasks on a date.",
                    "list [DATE]", "list 12/9/2026", "DATE FORMAT", DATE_FORMAT_MESSAGE),
            new CommandHelp("find", "Find tasks and notes whose text contains a keyword.",
                    "find KEYWORD", "find report"),
            new CommandHelp("note", "Add a note that you want Samantha to remember.",
                    "note CONTENT", "note buy milk"),
            new CommandHelp("notes", "Show all saved notes.", "notes", "notes"),
            new CommandHelp("edit-note", "Replace a note using its list number.",
                    "edit-note NOTE_NUMBER CONTENT", "edit-note 1 buy oat milk"),
            new CommandHelp("delete-note", "Remove a note using its list number.",
                    "delete-note NOTE_NUMBER", "delete-note 1"),
            new CommandHelp("mark", "Mark a task as done using its list number.",
                    "mark N", "mark 1"),
            new CommandHelp("unmark", "Mark a completed task as not done yet.",
                    "unmark N", "unmark 1"),
            new CommandHelp("delete", "Remove a task permanently using its list number.",
                    "delete N", "delete 1"),
            new CommandHelp("undo", "Undo the most recent change to a task or note.",
                    "undo", "undo"),
            new CommandHelp("bye", "Close Samantha when you are done.", "bye", "bye"));

    @FXML
    private BorderPane helpWindow;
    @FXML
    private Button helpButton;
    @FXML
    private Button todoButton;
    @FXML
    private Button deadlineButton;
    @FXML
    private Button eventButton;
    @FXML
    private Button listButton;
    @FXML
    private Button findButton;
    @FXML
    private Button noteButton;
    @FXML
    private Button notesButton;
    @FXML
    private Button editNoteButton;
    @FXML
    private Button deleteNoteButton;
    @FXML
    private Button markButton;
    @FXML
    private Button unmarkButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button undoButton;
    @FXML
    private Button byeButton;
    @FXML
    private Label commandTitle;
    @FXML
    private Label commandDescription;
    @FXML
    private Label commandSyntax;
    @FXML
    private Label commandExample;
    @FXML
    private Label commandPosition;
    @FXML
    private Label copyStatus;
    @FXML
    private Button copyButton;
    @FXML
    private VBox formatNote;
    @FXML
    private Label formatHeading;
    @FXML
    private Label formatText;

    private List<Button> commandButtons;
    private int selectedIndex;

    /**
     * Configures the command list and global keyboard navigation.
     */
    @FXML
    public void initialize() {
        commandButtons = List.of(
                helpButton, todoButton, deadlineButton, eventButton, listButton,
                findButton, noteButton, notesButton, editNoteButton, deleteNoteButton,
                markButton, unmarkButton, deleteButton, undoButton, byeButton);
        helpWindow.setFocusTraversable(true);
        helpWindow.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);
        showCommand(0);
        Platform.runLater(helpWindow::requestFocus);
    }

    /**
     * Selects a command from the mouse-accessible command list.
     *
     * @param event command-button action event
     */
    @FXML
    private void selectCommand(ActionEvent event) {
        int commandIndex = commandButtons.indexOf(event.getSource());
        if (commandIndex >= 0) {
            showCommand(commandIndex);
        }
    }

    /**
     * Copies the currently selected example to the system clipboard.
     */
    @FXML
    private void copyCommand() {
        CommandHelp command = COMMANDS.get(selectedIndex);
        ClipboardContent content = new ClipboardContent();
        content.putString(command.example);
        Clipboard.getSystemClipboard().setContent(content);
        copyButton.setText("Copied");
        copyStatus.setText("Copied “" + command.example + "” to the clipboard.");
    }

    /**
     * Handles keyboard-first navigation and actions for the command guide.
     *
     * @param event key event raised anywhere in the help window
     */
    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case J -> moveSelection(1);
            case K -> moveSelection(-1);
            case C, ENTER -> copyCommand();
            case Q, ESCAPE -> closeWindowFromKeyboard();
            default -> {
                return;
            }
        }
        event.consume();
    }

    /**
     * Moves the selected command by a keyboard navigation offset.
     *
     * @param offset positive for next command and negative for previous command
     */
    private void moveSelection(int offset) {
        int nextIndex = getAdjacentCommandIndex(selectedIndex, offset);
        showCommand(nextIndex);
    }

    /**
     * Returns the guide index reached by moving from a selected command.
     *
     * @param selectedIndex currently selected command index
     * @param offset positive for next command and negative for previous command
     * @return the wrapped command index after applying the offset
     */
    static int getAdjacentCommandIndex(int selectedIndex, int offset) {
        return Math.floorMod(selectedIndex + offset, COMMANDS.size());
    }

    /**
     * Returns the command words shown in the guide, in navigation order.
     *
     * @return immutable command word list
     */
    static List<String> getCommandNames() {
        return COMMANDS.stream().map(command -> command.name).toList();
    }

    /**
     * Indicates whether a command requires a date or time format hint.
     *
     * @param commandName command word in the guide
     * @return true if the command has a format hint
     */
    static boolean hasFormatNote(String commandName) {
        return COMMANDS.stream()
                .filter(command -> command.name.equals(commandName))
                .anyMatch(command -> command.formatMessage != null);
    }

    /**
     * Renders the selected command in the focused detail panel.
     *
     * @param commandIndex index of the command to show
     */
    private void showCommand(int commandIndex) {
        commandButtons.get(selectedIndex).getStyleClass().remove(SELECTED_COMMAND_STYLE);
        selectedIndex = commandIndex;
        commandButtons.get(selectedIndex).getStyleClass().add(SELECTED_COMMAND_STYLE);

        CommandHelp command = COMMANDS.get(selectedIndex);
        commandTitle.setText(command.name);
        commandDescription.setText(command.description);
        commandSyntax.setText(command.syntax);
        commandExample.setText(command.example);
        commandPosition.setText((selectedIndex + 1) + " / " + COMMANDS.size());
        copyButton.setText("Copy example");
        copyStatus.setText(COPY_HINT);
        updateFormatNote(command);
    }

    /**
     * Shows a format hint only for commands that accept a date or time.
     *
     * @param command currently selected command
     */
    private void updateFormatNote(CommandHelp command) {
        boolean hasFormatNote = command.formatMessage != null;
        formatNote.setManaged(hasFormatNote);
        formatNote.setVisible(hasFormatNote);
        if (hasFormatNote) {
            formatHeading.setText(command.formatHeading);
            formatText.setText(command.formatMessage);
        }
    }

    /**
     * Closes the stage containing this command guide.
     */
    private void closeWindowFromKeyboard() {
        Stage stage = (Stage) helpWindow.getScene().getWindow();
        stage.close();
    }

    /**
     * Stores the user-facing reference information for one command.
     */
    private static class CommandHelp {
        private final String name;
        private final String description;
        private final String syntax;
        private final String example;
        private final String formatHeading;
        private final String formatMessage;

        /**
         * Creates help content that does not need a date or time format hint.
         *
         * @param name command word
         * @param description concise explanation of the command
         * @param syntax command syntax
         * @param example copyable example command
         */
        private CommandHelp(String name, String description, String syntax, String example) {
            this(name, description, syntax, example, null, null);
        }

        /**
         * Creates the help content for one supported command.
         *
         * @param name command word
         * @param description concise explanation of the command
         * @param syntax command syntax
         * @param example copyable example command
         * @param formatHeading heading for an optional date or time format hint
         * @param formatMessage optional date or time format hint
         */
        private CommandHelp(String name, String description, String syntax, String example,
                            String formatHeading, String formatMessage) {
            this.name = name;
            this.description = description;
            this.syntax = syntax;
            this.example = example;
            this.formatHeading = formatHeading;
            this.formatMessage = formatMessage;
        }
    }
}
