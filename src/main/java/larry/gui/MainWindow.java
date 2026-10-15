package larry.gui;

import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import larry.Larry;

/**
 * Controls Larry's main graphical interface.
 */
public class MainWindow extends AnchorPane {
    private static final double BASE_WINDOW_HEIGHT = 620.0;
    private static final double BASE_WINDOW_WIDTH = 460.0;
    private static final double MIN_INTERFACE_SCALE = 1.0;
    private static final Duration EXIT_DELAY = Duration.seconds(5.0);
    private static final String EXIT_COMMAND_TYPE = "ExitCommand";
    private static final String LARRY_IMAGE_PATH = "/images/EvilLarry.jpg";
    private static final String LARRY_FONT_PATH = "/fonts/Unutterable-Regular.ttf";
    private static final double LARRY_FONT_SIZE = 20.0;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image larryImage = loadLarryImage();
    private final NumberBinding interfaceScale = createInterfaceScale();
    private final PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
    private Larry larry;

    /**
     * Creates a scale that grows with the smaller window dimension.
     * The minimum keeps text readable in compact windows, while the lack of an
     * upper bound lets the conversation make full use of larger windows.
     *
     * @return Responsive scale for conversation elements.
     */
    private NumberBinding createInterfaceScale() {
        NumberBinding availableScale = Bindings.min(
                widthProperty().divide(BASE_WINDOW_WIDTH),
                heightProperty().divide(BASE_WINDOW_HEIGHT));
        return Bindings.max(MIN_INTERFACE_SCALE, availableScale);
    }

    /**
     * Loads Larry's avatar from the resources packaged with the application.
     *
     * @return Larry's avatar image.
     */
    private static Image loadLarryImage() {
        String imageUrl = Objects.requireNonNull(
                MainWindow.class.getResource(LARRY_IMAGE_PATH),
                "Missing Larry image: " + LARRY_IMAGE_PATH).toExternalForm();
        return new Image(imageUrl);
    }

    /**
     * Configures the dialog view to follow the latest message automatically.
     */
    @FXML
    public void initialize() {
        loadLarryFont();
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                userInput.isDisabled() || userInput.getText().isBlank(),
                userInput.disabledProperty(), userInput.textProperty()));
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(scrollPane.getVmax()));
        dialogContainer.getChildren().add(DialogBox.getLarryDialog(
                "I'm EVIL LARRY. Your tasks belong to me now.\n"
                        + "Try 'todo read book' to add a task, or 'list' to see your tasks.",
                larryImage, "Welcome", interfaceScale));
    }

    /**
     * Registers Larry's display font before any dialog boxes are created.
     */
    private static void loadLarryFont() {
        String fontUrl = Objects.requireNonNull(
                MainWindow.class.getResource(LARRY_FONT_PATH),
                "Missing Larry font: " + LARRY_FONT_PATH).toExternalForm();
        Font loadedFont = Font.loadFont(fontUrl, LARRY_FONT_SIZE);
        if (loadedFont == null) {
            throw new IllegalStateException("Unable to load Larry's display font.");
        }
    }

    /**
     * Supplies the Larry instance that processes user commands.
     *
     * @param larry Larry instance shared by this window.
     */
    public void setLarry(Larry larry) {
        this.larry = larry;
    }

    /**
     * Displays the user's input and Larry's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (userInput.isDisabled() || input.isBlank()) {
            return;
        }
        assert larry != null : "Larry must be set before handling user input";
        String response = larry.getResponse(input);
        String commandType = larry.getCommandType();

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, interfaceScale),
                DialogBox.getLarryDialog(response, larryImage, commandType, interfaceScale));
        userInput.clear();

        if (commandType.equals(EXIT_COMMAND_TYPE)) {
            scheduleExit();
        }
    }

    /**
     * Keeps Larry's farewell visible briefly before closing the application window.
     */
    private void scheduleExit() {
        userInput.setDisable(true);

        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.playFromStart();
    }
}
