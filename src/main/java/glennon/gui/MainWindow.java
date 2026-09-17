package glennon.gui;

import glennon.CommandResponse;
import glennon.Glennon;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Handles chat interactions while leaving mission logic to Glennon.
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

    private Glennon glennon;

    /**
     * Holds the reading position while JavaFX reflows a resized viewport.
     */
    private Double scrollPositionBeforeResize;

    /**
     * Preserves the relative reading position when either window dimension changes.
     */
    @FXML
    private void initialize() {
        scrollPane.widthProperty().addListener((observable, oldWidth, newWidth) -> preserveScrollPosition());
        scrollPane.heightProperty().addListener((observable, oldHeight, newHeight) -> preserveScrollPosition());
    }

    /**
     * Restores the position after layout, combining width and height changes into one update.
     */
    private void preserveScrollPosition() {
        Scene scene = scrollPane.getScene();
        if (scrollPositionBeforeResize != null || scene == null) {
            return;
        }
        scrollPositionBeforeResize = scrollPane.getVvalue();
        scene.addPostLayoutPulseListener(new Runnable() {
            @Override
            public void run() {
                if (scrollPositionBeforeResize != null) {
                    scrollPane.setVvalue(scrollPositionBeforeResize);
                    scrollPositionBeforeResize = null;
                }
                scene.removePostLayoutPulseListener(this);
            }
        });
    }

    /**
     * Connects a session and displays its greeting or storage error.
     *
     * @param glennon application session used by this window.
     */
    public void setGlennon(Glennon glennon) {
        this.glennon = glennon;
        String welcome = glennon.getWelcome();
        dialogContainer.getChildren().add(glennon.hasStartupError()
                ? DialogBox.createErrorDialog(welcome)
                : DialogBox.createGlennonDialog(welcome));
        userInput.setDisable(glennon.hasStartupError());
        sendButton.setDisable(glennon.hasStartupError());
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Appends the exchange and lets the goodbye remain visible before closing.
     */
    @FXML
    private void handleUserInput() {
        if (glennon.hasExited() || glennon.hasStartupError()) {
            return;
        }
        String input = userInput.getText();
        CommandResponse response = glennon.getCommandResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.createUserDialog(input),
                response.isError() ? DialogBox.createErrorDialog(response.text())
                        : DialogBox.createGlennonDialog(response.text()));
        // Measure new content before scrolling, without reacting to history reflow on resize.
        scrollPane.applyCss();
        scrollPane.layout();
        scrollPositionBeforeResize = null;
        scrollPane.setVvalue(1.0);
        userInput.requestFocus();
        if (response.isError()) {
            userInput.selectAll();
        } else {
            userInput.clear();
        }
        if (glennon.hasExited()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> userInput.getScene().getWindow().hide());
            pause.play();
        }
    }
}
