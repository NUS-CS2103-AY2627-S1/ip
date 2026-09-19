package benji;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;


/**
 * Controls BENJI's main application window.
 */
public class MainWindow {
    // Most of the codes were reused from se-education.org javafx tutorial
    // Chatgpt 5.6 Terra Medium was used extensively to suggest some methods
    private static final String HELP_ROW_SPACING = "                  ";

    @FXML
    private AnchorPane mainPane;
    @FXML
    private StackPane headerPane;
    @FXML
    private AnchorPane footerPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button helpButton;
    @FXML
    private Button sendButton;

    private Benji benji;

    private final Image userImage = new Image(
            getClass().getResourceAsStream("/images/user.png"));
    private final Image benjiImage = new Image(
            getClass().getResourceAsStream("/images/benji.png"));


    /**
     * Runs after JavaFx has connected the FXML controls to this controller.
     */
    @FXML
    public void initialize() {
        configureResponsiveControls();
        addDialogBoxes(DialogBox.getBenjiDialog(
                "Hello! I am BENJI. What can I do for you?",
                benjiImage));


        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
    }

    /** Configures controls to scale within readable size limits as the window resizes. */
    private void configureResponsiveControls() {
        NumberBinding headerHeight = Bindings.max(50.0,
                Bindings.min(60.0, mainPane.widthProperty().multiply(0.10)));

        NumberBinding footerHeight = Bindings.max(60.0,
                Bindings.min(120.0, mainPane.widthProperty().multiply(0.15)));

        NumberBinding fontSize = Bindings.max(12.0,
                Bindings.min(24.0, mainPane.widthProperty().divide(33.0)));

        NumberBinding sendButtonWidth = Bindings.max(60.0,
                Bindings.min(120.0, mainPane.widthProperty().multiply(0.15)));
        NumberBinding controlPadding = Bindings.max(12.0,
                Bindings.min(24.0, mainPane.widthProperty().multiply(0.03)));


        headerPane.prefHeightProperty().bind(headerHeight);
        footerPane.prefHeightProperty().bind(footerHeight);
        ObjectBinding<Font> responsiveFont =
                Bindings.createObjectBinding(() -> Font.font(fontSize.doubleValue()), fontSize);
        ObjectBinding<Insets> buttonPadding =
                Bindings.createObjectBinding(() -> new Insets(
                        controlPadding.doubleValue() / 2,
                        controlPadding.doubleValue(),
                        controlPadding.doubleValue() / 2,
                        controlPadding.doubleValue()), controlPadding);
        helpButton.fontProperty().bind(responsiveFont);
        helpButton.paddingProperty().bind(buttonPadding);
        userInput.fontProperty().bind(responsiveFont);
        sendButton.fontProperty().bind(responsiveFont);
        sendButton.paddingProperty().bind(buttonPadding);
        sendButton.prefWidthProperty().bind(sendButtonWidth);

        updateScrollPaneAnchors(headerHeight.doubleValue(), footerHeight.doubleValue());
        updateFooterControlAnchors(sendButtonWidth.doubleValue(), controlPadding.doubleValue());
        headerHeight.addListener((observable, oldValue, newValue) ->
                updateScrollPaneAnchors(headerHeight.doubleValue(), footerHeight.doubleValue()));
        footerHeight.addListener((observable, oldValue, newValue) ->
                updateScrollPaneAnchors(headerHeight.doubleValue(), footerHeight.doubleValue()));
        sendButtonWidth.addListener((observable, oldValue, newValue) ->
                updateFooterControlAnchors(newValue.doubleValue(), controlPadding.doubleValue()));
        controlPadding.addListener((observable, oldValue, newValue) ->
                updateFooterControlAnchors(sendButtonWidth.doubleValue(), newValue.doubleValue()));
    }

    /** Keeps the conversation area between the responsive header and footer. */
    private void updateScrollPaneAnchors(double headerHeight, double footerHeight) {
        AnchorPane.setTopAnchor(scrollPane, headerHeight);
        AnchorPane.setBottomAnchor(scrollPane, footerHeight);
    }

    /** Keeps the input controls equal in height and separated as the footer scales. */
    private void updateFooterControlAnchors(double sendButtonWidth, double controlPadding) {
        AnchorPane.setTopAnchor(userInput, controlPadding);
        AnchorPane.setLeftAnchor(userInput, controlPadding);
        AnchorPane.setRightAnchor(userInput, sendButtonWidth + controlPadding * 2);
        AnchorPane.setBottomAnchor(userInput, controlPadding);
        AnchorPane.setTopAnchor(sendButton, controlPadding);
        AnchorPane.setRightAnchor(sendButton, controlPadding);
        AnchorPane.setBottomAnchor(sendButton, controlPadding);
    }

