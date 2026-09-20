package nexus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/** Tests the observable behavior of the JavaFX controls. */
class GuiTest {
    @TempDir
    Path temporaryDirectory;

    @BeforeAll
    static void startJavaFx() throws InterruptedException {
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(started::countDown);
        started.await();
    }

    @Test
    void dialogFactories_userAndNexusUseOppositeLayouts() throws Exception {
        runOnJavaFxThread(() -> {
            WritableImage image = new WritableImage(1, 1);
            DialogBox userDialog = DialogBox.getUserDialog("hello", image);
            DialogBox nexusDialog = DialogBox.getNexusDialog("hi", image);

            assertEquals(Pos.TOP_RIGHT, userDialog.getAlignment());
            assertInstanceOf(Label.class, userDialog.getChildren().get(0));
            assertInstanceOf(ImageView.class, userDialog.getChildren().get(1));
            assertEquals(Pos.TOP_LEFT, nexusDialog.getAlignment());
            assertInstanceOf(ImageView.class, nexusDialog.getChildren().get(0));
            assertInstanceOf(Label.class, nexusDialog.getChildren().get(1));
            return null;
        });
    }

    @Test
    void dialogFactories_errorResponseReceivesDistinctStyle() throws Exception {
        runOnJavaFxThread(() -> {
            DialogBox errorDialog = DialogBox.getNexusDialog(
                    "Please enter a command.", new WritableImage(1, 1), true);

            assertTrue(errorDialog.getStyleClass().contains("error-dialog"));
            return null;
        });
    }

    @Test
    void mainWindow_invalidCommandAddsErrorStyledNexusDialog() throws Exception {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = loader.load();
            MainWindow controller = loader.getController();
            controller.setNexus(new Nexus(temporaryDirectory.resolve("error-style.txt")));
            TextField userInput = (TextField) root.lookup("#userInput");
            Button sendButton = (Button) root.lookup("#sendButton");
            ScrollPane scrollPane = (ScrollPane) root.lookup("#scrollPane");
            VBox dialogContainer = (VBox) scrollPane.getContent();

            userInput.setText("unknown");
            sendButton.fire();

            Node response = dialogContainer.getChildren().get(2);
            assertTrue(response.getStyleClass().contains("error-dialog"));
            return null;
        });
    }

    @Test
    void mainWindow_blankCommandAddsErrorWithoutEmptyUserBubble() throws Exception {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = loader.load();
            MainWindow controller = loader.getController();
            controller.setNexus(new Nexus(temporaryDirectory.resolve("blank-command.txt")));
            TextField userInput = (TextField) root.lookup("#userInput");
            Button sendButton = (Button) root.lookup("#sendButton");
            ScrollPane scrollPane = (ScrollPane) root.lookup("#scrollPane");
            VBox dialogContainer = (VBox) scrollPane.getContent();

            userInput.setText("   ");
            sendButton.fire();

            assertEquals(2, dialogContainer.getChildren().size());
            Node response = dialogContainer.getChildren().get(1);
            assertTrue(response.getStyleClass().contains("error-dialog"));
            return null;
        });
    }

    @Test
    void mainWindow_widerSceneExpandsConversationArea() throws Exception {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = loader.load();
            assertInstanceOf(BorderPane.class, root);
            new Scene(root, 720, 720);
            root.applyCss();
            root.layout();

            ScrollPane scrollPane = (ScrollPane) root.lookup("#scrollPane");
            assertTrue(scrollPane.getWidth() > 600.0);
            return null;
        });
    }

    @Test
    void mainWindow_sendButtonAddsUserAndNexusDialogs() throws Exception {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = loader.load();
            MainWindow controller = loader.getController();
            controller.setNexus(new Nexus(temporaryDirectory.resolve("nexus.txt")));
            TextField userInput = (TextField) root.lookup("#userInput");
            Button sendButton = (Button) root.lookup("#sendButton");
            ScrollPane scrollPane = (ScrollPane) root.lookup("#scrollPane");
            VBox dialogContainer = (VBox) scrollPane.getContent();

            userInput.setText("todo read book");
            sendButton.fire();

            assertEquals(3, dialogContainer.getChildren().size());
            assertEquals("", userInput.getText());
            return null;
        });
    }

    @Test
    void mainWindow_enterAddsUserAndNexusDialogs() throws Exception {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = loader.load();
            MainWindow controller = loader.getController();
            controller.setNexus(new Nexus(temporaryDirectory.resolve("nexus-enter.txt")));
            TextField userInput = (TextField) root.lookup("#userInput");
            ScrollPane scrollPane = (ScrollPane) root.lookup("#scrollPane");
            VBox dialogContainer = (VBox) scrollPane.getContent();

            userInput.setText("todo write report");
            userInput.fireEvent(new ActionEvent());

            assertEquals(3, dialogContainer.getChildren().size());
            assertEquals("", userInput.getText());
            return null;
        });
    }

    /** Runs an assertion on the JavaFX application thread. */
    private static <T> T runOnJavaFxThread(Callable<T> assertion) throws Exception {
        FutureTask<T> task = new FutureTask<>(assertion);
        Platform.runLater(task);
        return task.get();
    }
}
