package rocky.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import rocky.Rocky;

/**
 * The chat window: a scrolling transcript of dialog boxes above a text field
 * where the user types the same commands the text UI accepts, e.g.
 * "todo read book" or "mark 2". It uses a Project Hail Mary theme: the
 * transcript sits against a deep-space gradient with a field of stars behind
 * it, under a title bar naming the chat. The two sides of the conversation are
 * shown differently, because the user is giving commands to an app rather than
 * chatting with another person: each command is echoed as a compact,
 * terminal-style line, while Rocky's replies appear as wider amber cards beside
 * his picture, or as dark red alert cards when they report an error.
 */
public class MainWindow extends VBox {
    private static final double WINDOW_WIDTH = 420;
    private static final double WINDOW_HEIGHT = 600;
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);
    private static final String TITLE_TEXT = "Rocky";
    // The sky is darkest at the top of the window and lightens towards the horizon at the bottom.
    private static final String SPACE_STYLE =
            "-fx-background-color: linear-gradient(to bottom, #02030A 0%, #070B1C 60%, #0C1330 100%);";
    // The stars have to show through the transcript, and a ScrollPane paints its
    // content area using -fx-background, so it needs both properties.
    private static final String TRANSPARENT_STYLE =
            "-fx-background: transparent; -fx-background-color: transparent; -fx-background-insets: 0;";
    private static final String TITLE_BAR_STYLE =
            "-fx-background-color: #070B1C; -fx-border-color: transparent transparent #1E2A52 transparent;"
                    + " -fx-border-width: 1;";
    private static final String TITLE_TEXT_STYLE =
            "-fx-text-fill: #9FE3F2; -fx-font-weight: bold; -fx-font-size: 13;";
    private static final String INPUT_ROW_STYLE =
            "-fx-background-color: #070B1C; -fx-border-color: #1E2A52 transparent transparent transparent;"
                    + " -fx-border-width: 1;";
    private static final String INPUT_FIELD_STYLE =
            "-fx-background-color: #101733; -fx-text-fill: #E8F1FF; -fx-prompt-text-fill: #6C7FA8;"
                    + " -fx-background-radius: 8;";
    private static final String SEND_BUTTON_STYLE =
            "-fx-background-color: #E0A040; -fx-text-fill: #1A1208; -fx-font-weight: bold;"
                    + " -fx-background-radius: 8;";

    private final VBox dialogContainer = new VBox();
    private final ScrollPane scrollPane = new ScrollPane(dialogContainer);
    private final StarField starField = new StarField();
    private final TextField userInput = new TextField();
    private final Button sendButton = new Button("Send");

    /** Builds the chat window, loads any saved tasks, and shows the greeting. */
    public MainWindow() {
        dialogContainer.setStyle(TRANSPARENT_STYLE);
        dialogContainer.setPadding(new Insets(8));
        dialogContainer.setSpacing(4);

        scrollPane.setStyle(TRANSPARENT_STYLE);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Keep the newest message in view as the conversation grows.
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));

        userInput.setStyle(INPUT_FIELD_STYLE);
        userInput.setPromptText("Type a command, e.g. todo read book");
        userInput.setOnAction(event -> handleUserInput());
        HBox.setHgrow(userInput, Priority.ALWAYS);
        sendButton.setStyle(SEND_BUTTON_STYLE);
        sendButton.setOnAction(event -> handleUserInput());

        HBox inputRow = new HBox(8, userInput, sendButton);
        inputRow.setStyle(INPUT_ROW_STYLE);
        inputRow.setPadding(new Insets(8));

        setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        getChildren().addAll(createTitleBar(), createSpaceView(), inputRow);

        Rocky.initialize();
        dialogContainer.getChildren().add(DialogBox.getRockyDialog(Rocky.getGreeting()));
    }

    /** Returns the strip at the top of the window naming the chat. */
    private HBox createTitleBar() {
        Label title = new Label(TITLE_TEXT);
        title.setStyle(TITLE_TEXT_STYLE);

        HBox titleBar = new HBox(title);
        titleBar.setAlignment(Pos.CENTER);
        titleBar.setStyle(TITLE_BAR_STYLE);
        titleBar.setPadding(new Insets(8));
        return titleBar;
    }

    /**
     * Returns the transcript layered in front of the stars: the starry sky is
     * painted at the back, the deep-space gradient behind that, and the
     * messages themselves scroll in front of both.
     */
    private StackPane createSpaceView() {
        StackPane spaceView = new StackPane(starField, scrollPane);
        spaceView.setStyle(SPACE_STYLE);

        // A Canvas has a fixed size of its own, so it has to be told to follow the window.
        starField.widthProperty().bind(spaceView.widthProperty());
        starField.heightProperty().bind(spaceView.heightProperty());

        VBox.setVgrow(spaceView, Priority.ALWAYS);
        return spaceView;
    }

    /**
     * Answers whatever the user typed, adds both the command and the reply to
     * the transcript, and clears the input field. Blank input is ignored.
     */
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }

        Rocky.Reply reply = Rocky.getReply(input);
        DialogBox rockyDialog = reply.isError()
                ? DialogBox.getRockyErrorDialog(reply.text())
                : DialogBox.getRockyDialog(reply.text());
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input), rockyDialog);
        userInput.clear();

        if (Rocky.isExitCommand(input)) {
            exitAfterFarewell();
        }
    }

    /**
     * Stops accepting input and closes the window a moment later, so the
     * user can still read the farewell after typing "bye".
     */
    private void exitAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
