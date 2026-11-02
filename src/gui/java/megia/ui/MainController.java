package megia.ui;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import megia.exception.MegiaException;
import megia.model.CommandResult;
import megia.model.Deadline;
import megia.model.Event;
import megia.model.Task;
import megia.model.TaskEntry;
import megia.model.Todo;
import megia.service.CommandExecutor;
import megia.service.LocalizationService;

/**
 * Coordinates the FXML chatbot transcript and command composer.
 */
public final class MainController {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm", Locale.ENGLISH);
    private static final double MAX_BUBBLE_WIDTH = 640;
    private static final double BUBBLE_WIDTH_FRACTION = 0.78;
    private static final double AVATAR_SIZE = 40;
    private static final double MESSAGE_GAP = 10;
    private static final List<LanguageChoice> LANGUAGE_CHOICES = List.of(
            new LanguageChoice("en", "language_english"),
            new LanguageChoice("cn", "language_chinese"));

    @FXML
    private ListView<TranscriptMessage> transcriptList;
    @FXML
    private Label titleLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private ComboBox<LanguageChoice> languageChoiceBox;
    @FXML
    private TextField commandInput;
    @FXML
    private Button sendButton;
    @FXML
    private Button userImageButton;
    @FXML
    private Button starterHelpButton;
    @FXML
    private Button starterTodoButton;
    @FXML
    private Button starterListButton;
    @FXML
    private Button starterFindButton;

    private final CommandExecutor commandExecutor;
    private final String startupError;
    private final boolean isCommandBlocked;
    private final ProfileImageService profileImageService;
    private final BooleanSupplier exitHandler;
    private final Runnable localizationListener = this::refreshLocalizedControls;
    private Image assistantAvatar;
    private Image userAvatar;
    private boolean isRefreshingLanguageChoice;

    /**
     * Creates a chatbot controller with its command executor and optional startup error.
     *
     * @param commandExecutor Shared command executor used by the chatbot.
     * @param startupError Localized startup error, or null when startup was clean.
     */
    public MainController(CommandExecutor commandExecutor, String startupError) {
        this(commandExecutor, startupError, new ProfileImageService(), (BooleanSupplier) null);
    }

    /**
     * Creates a chatbot controller that delegates exit requests.
     *
     * @param commandExecutor Shared command executor used by the chatbot.
     * @param startupError Localized startup error, or null when startup was clean.
     * @param exitHandler Callback that attempts to save and close the application.
     */
    public MainController(
            CommandExecutor commandExecutor, String startupError, Runnable exitHandler) {
        this(commandExecutor, startupError, new ProfileImageService(),
                asExitHandler(exitHandler));
    }

    MainController(CommandExecutor commandExecutor, String startupError,
                   ProfileImageService profileImageService) {
        this(commandExecutor, startupError, profileImageService, (BooleanSupplier) null);
    }

    MainController(CommandExecutor commandExecutor, String startupError,
                   ProfileImageService profileImageService, Runnable exitHandler) {
        this(commandExecutor, startupError, profileImageService, asExitHandler(exitHandler));
    }

    MainController(CommandExecutor commandExecutor, String startupError,
                   ProfileImageService profileImageService, BooleanSupplier exitHandler) {
        this.commandExecutor = commandExecutor;
        this.startupError = startupError;
        this.isCommandBlocked = startupError != null;
        this.profileImageService = profileImageService;
        this.exitHandler = exitHandler == null ? () -> {
            completeExit();
            return true;
        } : exitHandler;
    }

    private static BooleanSupplier asExitHandler(Runnable exitHandler) {
        if (exitHandler == null) {
            return null;
        }
        return () -> {
            exitHandler.run();
            return true;
        };
    }

