package milo.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import milo.Milo;
import milo.Response;

/** Controller for Milo's main GUI. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Milo milo;
    private final Image userImage = loadImage("/images/User.png");
    private final Image miloImage = loadImage("/images/Milo.png");

    /** Focuses the command field after window creation, leaving the scroll position user-controlled. */
    @FXML
    public void initialize() {
        Platform.runLater(userInput::requestFocus);
    }

    /** Injects Milo after the FXML view has loaded. */
    public void setMilo(Milo newMilo) {
        milo = newMilo;
        Response greeting = milo.getGuiGreeting();
        dialogContainer.getChildren().add(
                DialogBox.getMiloDialog(greeting.displayText(), miloImage, greeting.isError()));
    }

    /** Displays nonblank commands, retaining failed input so it can be corrected. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            userInput.requestFocus();
            return;
        }
        Response response = milo.getGuiResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getMiloDialog(formatForDialog(response.displayText()), miloImage, response.isError()));
        if (!response.isError()) {
            userInput.clear();
        }
        userInput.requestFocus();
        userInput.positionCaret(userInput.getLength());
        scrollToLatestReply();
    }

    /** Reveals a new reply after layout without binding or restricting manual scrolling. */
    private void scrollToLatestReply() {
        Platform.runLater(() -> {
            // Measure wrapped messages before scrolling to the updated bottom of the conversation.
            scrollPane.applyCss();
            scrollPane.layout();
            scrollPane.setVvalue(scrollPane.getVmax());
        });
    }

    /** Removes indentation that was only needed by the legacy console response layout. */
    private String formatForDialog(String response) {
        return String.join("\n", response.lines().map(String::stripLeading).toList());
    }

    /** Loads an avatar from the resources folder, or returns a blank placeholder when it is absent. */
    private Image loadImage(String resourcePath) {
        if (getClass().getResourceAsStream(resourcePath) == null) {
            return new WritableImage(1, 1);
        }
        return new Image(getClass().getResourceAsStream(resourcePath));
    }
}
