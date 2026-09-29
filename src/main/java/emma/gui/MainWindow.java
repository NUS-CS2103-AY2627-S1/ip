package emma.gui;

import emma.Emma;
import emma.Ui;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * The window the user talks to Emma in: a scrolling conversation above a text box
 * and a send button.
 *
 * <p>This class decides what goes where; how it all looks is in {@code /css/emma.css}.
 */
public class MainWindow extends AnchorPane {

    private static final String STYLESHEET = "/css/emma.css";

    private static final double WIDTH = 420;
    private static final double HEIGHT = 620;

    /** Tall enough for the text box plus the bar's own padding, and no taller. */
    private static final double INPUT_BAR_HEIGHT = 44;
    private static final double INPUT_FIELD_HEIGHT = 32;
    private static final double SEND_BUTTON_WIDTH = 58;

    private static final Duration CLOSE_DELAY = Duration.seconds(1);

    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox dialogContainer = new VBox();
    private final HBox inputBar = new HBox();
    private final TextField userInput = new TextField();
    private final Button sendButton = new Button("Send");

    private final Emma emma;
    private final Image userImage;
    private final Image emmaImage;

    /**
     * Builds the window and shows Emma's greeting.
     *
     * @param emma the chatbot the window talks to.
     */
    public MainWindow(Emma emma) {
        this.emma = emma;
        this.userImage = loadImage("/images/DaUser.png");
        this.emmaImage = loadImage("/images/DaEmma.png");

        layOutControls();
        wireUpInput();

        String loadMessage = emma.loadTasks();
        if (!loadMessage.isEmpty()) {
            // A non-empty load message means the saved tasks could not be read.
            addEmmaMessage(loadMessage, true);
        }
        addEmmaMessage(Ui.getGreeting(), false);
    }

    /** Arranges the scrolling conversation above the bar the user types in. */
    private void layOutControls() {
        this.setPrefSize(WIDTH, HEIGHT);
        this.getStyleClass().add("main-window");
        this.getStylesheets().add(MainWindow.class.getResource(STYLESHEET).toExternalForm());

        dialogContainer.setFillWidth(true);
        dialogContainer.getStyleClass().add("conversation");
        scrollPane.setContent(dialogContainer);
        scrollPane.getStyleClass().add("conversation-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        // Keep the newest message in view as the conversation grows.
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));

        userInput.setPromptText("Type a command, then press Enter");
        userInput.getStyleClass().add("input-field");
        userInput.setPrefHeight(INPUT_FIELD_HEIGHT);
        sendButton.getStyleClass().add("send-button");
        sendButton.setPrefSize(SEND_BUTTON_WIDTH, INPUT_FIELD_HEIGHT);

        // The text box takes whatever width the button leaves, at any window size.
        inputBar.getStyleClass().add("input-bar");
        inputBar.setAlignment(Pos.CENTER);
        inputBar.getChildren().addAll(userInput, sendButton);
        HBox.setHgrow(userInput, Priority.ALWAYS);

        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, INPUT_BAR_HEIGHT);

        AnchorPane.setBottomAnchor(inputBar, 0.0);
        AnchorPane.setLeftAnchor(inputBar, 0.0);
        AnchorPane.setRightAnchor(inputBar, 0.0);

        this.getChildren().addAll(scrollPane, inputBar);
    }

    /** Sends the typed line when the button is pressed or Enter is hit. */
    private void wireUpInput() {
        sendButton.setOnAction(event -> handleUserInput());
        userInput.setOnAction(event -> handleUserInput());
    }

    /** Shows what the user typed, then Emma's reply, and closes the window after "bye". */
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        addDialog(DialogBox.getUserDialog(input, userImage));
        String response = emma.getResponse(input);
        addEmmaMessage(response, emma.isError());
        userInput.clear();

        if (emma.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition wait = new PauseTransition(CLOSE_DELAY);
            wait.setOnFinished(event -> Platform.exit());
            wait.play();
        }
    }

    /**
     * Adds one of Emma's messages to the conversation, picking the bubble that suits it.
     *
     * @param message what Emma has to say.
     * @param isError true if Emma is complaining rather than reporting something done.
     */
    private void addEmmaMessage(String message, boolean isError) {
        addDialog(isError
                ? DialogBox.getEmmaErrorDialog(message, emmaImage)
                : DialogBox.getEmmaDialog(message, emmaImage));
    }

    /**
     * Puts one dialog box at the end of the conversation, first tying how wide it may
     * grow to how wide the window currently is. Every message goes through here so that
     * none of them is left with a width that ignores the window.
     *
     * @param box the dialog box to show.
     */
    private void addDialog(DialogBox box) {
        box.capWidthTo(scrollPane.widthProperty());
        dialogContainer.getChildren().add(box);
    }

    /**
     * Reads a picture that is packaged with the program.
     *
     * @param resourcePath the path of the image inside the jar.
     * @return the picture.
     */
    private Image loadImage(String resourcePath) {
        return new Image(MainWindow.class.getResourceAsStream(resourcePath));
    }

    /**
     * Returns how wide the window should open.
     *
     * @return the preferred width.
     */
    public static double getWindowWidth() {
        return WIDTH;
    }

    /**
     * Returns how tall the window should open.
     *
     * @return the preferred height.
     */
    public static double getWindowHeight() {
        return HEIGHT;
    }
}
