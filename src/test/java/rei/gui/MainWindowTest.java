package rei.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rei.Rei;

/** Tests JavaFX user interaction without requiring manual mouse or keyboard input. */
public class MainWindowTest {
    private static final int JAVA_FX_TIMEOUT_SECONDS = 10;

    @TempDir
    private Path testDirectory;

    @BeforeAll
    public static void startJavaFx() throws InterruptedException {
        CountDownLatch startupLatch = new CountDownLatch(1);
        Platform.startup(startupLatch::countDown);
        assertTrue(startupLatch.await(JAVA_FX_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Platform.setImplicitExit(false);
    }

    @AfterAll
    public static void stopJavaFx() {
        Platform.exit();
    }

    @Test
    public void userInput_validCommand_addsUserAndReiDialogs() throws InterruptedException {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
            loader.load();
            MainWindow controller = loader.getController();
            controller.setRei(new Rei(testDirectory.resolve("tasks.txt")));

            TextField userInput = (TextField) loader.getNamespace().get("userInput");
            VBox dialogContainer = (VBox) loader.getNamespace().get("dialogContainer");
            int initialDialogCount = dialogContainer.getChildren().size();

            userInput.setText("todo read book");
            userInput.fireEvent(new ActionEvent());

            assertEquals(initialDialogCount + 2, dialogContainer.getChildren().size());
            assertEquals("", userInput.getText());
            DialogBox responseDialog = (DialogBox) dialogContainer.getChildren().get(initialDialogCount + 1);
            Label response = (Label) responseDialog.lookup(".message-bubble");
            assertTrue(response.getText().contains("One less thing to remember. I've added: [T][ ] read book"));
        });
    }

    @Test
    public void userInput_invalidCommand_displaysErrorAndAllowsRecovery() throws InterruptedException {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
            loader.load();
            MainWindow controller = loader.getController();
            controller.setRei(new Rei(testDirectory.resolve("tasks.txt")));
            TextField input = (TextField) loader.getNamespace().get("userInput");
            VBox dialogs = (VBox) loader.getNamespace().get("dialogContainer");

            input.setText("mark 1");
            input.fireEvent(new ActionEvent());
            assertTrue(dialogs.getChildren().getLast().getStyleClass().contains("error-dialog"));
            input.setText("todo recover");
            input.fireEvent(new ActionEvent());
            assertTrue(dialogs.getChildren().getLast().getStyleClass().contains("rei-dialog"));
            assertEquals("", input.getText());
        });
    }

    @Test
    public void dialog_resize_expandsReplyWidth() throws InterruptedException {
        runOnJavaFxThread(() -> {
            DialogBox dialog = DialogBox.getReiDialog("A long task description ".repeat(30));
            Label message = (Label) dialog.lookup(".message-bubble");
            dialog.resize(400, 200);
            double narrowWidth = message.getMaxWidth();
            dialog.resize(700, 200);
            assertTrue(message.getMaxWidth() > narrowWidth);
            assertTrue(message.isWrapText());
        });
    }

    @Test
    public void userInput_bye_disablesInputAndClosesActualWindow() throws InterruptedException {
        CountDownLatch hiddenLatch = new CountDownLatch(1);
        AtomicReference<Stage> window = new AtomicReference<>();
        try {
            runOnJavaFxThread(() -> {
                FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
                Scene scene = new Scene(loader.load());
                MainWindow controller = loader.getController();
                controller.setRei(new Rei(testDirectory.resolve("tasks.txt")));
                Stage stage = new Stage();
                window.set(stage);
                stage.setScene(scene);
                stage.setOnHidden(event -> hiddenLatch.countDown());
                stage.show();
                TextField input = (TextField) loader.getNamespace().get("userInput");
                Button send = (Button) loader.getNamespace().get("sendButton");

                input.setText("bye");
                input.fireEvent(new ActionEvent());

                assertTrue(input.isDisabled());
                assertTrue(send.isDisabled());
            });
            assertTrue(hiddenLatch.await(JAVA_FX_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } finally {
            runOnJavaFxThread(() -> {
                if (window.get() != null) {
                    window.get().hide();
                }
            });
        }
    }

    /** Executes assertions on the JavaFX application thread and reports any failure to JUnit. */
    private static void runOnJavaFxThread(ThrowingRunnable action) throws InterruptedException {
        CountDownLatch completionLatch = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                failure.set(throwable);
            } finally {
                completionLatch.countDown();
            }
        });

        assertTrue(completionLatch.await(JAVA_FX_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        if (failure.get() != null) {
            fail(failure.get());
        }
    }

    /** Represents test code that can throw a checked exception. */
    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
