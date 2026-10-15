package nova.gui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import nova.command.CommandHandler;
import nova.exception.NovaException;
import nova.parser.Parser;
import nova.storage.Storage;
import nova.task.TaskList;
import nova.ui.Ui;

/** Controls the Nova JavaFX main window and connects it to the application logic. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Parser parser;
    private CommandHandler commandHandler;
    private Ui ui;
    private final ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
    private final PrintStream responseStream = new PrintStream(responseBuffer, true, StandardCharsets.UTF_8);
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image novaImage = new Image(this.getClass().getResourceAsStream("/images/DaNova.png"));

    /** Binds the GUI controller to Nova's parser, command handler, storage, and task list. */
    public void bindNovaComponents(Storage storage, TaskList tasks, Parser parser) {
        this.parser = parser;
        this.ui = new Ui(responseStream);
        this.commandHandler = new CommandHandler(storage, tasks, ui);
    }

    /** Initializes scrolling and binds the dialog container to the available viewport width. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
        dialogContainer.prefWidthProperty().bind(
                Bindings.createDoubleBinding(() -> scrollPane.getViewportBounds().getWidth(),
                        scrollPane.viewportBoundsProperty()));
        showGreeting();
    }

    /** Returns the friendly greeting shown when the GUI first opens. */
    private String getGreeting() {
        return "Hello! I'm Nova.\nWhat can I do for you?";
    }

    /** Adds Nova's greeting as the first dialog in the conversation. */
    private void showGreeting() {
        dialogContainer.getChildren().add(DialogBox.getNovaDialog(getGreeting(), novaImage));
    }

    /** Parses and executes the entered command, then displays Nova's real response. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        responseBuffer.reset();
        boolean shouldExit = false;
        boolean isError = false;

        try {
            Parser.ParsedCommand command = parser.parse(input);
            shouldExit = commandHandler.execute(command);
        } catch (NovaException exception) {
            ui.showError(exception);
            isError = true;
        }

        String response = responseBuffer.toString(StandardCharsets.UTF_8).strip();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getNovaDialog(response, novaImage, isError));
        userInput.clear();

        if (shouldExit) {
            Platform.exit();
        }
    }
}
