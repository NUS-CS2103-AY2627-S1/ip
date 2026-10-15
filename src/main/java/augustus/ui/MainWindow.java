package augustus.ui;

import augustus.Augustus;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main Augustus GUI.
 */
public class MainWindow extends AnchorPane {

    private static final double EXIT_DELAY_SECONDS = 1.0;

    private static final String WELCOME_MESSAGE =
            "Greetings. You stand before Augustus.\n"
                    + "State your task, and it shall be recorded.\n"
                    + "Try: todo | deadline | event | list | find | bye";
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private Augustus augustus;

    private final Image userImage =
            new Image(this.getClass().getResourceAsStream("/images/PublicUser.png"));
    private final Image augustusImage =
            new Image(this.getClass().getResourceAsStream("/images/AugustusPic.png"));

    /**
     * Initializes the main window.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Augustus chatbot instance into the GUI.
     *
     * @param augustus Augustus chatbot instance
     */
    public void setAugustus(Augustus augustus) {
        this.augustus = augustus;

        dialogContainer.getChildren().add(
                DialogBox.getWelcomeDialog(WELCOME_MESSAGE, augustusImage)
        );
    }

    /**
     * Processes user input and displays the user's message and Augustus's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        if (input.isBlank()) {
            return;
        }

        String response = augustus.getResponse(input);

        boolean isError = response.startsWith("ERROR:")
                || response.startsWith("Augustus does not recognise that command.");

        DialogBox augustusDialog;

        if (isError) {
            augustusDialog = DialogBox.getErrorDialog(response, augustusImage);
        } else {
            augustusDialog = DialogBox.getAugustusDialog(response, augustusImage);
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                augustusDialog
        );

        userInput.clear();

        if (input.trim().equalsIgnoreCase("bye")) {
            PauseTransition delay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
