package piplupbot;

import java.io.IOException;
import java.net.URL;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents the bot's face in a window: the conversation so far, as a column
 * of speech bubbles, above a box to type in and a button to send what is typed.
 *
 * <p>It plays two parts at once. As a JavaFX {@link Application}, it builds the
 * window and hands each line typed in the box to {@link PiplupBot#respondTo} --
 * the same method the console uses. As a {@link Ui}, it is where the bot's
 * replies go: {@link #show} adds each one to the conversation as a
 * {@link DialogBox}, just as {@link TextUi#show} prints it in the console. So
 * every command behaves the same in both faces, and a new command appears here
 * without any change to this class.</p>
 *
 * <p>This program never creates a {@code GuiUi} itself. {@link Launcher}
 * gives JavaFX the class, and JavaFX starts up, creates the object with the
 * no-argument constructor, and calls {@link #start}. Since nothing can be passed
 * to that constructor, the window makes its own bot, keeping its tasks in the
 * same file as the console -- which is what lets the two faces share one list.
 * It is also why the pictures can be made along with the object: by the time
 * JavaFX makes it, JavaFX is already running.</p>
 *
 * <p>What the window is made of, and where each part sits, is not written here
 * but in the layout file {@code view/MainWindow.fxml}, and how the parts look is
 * in the stylesheet {@code css/main.css}. This class is the layout file's
 * <em>controller</em>: loading the file puts each part it names into the field
 * of the same name marked {@code @FXML}, and makes the input box and the Send
 * button call {@link #handleInput}. What is left here is behavior -- what
 * happens when a line is sent, and how a reply is added.</p>
 */
public class GuiUi extends Application implements Ui {
    /** How long the goodbye stays on screen before the window closes. */
    private static final Duration GOODBYE_DELAY = Duration.seconds(1.5);

    /**
     * The narrowest the window can be made, in pixels: just wide enough for the
     * input box to show its whole hint beside the Send button.
     */
    private static final double MIN_WINDOW_WIDTH = 360;

    /**
     * The shortest the window can be made, in pixels: tall enough to show the
     * input bar and a few messages above it.
     */
    private static final double MIN_WINDOW_HEIGHT = 320;

    /** Where the window's layout file is, among the program's own files. */
    private static final String MAIN_WINDOW_PATH = "/view/MainWindow.fxml";

    /** Where the picture shown beside the user's messages is. */
    private static final String IMAGE_PATH_USER = "/images/user.png";

    /** Where the picture shown beside the bot's replies is. */
    private static final String IMAGE_PATH_BOT = "/images/piplupbot.png";

    /** The bot, keeping its tasks in the same file the console uses. */
    private final PiplupBot bot = new PiplupBot(PiplupBot.DEFAULT_FILE_PATH);

    /** The picture shown beside the user's messages. */
    private final Image userImage = new Image(getResourceUrl(IMAGE_PATH_USER).toExternalForm());

    /** The picture shown beside the bot's replies. */
    private final Image botImage = new Image(getResourceUrl(IMAGE_PATH_BOT).toExternalForm());

    /** The commands sent so far, for the Up and Down keys to bring back. */
    private final CommandHistory history = new CommandHistory();

    /** The scrolling area around the conversation. */
    @FXML
    private ScrollPane scrollPane;

    /** Everything said so far, one {@link DialogBox} per message, oldest at the top. */
    @FXML
    private VBox conversation;

    /** The box the user types each command into. */
    @FXML
    private TextField inputBox;

    /**
     * The button that sends what is in the input box, as pressing Enter does.
     * It is greyed out whenever there is nothing to send.
     */
    @FXML
    private Button sendButton;

    /**
     * Loads the window's layout, greets the user, and shows the window.
     *
     * @param stage The window JavaFX has created for this application.
     * @throws IOException If the layout file cannot be read.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getResourceUrl(MAIN_WINDOW_PATH));
        // This object is the controller, rather than one the loader would make
        // for itself. That is what puts the parts into this object's fields, so
        // the replies that show() adds land in the window on the screen.
        loader.setController(this);
        Parent layout = loader.load();

        // This window is the Ui, so the greeting arrives through show() below.
        bot.greet(this);

        stage.setTitle("PiplupBot");
        // Without an icon, the title bar and taskbar show the default Java one.
        stage.getIcons().add(botImage);
        // Stop the window being dragged so small that the pictures, bubbles and
        // input bar are squashed together. It can still be made as large as wanted.
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setScene(new Scene(layout));
        stage.show();
        inputBox.requestFocus();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Here that means adding a speech bubble from the bot to the
     * conversation, with the lines one under another. There is no need for the
     * console's dividers or indentation, as the bubble already marks where the
     * reply begins and ends.</p>
     */
    @Override
    public void show(String... lines) {
        conversation.getChildren().add(DialogBox.getBotDialog(String.join("\n", lines), botImage));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Here the explanation gets a bubble of a different color from an
     * ordinary reply, so that a mistake stands out. The console, which has no
     * colors, shows the same words in the same way as any other reply.</p>
     */
    @Override
    public void showError(PiplupBotException e) {
        String text = String.join("\n", e.getMessageLines());
        conversation.getChildren().add(DialogBox.getErrorDialog(text, botImage));
    }

    /**
     * Finishes setting up the window, once loading its layout file has put the
     * parts into the fields marked {@code @FXML}. The loader calls this itself,
     * finding it by its name.
     */
    @FXML
    private void initialize() {
        // Whenever a new message makes the conversation taller, scroll to the
        // bottom, so the newest message is always the one in view.
        conversation.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));

        // Grey out the Send button while there is nothing to send: when the box
        // is empty or holds only spaces, which handleInput() would ignore, or
        // when the box itself is switched off after "bye". A binding recomputes
        // this every time either property changes, so no code elsewhere needs to
        // remember to update the button -- and none may, since JavaFX refuses
        // to set a value that is bound.
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(
                this::hasNothingToSend, inputBox.textProperty(), inputBox.disableProperty()));

        // A filter, unlike an ordinary handler, sees each key press before the
        // input box does, so the box never gets to act on Up or Down itself.
        inputBox.addEventFilter(KeyEvent.KEY_PRESSED, this::handleHistoryKey);
    }

    /**
     * Returns whether there is nothing to send: the input box is empty, holds
     * only spaces, or has been switched off.
     */
    private boolean hasNothingToSend() {
        return inputBox.getText().isBlank() || inputBox.isDisable();
    }

    /**
     * Brings back an earlier command into the input box when Up is pressed, or
     * a later one when Down is pressed, as in a terminal. Any other key is left
     * for the input box to handle as usual.
     *
     * @param event The key press, before the input box has seen it.
     */
    private void handleHistoryKey(KeyEvent event) {
        switch (event.getCode()) {
            case UP -> history.moveToOlder(inputBox.getText());
            case DOWN -> history.moveToNewer(inputBox.getText());
            default -> {
                return;
            }
        }
        inputBox.setText(history.getSelected());
        // Put the cursor after the command, ready for it to be edited or sent.
        inputBox.end();
        // Stop the key press here, so JavaFX does not also use Up or Down to move
        // the focus to the Send button.
        event.consume();
    }

    /**
     * Sends the line in the input box to the bot, which answers through
     * {@link #show}, then closes the window if the line ended the conversation.
     * The layout file makes both pressing Enter in the box and clicking the
     * Send button call this. Either way, the input box is left ready for the
     * next line to be typed.
     */
    @FXML
    private void handleInput() {
        String input = inputBox.getText();
        inputBox.clear();
        // Clicking the Send button moves the keyboard focus onto the button, so
        // without this the user would have to click back into the box to type
        // again. Pressing Enter never leaves the box, so this changes nothing then.
        inputBox.requestFocus();
        if (input.isBlank()) {
            // Sending an empty box sends nothing, as a blank line does in the console.
            return;
        }

        history.add(input.trim());
        conversation.getChildren().add(DialogBox.getUserDialog(input.trim(), userImage));
        // Handing over this window as the Ui is what makes the reply appear in it.
        bot.respondTo(input, this);

        if (bot.isExit()) {
            // Close after a pause rather than at once, so the goodbye can be read.
            // Switching off the box switches off the Send button too, through
            // the binding made in initialize().
            inputBox.setDisable(true);
            PauseTransition pause = new PauseTransition(GOODBYE_DELAY);
            // Hiding a window is how it is closed: Stage.close() does just this.
            pause.setOnFinished(event -> inputBox.getScene().getWindow().hide());
            pause.play();
        }
    }

    /**
     * Returns the address of one of the files that come with the program, such
     * as a picture or a layout file. {@link DialogBox} uses it too, to find its
     * own layout file.
     *
     * <p>The path is looked up among the program's own files -- the ones in
     * {@code src/main/resources} -- rather than in the folder the program was
     * started from, so it works the same from IntelliJ, from Gradle and from the
     * JAR file. Inside the JAR file, though, the lookup is case-sensitive even on
     * Windows: {@code /images/User.png} would not find {@code user.png}.</p>
     *
     * @param path Where the file is, starting with {@code /},
     *             e.g. {@code "/images/user.png"}.
     * @return The file's address.
     * @throws IllegalStateException If the program has no such file.
     */
    static URL getResourceUrl(String path) {
        URL url = GuiUi.class.getResource(path);
        if (url == null) {
            // Say which file is missing. Otherwise a picture fails with a bare
            // NullPointerException, and a layout file with "Location is not set".
            throw new IllegalStateException("Missing resource: " + path);
        }
        return url;
    }
}
