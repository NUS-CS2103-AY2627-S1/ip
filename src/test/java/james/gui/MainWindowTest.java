package james.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import james.command.CommandResponse;
import james.command.Sticker;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Verifies automatic and manual scrolling in the conversation window.
 */
public class MainWindowTest {
    @BeforeAll
    public static void startJavaFx() throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            ready.countDown();
        });
        assertTrue(ready.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void initialize_overflowingConversation_scrollsWithoutScrollbarClick() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            new Scene(root, 500, 600);
            ScrollPane scrollPane = (ScrollPane) loader.getNamespace().get("scrollPane");
            VBox dialogs = (VBox) loader.getNamespace().get("dialogContainer");
            Region message = new Region();
            message.setMinHeight(2000);
            dialogs.getChildren().add(message);
            root.applyCss();
            root.layout();

            assertFalse(scrollPane.vvalueProperty().isBound());
            assertEquals(scrollPane.getVmax(), scrollPane.getVvalue());
            dialogs.fireEvent(new ScrollEvent(ScrollEvent.SCROLL, 10, 10, 10, 10,
                    false, false, false, false, false, false, 0, 120, 0, 120,
                    ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
                    ScrollEvent.VerticalTextScrollUnits.NONE, 0, 0, null));
            assertTrue(scrollPane.getVvalue() < scrollPane.getVmax());

            message.setMinHeight(3000);
            root.layout();
            assertEquals(scrollPane.getVmax(), scrollPane.getVvalue());
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }
    @Test
    public void createJames_textReply_hasSquareTopLeftCornerWithoutProfiles() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            DialogBox reply = DialogBox.createJames(new CommandResponse("reply", CommandResponse.Type.NORMAL, false));
            DialogBox user = DialogBox.createUser("input");
            VBox root = new VBox(user, reply);
            new Scene(root, 500, 600);
            root.applyCss();
            root.layout();

            assertEquals(1, user.getChildren().size());
            assertInstanceOf(TextFlow.class, user.getChildren().get(0));
            assertEquals(1, reply.getChildren().size());
            TextFlow text = assertInstanceOf(TextFlow.class, reply.getChildren().get(0));
            var radii = text.getBackground().getFills().get(0).getRadii();
            assertEquals(0, radii.getTopLeftHorizontalRadius());
            assertTrue(radii.getTopRightHorizontalRadius() > 0);
            assertTrue(radii.getBottomLeftHorizontalRadius() > 0);
            assertTrue(radii.getBottomRightHorizontalRadius() > 0);
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void createJames_eachDisplayMode_buildsExpectedContent() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            CommandResponse[] responses = {
                new CommandResponse("text", CommandResponse.Type.NORMAL, false),
                CommandResponse.createStickerOnly("fallback", Sticker.YATTA),
                CommandResponse.createWithSticker("error", CommandResponse.Type.ERROR, Sticker.GOMEN)
            };
            for (CommandResponse response : responses) {
                DialogBox box = DialogBox.createJames(response);
                assertEquals(1, box.getChildren().size());
                if (response.getDisplayMode() == CommandResponse.DisplayMode.TEXT_ONLY) {
                    TextFlow text = assertInstanceOf(TextFlow.class, box.getChildren().get(0));
                    assertEquals("text", assertInstanceOf(Text.class, text.getChildren().get(0)).getText());
                } else {
                    VBox content = assertInstanceOf(VBox.class, box.getChildren().get(0));
                    int stickerIndex = content.getChildren().size() - 1;
                    VBox bubble = assertInstanceOf(VBox.class, content.getChildren().get(stickerIndex));
                    ImageView sticker = assertInstanceOf(ImageView.class, bubble.getChildren().get(0));
                    assertFalse(sticker.getImage().isError());
                    assertTrue(sticker.isPreserveRatio());
                    assertEquals(response.getSticker().name() + " sticker", sticker.getAccessibleText());
                    if (response.getDisplayMode() == CommandResponse.DisplayMode.STICKER_ONLY) {
                        assertEquals(1, content.getChildren().size());
                        assertEquals(160, sticker.getFitWidth());
                    } else {
                        assertEquals(2, content.getChildren().size());
                        assertEquals(120, sticker.getFitWidth());
                        TextFlow text = assertInstanceOf(TextFlow.class, content.getChildren().get(0));
                        assertEquals("error", assertInstanceOf(Text.class, text.getChildren().getLast()).getText());
                        assertEquals("Error\n", ((Text) text.getChildren().getFirst()).getText());
                        assertTrue(text.getStyleClass().contains("error"));
                    }
                }
            }
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void initialize_bakeryThemeAtSmallSizes_keepsComposerAndHeaderVisible() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            for (int[] size : new int[][] {{500, 600}, {417, 220}}) {
                FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
                AnchorPane root = loader.load();
                new Scene(root, size[0], size[1]);
                VBox dialogs = (VBox) loader.getNamespace().get("dialogContainer");
                dialogs.getChildren().add(DialogBox.createJames(CommandResponse.createWithSticker(
                        "Gomen! A little flour in the gears.\nPlease provide a task number.",
                        CommandResponse.Type.ERROR, Sticker.NANKORE)));
                root.applyCss();
                root.layout();

                Region header = (Region) root.lookup(".bakery-header");
                Region input = (Region) loader.getNamespace().get("userInput");
                Region button = (Region) loader.getNamespace().get("sendButton");
                var inputBounds = input.localToScene(input.getBoundsInLocal());
                var buttonBounds = button.localToScene(button.getBoundsInLocal());
                assertEquals(inputBounds.getMinY(), buttonBounds.getMinY(), 0.1);
                assertEquals(inputBounds.getMaxY(), buttonBounds.getMaxY(), 0.1);
                assertEquals(8, buttonBounds.getMinX() - inputBounds.getMaxX(), 0.1);
                assertTrue(buttonBounds.getMaxX() <= size[0]);
                assertTrue(buttonBounds.getMaxY() <= size[1]);
                assertTrue(header.getHeight() < inputBounds.getMinY());
                ImageView artwork = (ImageView) header.lookup("ImageView");
                assertFalse(artwork.getImage().isError());
                TextFlow error = (TextFlow) dialogs.lookup(".error");
                Text text = (Text) error.getChildren().get(0);
                assertEquals(javafx.scene.paint.Color.web("#783c30"), text.getFill());
            }
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void createJames_groupedStickers_fitArtworkBelowWrappedText() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            for (int width : new int[] {417, 500}) {
                for (Sticker sticker : Sticker.values()) {
                    for (boolean isStandalone : new boolean[] {true, false}) {
                        CommandResponse response = isStandalone
                                ? CommandResponse.createStickerOnly("fallback", sticker)
                                : CommandResponse.createWithSticker("A long task description ".repeat(12),
                                        CommandResponse.Type.NORMAL, sticker);
                        DialogBox box = DialogBox.createJames(response);
                        VBox root = new VBox(box);
                        new Scene(root, width, 800);
                        root.applyCss();
                        root.layout();

                        VBox content = (VBox) box.getChildren().get(0);
                        VBox bubble = (VBox) content.getChildren().getLast();
                        ImageView artwork = (ImageView) bubble.getChildren().getFirst();
                        assertTrue(bubble.getWidth() < content.getWidth());
                        assertEquals(artwork.getBoundsInParent().getWidth()
                                + bubble.getInsets().getLeft() + bubble.getInsets().getRight(),
                                bubble.getWidth(), 1);
                        assertTrue(artwork.getBoundsInParent().getMaxY() <= bubble.getHeight());
                        assertEquals(0, bubble.getLayoutX(), 0.1);
                        assertFalse(bubble.getBackground().getFills().isEmpty());
                        if (!isStandalone) {
                            TextFlow text = (TextFlow) content.getChildren().getFirst();
                            assertTrue(text.getHeight() > 40);
                            assertEquals(4, bubble.getLayoutY() - text.getBoundsInParent().getMaxY(), 0.1);
                        }
                    }
                }
            }
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void createUser_shortAndLongCommands_alignRightAndWrap() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            for (int width : new int[] {417, 700}) {
                DialogBox shortCommand = DialogBox.createUser("list");
                DialogBox longCommand = DialogBox.createUser("todo a long description ".repeat(15));
                VBox root = new VBox(shortCommand, longCommand);
                new Scene(root, width, 800);
                root.applyCss();
                root.layout();
                TextFlow shortText = (TextFlow) shortCommand.getChildren().getFirst();
                TextFlow longText = (TextFlow) longCommand.getChildren().getFirst();
                assertTrue(shortText.getWidth() < width / 2.0);
                assertTrue(shortText.getLayoutX() > width / 2.0);
                assertTrue(longText.getWidth() < width * 0.85);
                assertTrue(longText.getHeight() > shortText.getHeight());
                assertEquals(shortText.getBoundsInParent().getMaxX(),
                        longText.getBoundsInParent().getMaxX(), 0.1);
            }
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void initialize_resizeHeight_compactsAndRestoresHeader() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            new Scene(root, 500, 600);
            root.applyCss();
            root.layout();
            Region header = (Region) loader.getNamespace().get("bakeryHeader");
            javafx.scene.control.Label subtitle =
                    (javafx.scene.control.Label) loader.getNamespace().get("headerSubtitle");
            double fullHeight = header.getHeight();
            root.resize(417, 220);
            root.applyCss();
            root.layout();
            assertFalse(subtitle.isVisible());
            assertFalse(subtitle.isManaged());
            assertTrue(header.getHeight() < fullHeight);
            root.resize(500, 600);
            root.applyCss();
            root.layout();
            assertTrue(subtitle.isVisible());
            assertTrue(subtitle.isManaged());
            assertEquals(fullHeight, header.getHeight(), 0.1);
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

}
