package bo.gui;

import bo.Tracker;
import bo.command.CommandException;
import bo.parser.Parser;
import bo.ui.GuiMessage;
import bo.ui.GuiUi;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for Bo's FXML-defined main window.
 */
public class MainWindow {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    @FXML private Button sendButton;

    private final GuiUi ui = new GuiUi();
    private final Tracker tracker = new Tracker(ui);
    private final Parser parser = new Parser();
    private final Image userImage = new Image(MainWindow.class.getResourceAsStream("/images/DaUser.png"));
    private final Image neutralOwl = new Image(
            MainWindow.class.getResourceAsStream("/images/Bo-neutral.png"));
    private final Image happyOwl = new Image(
            MainWindow.class.getResourceAsStream("/images/Bo-happy.png"));
    private final Image confusedOwl = new Image(
            MainWindow.class.getResourceAsStream("/images/Bo-confused.png"));

    /**
     * Initializes automatic scrolling after the FXML controls are injected.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        ui.showWelcome("");
        displayPendingMessages();
    }

    /**
     * Processes the command entered by the user and displays Bo's response.
     */
    @FXML
    private void handleUserInput() {
        String command = userInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        dialogContainer.getChildren().add(DialogBox.getUserDialog(command, userImage));
        userInput.clear();
        if (command.equals("bye")) {
            dialogContainer.getChildren().add(DialogBox.getBoDialog(
                    "Bye for now - try not to collect too many tasks!", neutralOwl));
            PauseTransition closeDelay = new PauseTransition(Duration.seconds(2));
            closeDelay.setOnFinished(event -> userInput.getScene().getWindow().hide());
            closeDelay.play();
            return;
        }
        try {
            parser.executeCommand(tracker, command);
        } catch (CommandException exception) {
            ui.showError(exception.getMessage());
        }
        displayPendingMessages();
    }

    /** Displays queued responses with their matching owl expression. */
    private void displayPendingMessages() {
        for (GuiMessage message : ui.takeMessages()) {
            Image owl = switch (message.type()) {
                case SUCCESS -> happyOwl;
                case ERROR -> confusedOwl;
                case NORMAL -> neutralOwl;
            };
            DialogBox dialog = message.type() == GuiMessage.MessageType.ERROR
                    ? DialogBox.getErrorDialog(message.text(), owl)
                    : DialogBox.getBoDialog(message.text(), owl);
            dialogContainer.getChildren().add(dialog);
        }
    }
}
