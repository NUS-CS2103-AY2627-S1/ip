package oz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Tests that the {@link MainWindow} layout and controller are correctly loaded via FXML.
 */
public class MainWindowTest {

    /** Temporary folder for test storage file. */
    @TempDir
    Path temporaryFolder;

    /** Controller instance under test. */
    private MainWindow mainWindowController;
    /** Root anchor pane loaded from FXML. */
    private AnchorPane rootLayout;
    /** Chatbot instance injected into controller. */
    private Oz oz;

    /**
     * Initializes the JavaFX toolkit once before running GUI unit tests.
     */
    @BeforeAll
    public static void initJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException exception) {
            // Toolkit has already been initialized
        }
    }

    /**
     * Loads MainWindow from FXML and injects a temporary Oz instance before each test.
     *
     * @throws IOException If the FXML resource cannot be loaded.
     */
    @BeforeEach
    public void setUp() throws IOException {
        Path storageFile = temporaryFolder.resolve("gui_test_tasks.txt");
        this.oz = new Oz(storageFile.toString());

        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
        this.rootLayout = fxmlLoader.load();
        this.mainWindowController = fxmlLoader.getController();
        this.mainWindowController.setOz(this.oz);
    }

    @Test
    public void fxmlLoading_validFxml_controllerAndLayoutInitialized() {
        assertNotNull(this.mainWindowController);
        assertNotNull(this.rootLayout);
        assertEquals(400.0, this.rootLayout.getPrefWidth());
        assertEquals(600.0, this.rootLayout.getPrefHeight());
        assertEquals(320.0, this.rootLayout.getMinWidth());
        assertEquals(400.0, this.rootLayout.getMinHeight());
    }

    @Test
    public void fxmlLoading_validFxml_childrenHierarchyConstructed() {
        assertEquals(3, this.rootLayout.getChildren().size());
        assertInstanceOf(TextField.class, this.rootLayout.getChildren().get(0));
        assertInstanceOf(Button.class, this.rootLayout.getChildren().get(1));
        assertInstanceOf(ScrollPane.class, this.rootLayout.getChildren().get(2));

        Button sendButton = (Button) this.rootLayout.getChildren().get(1);
        assertEquals("Send", sendButton.getText());

        TextField userInput = (TextField) this.rootLayout.getChildren().get(0);
        assertEquals("Enter a command, e.g. list", userInput.getPromptText());
    }

    @Test
    public void fxmlLoading_validFxml_stylesheetAttached() {
        assertTrue(this.rootLayout.getStylesheets().stream()
                .anyMatch((stylesheet) -> stylesheet.contains("main.css")));
    }

    @Test
    public void sendButton_blankAndNonBlankInput_updatesDisabledState() {
        TextField userInput = (TextField) this.rootLayout.getChildren().get(0);
        Button sendButton = (Button) this.rootLayout.getChildren().get(1);

        assertTrue(sendButton.isDisabled());

        userInput.setText("   ");
        assertTrue(sendButton.isDisabled());

        userInput.setText("list");
        assertFalse(sendButton.isDisabled());
    }

    @Test
    public void setOz_validOz_displaysWelcomeMessage() {
        ScrollPane scrollPane = (ScrollPane) this.rootLayout.getChildren().get(2);
        VBox dialogContainer = (VBox) scrollPane.getContent();

        assertEquals(1, dialogContainer.getChildren().size());
        DialogBox welcomeDialog = (DialogBox) dialogContainer.getChildren().get(0);
        TextArea welcomeMessage = (TextArea) welcomeDialog.getChildren().get(1);
        assertTrue(welcomeMessage.getText().contains("Try 'list'"));
    }

    @Test
    public void sendButton_validCommand_displaysTurnAndClearsInput() {
        TextField userInput = (TextField) this.rootLayout.getChildren().get(0);
        Button sendButton = (Button) this.rootLayout.getChildren().get(1);
        ScrollPane scrollPane = (ScrollPane) this.rootLayout.getChildren().get(2);
        VBox dialogContainer = (VBox) scrollPane.getContent();
        userInput.setText("todo read book");

        sendButton.fire();

        assertEquals("", userInput.getText());
        assertEquals(3, dialogContainer.getChildren().size());
        DialogBox responseDialog = (DialogBox) dialogContainer.getChildren().get(2);
        TextArea responseMessage = (TextArea) responseDialog.getChildren().get(1);
        assertTrue(responseMessage.getText().contains("[T][ ] read book"));
    }
}
