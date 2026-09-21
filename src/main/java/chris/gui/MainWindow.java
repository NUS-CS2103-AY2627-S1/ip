package chris.gui;

import chris.Chris;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Controls Chris's main chat window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Button listQuickButton;
    @FXML
    private Button helpQuickButton;

    private Chris chris;

    /** Keeps the latest dialog visible as the conversation grows. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        Platform.runLater(userInput::requestFocus);
    }

    /** Connects this window to Chris and displays the greeting. */
    public void setChris(Chris chris) {
        this.chris = chris;
        String greeting = chris.startGui();
        dialogContainer.getChildren().add(greeting.contains("OOPS!!!")
                ? DialogBox.getErrorDialog(greeting) : DialogBox.getChrisDialog(greeting));
    }

    /** Displays the entered command and Chris's response. */
    @FXML
    private void handleUserInput() {
        submitCommand(userInput.getText(), true);
    }

    /** Lists tasks without replacing a command the user is still typing. */
    @FXML
    private void handleListQuickAction() {
        submitCommand("list", false);
    }

    /** Opens the command reference without replacing a draft command. */
    @FXML
    private void handleHelpQuickAction() {
        submitCommand("help", false);
    }

    private void submitCommand(String input, boolean clearInput) {
        if (input.isBlank()) {
            return;
        }

        String response = chris.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                response.stripLeading().startsWith("OOPS!!!")
                        ? DialogBox.getErrorDialog(response)
                        : isReferenceCommand(input) ? DialogBox.getReferenceDialog(response)
                                : DialogBox.getChrisDialog(response));
        if (clearInput) {
            userInput.clear();
        }
        userInput.requestFocus();

        if (chris.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            listQuickButton.setDisable(true);
            helpQuickButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(Duration.seconds(1));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    private boolean isReferenceCommand(String input) {
        String command = input.strip().split("\\s+", 2)[0];
        return command.equals("list") || command.equals("find") || command.equals("help");
    }
}
