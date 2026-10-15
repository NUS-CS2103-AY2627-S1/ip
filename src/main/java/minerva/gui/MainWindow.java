package minerva.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import minerva.Minerva;
import minerva.ui.Ui;

/** Controller for the main GUI. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Minerva minerva;
    private final Ui ui = new Ui();

    // The original DaUser.png was a WebP file with the wrong extension.
    // JavaFX 17 cannot decode WebP, so use the verified PNG supplied by the tutorial.
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.jpeg"));
    private final Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        sendButton.disableProperty().bind(userInput.textProperty().isEmpty());
        userInput.requestFocus();
        dialogContainer.getChildren().addAll(
                createWelcomeScreen(),
                DialogBox.getDukeDialog(ui.getPromptMessage(), dukeImage)
        );
    }

    /** Creates the graphical welcome card shown when the application starts. */
    private VBox createWelcomeScreen() {
        Label title = new Label("MINERVA");
        title.getStyleClass().add("welcome-title");

        Label subtitle = new Label("Your personal task companion");
        subtitle.getStyleClass().add("welcome-subtitle");

        Label hint = new Label("Type help to see what I can do.");
        hint.getStyleClass().add("welcome-hint");

        VBox welcome = new VBox(title, subtitle, hint);
        welcome.getStyleClass().add("welcome-card");
        return welcome;
    }

    /** Injects the Minerva instance. */
    public void setMinerva(Minerva minerva) {
        this.minerva = minerva;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = minerva.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, dukeImage),
                DialogBox.getDukeDialog(ui.getPromptMessage(), dukeImage)
        );
        userInput.clear();
    }
}
