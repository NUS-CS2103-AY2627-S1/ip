package chud.gui;

import chud.command.Command;
import chud.exception.ChudException;
import chud.parser.Parser;
import chud.storage.Storage;
import chud.task.TaskList;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


/**
 * Controller for {@code MainWindow.fxml}: the scrollable column of chat bubbles, the text field
 * the user types into, and the send button. Each submitted line is parsed and executed exactly
 * as the console version does (see {@code chud.Chud}), except replies are collected via {@link
 * GuiUi} and shown as a bubble instead of printed.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private TaskList tasks;
    private Storage storage;
    private GuiUi ui;

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Wires this window to the chatbot's backend, and shows its welcome message. */
    void init(TaskList tasks, Storage storage, GuiUi ui) {
        this.tasks = tasks;
        this.storage = storage;
        this.ui = ui;

        ui.showWelcome();
        dialogContainer.getChildren().add(
                DialogBox.getChudDialog(ui.flush(), false, dialogContainer.widthProperty()));
        userInput.requestFocus();
    }

    /** Called when the user presses Enter or clicks Send: shows the exchange as two bubbles. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        userInput.clear();

        // command stays null if parsing/execution fails before a Command is ever built, so the
        // isExit() check below can't run on it -- that's fine, since failing to parse/execute
        // means nothing happened that would warrant exiting.
        Command command = null;
        try {
            command = Parser.parse(input);
            command.execute(tasks, ui, storage);
        } catch (ChudException e) {
            ui.showError(e.getMessage());
        } catch (RuntimeException e) {
            // A command should only ever fail with ChudException (its expected, user-facing
            // failure mode) -- this is a last-resort safety net against an unanticipated bug in
            // a command's own logic, so one bad command can't crash the whole GUI session.
            ui.showError("something broke (" + e.getClass().getSimpleName() + "). Try again.");
        }
        boolean isError = ui.hasError();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, dialogContainer.widthProperty()),
                DialogBox.getChudDialog(ui.flush(), isError, dialogContainer.widthProperty()));
        // Keep the caret in the input field so typing the next command doesn't need a re-click,
        // whether this reply was triggered by pressing Enter or by clicking Send.
        userInput.requestFocus();

        if (command != null && command.isExit()) {
            // Give the user a moment to read the goodbye message before the window closes.
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
