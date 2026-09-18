package plana.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import plana.command.CommandType;

/**
 * Tests Plana's JavaFX conversation presentation and scrolling behavior.
 */
class DialogBoxTest {
    private static final long TOOLKIT_START_TIMEOUT_SECONDS = 10;

    @BeforeAll
    static void startToolkit() throws InterruptedException {
        CountDownLatch startupComplete = new CountDownLatch(1);
        try {
            Platform.startup(startupComplete::countDown);
        } catch (IllegalStateException exception) {
            startupComplete.countDown();
        }
        assertTrue(startupComplete.await(TOOLKIT_START_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @AfterAll
    static void stopToolkit() {
        Platform.exit();
    }

    @Test
    void getUserDialog_compactRightAlignedMessageCreated() throws Exception {
        onFxThread(() -> {
            DialogBox userDialog = DialogBox.getUserDialog("todo buy milk");
            Label message = (Label) userDialog.getChildren().get(0);

            assertEquals(Pos.TOP_RIGHT, userDialog.getAlignment());
            assertEquals(1, userDialog.getChildren().size());
            assertTrue(message.getStyleClass().contains("user-label"));

            userDialog.resize(900, 100);
            assertEquals(420, message.getMaxWidth());
            return null;
        });
    }

    @Test
    void getPlanaDialog_responseAndErrorUseDistinctPresentation() throws Exception {
        onFxThread(() -> {
            DialogBox responseDialog = DialogBox.getPlanaDialog("Task added", CommandType.TODO);
            ImageView responseAvatar = (ImageView) responseDialog.getChildren().get(0);
            Label responseMessage = (Label) responseDialog.getChildren().get(1);
            DialogBox errorDialog = DialogBox.getPlanaDialog("Invalid command", CommandType.UNKNOWN, true);
            ImageView errorAvatar = (ImageView) errorDialog.getChildren().get(0);
            Label errorMessage = (Label) errorDialog.getChildren().get(1);

            assertEquals(Pos.TOP_LEFT, responseDialog.getAlignment());
            assertAvatar(responseAvatar, "plana-task-success.png");
            assertTrue(responseMessage.getStyleClass().contains("plana-label"));
            assertTrue(responseMessage.getStyleClass().contains("add-label"));

            responseDialog.resize(900, 100);
            assertEquals(620, responseMessage.getMaxWidth());
            assertAvatar(errorAvatar, "plana-confused.png");
            assertTrue(errorMessage.getStyleClass().contains("error-label"));
            return null;
        });
    }

    @Test
    void getPlanaDialog_normalAndKnownErrorRepliesUseCoolAvatar() throws Exception {
        onFxThread(() -> {
            DialogBox helpDialog = DialogBox.getPlanaDialog("Help is here!", CommandType.HELP);
            DialogBox malformedTodoDialog = DialogBox.getPlanaDialog(
                    "A ToDo description cannot be empty.", CommandType.TODO, true);
            DialogBox bannerDialog = DialogBox.getPlanaBannerDialog("Welcome!");

            assertAvatar((ImageView) helpDialog.getChildren().get(0), "plana-cool.png");
            assertAvatar((ImageView) malformedTodoDialog.getChildren().get(0), "plana-cool.png");
            assertAvatar((ImageView) bannerDialog.getChildren().get(0), "plana-cool.png");
            return null;
        });
    }

    @Test
    void contentGrowth_scrollsToLatestDialog() throws Exception {
        onFxThread(() -> {
            AnchorPane mainWindow = loadMainWindow();
            Scene scene = new Scene(mainWindow);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            try {
                ScrollPane scrollPane = (ScrollPane) scene.lookup("#scrollPane");
                VBox dialogContainer = (VBox) scene.lookup("#dialogContainer");
                dialogContainer.setMinHeight(1000);
                mainWindow.layout();
                scrollPane.setVvalue(0.0);

                dialogContainer.setMinHeight(1600);
                mainWindow.layout();

                assertEquals(1.0, scrollPane.getVvalue());
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    private void assertAvatar(ImageView avatar, String imageFileName) {
        assertTrue(avatar.getImage().getUrl().endsWith(imageFileName));
    }

    private AnchorPane loadMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        return loader.load();
    }

    private <T> T onFxThread(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get();
    }
}
