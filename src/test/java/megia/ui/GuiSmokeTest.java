package megia.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import megia.model.TaskStorage;
import megia.service.CommandExecutor;
import megia.service.LocalStorageService;
import megia.service.LocalizationService;
import megia.service.TaskService;

/**
 * Performs a desktop-capable smoke test of the JavaFX chatbot shell.
 */
@Tag("gui")
public final class GuiSmokeTest {
    private static final long FX_TIMEOUT_SECONDS = 10;
    private static final List<String> INJECTED_CONTROL_IDS = List.of(
            "transcriptList",
            "titleLabel",
            "subtitleLabel",
            "languageLabel",
            "languageChoiceBox",
            "commandInput",
            "sendButton",
            "userImageButton",
            "starterTodoButton",
            "starterListButton",
            "starterFindButton");

    /**
     * Starts the JavaFX toolkit once for the GUI test class.
     */
    @BeforeAll
    public static void startJavaFxToolkit() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException exception) {
            // The toolkit is already running when the test suite shares a JVM with another GUI test.
        }
        Platform.setImplicitExit(false);
    }

    /**
     * Loads FXML, uses help, renders a card, and refreshes controls after a language switch.
     *
     * @param temporaryDirectory Isolated directory used for task persistence.
     * @throws Exception If the JavaFX operation or assertion fails.
     */
    @Test
    public void loadsShellAndRefreshesLanguageAndTranscript(@TempDir Path temporaryDirectory)
            throws Exception {
        LocalizationService.setLanguage("en");
        runOnJavaFxThread(() -> {
            Path storagePath = temporaryDirectory.resolve("tasks.csv");
            CommandExecutor commandExecutor = new CommandExecutor(
                    new TaskService(new TaskStorage(), new LocalStorageService(storagePath.toString())));
            MainController controller = new MainController(
                    commandExecutor, null, new ProfileImageService());
            FXMLLoader loader = new FXMLLoader(
                    GuiSmokeTest.class.getResource("/megia/ui/MainView.fxml"));
            loader.setControllerFactory(type -> {
                if (type == MainController.class) {
                    return controller;
                }
                throw new IllegalStateException("Unexpected FXML controller: " + type.getName());
            });

            Parent root = loader.load();
            for (String controlId : INJECTED_CONTROL_IDS) {
                assertNotNull(loader.getNamespace().get(controlId),
                        "FXML did not inject " + controlId);
            }
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    GuiSmokeTest.class.getResource("/megia/ui/chat.css").toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
            root.applyCss();
            root.layout();

            Button sendButton = (Button) loader.getNamespace().get("sendButton");
            Button starterHelpButton = (Button) loader.getNamespace().get("starterHelpButton");
            TextField commandInput = (TextField) loader.getNamespace().get("commandInput");
            ListView<?> transcript = (ListView<?>) loader.getNamespace().get("transcriptList");
            assertNotNull(sendButton);
            assertEquals("Send", sendButton.getText());
            assertEquals("Show help", starterHelpButton.getText());

            LocalizationService.setLanguage("cn");
            assertEquals("发送", sendButton.getText());
            assertEquals("显示帮助", starterHelpButton.getText());

            starterHelpButton.fire();
            assertEquals("help", commandInput.getText());
            controller.handleSend();
            root.applyCss();
            root.layout();
            assertEquals(3, transcript.getItems().size());
            assertTrue(Files.notExists(storagePath));
            assertTrue(root.lookupAll(".label").stream()
                    .anyMatch(node -> node instanceof Label label
                            && label.getText().contains("可用指令：")));

            commandInput.setText("todo smoke test");
            controller.handleSend();
            assertEquals(5, transcript.getItems().size());
            assertTrue(Files.exists(storagePath));
            transcript.scrollTo(transcript.getItems().size() - 1);
            root.applyCss();
            root.layout();
            assertFalse(root.lookupAll(".task-card").isEmpty());
            assertFalse(root.lookupAll(".task-actions").isEmpty());

            controller.dispose();
            stage.close();
            LocalizationService.setLanguage("en");
        });
    }

    /**
     * Verifies that errors are labelled and failed commands remain available for retry.
     *
     * @param temporaryDirectory Isolated directory used to inject a persistence failure.
     * @throws Exception If the JavaFX operation or assertion fails.
     */
    @Test
    public void failedCommands_showLocalizedErrorsAndRemainRetryable(
            @TempDir Path temporaryDirectory) throws Exception {
        LocalizationService.setLanguage("en");
        Path storageDirectory = temporaryDirectory.resolve("storage");
        Path storagePath = storageDirectory.resolve("tasks.csv");

        runOnJavaFxThread(() -> {
            CommandExecutor commandExecutor = new CommandExecutor(
                    new TaskService(
                            new TaskStorage(), new LocalStorageService(storagePath.toString())));
            MainController controller = new MainController(
                    commandExecutor, null, new ProfileImageService());
            FXMLLoader loader = new FXMLLoader(
                    GuiSmokeTest.class.getResource("/megia/ui/MainView.fxml"));
            loader.setControllerFactory(type -> controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    GuiSmokeTest.class.getResource("/megia/ui/chat.css").toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            TextField commandInput = (TextField) root.lookup("#commandInput");
            Button sendButton = (Button) root.lookup("#sendButton");
            ListView<?> transcript = (ListView<?>) root.lookup("#transcriptList");
            commandInput.setText("unknown-command");
            sendButton.fire();
            root.applyCss();
            root.layout();

            assertEquals("unknown-command", commandInput.getText());
            assertEquals(1, root.lookupAll(".error-message").size());
            assertTrue(root.lookupAll(".error-label").stream()
                    .anyMatch(node -> node instanceof Label label
                            && label.getText().equals("Error")));

            LocalizationService.setLanguage("cn");
            root.applyCss();
            root.layout();
            assertTrue(root.lookupAll(".error-label").stream()
                    .anyMatch(node -> node instanceof Label label
                            && label.getText().equals("错误")));
            LocalizationService.setLanguage("en");

            int errorCount = root.lookupAll(".error-message").size();
            commandInput.setText("help");
            sendButton.fire();
            root.applyCss();
            root.layout();
            assertEquals("", commandInput.getText());
            transcript.scrollTo(2);
            root.applyCss();
            root.layout();
            assertEquals(errorCount, root.lookupAll(".error-message").size());

            commandInput.setText("todo retry once");
            sendButton.fire();
            root.applyCss();
            root.layout();
            assertEquals("todo retry once", commandInput.getText());
            assertTrue(Files.notExists(storagePath));

            Files.createDirectory(storageDirectory);
            sendButton.fire();
            assertEquals("", commandInput.getText());
            assertEquals("TODO,false,retry once", Files.readString(storagePath));

            controller.dispose();
            stage.close();
        });
    }

    /**
     * Verifies that transcript content stays readable and actionable while resizing.
     *
     * @param temporaryDirectory Isolated directory used for task persistence.
     * @throws Exception If the JavaFX operation or assertion fails.
     */
    @Test
    public void transcriptResizing_keepsLongContentAndActionsAccessible(
            @TempDir Path temporaryDirectory) throws Exception {
        LocalizationService.setLanguage("en");
        runOnJavaFxThread(() -> {
            Path storagePath = temporaryDirectory.resolve("tasks.csv");
            CommandExecutor commandExecutor = new CommandExecutor(
                    new TaskService(
                            new TaskStorage(), new LocalStorageService(storagePath.toString())));
            MainController controller = new MainController(
                    commandExecutor, null, new ProfileImageService());
            FXMLLoader loader = new FXMLLoader(
                    GuiSmokeTest.class.getResource("/megia/ui/MainView.fxml"));
            loader.setControllerFactory(type -> controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    GuiSmokeTest.class.getResource("/megia/ui/chat.css").toExternalForm());
            Stage stage = new Stage();
            stage.setMinWidth(540);
            stage.setMinHeight(620);
            stage.setWidth(540);
            stage.setHeight(620);
            stage.setScene(scene);
            stage.show();

            try {
                ListView<?> transcript = (ListView<?>) root.lookup("#transcriptList");
                TextField commandInput = (TextField) root.lookup("#commandInput");
                Button sendButton = (Button) root.lookup("#sendButton");
                applyLayout(root);
                assertNoVisibleHorizontalScrollBar(transcript);

                String longEnglishDescription = "unbroken".repeat(45);
                commandInput.setText("todo " + longEnglishDescription);
                sendButton.fire();
                String longChineseDescription = "这是一个很长的中文任务说明".repeat(20);
                commandInput.setText("todo " + longChineseDescription);
                sendButton.fire();
                LocalizationService.setLanguage("cn");
                commandInput.setText("list");
                sendButton.fire();
                transcript.scrollTo(transcript.getItems().size() - 1);

                for (double width : List.of(840.0, 540.0, 720.0, 540.0)) {
                    stage.setWidth(width);
                    stage.setHeight(width == 540.0 ? 620 : 760);
                    applyLayout(root);
                    assertNoVisibleHorizontalScrollBar(transcript);
                    assertLongDescriptionsWrap(root);
                    assertTaskActionsFitCards(root);
                }
            } finally {
                controller.dispose();
                stage.close();
                LocalizationService.setLanguage("en");
            }
        });
    }

    /**
     * Verifies that malformed startup storage blocks commands and remains unchanged on close.
     *
     * @param temporaryDirectory Isolated directory containing malformed task storage.
     * @throws Exception If the JavaFX operation or assertion fails.
     */
    @Test
    public void malformedStorage_blocksSessionAndRemainsUnchanged(@TempDir Path temporaryDirectory)
            throws Exception {
        LocalizationService.setLanguage("en");
        Path storagePath = temporaryDirectory.resolve("tasks.csv");
        String malformedContent = "TODO,false,\"unclosed";
        Files.writeString(storagePath, malformedContent);

        runOnJavaFxThread(() -> {
            MegiaGuiApplication application = new MegiaGuiApplication(
                    new LocalStorageService(storagePath.toString()));
            Stage stage = new Stage();
            application.start(stage);
            Parent root = stage.getScene().getRoot();
            root.applyCss();
            root.layout();

            TextField commandInput = (TextField) root.lookup("#commandInput");
            Button sendButton = (Button) root.lookup("#sendButton");
            assertTrue(commandInput.isDisabled());
            assertTrue(sendButton.isDisabled());
            for (String controlId : List.of(
                    "starterHelpButton", "starterTodoButton",
                    "starterListButton", "starterFindButton")) {
                assertTrue(root.lookup("#" + controlId).isDisabled());
            }
            assertTrue(root.lookupAll(".label").stream()
                    .anyMatch(node -> node instanceof Label label
                            && label.getText().contains(storagePath.toString())
                            && label.getText().contains("restart Megia")));
            assertEquals(1, root.lookupAll(".error-message").size());

            commandInput.setText("todo must not be saved");
            sendButton.fire();
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));

            assertFalse(stage.isShowing());
            assertEquals(malformedContent, Files.readString(storagePath));
        });
    }

    /**
     * Verifies that missing startup storage permits task creation and persistence.
     *
     * @param temporaryDirectory Isolated directory for newly created task storage.
     * @throws Exception If the JavaFX operation or assertion fails.
     */
    @Test
    public void missingStorage_allowsTaskCreationAndPersistence(@TempDir Path temporaryDirectory)
            throws Exception {
        LocalizationService.setLanguage("en");
        Path storagePath = temporaryDirectory.resolve("tasks.csv");

        runOnJavaFxThread(() -> {
            MegiaGuiApplication application = new MegiaGuiApplication(
                    new LocalStorageService(storagePath.toString()));
            Stage stage = new Stage();
            application.start(stage);
            Parent root = stage.getScene().getRoot();
            TextField commandInput = (TextField) root.lookup("#commandInput");
            Button sendButton = (Button) root.lookup("#sendButton");

            assertFalse(commandInput.isDisabled());
            assertFalse(sendButton.isDisabled());
            commandInput.setText("todo persisted task");
            sendButton.fire();
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));

            assertFalse(stage.isShowing());
            assertEquals("TODO,false,persisted task", Files.readString(storagePath));
        });
    }

    /**
     * Verifies that a failed {@code bye} save leaves the GUI usable and retryable.
     *
     * @param temporaryDirectory Isolated directory used to inject and resolve a save failure.
     * @throws Exception If the JavaFX operation or assertion fails.
     */
    @Test
    public void bye_saveFailureAllowsTaskActionAndExitRetry(@TempDir Path temporaryDirectory)
            throws Exception {
        LocalizationService.setLanguage("en");
        Path storageDirectory = temporaryDirectory.resolve("storage");
        Path storagePath = storageDirectory.resolve("tasks.csv");
        Files.createDirectory(storageDirectory);
        Files.writeString(storagePath, "TODO,false,persisted task");

        runOnJavaFxThread(() -> {
            MegiaGuiApplication application = new MegiaGuiApplication(
                    new LocalStorageService(storagePath.toString()));
            Stage stage = new Stage();
            application.start(stage);
            Parent root = stage.getScene().getRoot();
            TextField commandInput = (TextField) root.lookup("#commandInput");
            Button sendButton = (Button) root.lookup("#sendButton");

            Files.delete(storagePath);
            Files.delete(storageDirectory);
            commandInput.setText("bye");
            sendButton.fire();
            root.applyCss();
            root.layout();

            assertTrue(stage.isShowing());
            assertFalse(commandInput.isDisabled());
            assertFalse(sendButton.isDisabled());
            assertTrue(root.lookupAll(".label").stream()
                    .anyMatch(node -> node instanceof Label label
                            && label.getText().contains("try again")));
            String englishSendLabel = sendButton.getText();
            LocalizationService.setLanguage("cn");
            assertFalse(englishSendLabel.equals(sendButton.getText()));
            LocalizationService.setLanguage("en");

            commandInput.setText("list");
            sendButton.fire();
            root.applyCss();
            root.layout();
            HBox taskActions = (HBox) root.lookup(".task-actions");
            assertNotNull(taskActions);

            Files.createDirectory(storageDirectory);
            Button markButton = (Button) taskActions.getChildren().get(0);
            markButton.fire();
            assertEquals("TODO,true,persisted task", Files.readString(storagePath));

            commandInput.setText("bye");
            sendButton.fire();
            assertFalse(stage.isShowing());
            assertTrue(commandInput.isDisabled());
            LocalizationService.setLanguage("cn");
            assertEquals(englishSendLabel, sendButton.getText());
            LocalizationService.setLanguage("en");
        });
    }

    private static void runOnJavaFxThread(ThrowingRunnable action) throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                failure.set(throwable);
            } finally {
                completed.countDown();
            }
        });
        assertTrue(completed.await(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        if (failure.get() != null) {
            throw new AssertionError("JavaFX smoke test failed", failure.get());
        }
    }

    private static void applyLayout(Parent root) {
        root.applyCss();
        root.layout();
    }

    private static void assertNoVisibleHorizontalScrollBar(ListView<?> transcript) {
        assertTrue(transcript.lookupAll(".scroll-bar").stream()
                .filter(node -> node instanceof ScrollBar scrollBar
                        && scrollBar.getOrientation() == Orientation.HORIZONTAL)
                .noneMatch(Node::isVisible));
    }

    private static void assertLongDescriptionsWrap(Parent root) {
        List<Label> descriptionLabels = root.lookupAll(".task-description").stream()
                .filter(Label.class::isInstance)
                .map(Label.class::cast)
                .toList();
        assertTrue(descriptionLabels.size() >= 2);
        for (Label descriptionLabel : descriptionLabels) {
            assertTrue(descriptionLabel.getHeight() > descriptionLabel.getFont().getSize() * 2);
            assertContained(descriptionLabel, (VBox) descriptionLabel.getParent());
        }
    }

    private static void assertTaskActionsFitCards(Parent root) {
        List<HBox> taskActions = root.lookupAll(".task-actions").stream()
                .filter(HBox.class::isInstance)
                .map(HBox.class::cast)
                .toList();
        assertTrue(taskActions.size() >= 2);
        for (HBox taskAction : taskActions) {
            assertContained(taskAction, (VBox) taskAction.getParent());
            assertTrue(taskAction.getChildren().stream().allMatch(Node::isVisible));
        }
    }

    private static void assertContained(Node child, Node container) {
        Bounds childBounds = child.localToScene(child.getBoundsInLocal());
        Bounds containerBounds = container.localToScene(container.getBoundsInLocal());
        double tolerance = 0.5;
        assertTrue(childBounds.getMinX() >= containerBounds.getMinX() - tolerance);
        assertTrue(childBounds.getMaxX() <= containerBounds.getMaxX() + tolerance);
        assertTrue(childBounds.getMinY() >= containerBounds.getMinY() - tolerance);
        assertTrue(childBounds.getMaxY() <= containerBounds.getMaxY() + tolerance);
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
