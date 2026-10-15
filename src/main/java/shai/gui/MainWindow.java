package shai.gui;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import shai.Shai;

/** Controller for the main Shai GUI window. */
public class MainWindow extends AnchorPane {
    /** Full-window layer that centers the decorative background image. */
    @FXML
    private StackPane backgroundLayer;
    /** Decorative image displayed behind the conversation. */
    @FXML
    private ImageView backgroundImage;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Shai shai;

    /** Image displayed beside messages from the user. */
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/lebron.jpg"));
    /** Image displayed beside Shai's replies. */
    private final Image shaiImage = new Image(this.getClass().getResourceAsStream("/images/shai.jpg"));

    /** Initializes bindings and layout behavior for the main window. */
    @FXML
    public void initialize() {
        backgroundImage.fitWidthProperty().bind(backgroundLayer.widthProperty());
        backgroundImage.fitHeightProperty().bind(backgroundLayer.heightProperty());
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                userInput.getText().trim().isEmpty(), userInput.textProperty()));
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Shai instance used to process GUI commands. */
    public void setShai(Shai s) {
        shai = s;
    }

    /** Adds Shai's introductory message to the conversation. */
    public void showGreeting() {
        dialogContainer.getChildren().add(DialogBox.getShaiDialog(shai.getGreeting(), shaiImage));
        String startupReminder = shai.getStartupReminderResponse();
        if (!startupReminder.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getShaiDialog(startupReminder, shaiImage));
        }
    }

    /**
     * Displays the user's input and Shai's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }
        String response = shai.getResponse(input);
        DialogBox responseDialog = shai.wasLastResponseAnError()
                ? DialogBox.getErrorDialog(response, shaiImage)
                : DialogBox.getShaiDialog(response, shaiImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                responseDialog
        );
        userInput.clear();

        if (input.trim().equals("bye")) {
            Stage stage = (Stage) userInput.getScene().getWindow();
            PauseTransition goodbyeDelay = new PauseTransition(Duration.seconds(2));
            goodbyeDelay.setOnFinished(event -> stage.close());
            goodbyeDelay.play();
        }
    }
}