    /**
     * Configures the transcript cell factory and adds the initial greeting.
     */
    @FXML
    public void initialize() {
        assertControlsInjected();
        assistantAvatar = AvatarFactory.createAssistantAvatar();
        userAvatar = profileImageService.loadUserImage().orElseGet(AvatarFactory::createUserAvatar);
        languageChoiceBox.getItems().setAll(LANGUAGE_CHOICES);
        languageChoiceBox.getSelectionModel().selectedItemProperty().addListener((ignored, oldChoice,
                newChoice) -> {
                    if (!isRefreshingLanguageChoice && newChoice != null) {
                        LocalizationService.setLanguage(newChoice.code());
                    }
                });
        LocalizationService.addLanguageChangeListener(localizationListener);
        refreshLocalizedControls();
        transcriptList.setCellFactory(ignored -> new TranscriptCell());
        transcriptList.widthProperty().addListener((ignored, oldWidth, newWidth) ->
                transcriptList.refresh());
        appendMessage(false, LocalizationService.getMessage("greeting"), List.of());
        if (startupError != null) {
            appendErrorMessage(startupError);
        }
        setCommandControlsDisabled(isCommandBlocked);
        commandInput.requestFocus();
    }

    private void assertControlsInjected() {
        assert transcriptList != null : "FXML must inject transcriptList";
        assert titleLabel != null : "FXML must inject titleLabel";
        assert subtitleLabel != null : "FXML must inject subtitleLabel";
        assert languageLabel != null : "FXML must inject languageLabel";
        assert languageChoiceBox != null : "FXML must inject languageChoiceBox";
        assert commandInput != null : "FXML must inject commandInput";
        assert sendButton != null : "FXML must inject sendButton";
        assert userImageButton != null : "FXML must inject userImageButton";
        assert starterTodoButton != null : "FXML must inject starterTodoButton";
        assert starterListButton != null : "FXML must inject starterListButton";
        assert starterFindButton != null : "FXML must inject starterFindButton";
    }

    /**
     * Executes the command in the composer and appends both conversation turns.
     */
    @FXML
    public void handleSend() {
        String rawCommand = commandInput.getText();
        if (submitCommand(rawCommand)) {
            commandInput.clear();
        }
        commandInput.requestFocus();
    }

    private boolean submitCommand(String rawCommand) {
        if (isCommandBlocked) {
            return false;
        }
        appendMessage(true, rawCommand, List.of());

        try {
            return renderResult(commandExecutor.execute(rawCommand));
        } catch (MegiaException exception) {
            appendErrorMessage(LocalizationService.getException(
                    exception.getErrorCode(), exception.getMessageArguments()));
        } catch (RuntimeException exception) {
            appendErrorMessage(LocalizationService.getMessage("unexpected_error"));
        }
        return false;
    }

    /**
     * Places the help command in the command composer.
     */
    @FXML
    public void handleStarterHelp() {
        setStarterCommand("help");
    }

    /**
     * Places a todo example in the command composer.
     */
    @FXML
    public void handleStarterTodo() {
        setStarterCommand("todo ");
    }

    /**
     * Places a list example in the command composer.
     */
    @FXML
    public void handleStarterList() {
        setStarterCommand("list");
    }

    /**
     * Places a find example in the command composer.
     */
    @FXML
    public void handleStarterFind() {
        setStarterCommand("find ");
    }

