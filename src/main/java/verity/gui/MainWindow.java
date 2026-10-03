package verity.gui;

import java.io.InputStream;
import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.util.Duration;

import verity.Verity;
import verity.command.HelpCommand;

/**
 * Controls the main Verity GUI window.
 */
public class MainWindow {
    private static final String GOOD_BACKGROUND_RESOURCE =
            "/images/mc_bg_good.png";
    private static final String BAD_BACKGROUND_RESOURCE =
            "/images/mc_bg_bad.png";
    private static final Duration EXIT_DELAY = Duration.millis(750);

    private final Image goodBackground = loadImage(
            GOOD_BACKGROUND_RESOURCE, "Missing good Verity background resource.");
    private final Image badBackground = loadImage(
            BAD_BACKGROUND_RESOURCE, "Missing bad Verity background resource.");

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private StackPane conversationArea;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private VBox dialogContainer;
    @FXML
    private VBox emptyState;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final BooleanProperty isClosing = new SimpleBooleanProperty(false);
    private Verity verity;

    /**
     * Configures input validation and window controls after FXML loading.
     */
    @FXML
    public void initialize() {
        configureBackground();
        BooleanBinding isBlankInput = Bindings.createBooleanBinding(
                () -> userInput.getText().isBlank(),
                userInput.textProperty());
        sendButton.disableProperty().bind(isBlankInput.or(isClosing));
        userInput.disableProperty().bind(isClosing);
    }

    /**
     * Supplies the Verity instance used to process user commands.
     *
     * @param verity Verity instance for this window.
     */
    public void setVerity(Verity verity) {
        this.verity = Objects.requireNonNull(verity,
                "Verity instance must not be null.");
    }

    /**
     * Displays the user's input and Verity's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (isClosing.get() || input.isBlank()) {
            return;
        }

        String response = verity.getResponse(input);
        String commandType = verity.getCommandType();
        if (isHelpCommand(commandType)) {
            userInput.clear();
            showHelpWindow(formatResponseForGui(response));
            userInput.requestFocus();
            return;
        }
        boolean isError = isErrorResponse(commandType);

        setBackgroundImage(isError);
        hideEmptyState();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getVerityDialog(
                        formatResponseForGui(response), isError));
        Platform.runLater(() -> scrollPane.setVvalue(1.0));

        userInput.clear();
        userInput.requestFocus();

        if (verity.isExitRequested()) {
            closeAfterFarewell();
        }
    }

    static boolean isErrorResponse(String commandType) {
        return commandType == null;
    }

    static boolean isHelpCommand(String commandType) {
        return HelpCommand.class.getSimpleName().equals(commandType);
    }

    static String formatResponseForGui(String response) {
        return response.replaceAll("(?m)^_+\\R", "").trim();
    }

    private void showHelpWindow(String helpText) {
        TextArea helpTextArea = new TextArea(helpText);
        helpTextArea.setEditable(false);
        helpTextArea.setWrapText(true);
        helpTextArea.setPrefColumnCount(70);
        helpTextArea.setPrefRowCount(28);

        Dialog<Void> helpDialog = new Dialog<>();
        helpDialog.initOwner(userInput.getScene().getWindow());
        helpDialog.initModality(Modality.WINDOW_MODAL);
        helpDialog.setTitle("Verity Help");
        helpDialog.setResizable(true);
        helpDialog.getDialogPane().setContent(helpTextArea);
        helpDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        helpDialog.showAndWait();
    }

    private void hideEmptyState() {
        emptyState.setVisible(false);
        emptyState.setManaged(false);
    }

    private void configureBackground() {
        backgroundImage.setManaged(false);
        backgroundImage.setImage(goodBackground);
        backgroundImage.setPreserveRatio(true);
        backgroundImage.setFitHeight(0.0);

        Rectangle conversationClip = new Rectangle();
        conversationClip.widthProperty().bind(conversationArea.widthProperty());
        conversationClip.heightProperty().bind(conversationArea.heightProperty());
        conversationArea.setClip(conversationClip);

        backgroundImage.fitWidthProperty().bind(Bindings.createDoubleBinding(
                this::getCoverWidth,
                conversationArea.widthProperty(),
                conversationArea.heightProperty(),
                backgroundImage.imageProperty()));
        backgroundImage.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> (conversationArea.getWidth()
                        - backgroundImage.getBoundsInLocal().getWidth()) / 2,
                conversationArea.widthProperty(),
                backgroundImage.boundsInLocalProperty()));
        backgroundImage.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> (conversationArea.getHeight()
                        - backgroundImage.getBoundsInLocal().getHeight()) / 2,
                conversationArea.heightProperty(),
                backgroundImage.boundsInLocalProperty()));
    }

    private double getCoverWidth() {
        Image image = backgroundImage.getImage();
        if (image == null || image.getHeight() == 0) {
            return 0.0;
        }

        double width = conversationArea.getWidth();
        double height = conversationArea.getHeight();
        double imageWidthAtRequiredHeight = height
                * image.getWidth() / image.getHeight();
        return Math.max(width, imageWidthAtRequiredHeight);
    }

    private void setBackgroundImage(boolean isError) {
        backgroundImage.setImage(isError ? badBackground : goodBackground);
    }

    private void closeAfterFarewell() {
        isClosing.set(true);
        PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }

    private static Image loadImage(String resourcePath, String errorMessage) {
        InputStream imageStream = Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(resourcePath), errorMessage);
        Image image = new Image(imageStream);
        if (image.isError()) {
            throw new IllegalStateException(
                    "Invalid background resource " + resourcePath + ".",
                    image.getException());
        }
        return image;
    }
}
