package stewie.ui.gui;

import java.net.URL;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import stewie.model.TaskList;
import stewie.storage.StorageException;

/**
 * Assembles the workspace panels and coordinates navigation and the task summary.
 */
public class StewieGui extends BorderPane {
    private final Image stewiePhoto = loadPhoto();
    private final TaskList taskList;
    private final Label taskSummary = new Label();
    private final ChatPanel chatPanel;
    private final TaskListPanel listPanel;
    private final VBox helpPanel;

    /**
     * Creates a workspace connected to the supplied task list.
     *
     * @param taskList Task list shared by both views.
     */
    public StewieGui(TaskList taskList) {
        this.taskList = taskList;
        this.chatPanel = new ChatPanel(taskList, createLogo(48), this::refreshTaskSummary, this::showStorageError);
        this.listPanel = new TaskListPanel(taskList, this::refreshTaskSummary, this::showStorageError);
        this.helpPanel = createHelpPanel();
        setLeft(createSidebar());
        setCenter(chatPanel);
        refreshTaskSummary();
    }

    /**
     * Creates the sidebar containing the brand, navigation, and task summary.
     *
     * @return The styled sidebar.
     */
    private VBox createSidebar() {
        VBox sidebar = createSidebarContainer();
        sidebar.getChildren().addAll(createBrand(), createNavigation(), createSidebarSpacer(),
                createSummaryCard(), createSidebarFooter());
        return sidebar;
    }

