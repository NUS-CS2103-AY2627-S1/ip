package yourhand;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import yourhand.exceptions.YourHandException;
import yourhand.tasks.Deadline;
import yourhand.tasks.Event;
import yourhand.tasks.Task;

/** Provides the graphical interface for YourHand. */
public class MainWindow extends Application {
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());
    private static final int MESSAGE_SPACING = 10;
    private static final int CONTENT_PADDING = 15;
    private static final int INPUT_PADDING = 10;
    private static final int MESSAGE_HORIZONTAL_PADDING = 12;
    private static final int MESSAGE_VERTICAL_PADDING = 9;
    private static final int MESSAGE_CORNER_RADIUS = 12;
    private static final int MESSAGE_MAX_WIDTH = 560;
    private static final int HEADER_IMAGE_SIZE = 54;
    private static final int INTRODUCTION_IMAGE_WIDTH = 180;
    private static final int AVATAR_SIZE = 44;
    private static final int AVATAR_RADIUS = AVATAR_SIZE / 2;
    private static final int GOODBYE_DELAY_SECONDS = 5;
    private static final String USER_BUBBLE_COLOR = "rgba(96, 165, 250, 0.78)";
    private static final String BOT_BUBBLE_COLOR = "rgba(255, 255, 255, 0.78)";
    private static final String BACKGROUND_TINT = "rgba(240, 249, 255, 0.28)";
    private static final String ERROR_COLOR = "#b91c1c";

    private final YourHandEngine engine = new YourHandEngine();
    private final VBox messages = new VBox(MESSAGE_SPACING);
    private final TextField input = new TextField();
    private final ScrollPane conversation = new ScrollPane(messages);
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        messages.setPadding(new Insets(CONTENT_PADDING));
        messages.setStyle("-fx-background-color: transparent;");
        conversation.setFitToWidth(true);
        conversation.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conversation.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        messages.heightProperty().addListener((observable, oldHeight, newHeight) -> scrollToBottom());
        conversation.viewportBoundsProperty()
                .addListener((observable, oldBounds, newBounds) -> scrollToBottom());

        input.setPromptText("Type a command, or type help");
        input.setStyle("-fx-font-size: 14px; -fx-padding: " + MESSAGE_VERTICAL_PADDING
                + "px; -fx-background-color: " + BOT_BUBBLE_COLOR
                + "; -fx-border-color: rgba(209, 213, 219, 0.75); -fx-border-radius: 8px;");
        Button send = new Button("Send");
        send.setDefaultButton(true);
        setSendButtonStyle(send, USER_BUBBLE_COLOR);
        send.setOnMouseEntered(event -> setSendButtonStyle(send, "rgba(59, 130, 246, 0.86)"));
        send.setOnMouseExited(event -> setSendButtonStyle(send, USER_BUBBLE_COLOR));
        send.setOnMousePressed(event -> setSendButtonStyle(send, "rgba(37, 99, 235, 0.90)"));
        send.setOnMouseReleased(event -> setSendButtonStyle(send, "rgba(59, 130, 246, 0.86)"));
        send.setOnAction(event -> submitCommand());
        input.setOnAction(event -> submitCommand());

        StackPane conversationArea = createConversationArea();
        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(conversationArea);
        HBox inputArea = new HBox(10, input, send);
        inputArea.setPadding(new Insets(INPUT_PADDING));
        inputArea.setMinHeight(64);
        inputArea.setPrefHeight(64);
        inputArea.setStyle("-fx-background-color: " + BACKGROUND_TINT
                + "; -fx-border-color: rgba(209, 213, 219, 0.75); -fx-border-width: 1 0 0 0;");
        HBox.setHgrow(input, Priority.ALWAYS);
        root.setBottom(inputArea);
        stage.setTitle("YourHand");
        stage.setScene(new Scene(root, 600, 400));
        stage.setMinWidth(420);
        stage.setMinHeight(300);
        stage.show();
        conversation.lookup(".viewport").setStyle("-fx-background-color: transparent;");
        addWelcomeBanner();
        if (engine.hasStartupWarning()) {
            addInvalidCommandResponse(engine.getStartupWarning());
        }
        scrollToBottom();
    }

    private void submitCommand() {
        String command = input.getText();
        if (command == null || command.isBlank()) {
            return;
        }
        addMessage(command, true);
        Task taskToDelete = getTaskBeforeDeletion(command);
        ExecutionResult execution = engine.executeWithResult(command);
        String response = execution.message();
        try {
            if (!execution.successful()) {
                addInvalidCommandResponse(response);
            } else if (command.equalsIgnoreCase("list")) {
                addStructuredResponse("Your tasks", engine.getTaskList().getTasks());
            } else if (command.equalsIgnoreCase("help")) {
                addHelpResponse();
            } else if (command.toLowerCase().startsWith("view schedule ")) {
                String dateText = command.substring("view schedule ".length()).trim();
                LocalDate date = YourHand.parseTaskDateTime(dateText).getValue().toLocalDate();
                List<Integer> taskNumbers = engine.getTaskList().findTaskNumbersForDate(date);
                addResponseWithTasks(response, taskNumbers);
            } else if (command.toLowerCase().startsWith("find ")) {
                String keyword = command.substring("find ".length()).trim();
                List<Integer> taskNumbers = engine.getTaskList()
                        .findTaskNumbersByDescriptionKeyword(keyword);
                addResponseWithTasks(response, taskNumbers);
            } else if (isTaskCreationCommand(command)) {
                List<Task> tasks = engine.getTaskList().getTasks();
                addResponseWithTask(response, tasks.get(tasks.size() - 1), tasks.size());
            } else if (isTaskStatusCommand(command)) {
                int taskNumber = Integer.parseInt(command.substring(command.lastIndexOf(' ') + 1));
                addResponseWithTask(response, getTask(taskNumber), taskNumber);
            } else if (taskToDelete != null) {
                addResponseWithTask(response, taskToDelete, 0);
            } else {
                addMessage(response, false);
            }
        } catch (YourHandException exception) {
            LOGGER.log(Level.WARNING, "Unable to render a command response", exception);
            addInvalidCommandResponse(exception.getMessage());
        } catch (RuntimeException exception) {
            LOGGER.log(Level.WARNING, "Unable to render a command response", exception);
            addInvalidCommandResponse("I couldn't display that response. Please try again.");
        }
        input.clear();
        scrollToBottom();
        if (command.equalsIgnoreCase("bye")) {
            PauseTransition goodbyeDelay = new PauseTransition(Duration.seconds(GOODBYE_DELAY_SECONDS));
            goodbyeDelay.setOnFinished(event -> stage.close());
            goodbyeDelay.play();
        }
    }

    private void addWelcomeBanner() {
        VBox welcome = new VBox(8);
        welcome.setMaxWidth(MESSAGE_MAX_WIDTH);
        welcome.setPadding(new Insets(12, 16, 12, 16));
        welcome.setStyle("-fx-background-color: " + BOT_BUBBLE_COLOR + "; -fx-border-color: #d1d5db;"
                + " -fx-border-radius: 12px; -fx-background-radius: 12px;");

        ImageView hand = createHandImage(INTRODUCTION_IMAGE_WIDTH);
        Label title = new Label("YourHand");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        Label introduction = new Label("Your Mighty Hand that can do a lot of things >:)\n\n"
                + "Stop wasting my time! >:( Tell me what I can do now!\n\n"
                + "(psps type help to see what I can do)");
        introduction.setWrapText(true);
        introduction.setStyle("-fx-text-fill: #1f2937;");
        welcome.getChildren().addAll(hand, title, introduction);
        addBotNode(welcome);
    }

    private HBox createHeader() {
        ImageView hand = createHandImage(HEADER_IMAGE_SIZE);
        Label title = new Label("YourHand");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        Label subtitle = new Label("Your mighty hand for keeping life in hand");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #4b5563;");
        VBox text = new VBox(2, title, subtitle);
        HBox header = new HBox(12, hand, text);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(9, 15, 9, 15));
        header.setStyle("-fx-background-color: rgba(255, 255, 255, 0.94);"
                + " -fx-border-color: #d1d5db; -fx-border-width: 0 0 1 0;");
        return header;
    }

    private StackPane createConversationArea() {
        Region readabilityOverlay = new Region();
        readabilityOverlay.setStyle("-fx-background-color: " + BACKGROUND_TINT + ";");
        readabilityOverlay.setMouseTransparent(true);

        Image image = loadImage("/images/final-destination.jpg");
        StackPane area;
        if (image == null) {
            area = new StackPane(readabilityOverlay, conversation);
        } else {
            ImageView background = new ImageView(image);
            background.setPreserveRatio(false);
            background.setOpacity(0.72);
            area = new StackPane(background, readabilityOverlay, conversation);
            background.fitWidthProperty().bind(area.widthProperty());
            background.fitHeightProperty().bind(area.heightProperty());
        }
        area.setMinSize(0, 0);
        area.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        conversation.setMinSize(0, 0);
        conversation.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return area;
    }

    private void scrollToBottom() {
        Platform.runLater(() -> {
            conversation.setVvalue(1.0);
            Platform.runLater(() -> conversation.setVvalue(1.0));
        });
    }

    private void addMessage(String text, boolean fromUser) {
        Label message = new Label(text.trim());
        message.setWrapText(true);
        message.setMaxWidth(MESSAGE_MAX_WIDTH);
        message.setPadding(new Insets(MESSAGE_VERTICAL_PADDING, MESSAGE_HORIZONTAL_PADDING,
                MESSAGE_VERTICAL_PADDING, MESSAGE_HORIZONTAL_PADDING));
        boolean isError = !fromUser && isErrorMessage(text);
        message.setStyle(isError
                ? "-fx-background-color: rgba(254, 226, 226, 0.92); -fx-text-fill: " + ERROR_COLOR
                + "; -fx-border-color: #fca5a5; -fx-border-radius: 12px; -fx-background-radius: 12px;"
                : fromUser
                ? "-fx-background-color: " + USER_BUBBLE_COLOR
                + "; -fx-text-fill: white; -fx-background-radius: 12px;"
                : "-fx-background-color: " + BOT_BUBBLE_COLOR
                + "; -fx-text-fill: #1f2937; -fx-border-color: #d1d5db;"
                + " -fx-border-radius: " + MESSAGE_CORNER_RADIUS + "px; -fx-background-radius: "
                + MESSAGE_CORNER_RADIUS + "px;");

        HBox row = new HBox(MESSAGE_HORIZONTAL_PADDING);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setAlignment(fromUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        message.maxWidthProperty().bind(row.widthProperty().multiply(fromUser ? 0.60 : 0.82));
        Node avatar = createAvatar(fromUser);
        if (fromUser) {
            row.getChildren().addAll(message, avatar);
        } else {
            row.getChildren().addAll(avatar, message);
        }
        messages.getChildren().add(row);
    }

    private void addStructuredResponse(String heading, List<Task> tasks) {
        VBox card = createResponseCard(heading);
        if (tasks.isEmpty()) {
            addEmptyResponse(card);
        }
        for (int index = 0; index < tasks.size(); index++) {
            card.getChildren().add(createTaskCard(tasks.get(index), index + 1));
        }
        addBotNode(card);
    }

    private void addTaskNumbersResponse(String heading, List<Integer> taskNumbers) {
        VBox card = createResponseCard(heading);
        if (taskNumbers.isEmpty()) {
            addEmptyResponse(card);
        }
        for (int taskNumber : taskNumbers) {
            card.getChildren().add(createTaskCard(getTask(taskNumber), taskNumber));
        }
        addBotNode(card);
    }

    private void addResponseWithTasks(String response, List<Integer> taskNumbers) {
        VBox card = createResponseCard(firstResponseLine(response));
        addResponseDetails(card, response, taskNumbers.stream()
                .map(this::getTask)
                .toList());
        if (taskNumbers.isEmpty()) {
            addEmptyResponse(card);
        }
        for (int taskNumber : taskNumbers) {
            card.getChildren().add(createTaskCard(getTask(taskNumber), taskNumber));
        }
        addBotNode(card);
    }

    private void addResponseWithTask(String response, Task task, int taskNumber) {
        VBox card = createResponseCard(firstResponseLine(response));
        List<Task> responseTasks = task == null ? List.of() : List.of(task);
        addResponseDetails(card, response, responseTasks);
        if (task != null) {
            card.getChildren().add(createTaskCard(task, taskNumber));
        }
        addBotNode(card);
    }

    private void addResponseDetails(VBox card, String response, List<Task> tasks) {
        String[] lines = response.trim().split("\\R");
        for (int index = 1; index < lines.length; index++) {
            String line = lines[index];
            String trimmedLine = line.trim();
            if (!trimmedLine.isEmpty() && tasks.stream().noneMatch(task -> task.toString().equals(trimmedLine))) {
                Label detail = new Label(trimmedLine);
                detail.setWrapText(true);
                detail.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151;");
                card.getChildren().add(detail);
            }
        }
    }

    private String firstResponseLine(String response) {
        return response.trim().split("\\R", 2)[0];
    }

    private void addSingleTaskResponse(String heading, Task task, int taskNumber) {
        VBox card = createResponseCard(heading);
        card.getChildren().add(createTaskCard(task, taskNumber));
        addBotNode(card);
    }

    private void addHelpResponse() {
        VBox card = createResponseCard("YourHand at your service");

        card.getChildren().add(createHandImage(INTRODUCTION_IMAGE_WIDTH));

        Label introduction = new Label("Here is what I can help you keep in hand:");
        introduction.setWrapText(true);
        introduction.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151;");
        card.getChildren().add(introduction);

        String[][] commands = {
            {"todo DESCRIPTION", "Add something you need to do.", "#2563eb"},
            {"deadline DESCRIPTION /by DATE_OR_TIME", "Add something with a due date or time.", "#d97706"},
            {"event DESCRIPTION /from DATE_OR_TIME /to DATE_OR_TIME",
                "Add something happening over a period of time.", "#0891b2"},
            {"list", "Show all your tasks.", "#2563eb"},
            {"find KEYWORD", "Search your tasks.", "#2563eb"},
            {"view schedule DATE", "Show deadlines and events for a date.", "#0891b2"},
            {"mark NUMBER", "Mark a task as done.", "#16a34a"},
            {"unmark NUMBER", "Put a completed task back on your plate.", "#16a34a"},
            {"delete NUMBER", "Remove a task.", "#dc2626"},
            {"help", "Show this command guide.", "#7c3aed"},
            {"bye", "Let me rest my fingers.", "#6b7280"}
        };
        for (String[] command : commands) {
            card.getChildren().add(createHelpCard(command[0], command[1], command[2]));
        }
        addBotNode(card);
    }

    private void addInvalidCommandResponse(String response) {
        VBox errorResponse = new VBox(8);
        errorResponse.setMaxWidth(MESSAGE_MAX_WIDTH);
        errorResponse.setPadding(new Insets(10, 12, 10, 12));
        errorResponse.setStyle("-fx-background-color: rgba(254, 226, 226, 0.92); -fx-border-color: #fca5a5;"
                + " -fx-border-radius: 12px; -fx-background-radius: 12px;");

        Label errorMessage = new Label(response.trim());
        errorMessage.setWrapText(true);
        errorMessage.setStyle("-fx-text-fill: " + ERROR_COLOR + ";");

        Image reactionImage = loadImage("/images/invalid-command.png");
        if (reactionImage != null) {
            ImageView reaction = new ImageView(reactionImage);
            reaction.setPreserveRatio(true);
            reaction.setFitWidth(210);
            reaction.setSmooth(true);
            errorResponse.getChildren().add(reaction);
        }
        errorResponse.getChildren().add(errorMessage);
        addBotNode(errorResponse);
    }

    private ImageView createHandImage(double width) {
        ImageView hand = new ImageView();
        Image image = loadImage("/images/master-hand.jpg");
        if (image != null) {
            hand.setImage(image);
        }
        hand.setPreserveRatio(true);
        hand.setFitWidth(width);
        hand.setSmooth(true);
        return hand;
    }

    private VBox createHelpCard(String syntax, String description, String color) {
        VBox commandCard = new VBox(3);
        commandCard.setPadding(new Insets(10, 12, 10, 12));
        commandCard.setMaxWidth(MESSAGE_MAX_WIDTH);
        commandCard.setStyle("-fx-background-color: #f8fafc; -fx-border-color: " + color
                + "; -fx-border-width: 0 0 0 4px; -fx-background-radius: 7px;");

        Label commandLabel = new Label(syntax);
        commandLabel.setWrapText(true);
        commandLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #4b5563;");
        commandCard.getChildren().addAll(commandLabel, descriptionLabel);
        return commandCard;
    }

    private boolean isTaskCreationCommand(String command) {
        String lowerCaseCommand = command.toLowerCase();
        return lowerCaseCommand.startsWith("todo ")
                || lowerCaseCommand.startsWith("deadline ")
                || lowerCaseCommand.startsWith("event ");
    }

    private boolean isTaskStatusCommand(String command) {
        String lowerCaseCommand = command.toLowerCase();
        return lowerCaseCommand.startsWith("mark ") || lowerCaseCommand.startsWith("unmark ");
    }

    private Task getTaskBeforeDeletion(String command) {
        if (!command.toLowerCase().startsWith("delete ")) {
            return null;
        }
        try {
            int taskNumber = Integer.parseInt(command.substring(command.lastIndexOf(' ') + 1));
            return engine.getTaskList().getTask(taskNumber);
        } catch (NumberFormatException | YourHandException exception) {
            return null;
        }
    }

    private VBox createResponseCard(String heading) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setMaxWidth(MESSAGE_MAX_WIDTH);
        card.setStyle("-fx-background-color: " + BOT_BUBBLE_COLOR + "; -fx-border-color: #d1d5db;"
                + " -fx-border-radius: 12px; -fx-background-radius: 12px;");

        Label title = new Label(heading);
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        card.getChildren().add(title);
        return card;
    }

    private VBox createTaskCard(Task task, int taskNumber) {
        VBox card = new VBox(3);
        card.setPadding(new Insets(10, 12, 10, 12));
        card.setMaxWidth(MESSAGE_MAX_WIDTH);
        card.setStyle("-fx-background-color: #f8fafc; -fx-border-color: "
                + getTaskColor(task) + "; -fx-border-width: 0 0 0 4;"
                + " -fx-background-radius: 7px;");

        String taskLabel = taskNumber > 0 ? taskNumber + "  " : "";
        Label type = new Label(taskLabel + getTaskType(task));
        type.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: "
                + getTaskColor(task) + ";");
        Label description = new Label(task.getDescription());
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        Label status = new Label("Status: " + (task.getStatusIcon().equals("X") ? "Completed" : "Pending"));
        status.setStyle("-fx-font-size: 12px; -fx-text-fill: #4b5563;");
        card.getChildren().addAll(type, description, status);

        if (task instanceof Deadline deadline) {
            card.getChildren().add(createDetailsLabel("Due: " + deadline.getBy().toDisplayString()));
        } else if (task instanceof Event event) {
            card.getChildren().add(createDetailsLabel("From: " + event.getFrom().toDisplayString()
                    + "  •  To: " + event.getTo().toDisplayString()));
        }
        return card;
    }

    private Label createDetailsLabel(String text) {
        Label details = new Label(text);
        details.setWrapText(true);
        details.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
        return details;
    }

    private String getTaskType(Task task) {
        if (task instanceof Deadline) {
            return "DEADLINE";
        }
        if (task instanceof Event) {
            return "EVENT";
        }
        return "TO DO";
    }

    private String getTaskColor(Task task) {
        if (task instanceof Deadline) {
            return "#d97706";
        }
        if (task instanceof Event) {
            return "#0891b2";
        }
        return "#2563eb";
    }

    private void addEmptyResponse(VBox card) {
        Label empty = new Label("As empty as your wallet");
        empty.setStyle("-fx-text-fill: #6b7280; -fx-font-style: italic;");
        card.getChildren().add(empty);
    }

    private void addBotNode(Node node) {
        HBox row = new HBox(MESSAGE_HORIZONTAL_PADDING, createAvatar(false), node);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setAlignment(Pos.CENTER_LEFT);
        messages.getChildren().add(row);
    }

    private Task getTask(int taskNumber) {
        try {
            return engine.getTaskList().getTask(taskNumber);
        } catch (YourHandException exception) {
            throw new IllegalStateException("A scheduled task number must be valid.", exception);
        }
    }

    private boolean isErrorMessage(String text) {
        if (text == null) {
            return true;
        }
        return text.contains("I don't speak")
                || text.contains("You handed")
                || text.contains("Bro due")
                || text.contains("Walao")
                || text.contains("Don't make me guess")
                || text.contains("needs a")
                || text.contains("Your task number")
                || text.contains("Task numbers")
                || text.contains("Brother I free")
                || text.contains("Pick a task number")
                || text.contains("Tell me which")
                || text.contains("Tell me what")
                || text.contains("Use yyyy")
                || text.contains("Please don't use")
                || text.contains("Please remove")
                || text.contains("Please enter")
                || text.contains("control characters")
                || text.contains("couldn't complete")
                || text.contains("couldn't display")
                || text.contains("must end after")
                || text.contains("cannot end")
                || text.contains("couldn't save")
                || text.contains("must be");
    }

    private void setSendButtonStyle(Button send, String color) {
        send.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-font-size: 14px;"
                + " -fx-padding: 10px 20px; -fx-background-radius: 8px;");
    }

    private Node createAvatar(boolean fromUser) {
        String imagePath = fromUser ? "/images/user-avatar.jpg" : "/images/chatbot-hand.jpg";
        ImageView avatar = new ImageView();
        Image image = loadImage(imagePath);
        if (image != null) {
            avatar.setImage(image);
        }
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setPreserveRatio(false);
        avatar.setClip(new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
        return avatar;
    }

    /** Loads an optional UI image without preventing the application from starting. */
    private Image loadImage(String resourcePath) {
        URL resource = getClass().getResource(resourcePath);
        if (resource == null) {
            LOGGER.warning("UI image is missing: " + resourcePath);
            return null;
        }
        try {
            return new Image(resource.toExternalForm());
        } catch (RuntimeException exception) {
            LOGGER.log(Level.WARNING, "UI image could not be loaded: " + resourcePath, exception);
            return null;
        }
    }
}
