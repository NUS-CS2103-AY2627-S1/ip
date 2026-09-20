package bkxss;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Controls the main Bkxss conversation view defined in FXML. */
public class MainWindow {
    private static final double AVATAR_SIZE = 42.0;
    private static final String USER_AVATAR_PATH = "/images/cat-avatar.png";
    private static final String BOT_AVATAR_PATH = "/images/dog-avatar.png";

    @FXML
    private VBox conversation;
    @FXML
    private ScrollPane conversationScroll;
    @FXML
    private TextField commandInput;
    @FXML
    private Button sendButton;

    private ArrayList<Task> tasks;
    private Storage storage;
    private Image userAvatar;
    private Image botAvatar;

    /** Configures controls that depend only on the loaded view. */
    @FXML
    private void initialize() {
        userAvatar = loadImage(USER_AVATAR_PATH);
        botAvatar = loadImage(BOT_AVATAR_PATH);
        sendButton.disableProperty().bind(commandInput.disabledProperty().or(
                Bindings.createBooleanBinding(() -> commandInput.getText().isBlank(), commandInput.textProperty())));
        addBotMessage(new CommandResult("Hello hello ~ This is Bkxss here ;)\n"
                + "What can I do for you?\n\nTry: todo borrow book\nUse list to see your tasks.", false));
    }

    /**
     * Supplies the task and storage objects used for subsequent commands.
     *
     * @param tasks task list loaded at application startup
     * @param storage persistence destination for task changes
     */
    public void setDependencies(ArrayList<Task> tasks, Storage storage) {
        this.tasks = tasks;
        this.storage = storage;
    }

    /** Places keyboard focus in the command field after the window is visible. */
    void requestInputFocus() {
        commandInput.requestFocus();
    }

    /** Sends a command, restores keyboard focus, and scrolls to the new reply after layout. */
    @FXML
    private void sendCommand() {
        String command = commandInput.getText().trim();
        if (commandInput.isDisabled() || command.isBlank()) {
            return;
        }
        addUserMessage(command);
        CommandResult result = Bkxss.processCommandResult(command, tasks, storage);
        addBotMessage(result);
        commandInput.clear();
        updateInputAfterCommand(command, result);
        scrollToLatestMessage();
    }

    /** Disables the input after a valid exit command, or returns focus for the next command. */
    private void updateInputAfterCommand(String command, CommandResult result) {
        if (!result.isError() && Bkxss.isExitCommand(command)) {
            commandInput.setDisable(true);
            commandInput.setPromptText("Session finished. Close the window to exit.");
        } else {
            commandInput.requestFocus();
        }
    }

    /** Scrolls after the next layout pass so the newly added reply is visible. */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> {
            conversationScroll.applyCss();
            conversationScroll.layout();
            conversationScroll.setVvalue(1.0);
        });
    }

    /** Adds a compact command bubble on the right, leaving most space for bot replies. */
    private void addUserMessage(String message) {
        Label command = createMessageLabel(message);
        command.getStyleClass().add("user-message");
        command.setAccessibleText("You: " + message);
        command.maxWidthProperty().bind(conversation.widthProperty().subtract(88).multiply(0.85));
        ImageView avatar = createAvatar(userAvatar, "User avatar: cat");
        HBox row = new HBox(10, command, avatar);
        row.setAlignment(Pos.CENTER_RIGHT);
        conversation.getChildren().add(row);
    }

    /** Adds a compact response card; errors have both a text heading and a distinct color. */
    private void addBotMessage(CommandResult result) {
        Label heading = new Label(result.isError() ? "CHECK YOUR COMMAND" : "BKXSS");
        heading.getStyleClass().add("message-heading");
        Label message = createMessageLabel(result.message());
        message.setMaxWidth(Double.MAX_VALUE);
        VBox card = new VBox(6, heading, message);
        card.getStyleClass().add("bot-message");
        card.maxWidthProperty().bind(conversation.widthProperty().subtract(88).multiply(0.85));
        if (result.isError()) {
            card.getStyleClass().add("error-message");
        }
        ImageView avatar = createAvatar(botAvatar, "Bkxss avatar: dog");
        HBox row = new HBox(10, avatar, card);
        row.setAlignment(Pos.TOP_LEFT);
        conversation.getChildren().add(row);
    }

    /** Allows long replies to wrap and grow vertically even in a narrow window. */
    private static Label createMessageLabel(String message) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMinWidth(0);
        label.setMinHeight(Region.USE_PREF_SIZE);
        label.getStyleClass().add("message-text");
        return label;
    }

    /** Loads an image bundled with the application. */
    private Image loadImage(String resourcePath) {
        return new Image(getClass().getResource(resourcePath).toExternalForm());
    }

    /** Creates a small avatar view from a transparent circular image. */
    private static ImageView createAvatar(Image image, String accessibleText) {
        ImageView avatar = new ImageView(image);
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setPreserveRatio(true);
        avatar.setSmooth(true);
        avatar.setAccessibleText(accessibleText);
        avatar.getStyleClass().add("avatar");
        return avatar;
    }
}
