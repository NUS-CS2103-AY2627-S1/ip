package dingleberry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import dingleberry.command.Command;
import dingleberry.exception.DingleberryException;
import dingleberry.model.TaskList;
import dingleberry.parser.Parser;
import dingleberry.persistence.Storage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
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
import javafx.util.Duration;

/**
 * A JavaFX-based chatbot UI for the Dingleberry task app.
 */
public final class Main extends Application {
    /** Default storage location used by the GUI app. */
    private static final String DEFAULT_DATA_FILE_PATH =
            "./data/dingleberry.txt";
    /** Location of the stylesheet applied to the chat window. */
    private static final String STYLESHEET_PATH =
            "/dingleberry/dingleberry.css";
    /** Location of the cropped mascot artwork shown in the header. */
    private static final String MASCOT_IMAGE_PATH =
            "/dingleberry/dingleberry_mascot_crop.png";
    /** Fixed height of the branded header bar, in pixels. */
    private static final int HEADER_HEIGHT = 60;
    /** Diameter of the circular mascot avatar in the header, in pixels. */
    private static final int HEADER_AVATAR_SIZE = 40;
    /** Horizontal gap between the avatar and the title block. */
    private static final int HEADER_SPACING = 10;
    /** Vertical gap between speech bubbles in the chat log. */
    private static final int DIALOG_SPACING = 4;
    /** Padding around the chat log and the window's outer border. */
    private static final int DIALOG_PADDING = 6;
    /** Padding around the whole window content. */
    private static final int ROOT_PADDING = 8;
    /** Horizontal gap between the input field and the send button. */
    private static final int INPUT_BOX_SPACING = 8;
    /** Initial window width, in pixels. */
    private static final int WINDOW_WIDTH = 720;
    /** Initial window height, in pixels. */
    private static final int WINDOW_HEIGHT = 520;
    /** Smallest usable window width, in pixels. */
    private static final int MIN_WINDOW_WIDTH = 400;
    /** Smallest usable window height, in pixels. */
    private static final int MIN_WINDOW_HEIGHT = 320;
    /** Compact width reserved for the send button so it never grows. */
    private static final int SEND_BUTTON_WIDTH = 92;
    /** Time allowed for the farewell message to render before closing. */
    private static final int GOODBYE_DISPLAY_MILLIS = 800;

    /** Stores the current in-memory task list. */
    private final TaskList tasks;
    /** Persists the task list to disk. */
    private final Storage storage;
    /** Displays output in the GUI instead of the console. */
    private final ChatUi chatUi;
    /** Stores a startup load failure to display after the GUI is ready. */
    private final String startupLoadError;
    /** Stores corrupted-record warnings to display after the GUI is ready. */
    private final List<String> startupLoadWarnings;
    /** Holds the ordered speech bubbles shown in the chat log. */
    private final VBox dialogContainer = new VBox(DIALOG_SPACING);
    /** Scrolls the dialog container and keeps the latest message visible. */
    private final ScrollPane chatScrollPane = new ScrollPane(dialogContainer);
    /** User input field for commands. */
    private final TextField inputField = new TextField();
    /** Provides the stage for closing the app after an exit command. */
    private Stage stage;

    /**
     * Creates the GUI app and loads saved tasks if they exist.
     */
    public Main() {
        this(new Storage(DEFAULT_DATA_FILE_PATH));
    }

    /**
     * Creates the GUI with the supplied storage, allowing isolated task files.
     *
     * @param taskStorage the storage used to load and save tasks.
     */
    Main(final Storage taskStorage) {
        storage = taskStorage;
        TaskList loadedTasks;
        String loadError = null;
        List<String> loadWarnings = List.of();
        try {
            Storage.LoadResult loadResult = storage.loadWithReport();
            loadedTasks = new TaskList(loadResult.getTasks());
            loadWarnings = loadResult.getWarnings();
        } catch (IOException e) {
            loadedTasks = new TaskList();
            loadError = e.getMessage();
        }
        tasks = loadedTasks;
        startupLoadError = loadError;
        startupLoadWarnings = loadWarnings;
        chatUi = new ChatUi(dialogContainer);
    }

