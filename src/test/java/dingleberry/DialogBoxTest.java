package dingleberry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Verifies that {@link DialogBox} assigns a distinct, explicit visual
 * treatment per message type rather than inferring it from message text.
 */
class DialogBoxTest {

    /**
     * Starts the JavaFX toolkit once before any test constructs scene
     * graph nodes, since {@link DialogBox} loads mascot artwork via
     * {@link javafx.scene.image.Image}.
     */
    @BeforeAll
    static void initJavaFxToolkit() throws InterruptedException {
        final CountDownLatch toolkitReady = new CountDownLatch(1);
        try {
            Platform.startup(toolkitReady::countDown);
        } catch (IllegalStateException alreadyStarted) {
            toolkitReady.countDown();
        }
        toolkitReady.await();
    }

    @Test
    void getErrorDialogAppliesErrorStyleNotSuccessStyle() {
        final DialogBox errorDialog = DialogBox.getErrorDialog(
            "Dingleberry is confused! 🤔", "bogus is not a command");

        final VBox card = messageCard(errorDialog);
        assertTrue(card.getStyleClass().contains("error-message"));
        assertFalse(card.getStyleClass().contains("success-message"));
        assertEquals(2, errorDialog.getChildren().size(),
                "an error row should show a warning marker plus its card");
    }

    @Test
    void getErrorDialogKeepsHeadingAndDetailMessageVisible() {
        final DialogBox errorDialog = DialogBox.getErrorDialog(
            "Oopsie, something is missing! ⚠️", "todo needs a description");

        final VBox card = messageCard(errorDialog);
        final Label heading = (Label) card.getChildren().get(0);
        final Label detail = (Label) card.getChildren().get(1);
        assertEquals("Oopsie, something is missing! ⚠️", heading.getText());
        assertEquals("todo needs a description", detail.getText());
    }

    @Test
    void getSuccessDialogAppliesSuccessStyleNotError() {
        final DialogBox successDialog = DialogBox.getSuccessDialog(
            "Taskie added! 🎉", "[T][ ] nice");

        final VBox card = messageCard(successDialog);
        assertTrue(card.getStyleClass().contains("success-message"));
        assertFalse(card.getStyleClass().contains("error-message"));
    }

    @Test
    void getInformationDialogAppliesInformationStyleNotSuccessOrError() {
        final DialogBox informationDialog = DialogBox.getInformationDialog(
            "Your taskies 📋", "1.[T][ ] nice");

        final VBox card = messageCard(informationDialog);
        assertTrue(card.getStyleClass().contains("information-message"));
        assertFalse(card.getStyleClass().contains("success-message"));
        assertFalse(card.getStyleClass().contains("error-message"));
    }

    @Test
    void getUserDialogHasNoAvatarChild() {
        final DialogBox userDialog = DialogBox.getUserDialog("list");

        assertEquals(1, userDialog.getChildren().size(),
                "a user row should show only its bubble, with no avatar");
    }

    /**
     * Navigates to the message card nested inside a non-user dialog row's
     * icon-plus-accent-and-card structure.
     *
     * @param dialogBox the non-user dialog row to inspect.
     * @return the message card, containing the heading and body labels.
     */
    private static VBox messageCard(final DialogBox dialogBox) {
        final Node lastChild = dialogBox.getChildren()
                .get(dialogBox.getChildren().size() - 1);
        final HBox accentAndCard = (HBox) lastChild;
        return (VBox) accentAndCard.getChildren()
                .get(accentAndCard.getChildren().size() - 1);
    }
}
