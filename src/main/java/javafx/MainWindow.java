package javafx;

import java.util.Objects;

import cbt.Cbt;
import cbt.CbtResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controls the main chatbot window. */
public class MainWindow extends AnchorPane {
    private static final String WELCOME_MESSAGE = "Mission control online—I'm CBT, your calm task navigator.\n\n"
            + "Set a course with one of these commands:\n"
            + "  todo read a book\n"
            + "  deadline submit report /by 2/12/2026 1800\n"
            + "  event project meeting /from 2/12/2026 1400 /to 2/12/2026 1600\n\n"
            + "You can also use list, sort date, find KEYWORD, mark NUMBER, delete NUMBER, or bye.";

    private final Image cbtImage = loadImage("/images/daCbt.png");
    private final Image userImage = loadImage("/images/daUser.jpeg");

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Cbt cbt;
    private boolean hasShownWelcomeMessage;

    /** Sets up behavior that depends on all FXML controls having been injected. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the chatbot that handles commands entered in this window.
     *
     * @param cbt chatbot instance to use.
     */
    public void setCbt(Cbt cbt) {
        this.cbt = Objects.requireNonNull(cbt);
        if (!hasShownWelcomeMessage) {
            dialogContainer.getChildren().add(DialogBox.getCbtDialog(WELCOME_MESSAGE, cbtImage));
            cbt.getStartupWarning().ifPresent(warning -> dialogContainer.getChildren()
                    .add(DialogBox.getErrorDialog(warning, cbtImage)));
            hasShownWelcomeMessage = true;
        }
        Platform.runLater(userInput::requestFocus);
    }

    /** Creates message bubbles for the user's input and CBT's response. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        CbtResponse response = cbt.getResponseWithStatus(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                response.isError()
                        ? DialogBox.getErrorDialog(response.message(), cbtImage)
                        : DialogBox.getCbtDialog(response.message(), cbtImage));
        userInput.clear();

        exitIfRequested(input, Platform::exit);
    }

    /** Runs the supplied exit action when the input is a bye command. */
    static void exitIfRequested(String input, Runnable exitAction) {
        if (input.trim().equalsIgnoreCase("bye")) {
            exitAction.run();
        }
    }

    /** Returns the introductory help shown when the chatbot starts. */
    static String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /** Loads an image resource, failing early if it is missing. */
    private Image loadImage(String path) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }
}
