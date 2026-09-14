package arrodes.ui.gui;

import java.io.IOException;

import arrodes.ui.Ui;
import arrodes.ui.gui.components.DialogBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


/**
 * JavaFX controller for the main Arrodes graphical user interface.
 *
 * <p>The layout and controls are defined in {@code Gui.fxml}. JavaFX creates
 * this controller when the FXML file is loaded and injects the controls into
 * the fields below.</p>
 */
public class Gui extends AnchorPane implements Ui {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField textInput;
    @FXML
    private Button sendButton;

    /** Callback that receives commands submitted through the GUI. */
    private GuiListener inputListener;

    /**
     * Creates the GUI and loads its controls from {@code Gui.fxml}.
     *
     * <p>The FXML loader uses this object as both the root and the controller,
     * so callers can use {@code new Gui()} directly as the scene root.</p>
     */
    public Gui() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Gui.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Arrodes GUI layout", exception);
        }
        showOnLoadMessage();
    }
    /**
     * Registers the callback that receives input submitted through the GUI.
     *
     * @param inputListener callback invoked with the submitted user input
     */
    public void attachInputListener(GuiListener inputListener) {
        this.inputListener = inputListener;
    }

    /** Performs setup after all {@code @FXML} fields have been injected. */
    @FXML
    public void initialize() {
        configureSendButton();
        configureTextInput();
        configureAutoScroll();
    }

    /** Configures the send button to process submitted input. */
    private void configureSendButton() {
        sendButton.setOnAction((event) -> {
            handleUserInput();
        });
    }

    /** Configures the text field to process input submitted with Enter. */
    private void configureTextInput() {
        textInput.setOnAction((event) -> {
            handleUserInput();
        });
    }

    /** Keeps the conversation view scrolled to the latest message. */
    private void configureAutoScroll() {
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));
    }

    /** Displays submitted input and sends it to the application listener. */
    private void handleUserInput() {
        String userInput = readUserInput();
        displayUserInput(userInput);
        if (inputListener == null) {
            showError("Arrodes is not ready to receive requests yet.");
            return;
        }
        inputListener.execute(userInput);
    }

    /** Adds a submitted user message to the conversation view. */
    private void displayUserInput(String userInput) {
        dialogContainer.getChildren().addAll(DialogBox.getUserDialogBox(userInput));
    }

    @Override
    public String readUserInput() {
        String userInput = textInput.getText();
        textInput.clear();
        return userInput;
    }
    /**
     * Displays an arbitrary application message.
     * @param message text to print
     */
    @Override
    public void showMessage(String message) {
        dialogContainer.getChildren().addAll(DialogBox.getArrodesDialogBox(message));
    }

    /**
     * Displays an application error with a warning treatment.
     *
     * @param message error message to display
     */
    public void showError(String message) {
        dialogContainer.getChildren().addAll(DialogBox.getErrorDialogBox(message));
    }

    /** Displays the startup banner and greeting. */
    @Override
    public void showOnLoadMessage() {
        showMessage("""
            The mirror opens its eye...
            The Stigmata from the Primordial Land
            The Great Arrodes stands before you.
            Speak your request!""");
    }

    /** Displays the standard separator line. */
    @Override
    public void showSeparator() {
        showMessage("....");
    }

    /** Displays the application farewell. */
    @Override
    public void showOnExitMessage() {
        showMessage("The mirror shall close its eye. Until your next request.");
    }
}
