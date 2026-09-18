package habi;

import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Controls command submission and dialog updates in HABI's main window. */
public class MainWindow {
    private static final String GREETING =
            "Hello! I'm HABI, your steady habit-building companion.\nWhat small step can we plan today?";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private Habi habi;

    /** Configures automatic scrolling and shows HABI's greeting. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
        dialogContainer.getChildren().add(DialogBox.getHabiDialog(GREETING));
    }

    /**
     * Supplies the command-handling application instance.
     *
     * @param habi HABI instance used to execute commands.
     */
    public void setHabi(Habi habi) {
        this.habi = Objects.requireNonNull(habi);
        String startupError = habi.getStartupError();
        if (startupError != null) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(startupError));
        }
    }

    /** Gives keyboard focus to the command field after the window opens. */
    void focusInput() {
        userInput.requestFocus();
    }

    /** Submits the current command and appends both sides of the exchange. */
    @FXML
    private void handleUserInput() {
        if (habi == null) {
            throw new IllegalStateException("HABI has not been initialized.");
        }

        String input = userInput.getText().trim();
        String response = habi.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                response.startsWith("OOPS!")
                        ? DialogBox.getErrorDialog(response)
                        : DialogBox.getHabiDialog(response));
        userInput.clear();
        userInput.requestFocus();

        if (input.equals("bye")) {
            PauseTransition exitDelay = new PauseTransition(Duration.millis(650));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }
}
