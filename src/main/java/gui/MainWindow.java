package gui;

import gui.DialogBox.DialogType;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import nova.Nova;
/**
 * Main GUI window for Nova.
 */
public class MainWindow extends BorderPane {

    private final Nova nova;

    private final VBox dialogContainer;
    private final TextField userInput;
    private final Button sendButton;
    private final ScrollPane scrollPane;

    /**
     * Creates the main Nova GUI.
     *
     * @param nova Nova chatbot instance
     */
    public MainWindow(Nova nova) {
        this.nova = nova;



        dialogContainer = new VBox(14);
        dialogContainer.setPadding(new Insets(16));

        scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        userInput = new TextField();
        userInput.setPromptText("Enter a command...");
        userInput.setPrefHeight(38);

        sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setPrefHeight(38);
        sendButton.setPadding(new Insets(8, 16, 8, 16));


        BorderPane inputArea = new BorderPane();
        inputArea.setCenter(userInput);
        inputArea.setRight(sendButton);
        inputArea.setPadding(new Insets(10));
        setCenter(scrollPane);
        setBottom(inputArea);

        sendButton.setOnAction(event -> handleUserInput());
        userInput.setOnAction(event -> handleUserInput());

        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) ->
                scrollPane.setVvalue(1.0));

        addNovaMessage(
                "Hello! I'm Nova.\n"
                        + "What can I do for you?"
        );
    }

    /**
     * Handles a command entered by the user.
     */
    private void handleUserInput() {
        String input = userInput.getText().trim();

        if (input.isEmpty()) {
            return;
        }

        addUserMessage(input);

        String response = nova.getResponse(input);

        if (nova.wasLastResponseError()) {
            addErrorMessage(response);
        } else if (nova.wasLastResponseWarning()) {
            addWarningMessage(response);
        } else {
            addNovaMessage(response);
        }

        userInput.clear();
    }

    private void addWarningMessage(String message) {
        DialogBox dialogBox =
                new DialogBox("⚠ " + message, DialogType.WARNING);

        dialogContainer.getChildren().add(dialogBox);
    }


    private void addUserMessage(String message) {
        DialogBox dialogBox =
                new DialogBox(message, DialogType.USER);

        dialogContainer.getChildren().add(dialogBox);
    }

    private void addNovaMessage(String message) {
        DialogBox dialogBox =
                new DialogBox(message, DialogType.NOVA);

        dialogContainer.getChildren().add(dialogBox);
    }

    private void addErrorMessage(String message) {
        DialogBox dialogBox =
                new DialogBox("⚠ " + message, DialogType.ERROR);

        dialogContainer.getChildren().add(dialogBox);
    }
}

