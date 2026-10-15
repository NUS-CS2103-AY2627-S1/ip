package sevensix;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Provides a JavaFX graphical interface for the SevenSix chatbot.
 */
public class SevenSixGui extends Application {
    /** Classpath location of the stylesheet that defines the window's look. */
    private static final String STYLESHEET = "/css/main.css";

    /** The conversation messages displayed in the scroll pane. */
    private VBox dialogContainer;
    /** The scroll pane containing the conversation. */
    private ScrollPane scrollPane;
    /** The text field where the user enters a command. */
    private TextField userInput;
    /** The button that submits the current command. */
    private Button sendButton;
    /** The command processor shared with the console interface. */
    private final SevenSix chatbot;

    /**
     * Creates the GUI application and its command processor.
     */
    public SevenSixGui() {
        chatbot = new SevenSix();
    }

    /**
     * Creates and displays the SevenSix window.
     *
     * @param stage the primary JavaFX window.
     */
    @Override
    public void start(Stage stage) {
        VBox header = createHeader();
        scrollPane = createConversation();
        VBox inputBar = createInputBar();
        configureInputHandlers();
        BorderPane mainLayout = createMainLayout(header, inputBar);
        configureStage(stage, mainLayout);
        showGreeting();
    }

    /** Creates the branded header and short explanation displayed above the conversation.
     *
     * @return the configured conversation header.
     */
    private VBox createHeader() {
        return createHeaderLayout(createBrandRow(), createHeaderCopy());
    }

    /** Creates the introductory labels beneath the application identity.
     *
     * @return the styled introduction.
     */
    private VBox createHeaderCopy() {
        Label eyebrow = new Label(Ui.HEADER_EYEBROW);
        eyebrow.getStyleClass().add("eyebrow");
        Label title = new Label(Ui.HEADER_TITLE);
        title.getStyleClass().add("hero-title");
        title.setWrapText(true);
        Label subtitle = new Label(Ui.HEADER_SUBTITLE);
        subtitle.getStyleClass().add("hero-subtitle");
        subtitle.setWrapText(true);
        VBox headerCopy = new VBox(eyebrow, title, subtitle);
        headerCopy.getStyleClass().add("header-copy");
        return headerCopy;
    }

    /** Arranges the identity row and introduction in the header.
     *
     * @param brandRow the application identity and readiness indicator.
     * @param headerCopy the introductory labels.
     * @return the styled header layout.
     */
    private VBox createHeaderLayout(HBox brandRow, VBox headerCopy) {
        VBox header = new VBox(brandRow, headerCopy);
        header.getStyleClass().add("header");
        return header;
    }

    /** Creates the application identity row and readiness indicator.
     *
     * @return the configured brand row.
     */
    private HBox createBrandRow() {
        return arrangeBrandRow(createBrandMark(), createIdentity(), createHeaderSpacer(), createStatus());
    }

    /** Creates the application name and subtitle as one identity block.
     *
     * @return the styled identity labels.
     */
    private VBox createIdentity() {
        Label title = new Label(Ui.APPLICATION_NAME);
        title.getStyleClass().add("title");
        Label subtitle = new Label(Ui.APPLICATION_SUBTITLE);
        subtitle.getStyleClass().add("subtitle");
        VBox identity = new VBox(title, subtitle);
        identity.getStyleClass().add("identity");
        return identity;
    }

    /** Pushes the readiness indicator to the opposite edge of the header.
     *
     * @return an expanding spacer.
     */
    private Region createHeaderSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /** Creates the styled readiness indicator.
     *
     * @return the readiness label.
     */
    private Label createStatus() {
        Label status = new Label(Ui.READY_STATUS);
        status.getStyleClass().add("status-pill");
        return status;
    }

    /** Aligns the prepared brand components in one row.
     *
     * @param brandMark the application badge.
     * @param identity the application name and subtitle.
     * @param spacer the expanding gap.
     * @param status the readiness indicator.
     * @return the aligned brand row.
     */
    private HBox arrangeBrandRow(StackPane brandMark, VBox identity, Region spacer, Label status) {
        HBox brandRow = new HBox(brandMark, identity, spacer, status);
        brandRow.setAlignment(Pos.CENTER_LEFT);
        brandRow.getStyleClass().add("brand-row");
        return brandRow;
    }

    /** Creates the small geometric mark used in the application header.
     *
     * @return the configured brand mark.
     */
    private StackPane createBrandMark() {
        Circle circle = new Circle(19.0);
        circle.getStyleClass().add("brand-mark");
        Label initials = new Label("76");
        initials.getStyleClass().add("brand-mark-text");
        return new StackPane(circle, initials);
    }

