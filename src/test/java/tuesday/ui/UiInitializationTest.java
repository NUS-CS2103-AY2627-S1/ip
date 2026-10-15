package tuesday.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tuesday.task.TaskList;

/**
 * Exercises FXML failures and normal layout on the JavaFX thread using isolated save files.
 */
public class UiInitializationTest {
    @TempDir
    private Path temporaryDirectory;

    @BeforeAll
    public static void startJavaFx() throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> {
            Platform.setImplicitExit(false);
            return null;
        });
        Platform.startup(startup);
        startup.get(15, TimeUnit.SECONDS);
    }

    @Test
    public void loadTasks_missingFileAndFailedRead_haveDistinctOutcomes() throws Exception {
        onFxThread(() -> {
            TaskList tasks = MainWindow.loadTasks(temporaryDirectory.resolve("missing.txt").toString());
            assertEquals(0, tasks.size());
            UncheckedIOException failure = assertThrows(UncheckedIOException.class, () ->
                    MainWindow.loadTasks(temporaryDirectory.toString()));
            assertNotNull(failure.getCause());
            return null;
        });
    }

    @Test
    public void createDialog_missingMalformedOrIncompleteLayout_stopsBeforeUsingControls() throws Exception {
        Path malformed = temporaryDirectory.resolve("malformed.fxml");
        Path incomplete = temporaryDirectory.resolve("incomplete.fxml");
        Files.writeString(malformed, "not xml");
        Files.writeString(incomplete, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<?import javafx.scene.layout.HBox?>\n"
                + "<fx:root type=\"HBox\" xmlns:fx=\"http://javafx.com/fxml/1\"/>\n");
        onFxThread(() -> {
            assertThrows(IllegalStateException.class, () -> new DialogBox("text", null, null));
            IllegalStateException failure = assertThrows(IllegalStateException.class, () ->
                    new DialogBox("text", null, malformed.toUri().toURL()));
            assertTrue(failure.getCause() instanceof IOException);
            assertThrows(IllegalStateException.class, () ->
                    new DialogBox("text", null, incomplete.toUri().toURL()));
            return null;
        });
    }

    @Test
    public void loadMainWindow_missingOrMalformedResource_reportsFailure() throws Exception {
        Path malformed = temporaryDirectory.resolve("main.fxml");
        Files.writeString(malformed, "not xml");
        onFxThread(() -> {
            assertThrows(IllegalStateException.class, () -> Ui.loadMainWindow(null));
            assertThrows(IOException.class, () -> Ui.loadMainWindow(malformed.toUri().toURL()));
            return null;
        });
    }

    @Test
    public void loadMainWindow_unreadableSave_stopsInitialization() throws Exception {
        onFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(Ui.class.getResource("/view/MainWindow.fxml"));
            loader.setControllerFactory(type -> new MainWindow(temporaryDirectory.toString()));
            IOException failure = assertThrows(IOException.class, loader::load);
            Throwable cause = failure;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            assertTrue(cause instanceof IOException);
            return null;
        });
    }

    @Test
    public void loadMainWindow_validResources_loadsTasksAndLaysOutAtDifferentSizes() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "[T][ ] existing\n");
        onFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(Ui.class.getResource("/view/MainWindow.fxml"));
            loader.setControllerFactory(type -> new MainWindow(saveFile.toString()));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root, 600, 600);
            root.applyCss();
            root.layout();
            root.resize(400, 400);
            root.layout();
            root.resize(800, 800);
            root.layout();
            assertNotNull(scene.getRoot().lookup("#userInput"));
            DialogBox dialog = DialogBox.getTuesdayDialog("test response", null);
            Label label = (Label) dialog.lookup("#dialog");
            assertNotNull(label);
            assertEquals("test response", label.getText());
            assertEquals("[T][ ] existing\n", Files.readString(saveFile));
            return null;
        });
    }

    @Test
    public void handleUserInput_displayFailure_stopsWithoutExecutingCommandTwice() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "[T][ ] first\n[T][ ] second\n");
        onFxThread(() -> {
            IllegalStateException failure = new IllegalStateException("dialog failed");
            ArrayList<Throwable> reportedFailures = new ArrayList<>();
            MainWindow controller = new MainWindow(saveFile.toString(), (text, picture, isUser) -> {
                if (!isUser) {
                    throw failure;
                }
                return DialogBox.getUserDialog(text, picture);
            }, (message, cause) -> reportedFailures.add(cause));
            FXMLLoader loader = new FXMLLoader(Ui.class.getResource("/view/MainWindow.fxml"));
            loader.setControllerFactory(type -> controller);
            AnchorPane root = loader.load();
            TextField input = (TextField) root.lookup("#userInput");
            input.setText("delete 1");
            input.fireEvent(new ActionEvent());
            assertTrue(input.isDisabled());
            assertTrue(((Button) root.lookup("#sendButton")).isDisabled());
            assertEquals(1, reportedFailures.size());
            assertSame(failure, reportedFailures.get(0));
            assertEquals("[T][ ] second" + System.lineSeparator(), Files.readString(saveFile));
            VBox conversation = (VBox) loader.getNamespace().get("dialogContainer");
            assertEquals(0, conversation.getChildren().size());
            return null;
        });
    }

    @Test
    public void handleUserInput_normalConversation_savesAndDisplaysResponses() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        onFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(Ui.class.getResource("/view/MainWindow.fxml"));
            loader.setControllerFactory(type -> new MainWindow(saveFile.toString()));
            AnchorPane root = loader.load();
            new Scene(root, 600, 600);
            assertTrue(sendCommand(loader, "todo read book").startsWith("Got it. I've added"));
            assertTrue(sendCommand(loader, "list").contains("1.[T][ ] read book"));
            assertTrue(sendCommand(loader, "find book").contains("1.[T][ ] read book"));
            assertTrue(sendCommand(loader, "mark 1").contains("[T][X] read book"));
            assertTrue(sendCommand(loader, "unmark 1").contains("[T][ ] read book"));
            assertTrue(sendCommand(loader, "todo READ BOOK").contains("same description"));
            assertTrue(sendCommand(loader, "old").startsWith("Okay. I've kept"));
            assertTrue(sendCommand(loader, "todo READ BOOK").contains("same description"));
            assertTrue(sendCommand(loader, "new").startsWith("Got it. I've replaced"));
            root.applyCss();
            root.resize(400, 400);
            root.layout();
            root.resize(800, 800);
            root.layout();
            assertEquals("[T][ ] READ BOOK" + System.lineSeparator(), Files.readString(saveFile));
            TaskList loaded = MainWindow.loadTasks(saveFile.toString());
            assertEquals("[T][ ] READ BOOK", loaded.get(0).toString());
            assertTrue(sendCommand(loader, "delete 1").startsWith("Noted."));
            assertEquals("", Files.readString(saveFile));
            return null;
        });
    }

    @Test
    public void handleUserInput_unknownCommand_opensGuideOnlyWhenActivated() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        onFxThread(() -> {
            ArrayList<String> openedUrls = new ArrayList<>();
            MainWindow controller = new MainWindow(saveFile.toString());
            controller.setLinkOpener(openedUrls::add);
            FXMLLoader loader = new FXMLLoader(Ui.class.getResource("/view/MainWindow.fxml"));
            loader.setControllerFactory(type -> controller);
            AnchorPane root = loader.load();
            new Scene(root, 300, 300);
            assertEquals("Sir, I don't recognize the command \"task\".", sendCommand(loader, "task abc"));
            VBox conversation = (VBox) loader.getNamespace().get("dialogContainer");
            DialogBox response = (DialogBox) conversation.getChildren().getLast();
            Hyperlink link = (Hyperlink) response.lookup("#userGuideLink");
            assertTrue(link.isVisible());
            assertTrue(link.isManaged());
            assertEquals("https://soy046.github.io/ip/", link.getText());
            assertTrue(openedUrls.isEmpty());
            root.applyCss();
            root.layout();
            assertTrue(link.getBoundsInParent().getMaxX() <= link.getParent().getLayoutBounds().getWidth());
            link.fire();
            assertEquals(List.of("https://soy046.github.io/ip/"), openedUrls);
            sendCommand(loader, "deadline work /by tomorrow");
            DialogBox errorResponse = (DialogBox) conversation.getChildren().getLast();
            Hyperlink hiddenLink = (Hyperlink) errorResponse.lookup("#userGuideLink");
            assertFalse(hiddenLink.isVisible());
            assertFalse(hiddenLink.isManaged());
            Hyperlink userLink = (Hyperlink) conversation.getChildren().getFirst().lookup("#userGuideLink");
            assertFalse(userLink.isVisible());
            assertFalse(Files.exists(saveFile));
            return null;
        });
    }

    /**
     * Submits through the actual FXML event handler and reads the most recent Tuesday response.
     */
    private static String sendCommand(FXMLLoader loader, String command) {
        TextField input = (TextField) loader.getNamespace().get("userInput");
        input.setText(command);
        input.fireEvent(new ActionEvent());
        VBox conversation = (VBox) loader.getNamespace().get("dialogContainer");
        DialogBox response = (DialogBox) conversation.getChildren().getLast();
        return ((Label) response.lookup("#dialog")).getText();
    }

    /**
     * Runs each assertion on the JavaFX thread and propagates failures to JUnit.
     */
    private static <T> T onFxThread(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(15, TimeUnit.SECONDS);
    }
}
