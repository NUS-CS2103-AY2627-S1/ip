package jeff.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import jeff.Jeff;
import jeff.parser.Parser;
import jeff.storage.Storage;

/**
 * The JavaFX GUI for Jeff, styled like a messaging app: a colorful header bar,
 * a scrollable column of chat bubbles (the user's commands on the right with a
 * default profile picture, Jeff's replies on the left with a robot profile
 * picture, and error replies visually flagged), plus a text bar at the bottom
 * for typing commands. All command handling is delegated to
 * {@link Jeff#getResponse(String)}, so this class only wires up the window and
 * turns each exchange into dialog rows.
 */
public class MainApp extends Application {
    private static final double WINDOW_WIDTH = 480;
    private static final double WINDOW_HEIGHT = 600;
    private static final double SPACING = 8;
    private static final double HEADER_AVATAR_SIZE = 44;

    /** Text {@link jeff.ui.Ui#formatError(String)} puts in every error message Jeff returns. */
    private static final String ERROR_MARKER = "OOPS!!!";

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/user.png"));
    private final Image jeffImage = new Image(getClass().getResourceAsStream("/images/robot.png"));
    private final Jeff jeff = new Jeff(new Storage(Jeff.DEFAULT_SAVE_FILE_PATH));

    @Override
    public void start(Stage stage) {
        VBox dialogContainer = new VBox(SPACING);
        dialogContainer.setPadding(new Insets(SPACING));
        dialogContainer.getChildren().add(buildReplyDialog(jeff.getWelcomeMessage()));

        ScrollPane scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("transcript-scroll");
        // Keeps the transcript pinned to the newest message as the conversation grows.
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        TextField input = new TextField();
        input.setPromptText("Type a command, e.g. todo read book");
        input.getStyleClass().add("input-field");

        Button sendButton = new Button("Send ➤");
        sendButton.getStyleClass().add("send-button");

        Runnable sendInput = () -> sendInput(dialogContainer, input);
        sendButton.setOnAction(event -> sendInput.run());
        input.setOnAction(event -> sendInput.run());

        HBox inputBar = new HBox(SPACING, input, sendButton);
        HBox.setHgrow(input, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(scrollPane);
        root.setBottom(inputBar);
        BorderPane.setMargin(inputBar, new Insets(SPACING));

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());

        stage.setTitle("Jeff");
        stage.getIcons().add(jeffImage);
        stage.setScene(scene);
        stage.show();
    }

    /** Builds the colorful title bar shown above the transcript: Jeff's avatar, name, and a tagline. */
    private HBox createHeader() {
        ImageView avatar = new ImageView(jeffImage);
        avatar.setFitWidth(HEADER_AVATAR_SIZE);
        avatar.setFitHeight(HEADER_AVATAR_SIZE);
        avatar.setClip(new Circle(HEADER_AVATAR_SIZE / 2, HEADER_AVATAR_SIZE / 2, HEADER_AVATAR_SIZE / 2));

        Label title = new Label("Jeff");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("Your friendly task-tracking sidekick");
        subtitle.getStyleClass().add("app-subtitle");
        VBox titleBlock = new VBox(2, title, subtitle);

        HBox header = new HBox(SPACING, avatar, titleBlock);
        header.getStyleClass().add("app-header");
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    /** Sends the text field's contents to Jeff, appends the exchange as dialog rows, and clears the field. */
    private void sendInput(VBox dialogContainer, TextField input) {
        String text = input.getText();
        if (text.isBlank()) {
            return;
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(text, userImage), buildReplyDialog(jeff.getResponse(text)));
        input.clear();

        if (Parser.parseCommandType(text) == Parser.Command.BYE) {
            Platform.exit();
        }
    }

    /**
     * Wraps one of Jeff's replies (including the initial welcome message) in a dialog
     * row, using the error style whenever the reply reports a problem (see
     * {@link jeff.ui.Ui#formatError(String)}) so it catches the user's eye the same way
     * regardless of where in the conversation it appears.
     */
    private DialogBox buildReplyDialog(String response) {
        return response.contains(ERROR_MARKER)
                ? DialogBox.getErrorDialog(response, jeffImage)
                : DialogBox.getReplyDialog(response, jeffImage);
    }
}