    /**
     * Connects this window to the shared BENJI command processor.
     *
     * @param benji command processor used to respond to user input
     */
    public void setBenji(Benji benji) {
        this.benji = benji;
    }

    /** Adds one or more chat messages to the conversation area. */
    private void addDialogBoxes(DialogBox...dialogBoxes) {
        dialogContainer.getChildren().addAll(dialogBoxes);
    }

    /**
     * Sends the user's command to BENJI and shows both chat messages.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText().trim();

        if (userText.isEmpty()) {
            return;
        }

        Command command = Parser.getCommand(userText);
        if (command == Command.HELP) {
            addDialogBoxes(DialogBox.getUserDialog(userText, userImage));
            userInput.clear();
            showHelp();
            return;
        }



        String benjiText = benji.getResponse(userText);

        addDialogBoxes(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getBenjiDialog(benjiText, benjiImage));

        userInput.clear();

        if (command == Command.BYE) {
            exitAfterGoodbye();
        }
    }

    /**
     * Disables input and closes BENJI after showing the farewell message.
     */
    private void exitAfterGoodbye() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        helpButton.setDisable(true);

        PauseTransition exitDelay = new PauseTransition(Duration.seconds(1.5));
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }

    /**
     * Shows BENJI's command guide in a styled popup.
     */
    @FXML
    private void showHelp() {
        Dialog<Void> helpDialog = new Dialog<>();
        helpDialog.setTitle("BENJI Command Guide");
        helpDialog.setResizable(false);

        ButtonType closeButtonType = new ButtonType("Got it", ButtonBar.ButtonData.CANCEL_CLOSE);
        helpDialog.getDialogPane().getButtonTypes().add(closeButtonType);
        helpDialog.getDialogPane().setPrefSize(520, 560);
        helpDialog.getDialogPane().setMinSize(520, 560);
        helpDialog.getDialogPane().setMaxSize(520, 560);
        helpDialog.getDialogPane().setStyle("-fx-background-color: #111827;");

        Label title = new Label("How to use BENJI");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");

        Label subtitle = new Label("Type one of these commands into the message box.");
        subtitle.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 14;");

        Label note = new Label("Note: DATE_OR_TIME could be either in "
                + "YYYY-MM-DD format or\n a general description");
        note.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 14;");

        VBox guideContent = new VBox(14);
        guideContent.setPadding(new Insets(22));
        guideContent.getChildren().addAll(
                title,
                subtitle,
                note,
                createHelpRow("list", "Show every task."),
                createHelpRow("todo DESCRIPTION", "Add a to-do task."),
                createHelpRow("deadline DESCRIPTION /by DATE_OR_TIME", "Add a deadline task."),
                createHelpRow("event DESCRIPTION /from DATE_OR_TIME \n"
                        + HELP_ROW_SPACING
                        + "/to DATE_OR_TIME", "Add an event."),
                createHelpRow("mark TASK_NUMBER", "Mark a task as completed."),
                createHelpRow("unmark TASK_NUMBER", "Mark a task as not completed."),
                createHelpRow("delete TASK_NUMBER", "Remove a task."),
                createHelpRow("find KEYWORD", "Search for tasks."),
                createHelpRow("help", "Show this command guide."),
                createHelpRow("bye", "Exit BENJI."));

        ScrollPane guideScrollPane = new ScrollPane(guideContent);
        guideScrollPane.setFitToWidth(true);
        guideScrollPane.setPrefViewportHeight(420);
        guideScrollPane.setStyle("-fx-background: #111827; -fx-background-color: #111827;");

        helpDialog.getDialogPane().setContent(guideScrollPane);

        Button closeButton = (Button) helpDialog.getDialogPane().lookupButton(closeButtonType);
        closeButton.setStyle("-fx-background-color: #00BFFF;"
                + "-fx-text-fill: black;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;");

        helpDialog.showAndWait();
    }

    /**
     * Creates one command-and-description row for the Help guide.
     *
     * @param command command that the user can enter
     * @param description explanation of the command
     * @return a formatted guide row
     */
    private HBox createHelpRow(String command, String description) {
        Label commandLabel = new Label(command);
        commandLabel.setMinWidth(250);
        commandLabel.setStyle("-fx-text-fill: #00BFFF;"
                + "-fx-font-family: monospace;"
                + "-fx-font-weight: bold;");

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-text-fill: white;");

        HBox row = new HBox(14, commandLabel, descriptionLabel);
        HBox.setHgrow(descriptionLabel, Priority.ALWAYS);
        return row;
    }
}
