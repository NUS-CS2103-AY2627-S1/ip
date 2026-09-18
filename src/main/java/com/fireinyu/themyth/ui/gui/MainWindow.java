package com.fireinyu.themyth.ui.gui;

import java.util.function.DoubleSupplier;

import com.fireinyu.themyth.TheMyth;
import com.fireinyu.themyth.responses.Response;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final double EXIT_DELAY_SECONDS = 2.0;

    @FXML
    private ImageView linusOverlay;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private TheMyth theMyth;
    private LinusAnimator linusAnimator;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image theMythDefaultImage = new Image(getClass().getResourceAsStream("/images/DaMyth.png"));
    private final Image theMythHappyImage = new Image(getClass().getResourceAsStream("/images/DaMythHappy.png"));
    private final Image theMythAngryImage = new Image(getClass().getResourceAsStream("/images/DaMythAngry.png"));
    private Image theMythImage = theMythDefaultImage;

    /**
     * Constructs a new {@code MainWindow}.
     * This constructor is called by the FXML loader to instantiate the controller.
     */
    public MainWindow() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        // These assertions ensure that the FXML loader has injected the required UI components.
        assert linusOverlay != null : "fx:id=\"linusOverlay\" was not injected: check your FXML file.";
        assert scrollPane != null : "fx:id=\"scrollPane\" was not injected: check your FXML file.";
        assert dialogContainer != null : "fx:id=\"dialogContainer\" was not injected: check your FXML file.";
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        DoubleSupplier widthSupplier = () -> getWidth() > 0 ? getWidth() : LinusAnimator.FALLBACK_WIDTH;
        DoubleSupplier heightSupplier = () -> scrollPane.getHeight() > 0
                ? scrollPane.getHeight() : LinusAnimator.FALLBACK_HEIGHT;
        linusAnimator = new LinusAnimator(linusOverlay, widthSupplier, heightSupplier);
        linusAnimator.start();
        DialogBox.addOnDialogCreatedListener(linusAnimator::shakeViolently);
    }

    /**
     * Returns the animator controlling the linus overlay.
     *
     * @return the linus animator.
     */
    LinusAnimator getLinusAnimator() {
        return linusAnimator;
    }

    /**
     * Sets the TheMyth instance for the main window.
     *
     * @param theMyth The application logic instance.
     */
    public void setTheMyth(TheMyth theMyth) {
        assert theMyth != null;
        this.theMyth = theMyth;
        executeResponse(theMyth.start());
    }

    /**
     * Returns the current profile image used for TheMyth.
     *
     * @return the current {@link Image}.
     */
    Image getTheMythImage() {
        return theMythImage;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing TheMyth's reply
     * and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage)
        );

        // Application logic must be injected before handling user input.
        assert theMyth != null;
        Response response = theMyth.handleInput(input);

        assert response != null;
        executeResponse(response);
    }

    /**
     * Displays a response using its mood and schedules application exit when requested.
     */
    private void executeResponse(Response response) {
        theMythImage = switch (response.getMood()) {
            case HAPPY -> theMythHappyImage;
            case ANGRY -> theMythAngryImage;
            default -> theMythDefaultImage;
        };
        dialogContainer.getChildren().addAll(
                DialogBox.getTheMythDialog(response.getBody(), theMythImage)
        );
        userInput.clear();
        if (response.doExit()) {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(EXIT_DELAY_SECONDS), event -> Platform.exit())
            );
            timeline.setCycleCount(1);
            timeline.play();
        }
    }
}
