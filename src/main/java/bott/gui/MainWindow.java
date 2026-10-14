package bott.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import bott.Bott;

/**
 * The chat window: a scrolling transcript of {@link DialogBox} bubbles
 * above a text field and a Report button. Each line the user enters is
 * passed to a {@link Bott} instance and its reply shown as a new bubble.
 */
public class MainWindow extends VBox {

    /** How long the goodbye message stays visible before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);
    private static final double CONTROL_SPACING = 8;
    private static final Insets CONTROL_PADDING = new Insets(8);

    /** Camo-drab background behind the transcript, matching Sergeant Bott's colour scheme. */
    private static final Background BACKGROUND =
            new Background(new BackgroundFill(Color.web("#e9e6d8"), null, null));

    private final Bott bott;
    private final VBox dialogContainer = new VBox();
    private final TextField userInput = new TextField();
    private final Button sendButton = new Button("Report");

    /**
     * Builds the window and shows Sergeant Bott's greeting.
     *
     * @param bott Chatbot that answers the user's commands.
     */
    public MainWindow(Bott bott) {
        this.bott = bott;

        setBackground(BACKGROUND);

        ScrollPane scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setBackground(BACKGROUND);
        dialogContainer.setBackground(BACKGROUND);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        // Keep the newest message in view as the transcript grows.
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));

        userInput.setPromptText("Awaiting your orders, recruit... e.g. todo read book");
        userInput.setOnAction(event -> handleUserInput());
        sendButton.setOnAction(event -> handleUserInput());
        HBox.setHgrow(userInput, Priority.ALWAYS);
        HBox inputRow = new HBox(CONTROL_SPACING, userInput, sendButton);
        inputRow.setPadding(CONTROL_PADDING);
        inputRow.setBackground(BACKGROUND);

        getChildren().addAll(scrollPane, inputRow);
        dialogContainer.getChildren().add(DialogBox.getBottDialog(bott.getGreeting()));
    }

    /**
     * Sends the current input to Bott, shows both the command and the
     * reply, and, if the command was "bye", closes the window shortly
     * after so the farewell stays readable.
     */
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getBottDialog(bott.getResponse(input)));
        userInput.clear();

        if (Bott.isExitCommand(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition pause = new PauseTransition(EXIT_DELAY);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}
