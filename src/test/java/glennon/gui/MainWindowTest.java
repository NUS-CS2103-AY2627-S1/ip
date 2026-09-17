package glennon.gui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import glennon.Glennon;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Exercises the real FXML controls on the JavaFX application thread.
 */
class MainWindowTest {
    @TempDir
    private Path directory;

    private AnchorPane root;
    private TextField input;
    private Button sendButton;
    private VBox dialogContainer;
    private Stage stage;

    @BeforeAll
    static void startJavaFx() throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> {
            Platform.setImplicitExit(false);
            return null;
        });
        Platform.startup(startup);
        startup.get(10, TimeUnit.SECONDS);
    }

    @AfterAll
    static void stopJavaFx() {
        Platform.exit();
    }

    /**
     * Runs scenario steps on the FX thread with completed layout pulses between them.
     */
    private void runScenario(Runnable... steps) throws Exception {
        try {
            runOnFxThread(() -> {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
                root = loader.load();
                loader.<MainWindow>getController().setGlennon(
                        new Glennon(directory.resolve("missions.txt").toString()));
                stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                input = (TextField) root.lookup("#userInput");
                sendButton = (Button) root.lookup("#sendButton");
                dialogContainer = (VBox) root.lookup("#dialogContainer");
                return null;
            });
            awaitLayoutPulse();
            for (Runnable step : steps) {
                runOnFxThread(() -> {
                    step.run();
                    return null;
                });
                awaitLayoutPulse();
            }
        } finally {
            runOnFxThread(() -> {
                if (stage != null) {
                    stage.close();
                }
                return null;
            });
        }
    }

    /**
     * Propagates JavaFX exceptions and assertion failures to the JUnit thread.
     */
    private void runOnFxThread(Callable<Void> action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }

    /**
     * Waits until a pulse has laid out deferred input, scrolling, and stage changes.
     */
    private void awaitLayoutPulse() throws Exception {
        FutureTask<Void> completed = new FutureTask<>(() -> null);
        runOnFxThread(() -> {
            AnimationTimer timer = new AnimationTimer() {
                private boolean hasSeenPulse;

                @Override
                public void handle(long now) {
                    if (hasSeenPulse) {
                        stop();
                        completed.run();
                    }
                    hasSeenPulse = true;
                }
            };
            timer.start();
            Platform.requestNextPulse();
            return null;
        });
        completed.get(10, TimeUnit.SECONDS);
    }

    private void submit(String command) {
        input.setText(command);
        sendButton.fire();
    }

    private String lastResponse() {
        return ((Label) dialogContainer.getChildren().getLast().lookup("#dialog")).getText();
    }

    private VBox lastMessage() {
        return (VBox) dialogContainer.getChildren().getLast().lookup("#message");
    }

    /**
     * Checks that an error is distinguished by a visible label as well as its style.
     */
    private void assertErrorMessage() {
        assertTrue(lastMessage().getStyleClass().contains("error-card"));
        Label messageType = (Label) lastMessage().lookup("#messageType");
        assertTrue(messageType.isVisible());
        assertTrue(messageType.isManaged());
        assertEquals("ATTENTION NEEDED", messageType.getText());
    }

    /**
     * Checks that a normal reply does not reserve space for an error label.
     */
    private void assertNormalMessage() {
        assertTrue(lastMessage().getStyleClass().contains("reply-card"));
        assertFalse(lastMessage().getStyleClass().contains("error-card"));
        Label messageType = (Label) lastMessage().lookup("#messageType");
        assertFalse(messageType.isVisible());
        assertFalse(messageType.isManaged());
    }

    /**
     * Checks containment in scene coordinates while allowing fractional layout rounding.
     */
    private void assertContainedIn(Node child, Node parent) {
        Bounds childBounds = child.localToScene(child.getLayoutBounds());
        Bounds parentBounds = parent.localToScene(parent.getLayoutBounds());
        assertTrue(childBounds.getMinX() >= parentBounds.getMinX() - 1);
        assertTrue(childBounds.getMinY() >= parentBounds.getMinY() - 1);
        assertTrue(childBounds.getMaxX() <= parentBounds.getMaxX() + 1);
        assertTrue(childBounds.getMaxY() <= parentBounds.getMaxY() + 1);
    }

    /**
     * Checks that controls remain visible and separate from the scrolling transcript.
     */
    private void assertComposerFits() {
        VBox composer = (VBox) root.lookup("#composer");
        ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
        assertContainedIn(composer, root);
        assertContainedIn(input, composer);
        assertContainedIn(sendButton, composer);
        assertTrue(input.getWidth() > 100);
        assertTrue(input.localToScene(input.getLayoutBounds()).getMaxX()
                <= sendButton.localToScene(sendButton.getLayoutBounds()).getMinX());
        assertTrue(scroll.localToScene(scroll.getLayoutBounds()).getMaxY()
                <= composer.localToScene(composer.getLayoutBounds()).getMinY() + 1);
        assertTrue(dialogContainer.getWidth() <= scroll.getViewportBounds().getWidth() + 1);
    }

    /**
     * Checks that the compact avatar and header remain inside the resized window.
     */
    private void assertHeaderFits() {
        Node header = root.lookup(".header");
        ImageView avatar = (ImageView) root.lookup("#glennonAvatar");
        assertContainedIn(header, root);
        assertContainedIn(avatar, header);
        assertContainedIn(root.lookup(".title"), header);
        assertContainedIn(root.lookup(".subtitle"), header);
        assertTrue(avatar.getBoundsInLocal().getWidth() > 0);
        assertTrue(avatar.getBoundsInLocal().getWidth() <= 48);
        assertTrue(avatar.getBoundsInLocal().getHeight() > 0);
        assertTrue(avatar.getBoundsInLocal().getHeight() <= 48);
    }

    /**
     * Checks that the original artwork loads without obscuring the greeting.
     */
    private void assertOriginalArtworkLoads() {
        ImageView avatar = (ImageView) root.lookup("#glennonAvatar");
        Image avatarImage = avatar.getImage();
        assertNotNull(avatarImage);
        assertFalse(avatarImage.isError());
        assertTrue(avatarImage.getWidth() > 0);
        assertEquals(Main.class.getResource("/images/glennon-avatar.png").toExternalForm(), avatarImage.getUrl());
        assertEquals(0.0, avatarImage.getPixelReader().getColor(0, 0).getOpacity());
        assertTrue(avatar.isPreserveRatio());

        Region viewport = (Region) root.lookup("#scrollPane").lookup(".viewport");
        assertEquals(1, viewport.getBackground().getImages().size());
        BackgroundImage background = viewport.getBackground().getImages().getFirst();
        assertFalse(background.getImage().isError());
        assertTrue(background.getImage().getWidth() > 0);
        assertEquals(Main.class.getResource("/images/mission-background.png").toExternalForm(),
                background.getImage().getUrl());
        assertTrue(background.getSize().isCover());
        assertEquals(BackgroundPosition.CENTER, background.getPosition());
        assertEquals(BackgroundRepeat.NO_REPEAT, background.getRepeatX());
        assertEquals(BackgroundRepeat.NO_REPEAT, background.getRepeatY());
        Color replyFill = (Color) lastMessage().getBackground().getFills().getFirst().getFill();
        assertEquals(1.0, replyFill.getOpacity());
        assertHeaderFits();
    }

    @Test
    void initialize_freshSession_displaysGreeting() throws Exception {
        runScenario(() -> {
            assertEquals(1, dialogContainer.getChildren().size());
            assertEquals("Hey there! Glennon online.\nWhat's the mission?", lastResponse());
            assertFalse(input.isDisabled());
            assertNormalMessage();
            assertSame(input, root.getScene().getFocusOwner());
            assertOriginalArtworkLoads();
        });
    }

    @Test
    void handleUserInput_sendButton_addsExchangeAndClearsInput() throws Exception {
        runScenario(() -> {
            submit("todo send a mission");
            assertEquals(3, dialogContainer.getChildren().size());
            assertEquals("", input.getText());
            assertEquals("Mission added: [T][ ] send a mission\nMission log now has 1 mission.", lastResponse());
            assertNormalMessage();
        });
    }

    @Test
    void handleUserInput_enterKey_usesSameCommandHandler() throws Exception {
        runScenario(() -> {
            input.setText("todo keyboard mission");
            input.fireEvent(new ActionEvent());
            submit("list");
            assertEquals("Mission log:\n1. [T][ ] keyboard mission", lastResponse());
        });
    }

    @Test
    void dialogBox_speakers_useDistinctAlignmentAndCardWidths() throws Exception {
        runScenario(() -> {
            submit("list");
        }, () -> {
            DialogBox user = (DialogBox) dialogContainer.getChildren().get(1);
            DialogBox reply = (DialogBox) dialogContainer.getChildren().get(2);
            assertEquals(Pos.TOP_RIGHT, user.getAlignment());
            assertEquals(Pos.TOP_LEFT, reply.getAlignment());
            VBox userMessage = (VBox) user.lookup("#message");
            VBox replyMessage = (VBox) reply.lookup("#message");
            assertTrue(userMessage.getStyleClass().contains("user-card"));
            assertTrue(replyMessage.getStyleClass().contains("reply-card"));
            assertTrue(userMessage.getWidth() < replyMessage.getWidth());
            assertTrue(replyMessage.getWidth() >= reply.getWidth() * 0.9);
        });
    }

    @Test
    void handleUserInput_longMission_wrapsWithoutLosingText() throws Exception {
        String mission = "a long mission description ".repeat(20).stripTrailing();
        runScenario(() -> {
            submit("todo " + mission);
            stage.setWidth(380);
            stage.setHeight(500);
        }, () -> {
            Label reply = (Label) dialogContainer.getChildren().getLast().lookup("#dialog");
            assertTrue(reply.isWrapText());
            assertTrue(reply.getHeight() > 40);
            assertTrue(reply.getText().contains(mission));
            assertTrue(reply.getWidth() < 380);
            DialogBox user = (DialogBox) dialogContainer.getChildren().get(1);
            VBox userMessage = (VBox) user.lookup("#message");
            assertTrue(userMessage.getWidth() <= user.getWidth() * 0.85);
            assertContainedIn(reply, lastMessage());
            assertComposerFits();
        });
    }

    @Test
    void handleUserInput_manyExchanges_scrollsToLatestResponse() throws Exception {
        runScenario(() -> {
            for (int i = 0; i < 20; i++) {
                submit("todo mission " + i);
            }
        }, () -> {
            ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
            assertEquals(1.0, scroll.getVvalue());
            assertEquals(41, dialogContainer.getChildren().size());
            assertContainedIn(lastMessage(), scroll.lookup(".viewport"));
        });
    }

    @Test
    void handleUserInput_bye_disablesFurtherSubmissions() throws Exception {
        runScenario(() -> {
            submit("bye");
            assertEquals("Signing off. Catch you on the next mission!", lastResponse());
            assertTrue(input.isDisabled());
            assertTrue(sendButton.isDisabled());
            submit("todo too late");
            assertEquals(3, dialogContainer.getChildren().size());
        });
    }

    @Test
    void handleUserInput_blankInput_reportsErrorAndPreservesMissions() throws Exception {
        runScenario(() -> {
            submit("todo keep blank case");
            submit("");
            assertTrue(lastResponse().startsWith("Mission control alert!"));
            assertErrorMessage();
            assertEquals("", input.getText());
            submit("list");
            assertEquals("Mission log:\n1. [T][ ] keep blank case", lastResponse());
            assertNormalMessage();
        });
    }

    @Test
    void handleUserInput_invalidIndex_reportsErrorAndPreservesMissions() throws Exception {
        runScenario(() -> {
            submit("todo keep index case");
            submit("delete 99");
            assertTrue(lastResponse().startsWith("Mission control alert!"));
            assertErrorMessage();
            assertEquals("delete 99", input.getText());
            assertEquals("delete 99", input.getSelectedText());
            submit("list");
            assertEquals("Mission log:\n1. [T][ ] keep index case", lastResponse());
            assertNormalMessage();
        });
    }

    @Test
    void handleUserInput_filteredNumbers_updatesOnlyDisplayedMissions() throws Exception {
        String expectedList = "Mission log:\n1. [T][ ] keep first\n"
                + "2. [D][X] choose second (by: Sep 17 2026, 11:59 PM)";
        runScenario(() -> {
            submit("todo keep first");
            submit("deadline choose second /by 17/9/2026");
            submit("todo remove third");
            submit("find remove");
            assertEquals("Matching missions located:\n3. [T][ ] remove third", lastResponse());
            submit("delete 3");
            assertEquals("Mission removed:\n  [T][ ] remove third\nMission log now has 2 missions.",
                    lastResponse());
            submit("on 17/9/2026");
            assertEquals("Missions on Sep 17 2026:\n"
                    + "2. [D][ ] choose second (by: Sep 17 2026, 11:59 PM)", lastResponse());
            submit("mark 2");
            submit("list");
            assertEquals(expectedList, lastResponse());
            assertNormalMessage();
        });
        assertEquals(expectedList, new Glennon(directory.resolve("missions.txt").toString()).getResponse("list"));
    }

    @Test
    void handleUserInput_unicodeBlankDescriptions_keepsErrorsEditableAndRecovers() throws Exception {
        String[] commands = {"todo \u00a0", "deadline \u202f /by 17/9/2026",
            "event \u2007 /from 17/9/2026 /to 17/9/2026"};
        runScenario(() -> {
            submit("todo keep Unicode case");
            for (String command : commands) {
                submit(command);
                assertTrue(lastResponse().startsWith("Mission control alert!\n"));
                assertErrorMessage();
                assertEquals(command, input.getText());
                assertEquals(command, input.getSelectedText());
            }
            submit("list");
            assertEquals("Mission log:\n1. [T][ ] keep Unicode case", lastResponse());
            submit("todo \u00a0corrected mission\u202f");
            assertEquals("Mission added: [T][ ] corrected mission\nMission log now has 2 missions.",
                    lastResponse());
            assertEquals("", input.getText());
            assertNormalMessage();
        });
    }

    @Test
    void initialize_corruptStorage_disablesInputAndPreservesData() throws Exception {
        Files.writeString(directory.resolve("missions.txt"), "corrupt");
        runScenario(() -> {
            assertTrue(lastResponse().contains("Mission data is corrupted at line 1."));
            assertErrorMessage();
            assertTrue(input.isDisabled());
            assertTrue(sendButton.isDisabled());
            submit("todo cannot overwrite");
            assertEquals(1, dialogContainer.getChildren().size());
        });
        assertEquals("corrupt", Files.readString(directory.resolve("missions.txt")));
    }

    @Test
    void handleUserInput_parserError_selectsCommandForCorrection() throws Exception {
        runScenario(() -> {
            submit("todo keep parser case");
            submit("deadline missing date");
            assertErrorMessage();
            assertEquals("deadline missing date", input.getSelectedText());
            assertSame(input, root.getScene().getFocusOwner());
            submit("list");
            assertNormalMessage();
            assertEquals("Mission log:\n1. [T][ ] keep parser case", lastResponse());
            assertEquals("", input.getText());
        });
    }

    @Test
    void handleUserInput_saveFailure_highlightsErrorAndAllowsReadOnlyRecovery() throws Exception {
        Path data = directory.resolve("missions.txt");
        runScenario(() -> {
            assertDoesNotThrow(() -> Files.createDirectory(data));
            submit("todo failed save case");
            assertErrorMessage();
            assertEquals("Mission control alert!\nGlennon could not save the mission data.", lastResponse());
            assertEquals("todo failed save case", input.getSelectedText());
            assertFalse(input.isDisabled());
            assertFalse(sendButton.isDisabled());
            assertTrue(Files.isDirectory(data));
            submit("list");
            assertNormalMessage();
            assertEquals("Mission log:", lastResponse());
        });
    }

    @Test
    void handleUserInput_errorWordsInMission_keepsNormalPresentation() throws Exception {
        runScenario(() -> {
            submit("todo Mission control alert!");
            assertNormalMessage();
            submit("find Mission control alert!");
            assertNormalMessage();
            assertTrue(lastResponse().contains("[T][ ] Mission control alert!"));
        });
    }

    @Test
    void handleUserInput_sendButton_returnsFocusToCommandField() throws Exception {
        runScenario(() -> {
            sendButton.requestFocus();
            assertSame(sendButton, root.getScene().getFocusOwner());
            submit("todo continue with keyboard");
        }, () -> {
            assertSame(input, root.getScene().getFocusOwner());
            input.setText("mark 1");
            input.fireEvent(new ActionEvent());
            assertEquals("Mission marked complete:\n  [T][X] continue with keyboard", lastResponse());
        });
    }

    @Test
    void resize_narrowAndWideWindow_keepsComposerAndTranscriptWithinBounds() throws Exception {
        runScenario(() -> {
            submit("todo resizing mission");
            stage.setWidth(380);
            stage.setHeight(360);
        }, () -> {
            assertComposerFits();
            assertHeaderFits();
            stage.setWidth(900);
            stage.setHeight(800);
        }, () -> {
            assertComposerFits();
            assertHeaderFits();
            assertTrue(root.getWidth() > 800);
            assertTrue(lastMessage().getWidth() > 600);
        });
    }

    @Test
    void handleUserInput_longUnbrokenMission_wrapsWithinNarrowWindow() throws Exception {
        String mission = "unbroken".repeat(60);
        runScenario(() -> {
            stage.setWidth(380);
            stage.setHeight(500);
            submit("todo " + mission);
        }, () -> {
            Label reply = (Label) lastMessage().lookup("#dialog");
            assertTrue(reply.isWrapText());
            assertTrue(reply.getHeight() > 40);
            assertTrue(reply.getText().contains(mission));
            assertContainedIn(reply, lastMessage());
            assertComposerFits();
        });
    }

    @Test
    void resize_readingHistory_preservesScrollUntilNextSubmission() throws Exception {
        runScenario(() -> {
            stage.setWidth(700);
            for (int i = 0; i < 20; i++) {
                submit("todo history mission " + i + " with enough detail to reflow when narrowed");
            }
        }, () -> {
            ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
            scroll.setVvalue(0.25);
            stage.setWidth(380);
        }, () -> {
            ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
            assertEquals(0.25, scroll.getVvalue(), 0.01);
            submit("find history mission 19");
        }, () -> {
            ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
            assertEquals(1.0, scroll.getVvalue());
            assertTrue(lastResponse().contains("history mission 19"));
        });
    }

    @Test
    void scrollPane_pageUp_readsOlderRepliesUsingKeyboard() throws Exception {
        runScenario(() -> {
            for (int i = 0; i < 20; i++) {
                submit("todo keyboard history mission " + i);
            }
        }, () -> {
            ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
            assertEquals(1.0, scroll.getVvalue());
            assertTrue(scroll.isFocusTraversable());
            scroll.requestFocus();
            assertSame(scroll, root.getScene().getFocusOwner());
            scroll.fireEvent(new KeyEvent(
                    KeyEvent.KEY_PRESSED, "", "", KeyCode.PAGE_UP, false, false, false, false));
        }, () -> {
            ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
            assertTrue(scroll.getVvalue() < 1.0);
            assertSame(scroll, root.getScene().getFocusOwner());
        });
    }

    @Test
    void resize_atTranscriptBottom_keepsLatestReplyVisible() throws Exception {
        runScenario(() -> {
            stage.setWidth(700);
            stage.setHeight(500);
            for (int i = 0; i < 20; i++) {
                submit("todo bottom resize mission " + i);
            }
        }, () -> {
            ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
            assertEquals(1.0, scroll.getVvalue());
            assertContainedIn(lastMessage(), scroll.lookup(".viewport"));
            stage.setWidth(380);
        }, () -> {
            ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
            assertEquals(1.0, scroll.getVvalue());
            assertContainedIn(lastMessage(), scroll.lookup(".viewport"));
        });
    }
}
