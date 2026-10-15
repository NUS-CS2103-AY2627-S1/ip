package charliek.ui;

import java.net.URL;
import java.nio.file.Path;

import charliek.command.Command;
import charliek.exception.CharlieKException;
import charliek.exception.TaskStorageException;
import charliek.model.SampleData;
import charliek.model.TaskList;
import charliek.parser.Parser;
import charliek.storage.Storage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controller for the main CharlieK chat window.
 *
 * <p>The controller delegates command interpretation and task management to
 * the existing application classes. It only translates command input and
 * rendered responses into JavaFX dialog boxes.</p>
 */
public class MainWindow {
    /**
     * Resource path for the user's avatar.
     */
    private static final String USER_IMAGE_PATH = "/images/user.png";

    /**
     * Resource path for the chatbot's avatar.
     */
    private static final String CHATBOT_IMAGE_PATH = "/images/chatbot.png";

    /**
     * Error shown when the user submits an empty GUI input field.
     */
    private static final String EMPTY_INPUT_ERROR =
            "That command format is not winning. Use 'help' to check the playbook.";

    /**
     * Scrolls through the conversation history.
     */
    @FXML
    private ScrollPane scrollPane;

    /**
     * Holds dialog boxes in chronological order.
     */
    @FXML
    private VBox dialogContainer;

    /**
     * Accepts commands entered by the user.
     */
    @FXML
    private TextField userInput;

    /**
     * Sends the command currently in {@link #userInput}.
     */
    @FXML
    private Button sendButton;

    /**
     * Stores tasks shared by all commands in this GUI session.
     */
    private final TaskList tasks;

    /**
     * Persists tasks to the same file used by the console application.
     */
    private final Storage storage;

    /**
     * Parses commands and executes them against the shared task state.
     */
    private final Parser parser;

    /**
     * Captures the existing response formatting without writing to standard output.
     */
    private final Ui ui;

    /**
     * Avatar shown beside user messages.
     */
    private final Image userImage;

    /**
     * Avatar shown beside chatbot messages.
     */
    private final Image chatbotImage;

    /**
     * Captures one command's response before it is rendered as one dialog.
     */
    private StringBuilder responseBuffer;

    /**
     * Tracks whether the current command response contains an error.
     */
    private boolean responseContainsError;

    /**
     * Creates the controller's domain collaborators before FXML injection.
     */
    public MainWindow() {
        tasks = new TaskList();
        storage = new Storage(Path.of("data/charliek.csv"));
        userImage = loadImage(USER_IMAGE_PATH);
        chatbotImage = loadImage(CHATBOT_IMAGE_PATH);
        ui = new Ui(this::captureResponse, ignored -> responseContainsError = true);
        parser = new Parser(tasks, ui, storage);
    }

    /**
     * Sets up scrolling and adds the initial chatbot message.
     */
    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);
        dialogContainer.heightProperty().addListener(observable ->
                Platform.runLater(() -> scrollPane.setVvalue(1.0)));
        showInitialMessage();
    }

    /**
     * Handles both the Send button and the Enter key in the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            userInput.clear();
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(EMPTY_INPUT_ERROR, chatbotImage));
            return;
        }

        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        userInput.clear();

        boolean shouldExit = false;
        responseBuffer = new StringBuilder();
        try {
            Command command = parser.parse(input);
            command.execute();
            shouldExit = command.isExit();
        } catch (CharlieKException exception) {
            ui.showError(exception.getMessage());
        } catch (RuntimeException exception) {
            ui.showProcessingError();
        } finally {
            String response = responseBuffer.toString().stripTrailing();
            responseBuffer = null;
            boolean isErrorResponse = responseContainsError;
            responseContainsError = false;
            if (!response.isBlank()) {
                dialogContainer.getChildren().add(isErrorResponse
                        ? DialogBox.getErrorDialog(response, chatbotImage)
                        : DialogBox.getChatbotDialog(response, chatbotImage));
            }
        }

        if (shouldExit) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /**
     * Loads persisted tasks and reports a load failure in the initial message.
     */
    private void showInitialMessage() {
        String message = "Hello! I'm CharlieK." + System.lineSeparator()
                + "What can I do for you? Let's make this task list great again!";
        boolean messageContainsError = false;
        try {
            tasks.replaceWith(storage.loadOrCreate(SampleData.create()));
        } catch (TaskStorageException exception) {
            message += System.lineSeparator() + System.lineSeparator() + exception.getMessage();
            messageContainsError = true;
        } catch (RuntimeException exception) {
            message += System.lineSeparator() + System.lineSeparator()
                    + "I couldn't load saved tasks because the saved data is invalid.";
            messageContainsError = true;
        }
        dialogContainer.getChildren().add(messageContainsError
                ? DialogBox.getErrorDialog(message, chatbotImage)
                : DialogBox.getChatbotDialog(message, chatbotImage));
    }

    /**
     * Loads an avatar from the application's resources.
     *
     * @param resourcePath the absolute classpath path of the image.
     * @return the loaded image.
     * @throws IllegalStateException when the resource cannot be found.
     */
    private static Image loadImage(String resourcePath) {
        URL imageResource = MainWindow.class.getResource(resourcePath);
        if (imageResource == null) {
            throw new IllegalStateException("Unable to find image resource: " + resourcePath);
        }
        return new Image(imageResource.toExternalForm());
    }

    /**
     * Appends output produced by command collaborators to the current response.
     */
    private void captureResponse(String output) {
        if (responseBuffer != null) {
            responseBuffer.append(output);
        }
    }
}
