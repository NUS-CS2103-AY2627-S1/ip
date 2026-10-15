package zinc.javafx;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import zinc.ui.HelpCategory;
import zinc.ui.HelpContent;
import zinc.ui.HelpTopic;

/** Displays Zinc's command reference in a browsable modal window. */
public class HelpWindow extends BorderPane {
    /** The minimum width of the help window. */
    private static final double MINIMUM_WIDTH = 680;

    /** The minimum height of the help window. */
    private static final double MINIMUM_HEIGHT = 440;

    /** Help topics indexed by their corresponding tree items. */
    private final Map<TreeItem<String>, HelpTopic> topicsByTreeItem = new HashMap<>();

    /** The tree used to browse command categories and topics. */
    @FXML
    private TreeView<String> commandTree;

    /** The title of the selected help topic. */
    @FXML
    private Label topicTitleLabel;

    /** The description of the selected help topic. */
    @FXML
    private Label descriptionLabel;

    /** The aliases of the selected help topic. */
    @FXML
    private Label aliasesLabel;

    /** The supported usages of the selected help topic. */
    @FXML
    private Label usagesLabel;

    /** Loads the command-browser layout and content. */
    public HelpWindow() {
        try {
            URL layoutUrl = Objects.requireNonNull(HelpWindow.class.getResource("/view/HelpWindow.fxml"),
                    "Help-window layout is missing");
            FXMLLoader fxmlLoader = new FXMLLoader(layoutUrl);
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the help-window layout", exception);
        }
    }

    /** Populates the command tree and displays the first topic. */
    @FXML
    public void initialize() {
        TreeItem<String> rootItem = new TreeItem<>("Commands");
        TreeItem<String> firstTopicItem = null;

        for (HelpCategory category : HelpCategory.values()) {
            TreeItem<String> categoryItem = createCategoryItem(category);
            rootItem.getChildren().add(categoryItem);
            if (firstTopicItem == null && !categoryItem.getChildren().isEmpty()) {
                firstTopicItem = categoryItem.getChildren().get(0);
            }
        }

        commandTree.setRoot(rootItem);
        commandTree.setShowRoot(false);
        commandTree.getSelectionModel().selectedItemProperty()
                .addListener((ignoredObservable, ignoredOldItem, selectedItem) ->
                        displaySelectedItem(selectedItem));
        if (firstTopicItem != null) {
            commandTree.getSelectionModel().select(firstTopicItem);
        }
    }

    /**
     * Displays this command browser as a modal child of the supplied window.
     *
     * @param owner The window that owns the command browser.
     */
    public void show(Window owner) {
        assert owner != null : "Help-window owner must not be null";
        URL stylesheetUrl = Objects.requireNonNull(HelpWindow.class.getResource("/view/main.css"),
                "Main stylesheet is missing");
        Scene scene = new Scene(this);
        scene.getStylesheets().add(stylesheetUrl.toExternalForm());

        Stage stage = new Stage();
        stage.setTitle("Zinc Command List");
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setMinWidth(MINIMUM_WIDTH);
        stage.setMinHeight(MINIMUM_HEIGHT);
        stage.setScene(scene);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });
        stage.showAndWait();
    }

    /** Creates an expanded tree item containing every topic in one category. */
    private TreeItem<String> createCategoryItem(HelpCategory category) {
        TreeItem<String> categoryItem = new TreeItem<>(category.getDisplayName());
        HelpContent.getTopics().stream()
                .filter(topic -> topic.getCategory() == category)
                .forEach(topic -> {
                    TreeItem<String> topicItem = new TreeItem<>(topic.getTitle());
                    topicsByTreeItem.put(topicItem, topic);
                    categoryItem.getChildren().add(topicItem);
                });
        categoryItem.setExpanded(true);
        return categoryItem;
    }

    /** Displays a selected topic, or selects the first topic when a category is selected. */
    private void displaySelectedItem(TreeItem<String> selectedItem) {
        if (selectedItem == null) {
            return;
        }

        HelpTopic topic = topicsByTreeItem.get(selectedItem);
        if (topic == null && !selectedItem.getChildren().isEmpty()) {
            commandTree.getSelectionModel().select(selectedItem.getChildren().get(0));
            return;
        }
        if (topic != null) {
            displayTopic(topic);
        }
    }

    /** Updates the detail pane using the selected help topic. */
    private void displayTopic(HelpTopic topic) {
        topicTitleLabel.setText(topic.getTitle());
        descriptionLabel.setText(topic.getDescription());
        usagesLabel.setText("Usage:\n\n" + String.join("\n", topic.getUsages()));

        boolean hasAliases = !topic.getAliases().isEmpty();
        aliasesLabel.setText(hasAliases ? "Aliases: " + String.join(", ", topic.getAliases()) : "");
        aliasesLabel.setVisible(hasAliases);
        aliasesLabel.setManaged(hasAliases);
    }

    /** Closes the command browser. */
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) getScene().getWindow();
        stage.close();
    }
}
