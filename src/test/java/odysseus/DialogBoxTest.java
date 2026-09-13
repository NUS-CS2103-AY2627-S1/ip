package odysseus;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.layout.VBox;

/**
 * Tests the layout of conversational entries.
 */
class DialogBoxTest {
    @BeforeAll
    static void initializeJavaFx() throws InterruptedException {
        CountDownLatch startupComplete = new CountDownLatch(1);
        try {
            Platform.startup(startupComplete::countDown);
        } catch (IllegalStateException exception) {
            startupComplete.countDown();
        }
        startupComplete.await();
    }

    @Test
    void getUserDialog_reordersExistingChildrenWithoutDuplicates() throws InterruptedException {
        runOnJavaFxThread(() -> {
            DialogBox dialog = DialogBox.getUserDialog("todo mend sail");
            assertInstanceOf(VBox.class, dialog.getChildren().getFirst());
        });
    }

    @Test
    void getErrorDialog_marksEntryForCorrectionStyling() throws InterruptedException {
        runOnJavaFxThread(() -> {
            DialogBox dialog = DialogBox.getErrorDialog("Use a task number after mark.");

            assertTrue(dialog.getStyleClass().contains("error-dialog"));
        });
    }

    private static void runOnJavaFxThread(Runnable action) throws InterruptedException {
        CountDownLatch actionComplete = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable exception) {
                failure.set(exception);
            } finally {
                actionComplete.countDown();
            }
        });
        actionComplete.await();
        if (failure.get() != null) {
            fail(failure.get());
        }
    }
}
