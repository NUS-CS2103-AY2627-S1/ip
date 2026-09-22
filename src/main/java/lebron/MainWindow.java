package lebron;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controller for {@code MainWindow.fxml}: shows the conversation as a
 * scrolling column of {@link DialogBox} rows and forwards typed input to the
 * {@link Lebron} backend.
 */
public class MainWindow {
    /**
     * Prefix Lebron uses on every user-facing error message; see
     * {@link lebron.exception.LebronException}.
     */
    private static final String ERROR_PREFIX = "OOPS!!!";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Lebron lebron;

    private final Image lebronImage = new Image(getClass().getResourceAsStream("/images/lebron.jpg"));

    /** Keeps the scroll pane pinned to the newest message. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the chatbot backend; must be called once before the window is
     * shown.
     *
     * @param lebron the backend to send user input to
     */
    public void setLebron(Lebron lebron) {
        this.lebron = lebron;
    }

    /** Shows Lebron's greeting as the first message in the conversation. */
    public void showGreeting() {
        dialogContainer.getChildren().add(
                DialogBox.getLebronDialog(
                        "Hey, I'm Lebron, your coach for today. What's the game plan?", lebronImage, false));
    }

    /**
     * Reads the text in {@code userInput}, appends a dialog box for it and
     * one for Lebron's response, then clears the field. Closes the
     * application if the command was {@code bye}.
     */
    @FXML
    private void handleUserInput() {
        // Main.start() must call setLebron() before the window is shown, so
        // this can only be null if that wiring is ever broken -- fail loudly
        // here under -ea instead of a confusing NullPointerException below.
        assert lebron != null : "setLebron() must be called before the window is shown";
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = lebron.getResponse(input);
        boolean isError = response.startsWith(ERROR_PREFIX);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getLebronDialog(response, lebronImage, isError));
        userInput.clear();
        if (lebron.isExit()) {
            Platform.exit();
        }
    }
}
