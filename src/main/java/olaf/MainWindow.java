package olaf;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controls the main chat window and translates JavaFX actions into Olaf commands.
 */
public class MainWindow {
    private static final String USER_IMAGE_PATH = "/images/user-profile-pic.jpg";
    private static final String OLAF_IMAGE_PATH = "/images/chatbot-profile-pic.png";
    private static final double PROFILE_PICTURE_SIZE = 64.0;
    private static final String UNEXPECTED_ERROR_MESSAGE =
            "error: Unable to process the command. See the console for details.";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final BooleanProperty isBusy = new SimpleBooleanProperty(false);
    private final BooleanProperty isConversationEnded = new SimpleBooleanProperty(false);

    private Olaf olaf;
    private Executor commandExecutor;
    private Image userImage;
    private Image olafImage;

    /**
     * Creates a controller whose controls are supplied by FXML before dependencies are injected.
     */
    public MainWindow() {
    }

    @FXML
    private void initialize() {
        userImage = loadImage(USER_IMAGE_PATH);
        olafImage = loadImage(OLAF_IMAGE_PATH);

        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(scrollPane.getVmax()));

        BooleanBinding isInputBlank = Bindings.createBooleanBinding(
                () -> userInput.getText().trim().isEmpty(), userInput.textProperty());
        sendButton.disableProperty().bind(
                isInputBlank.or(isBusy).or(isConversationEnded));
        userInput.disableProperty().bind(isBusy.or(isConversationEnded));
    }

    /**
     * Injects the application facade after the FXML controls have been initialized.
     *
     * @param olaf application facade used to execute commands
     */
    public void setOlaf(Olaf olaf) {
        this.olaf = Objects.requireNonNull(olaf);
        addOlafMessage(olaf.getWelcomeMessage());
    }

    /**
     * Injects the executor used to keep command and storage work off the JavaFX thread.
     *
     * @param commandExecutor executor that runs commands sequentially
     */
    public void setCommandExecutor(Executor commandExecutor) {
        this.commandExecutor = Objects.requireNonNull(commandExecutor);
    }

    /**
     * Displays a startup failure and prevents commands from being submitted.
     *
     * @param message formatted startup error to display
     */
    public void showStartupError(String message) {
        addOlafMessage(Objects.requireNonNull(message));
        isConversationEnded.set(true);
    }

    /**
     * Places keyboard focus in the command field when input is available.
     */
    public void requestInputFocus() {
        if (!isConversationEnded.get()) {
            userInput.requestFocus();
        }
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (!canSubmitCommand(input)) {
            return;
        }

        dialogContainer.getChildren().add(ChatMessage.createUserMessage(input, userImage));
        userInput.clear();
        isBusy.set(true);

        Task<Olaf.CommandResult> commandTask = createCommandTask(input);
        try {
            commandExecutor.execute(commandTask);
        } catch (RejectedExecutionException exception) {
            handleUnexpectedFailure(exception);
        }
    }

    private boolean canSubmitCommand(String input) {
        boolean isReady = olaf != null && commandExecutor != null;
        boolean canAcceptInput = !isBusy.get() && !isConversationEnded.get();
        return !input.isEmpty() && isReady && canAcceptInput;
    }

    private Task<Olaf.CommandResult> createCommandTask(String input) {
        Task<Olaf.CommandResult> commandTask = new Task<>() {
            @Override
            protected Olaf.CommandResult call() {
                return olaf.executeCommand(input);
            }
        };
        commandTask.setOnSucceeded(event -> handleCommandResult(commandTask.getValue()));
        commandTask.setOnFailed(event -> handleUnexpectedFailure(commandTask.getException()));
        return commandTask;
    }

    private void handleCommandResult(Olaf.CommandResult result) {
        isBusy.set(false);
        switch (result.status()) {
            case CONTINUE:
                addOlafMessage(result.message());
                requestInputFocus();
                break;
            case EXIT_REQUESTED:
                Platform.exit();
                break;
            case FATAL_ERROR:
                addOlafMessage(result.message());
                isConversationEnded.set(true);
                break;
            default:
                throw new IllegalStateException("Unsupported command status: " + result.status());
        }
    }

    private void handleUnexpectedFailure(Throwable exception) {
        System.err.println("Unexpected failure while processing an Olaf command.");
        exception.printStackTrace(System.err);
        addOlafMessage(UNEXPECTED_ERROR_MESSAGE);
        isBusy.set(false);
        requestInputFocus();
    }

    private void addOlafMessage(String message) {
        dialogContainer.getChildren().add(ChatMessage.createOlafMessage(message, olafImage));
    }

    private Image loadImage(String path) {
        URL resource = MainWindow.class.getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Required profile picture is missing: " + path);
        }

        Image image = new Image(resource.toExternalForm(), PROFILE_PICTURE_SIZE,
                PROFILE_PICTURE_SIZE, true, true, false);
        if (image.isError()) {
            throw new IllegalStateException("Unable to load profile picture: " + path,
                    image.getException());
        }
        return image;
    }
}
