package lloyd.gui;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lloyd.Lloyd;
import lloyd.LloydMood;
import lloyd.LloydResponse;

/** Displays and coordinates Lloyd's JavaFX user interface. */
public class Main extends Application {
    private static final double WINDOW_WIDTH = 400.0;
    private static final double WINDOW_HEIGHT = 600.0;
    private static final double MIN_WINDOW_WIDTH = 350.0;
    private static final double MIN_WINDOW_HEIGHT = 450.0;
    private static final double INPUT_HEIGHT = 55.0;

    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox dialogContainer = new VBox();
    private final TextField userInput = new TextField();
    private final Button sendButton = new Button("Send");
    private final CommandHistory commandHistory = new CommandHistory();

    private Lloyd lloyd;

    /** Builds and displays the main Lloyd window. */
    @Override
    public void start(Stage stage) {
        configureConversationArea();
        configureInputArea();

        AnchorPane mainLayout = createMainLayout();
        Scene scene = new Scene(mainLayout, WINDOW_WIDTH, WINDOW_HEIGHT);

        stage.setScene(scene);
        stage.setTitle("Lloyd");
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setResizable(true);
        stage.show();

        try {
            lloyd = new Lloyd(Path.of("data", "lloyd.txt"));
            dialogContainer.getChildren().add(
                    DialogBox.createLloydDialog(
                            lloyd.getGreeting(), LloydMood.CONFIDENT));
        } catch (IOException e) {
            dialogContainer.getChildren().add(DialogBox.createLloydDialog(
                    "Unable to load tasks from data/lloyd.txt.\n"
                            + "Check that the file is readable and contains valid task data.",
                    LloydMood.ALARMED));
            disableInput();
        }
    }

    /** Configures the scrolling list that contains conversation messages. */
    private void configureConversationArea() {
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        dialogContainer.setPadding(new Insets(8.0));
        dialogContainer.setSpacing(4.0);

        scrollPane.setContent(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Configures command submission from the text field and button. */
    private void configureInputArea() {
        userInput.setPromptText("Enter a command...");
        userInput.setOnAction(event -> handleUserInput());
        userInput.setOnKeyPressed(this::handleHistoryNavigation);
        userInput.setMaxWidth(Double.MAX_VALUE);
        userInput.setPrefHeight(INPUT_HEIGHT);

        sendButton.setOnAction(event -> handleUserInput());
        sendButton.setPrefSize(70.0, INPUT_HEIGHT);
    }

    /** Moves backward or forward through submitted commands using the arrow keys. */
    private void handleHistoryNavigation(KeyEvent event) {
        String recalledInput;
        if (event.getCode() == KeyCode.UP) {
            recalledInput = commandHistory.previous(userInput.getText());
        } else if (event.getCode() == KeyCode.DOWN) {
            recalledInput = commandHistory.next(userInput.getText());
        } else {
            return;
        }

        userInput.setText(recalledInput);
        userInput.positionCaret(recalledInput.length());
        event.consume();
    }

    /**
     * Creates the anchored layout used by the application window.
     *
     * @return Configured main layout.
     */
    private AnchorPane createMainLayout() {
        AnchorPane mainLayout = new AnchorPane();
        HBox inputArea = new HBox(userInput, sendButton);

        mainLayout.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainLayout.getChildren().addAll(scrollPane, inputArea);

        HBox.setHgrow(userInput, Priority.ALWAYS);
        inputArea.setPrefHeight(INPUT_HEIGHT);

        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, INPUT_HEIGHT);

        AnchorPane.setLeftAnchor(inputArea, 0.0);
        AnchorPane.setRightAnchor(inputArea, 0.0);
        AnchorPane.setBottomAnchor(inputArea, 0.0);
        return mainLayout;
    }

    /** Sends one nonblank command to Lloyd and displays both sides of the exchange. */
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        commandHistory.add(input);
        LloydResponse reply = lloyd.getReply(input);
        dialogContainer.getChildren().addAll(
                DialogBox.createUserDialog(input),
                DialogBox.createLloydDialog(reply.text(), reply.mood()));
        userInput.clear();

        if (lloyd.isExitRequested()) {
            disableInput();
        }
    }

    /** Prevents more commands from being entered after exit or startup failure. */
    private void disableInput() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
    }
}