    @Override
    public void start(final Stage primaryStage) {
        stage = primaryStage;
        configureChat();
        BorderPane root = createRoot(createHeader(), createInputBox());
        configureStage(primaryStage, createScene(root));
        primaryStage.show();
        showStartupMessages();
    }

    /**
     * Configures the conversation layout and scrolls after new rows appear.
     */
    private void configureChat() {
        dialogContainer.setPadding(new Insets(DIALOG_PADDING));
        dialogContainer.getStyleClass().add("dialog-container");

        chatScrollPane.setFitToWidth(true);
        chatScrollPane.getStyleClass().add("chat-scroll-pane");
        dialogContainer.heightProperty().addListener(
                (observable, oldHeight, newHeight) -> Platform.runLater(
                        () -> chatScrollPane.setVvalue(1.0)));
    }

    /**
     * Creates the command composer with shared Send and Enter behavior.
     *
     * @return the assembled input controls.
     */
    private HBox createInputBox() {
        Button sendButton = createSendButton();
        inputField.setPromptText("Type a command...");
        inputField.getStyleClass().add("input-field");
        inputField.setOnAction(event -> handleUserInput());
        HBox.setHgrow(inputField, Priority.ALWAYS);

        HBox inputBox = new HBox(INPUT_BOX_SPACING, inputField, sendButton);
        inputBox.getStyleClass().add("composer-bar");
        return inputBox;
    }

