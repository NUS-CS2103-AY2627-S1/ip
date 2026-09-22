package genie.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Controller for the main Genie chat window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    private Genie genie;
    private final Image userImage = new Image(
            MainWindow.class.getResourceAsStream("/images/pp.png"));
    private final Image genieImage = new Image(
            MainWindow.class.getResourceAsStream("/images/genie.png"));

    /** Injects the command-processing application instance. */
    public void setGenie(Genie genie) {
        assert genie != null : "Genie instance must be provided";
        this.genie = genie;
        dialogContainer.getChildren().add(DialogBox.getGenieDialog(
                "Hello! I'm Genie. What can I do for you?", genieImage));
    }

    /** Displays the user's command and Genie response. */
    @FXML
    private void handleUserInput() {
        assert genie != null : "Genie instance must be initialized before input handling";
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = genie.processCommand(input);
        DialogBox genieDialog = response.startsWith("OOPS!!!")
                ? DialogBox.getErrorDialog(response, genieImage)
                : DialogBox.getGenieDialog(response, genieImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                genieDialog);
        userInput.clear();
        scrollToLatestMessage();

        if (input.equalsIgnoreCase("bye")) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> userInput.getScene().getWindow().hide());
            pause.play();
        }
    }

    /** Scrolls to the newest message after JavaFX lays out the new dialogs. */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> {
            scrollPane.setVvalue(1.0);
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        });
    }
}
