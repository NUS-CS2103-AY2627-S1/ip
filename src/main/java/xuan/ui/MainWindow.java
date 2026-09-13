package xuan.ui;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import xuan.Xuan;

/**
 * Controller for the main GUI window of Xuan.
 */
public class MainWindow extends AnchorPane {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private final Image userImage = new Image(
            this.getClass().getResourceAsStream("/images/user.png"));

    private final Image xuanImage = new Image(
            this.getClass().getResourceAsStream("/images/xuan.png"));

    private Xuan xuan;

    /**
     * Initializes the main window after its FXML elements have been loaded.
     */
    @FXML
    public void initialize() {
        dialogContainer.setFillWidth(true);

        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) -> scrollPane.setVvalue(1.0));

        sendButton.setOnMouseEntered(event -> scaleSendButton(0.95));
        sendButton.setOnMouseExited(event -> scaleSendButton(1.0));
    }

    /**
     * Sets the Xuan chatbot instance used by this window
     * and displays the greeting message.
     *
     * @param xuan the Xuan chatbot instance
     */
    public void setXuan(Xuan xuan) {
        this.xuan = xuan;

        dialogContainer.getChildren().add(
                DialogBox.getXuanDialog(
                        xuan.getGreeting(),
                        new ImageView(xuanImage))
        );
    }

    /**
     * Handles the user's input and displays the user message
     * and Xuan's response.
     */
    @FXML
    private void handleUserInput() {
        assert xuan != null;
        String input = userInput.getText();

        if (input.isBlank()) {
            return;
        }

        String response = xuan.getResponse(input);

        boolean isError = response.startsWith("Mission alert - ")
                || response.startsWith("Xuan: Mission alert - ");

        DialogBox userDialog = DialogBox.getUserDialog(
                input, new ImageView(userImage));

        DialogBox xuanDialog;

        if (input.trim().equals("help")) {
            xuanDialog = DialogBox.getXuanHelpDialog(
                    response, new ImageView(xuanImage));
        } else if (isError) {
            xuanDialog = DialogBox.getXuanErrorDialog(
                    response, new ImageView(xuanImage));
        } else {
            xuanDialog = DialogBox.getXuanDialog(
                    response, new ImageView(xuanImage));
        }

        dialogContainer.getChildren().addAll(
                userDialog, xuanDialog);

        userInput.clear();
    }

    /**
     * Changes the size of the send button with a short animation.
     *
     * @param scale the target scale of the send button
     */
    private void scaleSendButton(double scale) {
        ScaleTransition transition =
                new ScaleTransition(Duration.millis(100), sendButton);

        transition.setToX(scale);
        transition.setToY(scale);
        transition.play();
    }
}
