package hampster.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hampster.command.Command;
import hampster.exception.HampsterException;
import hampster.parser.CommandParser;
import hampster.storage.Storage;
import hampster.task.TaskList;
import hampster.ui.Ui;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Controls the main FXML-based Hampster window. */
public class MainWindow extends AnchorPane {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private TaskList tasks;

    /** Messages produced by the current command, combined into one response. */
    private final List<String> responseBuffer = new ArrayList<>();

    private final Ui outputUi = new Ui() {
        @Override
        public void showMessage(String... messages) {
            responseBuffer.addAll(List.of(messages));
        }

        @Override
        public void printLine() {
            // Separators are intentionally omitted from the chat view.
        }

        @Override
        public void shutdown() {
            // The GUI remains open until its window is closed.
        }
    };

    /** Initializes bindings for the main window controls. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollToLatestMessage());

        String storageWarning = "";
        try {
            tasks = Storage.load();
        } catch (IOException exception) {
            tasks = new TaskList();
            storageWarning = "I could not open the dossier, minion. "
                    + "Starting with an empty task list.";
        }

        addMessage("Heh heh... I'm Hampster, your tiny evil task overlord.\n"
                        + "Welcome to my lair, minion. What shall we conquer?",
                MessageKind.HAMPSTER);
        if (!storageWarning.isEmpty()) {
            addMessage(storageWarning, MessageKind.ERROR);
        }
        Platform.runLater(userInput::requestFocus);
    }

    /** Handles a command submitted through the GUI input field. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();

        if (input.isEmpty()) {
            return;
        }

        addMessage(input, MessageKind.USER);
        userInput.clear();

        try {
            Command command = CommandParser.parse(input);
            command.execute(tasks, outputUi);
            if (!responseBuffer.isEmpty()) {
                addMessage(String.join("\n", responseBuffer), MessageKind.HAMPSTER);
            }
            if (!Storage.save(tasks)) {
                addMessage("I completed the command, but could not save the dossier.",
                        MessageKind.ERROR);
            }
        } catch (HampsterException exception) {
            addMessage("Pathetic! That command has failed: " + exception.getMessage(), MessageKind.ERROR);
        } finally {
            responseBuffer.clear();
        }
    }

    /** Adds a message and keeps the newest conversation entry visible. */
    private void addMessage(String message, MessageKind kind) {
        dialogContainer.getChildren().add(createMessage(message, kind));
        scrollToLatestMessage();
    }

    /** Scrolls after layout so the newest message is visible at the bottom. */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> {
            dialogContainer.applyCss();
            dialogContainer.layout();
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
    }

    /** Creates a styled conversation message for the selected message kind. */
    private HBox createMessage(String message, MessageKind kind) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.maxWidthProperty().bind(dialogContainer.widthProperty().multiply(
                kind == MessageKind.USER ? 0.78 : 0.92));
        label.getStyleClass().add(kind.getStyleClass());

        HBox box = new HBox(label);
        box.setAlignment(kind == MessageKind.USER ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        box.setMaxWidth(Double.MAX_VALUE);
        box.getStyleClass().add("message-row");
        return box;
    }

    /** Describes who produced a message and how it should be styled. */
    private enum MessageKind {
        USER("user-message"),
        HAMPSTER("hampster-message"),
        ERROR("error-message");

        private final String styleClass;

        MessageKind(String styleClass) {
            this.styleClass = styleClass;
        }

        String getStyleClass() {
            return styleClass;
        }
    }
}
