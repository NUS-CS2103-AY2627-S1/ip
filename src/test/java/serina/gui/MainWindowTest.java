package serina.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import serina.ResponseMessage;
import serina.Serina;
import serina.storage.Storage;

/**
 * Tests Serina's JavaFX controls and minimum-size layout.
 */
@Tag("gui")
public class MainWindowTest {
    private static final int SNAPSHOT_HEIGHT = 520;
    private static final int SNAPSHOT_WIDTH = 420;

    @TempDir
    private Path temporaryDirectory;

    @BeforeAll
    public static void startJavaFx() {
        Platform.startup(() -> {
            // Keep the shared toolkit alive after the shutdown test closes its window.
            Platform.setImplicitExit(false);
        });
    }

    @AfterAll
    public static void stopJavaFx() {
        Platform.exit();
    }

    @Test
    public void mainWindow_minimumSizeAndUserActions_controlsRemainUsableAndSeparate() throws Exception {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Region root = loader.load();
            MainWindow controller = loader.getController();
            controller.setSerina(new Serina(new Storage(temporaryDirectory.resolve("serina.txt"))));

            Scene scene = new Scene(root, SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            root.resize(SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT);
            root.applyCss();
            root.layout();

            TextField userInput = (TextField) root.lookup("#userInput");
            Button sendButton = (Button) root.lookup("#sendButton");
            Button helpButton = (Button) root.lookup("#helpButton");
            VBox dialogContainer = (VBox) root.lookup("#dialogContainer");

            assertControlsDoNotOverlap(userInput, sendButton, scene);
            assertEquals("Enter a command…", userInput.getPromptText());

            userInput.setText("todo preserve this draft");
            userInput.positionCaret(5);
            helpButton.fire();
            assertEquals("todo preserve this draft", userInput.getText());
            assertEquals(5, userInput.getCaretPosition());
            assertEquals(3, dialogContainer.getChildren().size());

            userInput.setText("list");
            sendButton.fire();
            assertEquals(5, dialogContainer.getChildren().size());
            assertEquals("", userInput.getText());

            userInput.setText("nonsense");
            Event.fireEvent(userInput, new ActionEvent());
            assertEquals(7, dialogContainer.getChildren().size());
            assertEquals("nonsense", userInput.getText());
            assertEquals("nonsense".length(), userInput.getCaretPosition());

            userInput.setText("   ");
            sendButton.fire();
            assertEquals(7, dialogContainer.getChildren().size());

            root.applyCss();
            root.layout();
            assertEquals(1, root.lookupAll(".error-panel").size());
            Region errorPanel = (Region) root.lookup(".error-panel");
            Label errorHeading = (Label) errorPanel.lookup(".severity-label");
            assertEquals("Error", errorHeading.getText());
            assertMessageWidthsAreAsymmetric(dialogContainer);
            ScrollPane scrollPane = (ScrollPane) root.lookup("#scrollPane");
            scrollPane.setVvalue(scrollPane.getVmax());
            saveSnapshot(root, SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT, "serina-error-window.png");

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
            try {
                assertTrue(stage.isShowing());
                userInput.setText("bye");
                sendButton.fire();
                assertEquals(9, dialogContainer.getChildren().size());
                assertTrue(userInput.isDisabled());
                assertTrue(sendButton.isDisabled());
                assertTrue(helpButton.isDisabled());
                assertFalse(stage.isShowing());

                root.applyCss();
                root.layout();
                saveSnapshot(root, SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT, "serina-main-window.png");
            } finally {
                stage.close();
            }
            return null;
        });
    }

    @Test
    public void dialogBox_warningResponse_showsTextualWarningHeading() throws Exception {
        runOnJavaFxThread(() -> {
            DialogBox dialogBox = DialogBox.createSerinaDialog(ResponseMessage.warning("Check storage."));

            assertTrue(dialogBox.getStyleClass().contains("serina-dialog"));
            assertTrue(dialogBox.lookupAll(".warning-panel").size() == 1);
            Label warningHeading = dialogBox.lookupAll(".severity-label").stream()
                    .map(Label.class::cast)
                    .filter(Label::isManaged)
                    .findFirst()
                    .orElseThrow();
            assertEquals("Warning", warningHeading.getText());
            return null;
        });
    }

    @Test
    public void mainWindow_supportedSizes_controlsRemainWithinLayout() throws Exception {
        int[][] windowSizes = {{420, 520}, {560, 720}, {900, 800}};
        for (int[] windowSize : windowSizes) {
            runOnJavaFxThread(() -> {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
                Region root = loader.load();
                MainWindow controller = loader.getController();
                controller.setSerina(new Serina(new Storage(temporaryDirectory.resolve(
                        "serina-" + windowSize[0] + "x" + windowSize[1] + ".txt"))));
                Scene scene = new Scene(root, windowSize[0], windowSize[1]);
                scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
                root.resize(windowSize[0], windowSize[1]);
                root.applyCss();
                root.layout();

                TextField userInput = (TextField) root.lookup("#userInput");
                Button sendButton = (Button) root.lookup("#sendButton");
                Node headerSubtitle = root.lookup("#headerSubtitle");
                Node captainIdentity = root.lookup("#captainIdentity");
                ImageView serinaPortrait = (ImageView) root.lookup("#serinaPortrait");
                ImageView headerBackground = (ImageView) root.lookup("#headerBackground");
                assertControlsDoNotOverlap(userInput, sendButton, scene);
                assertEquals(windowSize[0] >= 480, headerSubtitle.isVisible());
                assertEquals(windowSize[0] >= 720, captainIdentity.isVisible());
                assertNotNull(serinaPortrait.getImage());
                assertNotNull(serinaPortrait.getClip());
                assertEquals(windowSize[0], headerBackground.getFitWidth(), 1.0);
                String fileName = "serina-main-window-" + windowSize[0] + "x" + windowSize[1] + ".png";
                saveSnapshot(root, windowSize[0], windowSize[1], fileName);
                return null;
            });
        }
    }

    @Test
    public void mainWindow_shrinkAfterExpansion_keepsHelpInputAndMessagesWithinViewport() throws Exception {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Region root = loader.load();
            MainWindow controller = loader.getController();
            controller.setSerina(new Serina(new Storage(temporaryDirectory.resolve("horizontal-resize.txt"))));
            StackPane host = new StackPane(root);
            Scene scene = new Scene(host, 900, 720);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            host.resize(900, 720);
            host.applyCss();
            host.layout();

            TextField userInput = (TextField) root.lookup("#userInput");
            Button helpButton = (Button) root.lookup("#helpButton");
            Button sendButton = (Button) root.lookup("#sendButton");
            userInput.setText("todo " + "Review the mission briefing carefully. ".repeat(5));
            sendButton.fire();
            userInput.setText("todo unfinished order");

            int[] widths = {900, 560, 480, 479, 420, 400, 320, 900, 420};
            for (int width : widths) {
                host.resize(width, 720);
                host.applyCss();
                host.layout();
                assertTrue(root.getWidth() <= width + 1, "Window must shrink to " + width);
                assertWithinHorizontalViewport(helpButton, width);
                assertWithinHorizontalViewport(userInput, width);
                assertWithinHorizontalViewport(sendButton, width);
                assertTrue(userInput.getWidth() >= 100, "Input must remain usable at " + width);
                for (Node messagePanel : root.lookupAll(".message-panel")) {
                    assertWithinHorizontalViewport(messagePanel, width);
                }
                assertEquals("todo unfinished order", userInput.getText());
            }

            helpButton.fire();
            assertEquals("todo unfinished order", userInput.getText());
            host.applyCss();
            host.layout();
            saveSnapshot(root, 420, 720, "serina-horizontal-resize.png");
            return null;
        });
    }

    /**
     * Checks both edges of a control or message against the current viewport width.
     */
    private static void assertWithinHorizontalViewport(Node node, double width) {
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        assertTrue(bounds.getMinX() >= -1, "Left edge must remain visible: " + node.getId());
        assertTrue(bounds.getMaxX() <= width + 1, "Right edge must remain visible: " + node.getId());
    }

    @Test
    public void mainWindow_oversizedResponse_scrollsToResponseBeginning() throws Exception {
        Region[] rootHolder = new Region[1];
        ScrollPane[] scrollPaneHolder = new ScrollPane[1];
        VBox[] dialogContainerHolder = new VBox[1];

        runOnJavaFxThread(() -> {
            Serina serina = new Serina(new Storage(temporaryDirectory.resolve("long-response.txt")));
            for (int taskNumber = 1; taskNumber <= 40; taskNumber++) {
                serina.executeCommand("todo scheduled task " + taskNumber);
            }

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Region root = loader.load();
            MainWindow controller = loader.getController();
            controller.setSerina(serina);
            Scene scene = new Scene(root, SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            root.resize(SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT);
            root.applyCss();
            root.layout();

            TextField userInput = (TextField) root.lookup("#userInput");
            Button sendButton = (Button) root.lookup("#sendButton");
            userInput.setText("list");
            sendButton.fire();
            root.applyCss();
            root.layout();

            rootHolder[0] = root;
            scrollPaneHolder[0] = (ScrollPane) root.lookup("#scrollPane");
            dialogContainerHolder[0] = (VBox) root.lookup("#dialogContainer");
            return null;
        });

        runOnJavaFxThread(() -> {
            Region root = rootHolder[0];
            ScrollPane scrollPane = scrollPaneHolder[0];
            VBox dialogContainer = dialogContainerHolder[0];
            root.applyCss();
            root.layout();

            Node response = dialogContainer.getChildren().get(2);
            assertTrue(response.getBoundsInParent().getHeight() > scrollPane.getViewportBounds().getHeight());
            assertEquals(response.getBoundsInParent().getMinY(), getVisibleTop(scrollPane, dialogContainer), 2.0);
            return null;
        });
    }

    @Test
    public void mainWindow_resizeWhileReadingHistory_preservesTopVisibleMessage() throws Exception {
        Region[] rootHolder = new Region[1];
        ScrollPane[] scrollPaneHolder = new ScrollPane[1];
        VBox[] dialogContainerHolder = new VBox[1];

        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Region root = loader.load();
            MainWindow controller = loader.getController();
            controller.setSerina(new Serina(new Storage(temporaryDirectory.resolve("resize-history.txt"))));
            Scene scene = new Scene(root, SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            root.resize(SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT);

            Button helpButton = (Button) root.lookup("#helpButton");
            for (int commandNumber = 0; commandNumber < 8; commandNumber++) {
                helpButton.fire();
            }
            root.applyCss();
            root.layout();

            rootHolder[0] = root;
            scrollPaneHolder[0] = (ScrollPane) root.lookup("#scrollPane");
            dialogContainerHolder[0] = (VBox) root.lookup("#dialogContainer");
            return null;
        });

        Node[] anchorHolder = new Node[1];
        runOnJavaFxThread(() -> {
            Region root = rootHolder[0];
            ScrollPane scrollPane = scrollPaneHolder[0];
            VBox dialogContainer = dialogContainerHolder[0];
            scrollPane.setVvalue(0.35);
            anchorHolder[0] = findTopVisibleMessage(scrollPane, dialogContainer);

            root.resize(560, SNAPSHOT_HEIGHT);
            root.applyCss();
            root.layout();
            return null;
        });

        runOnJavaFxThread(() -> {
            Node topVisibleMessage = findTopVisibleMessage(scrollPaneHolder[0], dialogContainerHolder[0]);
            assertSame(anchorHolder[0], topVisibleMessage);
            return null;
        });
    }

    /**
     * Checks that short user commands remain narrower than full-width Serina responses.
     */
    private static void assertMessageWidthsAreAsymmetric(VBox dialogContainer) {
        DialogBox initialResponse = (DialogBox) dialogContainer.getChildren().get(0);
        DialogBox helpCommand = (DialogBox) dialogContainer.getChildren().get(1);
        Region responsePanel = (Region) initialResponse.lookup(".message-panel");
        Region commandPanel = (Region) helpCommand.lookup(".message-panel");

        assertTrue(commandPanel.getWidth() < responsePanel.getWidth());
    }

    /**
     * Checks that the text field and send button remain separate and inside the scene.
     */
    private static void assertControlsDoNotOverlap(TextField userInput, Button sendButton, Scene scene) {
        Bounds inputBounds = userInput.localToScene(userInput.getBoundsInLocal());
        Bounds buttonBounds = sendButton.localToScene(sendButton.getBoundsInLocal());

        assertTrue(inputBounds.getMaxX() <= buttonBounds.getMinX());
        assertTrue(buttonBounds.getMaxX() <= scene.getWidth());
        assertFalse(inputBounds.intersects(buttonBounds));
    }

    /**
     * Returns the first message intersecting the top edge of the transcript viewport.
     */
    private static Node findTopVisibleMessage(ScrollPane scrollPane, VBox dialogContainer) {
        double visibleTop = getVisibleTop(scrollPane, dialogContainer);
        return dialogContainer.getChildren().stream()
                .filter(child -> child.getBoundsInParent().getMaxY() >= visibleTop)
                .findFirst()
                .orElseThrow();
    }

    /**
     * Returns the vertical content coordinate at the viewport's top edge.
     */
    private static double getVisibleTop(ScrollPane scrollPane, VBox dialogContainer) {
        double scrollableHeight = Math.max(0,
                dialogContainer.getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight());
        double valueRange = scrollPane.getVmax() - scrollPane.getVmin();
        if (scrollableHeight == 0 || valueRange == 0) {
            return 0;
        }
        double scrollFraction = (scrollPane.getVvalue() - scrollPane.getVmin()) / valueRange;
        return scrollFraction * scrollableHeight;
    }

    /**
     * Saves a rendered window for visual inspection.
     */
    private static void saveSnapshot(Region root, int width, int height, String fileName) throws IOException {
        WritableImage image = new WritableImage(width, height);
        root.snapshot(null, image);
        PixelReader pixelReader = image.getPixelReader();
        BufferedImage bufferedImage = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bufferedImage.setRGB(x, y, pixelReader.getArgb(x, y));
            }
        }

        Path reportDirectory = Path.of("build", "reports", "gui");
        Files.createDirectories(reportDirectory);
        ImageIO.write(bufferedImage, "png", reportDirectory.resolve(fileName).toFile());
    }

    /**
     * Runs an action on the JavaFX Application Thread and returns its result.
     */
    private static <T> T runOnJavaFxThread(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }
}
