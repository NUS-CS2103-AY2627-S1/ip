package crystal.gui;

import java.io.InputStream;
import java.util.Objects;

import crystal.Crystal;
import crystal.parser.CommandType;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main messaging window and forwards user input to Crystal.
 */
public final class MainWindow {
    /** Introductory message displayed when the window opens. */
    private static final String WELCOME_MESSAGE = "Hiii!!! I'm Crystal, your sparkly task bestie! "
            + "Send me a command to get started!!\n\n"
            + "You can try: todo hw, list, find book, or bye (closes window).\n"
            + "Enter 'help' whenever you need my command guide!";

    /** Prefix included in console-oriented Crystal responses. */
    private static final String CRYSTAL_PREFIX = "Crystal: ";

    /** Prefix identifying a user-facing error response from Crystal. */
    private static final String ERROR_PREFIX = "Crystal: Oopsies!!! ";

    /** Time the farewell remains visible before the window closes. */
    private static final Duration WINDOW_CLOSE_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane messageScrollPane;

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField commandInput;

    @FXML
    private HBox messageComposer;

    /** Crystal instance that owns the current task session. */
    private Crystal crystal;

    /** Avatar displayed beside Crystal's messages. */
    private Image crystalAvatar;

    /** Avatar displayed beside the user's messages. */
    private Image userAvatar;

    /**
     * Creates the controller instance used by the FXML loader.
     */
    public MainWindow() {
    }

    /**
     * Configures automatic scrolling after the FXML controls are injected.
     */
    @FXML
    private void initialize() {
        messageContainer.heightProperty().addListener((observableHeight, previousHeight, currentHeight) ->
                messageScrollPane.setVvalue(1.0));
    }

    /**
     * Supplies Crystal's command service and displays the initial greeting.
     *
     * @param crystal Crystal instance for this window's session.
     */
    public void setCrystal(Crystal crystal) {
        this.crystal = crystal;
        crystalAvatar = loadImage("/images/crystal-avatar.png");
        userAvatar = loadImage("/images/user-avatar.png");
        messageContainer.getChildren().add(
                DialogBox.createCrystalDialog(WELCOME_MESSAGE, crystalAvatar));
        Platform.runLater(commandInput::requestFocus);
    }

    /**
     * Displays a nonblank command and Crystal's response, then clears the composer.
     */
    @FXML
    private void handleUserInput() {
        String userCommand = commandInput.getText();
        boolean shouldCloseWindow = isExitCommand(userCommand);
        String completeResponse = crystal.getResponse(userCommand);
        String crystalResponse = formatCrystalResponse(completeResponse);
        DialogBox crystalDialog = isErrorResponse(completeResponse)
                ? DialogBox.createCrystalErrorDialog(crystalResponse, crystalAvatar)
                : DialogBox.createCrystalDialog(crystalResponse, crystalAvatar);
        if (!userCommand.isBlank()) {
            messageContainer.getChildren().add(
                    DialogBox.createUserDialog(userCommand, userAvatar));
        }
        messageContainer.getChildren().add(crystalDialog);
        commandInput.clear();
        if (shouldCloseWindow) {
            scheduleWindowClose();
        } else {
            commandInput.requestFocus();
        }
    }

    /**
     * Returns whether the command is the exact instruction that exits Crystal.
     *
     * @param userCommand stripped command entered by the user.
     * @return {@code true} only for the exit command.
     */
    static boolean isExitCommand(String userCommand) {
        return userCommand.equals(CommandType.EXIT.getKeyword());
    }

    /**
     * Returns whether a response begins with Crystal's error marker.
     *
     * @param response complete response produced by Crystal.
     * @return {@code true} when the response reports an error.
     */
    static boolean isErrorResponse(String response) {
        return response.startsWith(ERROR_PREFIX);
    }

    /**
     * Disables further input and closes the window after the farewell delay.
     */
    private void scheduleWindowClose() {
        messageComposer.setDisable(true);
        PauseTransition closeDelay = new PauseTransition(WINDOW_CLOSE_DELAY);
        closeDelay.setOnFinished(finishedEvent ->
                messageComposer.getScene().getWindow().hide());
        closeDelay.play();
    }

    /**
     * Removes console-only prefixes and indentation from a response shown in a named bubble.
     *
     * @param response complete response produced by Crystal.
     * @return response formatted for the graphical message bubble.
     */
    private static String formatCrystalResponse(String response) {
        return response.replace(CRYSTAL_PREFIX, "")
                .replace("\n         ", "\n");
    }

    /**
     * Loads a required image resource from the application classpath.
     *
     * @param resourcePath absolute classpath location of the image.
     * @return decoded image.
     */
    private static Image loadImage(String resourcePath) {
        InputStream imageStream = Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(resourcePath),
                "Missing image resource: " + resourcePath);
        return new Image(imageStream);
    }
}