    /**
     * Creates the sidebar's outer layout.
     */
    private VBox createSidebarContainer() {
        VBox sidebar = new VBox(24);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(30, 22, 26, 22));
        sidebar.setPrefWidth(270);
        return sidebar;
    }

    /**
     * Creates the sidebar portrait, brand name, and tagline.
     */
    private HBox createBrand() {
        HBox brand = new HBox(12);
        brand.setAlignment(Pos.CENTER_LEFT);
        StackPane logo = createLogo(46);
        VBox brandText = new VBox(2);
        Label brandName = new Label("stewie.");
        brandName.getStyleClass().add("brand-name");
        Label brandTagline = new Label("a modest command centre");
        brandTagline.getStyleClass().add("muted-label");
        brandText.getChildren().addAll(brandName, brandTagline);
        brand.getChildren().addAll(logo, brandText);

        return brand;
    }

    /**
     * Creates navigation controls whose handlers only select a page and its highlight.
     */
    private VBox createNavigation() {
        VBox navigation = new VBox(8);
        Button chatButton = createNavigationButton("fth-message-circle", "Chat", true);
        Button listButton = createNavigationButton("fth-list", "My List", false);
        Button helpButton = createNavigationButton("fth-help-circle", "Help", false);
        chatButton.setOnAction(event -> {
            showChatPanel();
            selectNavigation(chatButton, listButton, helpButton);
        });
        listButton.setOnAction(event -> {
            showListPanel();
            selectNavigation(listButton, chatButton, helpButton);
        });
        helpButton.setOnAction(event -> {
            showHelpPanel();
            selectNavigation(helpButton, chatButton, listButton);
        });
        navigation.getChildren().addAll(chatButton, listButton, helpButton);
        return navigation;
    }

    /**
     * Restores the conversation while discarding pending list animations.
     */
    private void showChatPanel() {
        listPanel.cancelCompletionDelays();
        setCenter(chatPanel);
    }

    /**
     * Refreshes the list on entry, retaining delays when its selected tab is clicked again.
     */
    private void showListPanel() {
        if (getCenter() != listPanel) {
            listPanel.cancelCompletionDelays();
            listPanel.refresh();
        }
        setCenter(listPanel);
    }

    /**
     * Opens the reference and stops timers belonging to the previous list view.
     */
    private void showHelpPanel() {
        listPanel.cancelCompletionDelays();
        setCenter(helpPanel);
    }

    /**
     * Keeps exactly one navigation button highlighted.
     */
    private void selectNavigation(Button selected, Button... others) {
        for (Button button : others) {
            button.getStyleClass().remove("navigation-button-active");
        }
        if (!selected.getStyleClass().contains("navigation-button-active")) {
            selected.getStyleClass().add("navigation-button-active");
        }
    }

    /**
     * Creates flexible space that keeps the summary near the bottom.
     */
    private Region createSidebarSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /**
     * Creates the card containing the live task count and its explanatory labels.
     */
    private VBox createSummaryCard() {
        VBox summaryCard = new VBox(12);
        summaryCard.getStyleClass().add("summary-card");
        Label summaryTitle = new Label("Your space");
        summaryTitle.getStyleClass().add("summary-title");
        taskSummary.getStyleClass().add("summary-value");
        Label summaryHint = new Label("Even grand plans need a list.");
        summaryHint.getStyleClass().add("summary-hint");
        summaryHint.setWrapText(true);
        summaryCard.getChildren().addAll(summaryTitle, taskSummary, summaryHint);

        return summaryCard;
    }

    /**
     * Creates the sidebar's closing caption.
     */
    private Label createSidebarFooter() {
        Label footer = new Label("supervised by a genius");
        footer.getStyleClass().add("muted-label");
        return footer;
    }

    /**
     * Creates a scrollable reference containing every GUI command format.
     *
     * @return The styled Help panel.
     */
    private VBox createHelpPanel() {
        VBox panel = new VBox();
        panel.getStyleClass().add("chat-panel");
        HBox header = new HBox();
        header.getStyleClass().add("chat-header");
        Label title = new Label("Help — Command formats");
        title.getStyleClass().add("chat-title");
        header.getChildren().add(title);

        Label commands = new Label(GuiCommandHandler.COMMAND_HELP);
        commands.setWrapText(true);
        commands.getStyleClass().addAll("message-bubble", "assistant-bubble");
        VBox content = new VBox(commands);
        content.setPadding(new Insets(28, 48, 28, 48));
        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("conversation-scroll");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        panel.getChildren().addAll(header, scroll);
        return panel;
    }

    /**
     * Displays a failed card action even when the user is currently viewing My List.
     */
    private void showStorageError(StorageException exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Task change was not saved");
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }

    /**
     * Refreshes the task count displayed in the sidebar.
     */
    private void refreshTaskSummary() {
        String[] tasks = taskList.produceTaskList();
        long completedTasks = 0;
        for (String task : tasks) {
            if (task.contains("[X]")) {
                completedTasks++;
            }
        }
        taskSummary.setText(String.format("%d tasks  ·  %d done", tasks.length, completedTasks));
    }

    /**
     * Loads the optional portrait, allowing the interface to use a text logo if it is missing or corrupt.
     */
    private Image loadPhoto() {
        URL resource = StewieGui.class.getResource("/images/stewie_photo.png");
        if (resource == null) {
            return null;
        }
        Image image = new Image(resource.toExternalForm());
        return image.isError() ? null : image;
    }

    /**
     * Creates a Stewie image logo while preserving the original proportions.
     *
     * @param size The width and height available for the logo.
     * @return A logo node.
     */
    private StackPane createLogo(double size) {
        ImageView portrait = new ImageView(stewiePhoto);
        portrait.setFitWidth(size);
        portrait.setFitHeight(size);
        portrait.setPreserveRatio(true);
        portrait.setSmooth(true);
        portrait.setAccessibleText("Stewie");

        StackPane logo = stewiePhoto == null ? new StackPane(new Label("S")) : new StackPane(portrait);
        logo.setMinSize(size, size);
        logo.setPrefSize(size, size);
        logo.setMaxSize(size, size);
        return logo;
    }

    /**
     * Creates one item in the sidebar navigation.
     *
     * @param icon The navigation icon.
     * @param labelText The navigation label.
     * @param isActive Whether the item represents the current page.
     * @return A navigation button.
     */
    private Button createNavigationButton(String icon, String labelText, boolean isActive) {
        FontIcon navigationIcon = new FontIcon(icon);
        navigationIcon.setIconSize(20);
        Button button = new Button(labelText, navigationIcon);
        button.setGraphicTextGap(16);
        navigationIcon.iconColorProperty().bind(button.textFillProperty());
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add("navigation-button");
        if (isActive) {
            button.getStyleClass().add("navigation-button-active");
        }
        return button;
    }
}
