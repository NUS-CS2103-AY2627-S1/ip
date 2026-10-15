package samantha.ui;

import java.io.IOException;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import samantha.Samantha;

/**
 * Controls Samantha's main JavaFX conversation window.
 */
public class MainWindow extends AnchorPane {
    private static final double FAREWELL_DELAY_MILLIS = 700;
    private static final double HELP_WINDOW_MIN_WIDTH = 680;
    private static final double HELP_WINDOW_MIN_HEIGHT = 520;
    private static final String HELP_COMMAND = "help";
    private static final String HELP_OPENED_MESSAGE = "I've opened a clearer guide for you.";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Button helpButton;

    private final Image userImage = loadImage("/images/Theodore.png");
    private final Image samanthaImage = loadImage("/images/SamanthaAvatar.png");
    private Samantha samantha;
    private Stage helpStage;
    private boolean hasShownInitialResponses;

    /**
     * Initializes the dialog container's automatic scroll behavior.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the backend used to process commands and displays the greeting.
     *
     * @param samantha backend application instance
     */
    public void setSamantha(Samantha samantha) {
        this.samantha = samantha;
        if (!hasShownInitialResponses) {
            List<String> initialResponses = samantha.getInitialResponses();
            boolean isGreeting = true;
            for (String response : initialResponses) {
                dialogContainer.getChildren().add(createSamanthaReply(response, !isGreeting));
                isGreeting = false;
            }
            hasShownInitialResponses = true;
        }
        userInput.requestFocus();
    }

    /**
     * Processes the command entered in the text field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        if (HELP_COMMAND.equalsIgnoreCase(input)) {
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(input, userImage),
                    DialogBox.getSamanthaDialog(HELP_OPENED_MESSAGE, samanthaImage));
            openHelpWindow();
            userInput.clear();
            userInput.requestFocus();
            return;
        }

        String response = samantha.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                createSamanthaReply(response, samantha.isLastResponseError()));
        userInput.clear();
        userInput.requestFocus();

        if (samantha.shouldOpenHelpGuide()) {
            openHelpWindow();
        }

        if (samantha.isExitRequested()) {
            closeAfterFarewell();
        }
    }

    /**
     * Opens the command guide from the title-row help control.
     */
    @FXML
    private void openHelpFromButton() {
        openHelpWindow();
    }

    /**
     * Opens the readable command guide in a separate, reusable window.
     */
    private void openHelpWindow() {
        if (samantha != null) {
            samantha.resetUnknownCommandStreak();
        }
        if (helpStage != null && helpStage.isShowing()) {
            helpStage.toFront();
            helpStage.requestFocus();
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/HelpWindow.fxml"));
            Parent helpWindow = fxmlLoader.load();
            Stage newHelpStage = new Stage();
            newHelpStage.setTitle("Samantha Help");
            newHelpStage.setMinWidth(HELP_WINDOW_MIN_WIDTH);
            newHelpStage.setMinHeight(HELP_WINDOW_MIN_HEIGHT);
            newHelpStage.initOwner(userInput.getScene().getWindow());
            newHelpStage.setScene(new Scene(helpWindow));
            newHelpStage.setOnHidden(event -> helpStage = null);
            helpStage = newHelpStage;
            helpStage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to open the help window.", e);
        }
    }

    /**
     * Leaves the farewell visible before closing the JavaFX window.
     */
    private void closeAfterFarewell() {
        PauseTransition pause = new PauseTransition(Duration.millis(FAREWELL_DELAY_MILLIS));
        pause.setOnFinished(event -> {
            Stage stage = (Stage) userInput.getScene().getWindow();
            stage.close();
        });
        pause.play();
    }

    /**
     * Creates Samantha's reply using an error style when the command failed.
     *
     * @param response reply text
     * @param isError whether the reply should be highlighted as an error
     * @return Samantha dialog box
     */
    private DialogBox createSamanthaReply(String response, boolean isError) {
        if (isError && samantha.isLastResponseUnknownCommand()) {
            return DialogBox.getSamanthaErrorDialog(response, samanthaImage, this::openHelpWindow);
        }
        if (isError) {
            return DialogBox.getSamanthaErrorDialog(response, samanthaImage);
        }
        return DialogBox.getSamanthaDialog(response, samanthaImage);
    }

    /**
     * Loads an image from the classpath resources.
     *
     * @param resourcePath absolute classpath path
     * @return loaded image
     */
    private static Image loadImage(String resourcePath) {
        return new Image(MainWindow.class.getResourceAsStream(resourcePath));
    }
}