    /** Creates the scrollable conversation area.
     *
     * @return the configured conversation scroll pane.
     */
    private ScrollPane createConversation() {
        dialogContainer = new VBox();
        dialogContainer.getStyleClass().add("dialog-container");
        ScrollPane conversation = new ScrollPane(dialogContainer);
        conversation.setFitToWidth(true);
        conversation.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversation.getStyleClass().add("conversation");
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                conversation.setVvalue(1.0));
        return conversation;
    }

    /** Creates the command composer, helper text, and send button.
     *
     * @return the configured input bar.
     */
    private VBox createInputBar() {
        Label composerLabel = new Label(Ui.COMPOSER_LABEL);
        composerLabel.getStyleClass().add("composer-label");
        userInput = new TextField();
        userInput.setPromptText(Ui.INPUT_PROMPT);
        userInput.getStyleClass().add("command-field");
        sendButton = new Button(Ui.SEND_BUTTON_LABEL);
        sendButton.setDefaultButton(true);
        sendButton.getStyleClass().add("send-button");
        sendButton.setMinWidth(Region.USE_PREF_SIZE);
        HBox controlRow = new HBox(userInput, sendButton);
        controlRow.getStyleClass().add("control-row");
        HBox.setHgrow(userInput, Priority.ALWAYS);
        Label helperText = new Label(Ui.COMPOSER_HINT);
        helperText.getStyleClass().add("composer-hint");
        helperText.setWrapText(true);
        VBox inputBar = new VBox(composerLabel, controlRow, helperText);
        inputBar.getStyleClass().add("input-bar");
        return inputBar;
    }

    /** Connects both input controls to the command handler. */
    private void configureInputHandlers() {
        sendButton.setOnAction(event -> handleUserInput());
        userInput.setOnAction(event -> handleUserInput());
    }

    /** Creates the main window layout from its three sections.
     *
     * @param header the header displayed at the top.
     * @param inputBar the command input displayed at the bottom.
     * @return the configured main layout.
     */
    private BorderPane createMainLayout(VBox header, VBox inputBar) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(header);
        mainLayout.setCenter(scrollPane);
        mainLayout.setBottom(inputBar);
        mainLayout.getStyleClass().add("app-shell");
        return mainLayout;
    }

    /** Configures and displays the primary application window.
     *
     * @param stage the primary application window.
     * @param mainLayout the layout displayed in the window.
     */
    private void configureStage(Stage stage, BorderPane mainLayout) {
        Scene scene = new Scene(mainLayout, 900.0, 720.0);
        scene.getStylesheets().add(getClass().getResource(STYLESHEET).toExternalForm());
        stage.setTitle(Ui.APPLICATION_NAME);
        stage.setMinWidth(560.0);
        stage.setMinHeight(520.0);
        stage.setScene(scene);
        stage.show();
    }

    /** Displays the initial greeting and focuses the command field. */
    private void showGreeting() {
        addMessage(DialogBox.getBotDialog(Ui.getGreeting()));
        showStorageWarning();
        userInput.requestFocus();
    }

    /** Makes storage problems visible before the user attempts a change. */
    private void showStorageWarning() {
        String warning = chatbot.getStartupWarning();
        if (!warning.isEmpty()) {
            addMessage(DialogBox.getErrorDialog(Ui.formatError(warning)));
        }
    }

    /**
     * Adds the entered command and the chatbot response to the conversation.
     */
    private void handleUserInput() {
        String command = userInput.getText();
        if (command.isBlank()) {
            return;
        }

        displayCommandResponse(command);
        finishSubmission(command);
    }

    /** Clears the composer and either exits or schedules scrolling after the reply is laid out.
     *
     * @param command the command that has just been processed.
     */
    private void finishSubmission(String command) {
        userInput.clear();
        if (Parser.isExitCommand(command)) {
            Platform.exit();
            return;
        }
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    /** Displays the entered command and the chatbot response.
     *
     * @param command the command entered by the user.
     */
    private void displayCommandResponse(String command) {
        addMessage(DialogBox.getUserDialog(command));
        addMessage(createReplyDialog(chatbot.getResponse(command)));
    }

    /**
     * Creates the message row for a chatbot reply, highlighting replies that report errors.
     *
     * @param response the chatbot reply.
     * @return an error row for error replies, or a normal reply row otherwise.
     */
    private DialogBox createReplyDialog(String response) {
        return Ui.isErrorResponse(response)
                ? DialogBox.getErrorDialog(response)
                : DialogBox.getBotDialog(response);
    }

    /**
     * Adds one message row to the end of the conversation.
     *
     * @param dialog the message row to display.
     */
    private void addMessage(DialogBox dialog) {
        dialogContainer.getChildren().add(dialog);
    }
}
