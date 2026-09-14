package nova.gui;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import nova.Nova;
import nova.Reply;

/**
 * Controller for the main window: shows the conversation and passes what the
 * user types to the chatbot.
 */
public class MainWindow extends AnchorPane {
    /** How long the goodbye stays on screen before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.2);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Nova nova;

    private final Image novaMark = new Image(getClass().getResourceAsStream("/images/DaNova.png"));

    /** Creates the controller. JavaFX requires a public no-argument constructor. */
    public MainWindow() {
    }

    /** Keeps the newest message in view as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Attaches the chatbot and shows its opening messages.
     *
     * @param nova the chatbot this window talks to
     */
    public void setNova(Nova nova) {
        this.nova = nova;
        addNovaDialog(nova.getGreeting());
        addNovaDialog(nova.loadTasks());
    }

    /**
     * Sends whatever is in the text field to the chatbot and shows both sides
     * of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        userInput.clear();
        if (input.isEmpty()) {
            return;
        }

        add(DialogBox.getUserDialog(input));
        addNovaDialog(nova.getResponse(input));

        if (nova.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition wait = new PauseTransition(EXIT_DELAY);
            wait.setOnFinished(event -> getScene().getWindow().hide());
            wait.play();
        }
    }

    /** Adds a reply from the chatbot, unless it had nothing to say. */
    private void addNovaDialog(Reply reply) {
        if (reply.isEmpty()) {
            return;
        }
        add(DialogBox.getNovaDialog(reply.text(), novaMark, reply.isError()));
    }

    /** Adds a bubble and lets it reflow with the window. */
    private void add(DialogBox box) {
        box.wrapWithin(dialogContainer.widthProperty());
        dialogContainer.getChildren().add(box);
    }
}