    /**
     * Opens a chooser so the user can replace their profile image.
     */
    @FXML
    public void handleChooseUserImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(LocalizationService.getMessage("profile_image_dialog_title"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                LocalizationService.getMessage("profile_image_file_description"),
                "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        File selectedFile = fileChooser.showOpenDialog(transcriptList.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }

        Path selectedPath = selectedFile.toPath();
        Optional<Image> selectedImage = profileImageService.loadImage(selectedPath);
        if (selectedImage.isEmpty()) {
            showErrorMessage(LocalizationService.getMessage("profile_image_invalid"));
            return;
        }
        if (!profileImageService.saveUserImage(selectedPath)) {
            showErrorMessage(LocalizationService.getMessage("profile_image_save_error"));
            return;
        }

        userAvatar = selectedImage.get();
        transcriptList.refresh();
    }

    /**
     * Removes this controller's language listener when the application closes.
     */
    public void dispose() {
        LocalizationService.removeLanguageChangeListener(localizationListener);
    }

    /**
     * Displays a localized system error in the assistant side of the transcript.
     *
     * @param message Error message to display.
     */
    public void showErrorMessage(String message) {
        appendErrorMessage(message);
    }

    private void refreshLocalizedControls() {
        titleLabel.setText(LocalizationService.getMessage("app_title"));
        subtitleLabel.setText(LocalizationService.getMessage("app_subtitle"));
        languageLabel.setText(LocalizationService.getMessage("language_label"));
        starterHelpButton.setText(LocalizationService.getMessage("starter_help"));
        starterTodoButton.setText(LocalizationService.getMessage("starter_todo"));
        starterListButton.setText(LocalizationService.getMessage("starter_list"));
        starterFindButton.setText(LocalizationService.getMessage("starter_find"));
        commandInput.setPromptText(LocalizationService.getMessage("command_prompt"));
        sendButton.setText(LocalizationService.getMessage("send"));
        userImageButton.setText(LocalizationService.getMessage("profile_image_choose"));
        transcriptList.refresh();

        isRefreshingLanguageChoice = true;
        try {
            languageChoiceBox.getItems().setAll(LANGUAGE_CHOICES);
            LANGUAGE_CHOICES.stream()
                    .filter(choice -> choice.code().equals(LocalizationService.getLanguage()))
                    .findFirst()
                    .ifPresent(choice -> languageChoiceBox.getSelectionModel().select(choice));
        } finally {
            isRefreshingLanguageChoice = false;
        }
    }

    private void setStarterCommand(String command) {
        if (isCommandBlocked) {
            return;
        }
        commandInput.setText(command);
        commandInput.positionCaret(command.length());
        commandInput.requestFocus();
    }

    private boolean renderResult(CommandResult result) {
        return switch (result) {
            case CommandResult.TaskList taskList -> {
                renderTaskList(taskList);
                yield true;
            }
            case CommandResult.TaskMutation mutation -> {
                renderMutation(mutation);
                yield true;
            }
            case CommandResult.Empty ignored -> appendMessage(
                    false, LocalizationService.getMessage("empty"), List.of());
            case CommandResult.Help ignored -> appendMessage(
                    false, LocalizationService.getMessage("help"), List.of());
            case CommandResult.Exit ignored -> exitHandler.getAsBoolean();
        };
    }

    void completeExit() {
        appendMessage(false, LocalizationService.getMessage("farewell"), List.of());
        setCommandControlsDisabled(true);
    }

    private void renderTaskList(CommandResult.TaskList taskList) {
        if (taskList.entries().isEmpty()) {
            String message = switch (taskList.query().type()) {
                case ALL -> LocalizationService.getMessage("task_storage_empty");
                case DATE -> String.format(
                        LocalizationService.getMessage("task_storage_date_empty"),
                        taskList.query().value());
                case FIND -> String.format(
                        LocalizationService.getMessage("task_storage_find_empty"),
                        taskList.query().value());
            };
            appendMessage(false, message, List.of());
            return;
        }

        String heading = switch (taskList.query().type()) {
            case ALL -> LocalizationService.getMessage("task_storage_list");
            case DATE -> String.format(
                    LocalizationService.getMessage("task_storage_date_list"),
                    taskList.query().value());
            case FIND -> LocalizationService.getMessage("task_storage_find_list");
        };
        appendMessage(false, heading, taskList.entries(), true);
    }

    private void renderMutation(CommandResult.TaskMutation mutation) {
        String message;
        if (mutation.operation() == CommandResult.MutationType.ADD) {
            message = LocalizationService.getMessage("task_storage_add") + "\n"
                    + String.format(
                            LocalizationService.getMessage("task_storage_add_2"),
                            mutation.taskCount());
        } else {
            String messageKey = "task_storage_" + mutation.operation().name().toLowerCase();
            message = LocalizationService.getMessage(messageKey);
        }
        appendMessage(false, message, List.of(mutation.task()),
                mutation.operation() != CommandResult.MutationType.DELETE);
    }

    private boolean appendMessage(boolean isUser, String text, List<TaskEntry> tasks) {
        appendMessage(isUser, text, tasks, !isUser);
        return true;
    }

    private void appendMessage(
            boolean isUser, String text, List<TaskEntry> tasks, boolean areTasksActionable) {
        transcriptList.getItems().add(new TranscriptMessage(
                isUser ? MessageKind.USER : MessageKind.ASSISTANT,
                text, tasks, areTasksActionable && !isCommandBlocked));
        transcriptList.scrollTo(transcriptList.getItems().size() - 1);
    }

    private void appendErrorMessage(String message) {
        transcriptList.getItems().add(new TranscriptMessage(
                MessageKind.ERROR, message, List.of(), false));
        transcriptList.scrollTo(transcriptList.getItems().size() - 1);
    }

    private void setCommandControlsDisabled(boolean isDisabled) {
        commandInput.setDisable(isDisabled);
        sendButton.setDisable(isDisabled);
        starterHelpButton.setDisable(isDisabled);
        starterTodoButton.setDisable(isDisabled);
        starterListButton.setDisable(isDisabled);
        starterFindButton.setDisable(isDisabled);
    }

    private void confirmAndDelete(TaskEntry entry) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle(LocalizationService.getMessage("delete_confirmation_title"));
        confirmation.setHeaderText(LocalizationService.getMessage("delete_confirmation_header"));
        confirmation.setContentText(String.format(
                LocalizationService.getMessage("delete_confirmation_content"),
                entry.id(), entry.task().getDescription()));
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            submitCommand("delete " + entry.id());
        }
    }

    private HBox createTaskActions(TaskEntry entry) {
        String toggleCommand = entry.task().isDone() ? "unmark " : "mark ";
        String toggleLabel = entry.task().isDone()
                ? LocalizationService.getMessage("task_action_unmark")
                : LocalizationService.getMessage("task_action_mark");
        Button toggleButton = new Button(toggleLabel);
        toggleButton.setOnAction(ignored -> submitCommand(toggleCommand + entry.id()));

        Button deleteButton = new Button(LocalizationService.getMessage("task_action_delete"));
        deleteButton.setOnAction(ignored -> confirmAndDelete(entry));

        HBox actions = new HBox(8, toggleButton, deleteButton);
        actions.getStyleClass().add("task-actions");
        return actions;
    }

    private static String getTaskTypeKey(Task task) {
        if (task instanceof Todo) {
            return "task_type_todo";
        }
        if (task instanceof Deadline) {
            return "task_type_deadline";
        }
        if (task instanceof Event) {
            return "task_type_event";
        }
        return "task_type_task";
    }

    private static String getTaskDetails(Task task) {
        if (task instanceof Deadline deadline) {
            return String.format(
                    LocalizationService.getMessage("task_deadline_details"),
                    formatDateTime(deadline.getDeadline()));
        }
        if (task instanceof Event event) {
            return String.format(
                    LocalizationService.getMessage("task_event_details"),
                    formatDateTime(event.getStartTime()), formatDateTime(event.getEndTime()));
        }
        return "";
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMATTER);
    }

    private enum MessageKind {
        USER,
        ASSISTANT,
        ERROR
    }

    private record TranscriptMessage(
            MessageKind kind, String text, List<TaskEntry> tasks, boolean areTasksActionable) {
        private TranscriptMessage {
            tasks = List.copyOf(tasks);
        }
    }

    private record LanguageChoice(String code, String labelKey) {
        @Override
        public String toString() {
            return LocalizationService.getMessage(labelKey);
        }
    }

    private final class TranscriptCell extends ListCell<TranscriptMessage> {
        @Override
        protected void updateItem(TranscriptMessage message, boolean empty) {
            super.updateItem(message, empty);
            if (empty || message == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            VBox content = createMessageBubble(message);
            HBox messageRow = createMessageRow(message, content);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(messageRow);
            setText(null);
        }

        private VBox createMessageBubble(TranscriptMessage message) {
            VBox content = new VBox(8);
            if (message.kind() == MessageKind.ERROR) {
                Label errorLabel = new Label(LocalizationService.getMessage("error_label"));
                errorLabel.getStyleClass().add("error-label");
                content.getChildren().add(errorLabel);
            }
            Label messageLabel = new Label(message.text());
            messageLabel.setWrapText(true);
            messageLabel.setMinWidth(0);
            content.getChildren().add(messageLabel);
            for (TaskEntry task : message.tasks()) {
                content.getChildren().add(createTaskCard(task, message.areTasksActionable()));
            }
            content.maxWidthProperty().bind(Bindings.max(
                    180,
                    Bindings.min(MAX_BUBBLE_WIDTH,
                            widthProperty().subtract(AVATAR_SIZE + MESSAGE_GAP)
                                    .multiply(BUBBLE_WIDTH_FRACTION))));
            content.getStyleClass().add("message-bubble");
            content.getStyleClass().add(switch (message.kind()) {
                case USER -> "user-message";
                case ASSISTANT -> "assistant-message";
                case ERROR -> "error-message";
            });
            return content;
        }

        private HBox createMessageRow(TranscriptMessage message, VBox content) {
            boolean isUser = message.kind() == MessageKind.USER;
            StackPane avatar = createAvatar(isUser);
            HBox row = isUser
                    ? new HBox(MESSAGE_GAP, content, avatar)
                    : new HBox(MESSAGE_GAP, avatar, content);
            row.setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
            row.setMaxWidth(Double.MAX_VALUE);
            row.prefWidthProperty().bind(widthProperty());
            row.getStyleClass().add("message-row");
            row.getStyleClass().add(isUser ? "user-row" : "assistant-row");
            return row;
        }

        private StackPane createAvatar(boolean isUser) {
            ImageView imageView = new ImageView(isUser ? userAvatar : assistantAvatar);
            imageView.setFitWidth(AVATAR_SIZE - 4);
            imageView.setFitHeight(AVATAR_SIZE - 4);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setClip(new Circle(AVATAR_SIZE / 2 - 2, AVATAR_SIZE / 2 - 2,
                    AVATAR_SIZE / 2 - 2));

            StackPane avatar = new StackPane(imageView);
            avatar.setMinSize(AVATAR_SIZE, AVATAR_SIZE);
            avatar.setPrefSize(AVATAR_SIZE, AVATAR_SIZE);
            avatar.setMaxSize(AVATAR_SIZE, AVATAR_SIZE);
            avatar.getStyleClass().add(isUser ? "user-avatar" : "assistant-avatar");
            return avatar;
        }

        private VBox createTaskCard(TaskEntry entry, boolean areTasksActionable) {
            Label idLabel = new Label(Integer.toString(entry.id()));
            idLabel.getStyleClass().add("task-id");
            Label typeLabel = new Label(LocalizationService.getMessage(getTaskTypeKey(entry.task())));
            typeLabel.getStyleClass().add("task-type");
            HBox heading = new HBox(10, idLabel, typeLabel);

            Label descriptionLabel = new Label(entry.task().getDescription());
            descriptionLabel.getStyleClass().add("task-description");
            descriptionLabel.setWrapText(true);
            descriptionLabel.setMinWidth(0);

            Label statusLabel = new Label(LocalizationService.getMessage(
                    entry.task().isDone() ? "task_status_done" : "task_status_pending"));
            statusLabel.getStyleClass().add("task-meta");

            VBox card = new VBox(8, heading, descriptionLabel, statusLabel);
            String details = getTaskDetails(entry.task());
            if (!details.isBlank()) {
                Label detailsLabel = new Label(details);
                detailsLabel.getStyleClass().add("task-meta");
                detailsLabel.setWrapText(true);
                detailsLabel.setMinWidth(0);
                card.getChildren().add(detailsLabel);
            }
            if (areTasksActionable) {
                card.getChildren().add(createTaskActions(entry));
            }
            card.getStyleClass().add("task-card");
            return card;
        }
    }
}