    /**
     * Creates a compact Send button enabled only for meaningful input.
     *
     * @return the configured Send button.
     */
    private Button createSendButton() {
        Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("send-button");
        // Fixed so the button stays compact while the input field absorbs
        // any extra width from resizing.
        sendButton.setMinWidth(SEND_BUTTON_WIDTH);
        sendButton.setPrefWidth(SEND_BUTTON_WIDTH);
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> inputField.getText().trim().isEmpty(),
                inputField.textProperty()));
        sendButton.setOnAction(event -> handleUserInput());
        return sendButton;
    }

    /**
     * Arranges the header, conversation, and composer in the window.
     *
     * @param header the branded header.
     * @param inputBox the command composer.
     * @return the assembled window layout.
     */
    private BorderPane createRoot(final HBox header, final HBox inputBox) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(ROOT_PADDING));
        root.setTop(header);
        root.setCenter(chatScrollPane);
        root.setBottom(inputBox);
        BorderPane.setMargin(header,
                new Insets(0, 0, DIALOG_PADDING, 0));
        BorderPane.setMargin(inputBox,
                new Insets(DIALOG_PADDING, 0, 0, 0));
        return root;
    }

    /**
     * Creates the styled scene or reports a missing stylesheet.
     *
     * @param root the window layout.
     * @return the scene with the application stylesheet.
     */
    private Scene createScene(final BorderPane root) {
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        URL stylesheet = getClass().getResource(STYLESHEET_PATH);
        if (stylesheet == null) {
            throw new IllegalStateException(
                    "Missing GUI resource: " + STYLESHEET_PATH);
        }
        scene.getStylesheets().add(stylesheet.toExternalForm());
        return scene;
    }

    /**
     * Configures the window and its minimum usable dimensions.
     *
     * @param primaryStage the application window.
     * @param scene the conversation scene.
     */
    private void configureStage(final Stage primaryStage, final Scene scene) {
        primaryStage.setTitle("Dingleberry");
        primaryStage.setScene(scene);
        // The window is resizable by default; give it a floor so the
        // header, conversation, and composer stay usable at small sizes.
        primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
        primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);
    }

    /**
     * Displays the greeting before any task-loading errors or warnings.
     */
    private void showStartupMessages() {
        chatUi.showWelcome();
        if (startupLoadError != null) {
            chatUi.showLoadingError(startupLoadError);
        }
        for (String warning : startupLoadWarnings) {
            chatUi.showLoadingWarning(warning);
        }
    }

    /**
     * Builds the compact branded header bar shown above the conversation:
     * a circular mascot avatar next to the app name and a short subtitle.
     *
     * @return the assembled header bar.
     */
    private HBox createHeader() {
        HBox header = new HBox(
                HEADER_SPACING, createMascotView(), createTitleBlock());
        header.getStyleClass().add("header-bar");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(HEADER_HEIGHT);
        header.setPrefHeight(HEADER_HEIGHT);
        header.setMaxHeight(HEADER_HEIGHT);
        return header;
    }

    /**
     * Loads the header artwork or reports a missing or unreadable resource.
     *
     * @return the loaded mascot image.
     */
    private Image loadMascotImage() {
        try (InputStream mascotStream = getClass().getResourceAsStream(
                MASCOT_IMAGE_PATH)) {
            if (mascotStream == null) {
                throw new IllegalStateException(
                        "Missing GUI resource: " + MASCOT_IMAGE_PATH);
            }
            return new Image(mascotStream);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not read GUI resource: " + MASCOT_IMAGE_PATH, e);
        }
    }

    /**
     * Creates the circular header avatar with its accessible description.
     *
     * @return the configured mascot view.
     */
    private ImageView createMascotView() {
        ImageView mascotView = new ImageView(loadMascotImage());
        mascotView.setFitWidth(HEADER_AVATAR_SIZE);
        mascotView.setFitHeight(HEADER_AVATAR_SIZE);
        mascotView.setPreserveRatio(true);
        mascotView.setSmooth(true);
        // Clip to a circle so the square artwork reads as a round avatar.
        mascotView.setClip(new Circle(HEADER_AVATAR_SIZE / 2.0,
                HEADER_AVATAR_SIZE / 2.0, HEADER_AVATAR_SIZE / 2.0));
        mascotView.setAccessibleText("Dingleberry mascot");
        return mascotView;
    }

    /**
     * Creates the app name and subtitle displayed beside the mascot.
     *
     * @return the aligned title labels.
     */
    private VBox createTitleBlock() {
        Label titleLabel = new Label("Dingleberry");
        titleLabel.getStyleClass().add("title-label");

        Label subtitleLabel = new Label("Task assistant");
        subtitleLabel.getStyleClass().add("subtitle-label");

        VBox titleBlock = new VBox(titleLabel, subtitleLabel);
        titleBlock.setAlignment(Pos.CENTER_LEFT);
        return titleBlock;
    }

    /**
     * Handles a command typed into the JavaFX input field.
     */
    private void handleUserInput() {
        final String input = inputField.getText();
        if (input == null) {
            return;
        }

        final String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            chatUi.showIncorrectParametersError(
                    "Please give me a command or a task description.");
            inputField.clear();
            inputField.requestFocus();
            return;
        }

        dialogContainer.getChildren()
                .add(DialogBox.getUserDialog(trimmedInput));
        inputField.clear();

        if (executeCommandAndCheckExit(trimmedInput)) {
            closeAfterGoodbye();
            return;
        }
        // Keep the caret in the input field after every command so the
        // user can keep typing without an extra click, whether they sent
        // it with Enter or the Send button.
        inputField.requestFocus();
    }

    /**
     * Executes a command and reports validation failures through the chat UI.
     *
     * @param input the non-blank command input.
     * @return true when a successful command requests application shutdown.
     */
    private boolean executeCommandAndCheckExit(final String input) {
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, chatUi, storage);
            return command.isExit();
        } catch (DingleberryException e) {
            showCommandError(e);
            return false;
        }
    }

    /**
     * Displays the error category corresponding to invalid user input.
     *
     * @param exception the failure reported by parsing or execution.
     */
    private void showCommandError(final DingleberryException exception) {
        if (exception.isWrongCommand()) {
            chatUi.showWrongCommandError(exception.getMessage());
        } else {
            chatUi.showIncorrectParametersError(exception.getMessage());
        }
    }

    /**
     * Disables input and allows the farewell to render before closing.
     */
    private void closeAfterGoodbye() {
        chatUi.showGoodbye();
        inputField.setDisable(true);
        PauseTransition goodbyeDelay = new PauseTransition(
                Duration.millis(GOODBYE_DISPLAY_MILLIS));
        goodbyeDelay.setOnFinished(event -> stage.close());
        goodbyeDelay.play();
    }
}
