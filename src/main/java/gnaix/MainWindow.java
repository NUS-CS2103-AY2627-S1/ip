package gnaix;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main Gnaix GUI.
 */
public class MainWindow extends AnchorPane {
    private static final double SCROLL_SPEED_MULTIPLIER = 2.5;

    private final Image userImage =
            new Image(getClass().getResourceAsStream("/images/Stewie.png"));

    private final Image gnaixImage =
            new Image(getClass().getResourceAsStream("/images/Brian.png"));

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    @FXML
    private ImageView headerImage;

    private Gnaix gnaix;

    /**
     * Connects the controller to the Gnaix application.
     *
     * @param gnaix Gnaix application instance.
     */
    public void setGnaix(Gnaix gnaix) {
        this.gnaix = gnaix;
    }

    /**
     * Initializes the main window.
     */
    @FXML
    public void initialize() {
        headerImage.setImage(gnaixImage);

        dialogContainer.heightProperty().addListener(observable -> scrollToBottom());
        scrollPane.addEventFilter(ScrollEvent.SCROLL, this::handleScroll);

        addDialogBoxes(DialogBox.getGnaixDialog(
                "Hello. I'm Gnaix.\nWhat do you need?",
                gnaixImage));
        userInput.requestFocus();
    }

    /**
     * Handles a command entered by the user.
     */
    @FXML
    private void handleUserInput() {
        assert gnaix != null : "Gnaix must be initialised before handling input";

        String input = userInput.getText().trim();

        if (input.isEmpty()) {
            return;
        }

        GuiResponse response = gnaix.getGuiResponse(input);

        addDialogBoxes(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getGnaixDialog(response, gnaixImage));

        userInput.clear();

        if (input.equalsIgnoreCase("bye")) {
            sendButton.getScene().getWindow().hide();
        }
    }

    /**
     * Adds dialogs and lets each row track the current conversation width.
     *
     * @param dialogs Dialog rows to add.
     */
    private void addDialogBoxes(DialogBox... dialogs) {
        for (DialogBox dialogBox : dialogs) {
            dialogBox.prefWidthProperty().bind(dialogContainer.widthProperty());
            dialogContainer.getChildren().add(dialogBox);
        }
    }

    /**
     * Scrolls after JavaFX has laid out newly added dialog boxes.
     */
    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    /**
     * Makes mouse-wheel scrolling feel more responsive in long conversations.
     *
     * @param event Scroll event from the conversation area.
     */
    private void handleScroll(ScrollEvent event) {
        double contentHeight = dialogContainer.getBoundsInLocal().getHeight();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double scrollableHeight = contentHeight - viewportHeight;

        if (scrollableHeight <= 0) {
            return;
        }

        double scrollDelta = event.getDeltaY() * SCROLL_SPEED_MULTIPLIER / scrollableHeight;
        double nextValue = scrollPane.getVvalue() - scrollDelta;
        scrollPane.setVvalue(Math.max(0.0, Math.min(1.0, nextValue)));
        event.consume();
    }
}
