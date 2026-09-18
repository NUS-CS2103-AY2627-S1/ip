package korvus.views.components;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * ChatBox class for to contain the chat messages for the GUI.
 */
public class ChatBox extends ScrollPane {
    private static double BOT_DURATION = 800;
    private static double BOT_DIST = -500;
    private static double USER_DURATION = 200;
    private static double USER_DIST = 100;
    private static double ORIGIN = 0;

    private VBox chatLog;
    private ObservableList<ChatMessage> chatMessagesList;

    /**
     * Returns a ChatBox to be displayed in the App GUI.
     */
    public ChatBox() {
        this.chatLog = new VBox();

        this.setContent(chatLog);
        this.setUpScrollPane();
        this.setUpChatMessagesList();
    }

    /**
     * Initialises the ChatBox to the correct parameters.
     */
    private void setUpScrollPane() {
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        this.vvalueProperty().bind(chatLog.heightProperty());
        this.setFitToWidth(true);
    }

    /**
     * Initialises the ChatMessageList containing messages to be added to the ChatBox.
     */
    private void setUpChatMessagesList() {

        // ListChangeListener to automatically flush ChatMessages into ChatBox smoothly.
        this.chatMessagesList = FXCollections.observableArrayList();
        this.chatMessagesList.addListener((ListChangeListener<ChatMessage>) change -> {
            while (change.next()) {
                if (change.wasAdded() && change.getFrom() == 0) {
                    addMessage(change.getList().getFirst());
                } else if (change.wasRemoved() && !change.getList().isEmpty()) {
                    addMessage(change.getList().getFirst());
                }
            }
        });
    }

    /**
     * Adds a new ChatMessage to the ChatBox.
     *
     * @param chatMessage ChatMessage to be added.
     */
    private void addMessage(ChatMessage chatMessage) {
        TranslateTransition transition = createTransition(chatMessage);

        chatMessage.maxWidthProperty().bind(this.chatLog.widthProperty());

        this.chatLog.getChildren().add(chatMessage);
        transition.play();
        transition.setOnFinished(e -> chatMessagesList.remove(chatMessage));
    }

    /**
     * Adds a ChatMessage to the ChatMessageList.
     *
     * @param chatMessage ChatMessage to be added to the ChatBox.
     */
    public void addMessageToQueue(ChatMessage chatMessage) {
        chatMessagesList.add(chatMessage);
    }

    /**
     * Creates the smooth transition for ChatMessages to show up in the ChatBox.
     *
     * @param chatMessage ChatMessage to apply the transition to.
     * @return Transition attached to th given ChatMessage.
     */
    private TranslateTransition createTransition(ChatMessage chatMessage) {
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setNode(chatMessage);

        if (chatMessage.isBotMessage()) {
            translateTransition.setDuration(Duration.millis(BOT_DURATION));
            chatMessage.setTranslateX(BOT_DIST);
            translateTransition.setToX(ORIGIN);
        } else {
            translateTransition.setDuration(Duration.millis(USER_DURATION));
            chatMessage.setTranslateX(USER_DIST);
            translateTransition.setToX(ORIGIN);
        }
        return translateTransition;
    }
}
