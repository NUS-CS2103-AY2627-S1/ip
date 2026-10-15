package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/** Verifies message content, appearance, and responsive sizing on the JavaFX thread. */
class DialogBoxTest {
    /** Initializes the shared toolkit before any controls are constructed. */
    @BeforeAll
    static void startToolkit() throws Exception {
        FxTestSupport.startToolkit();
    }

    /** Each message type preserves its text, alignment, and responsive width.
     *
     * @param type the message variant to verify.
     */
    @ParameterizedTest
    @ValueSource(strings = {"user", "bot", "error"})
    void dialogPreservesMessageAndLayout(String type) throws Exception {
        FxTestSupport.runOnFxThread(() -> verifyDialog(type));
    }

    /** Checks the common contract and the appropriate avatar arrangement.
     *
     * @param type the message variant to verify.
     */
    private void verifyDialog(String type) {
        DialogBox dialog = createDialog(type);
        verifyMessage(dialog, type);
        verifyRow(dialog, type.equals("user"));
        verifyResponsiveWidth(dialog, type.equals("user") ? 0.8 : 0.86);
        verifyAvatarArrangement(dialog, type.equals("user"));
    }

    /** Checks text wrapping and the message variant's style.
     *
     * @param dialog the message row to inspect.
     * @param type the expected message variant.
     */
    private void verifyMessage(DialogBox dialog, String type) {
        Label message = (Label) dialog.getChildren().getLast();
        assertEquals("First line\n第二行", message.getText());
        assertTrue(message.isWrapText());
        assertEquals(Region.USE_PREF_SIZE, message.getMinHeight());
        assertTrue(message.getStyleClass().containsAll(List.of("message", type + "-message")));
    }

    /** Checks the row's position and number of children.
     *
     * @param dialog the message row to inspect.
     * @param isUser whether the row represents a user command.
     */
    private void verifyRow(DialogBox dialog, boolean isUser) {
        assertTrue(dialog.getStyleClass().contains("dialog-row"));
        assertEquals(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT, dialog.getAlignment());
        assertEquals(Double.MAX_VALUE, dialog.getMaxWidth());
        assertEquals(isUser ? 1 : 2, dialog.getChildren().size());
    }

    /** Checks the leading avatar only for replies, since user rows have no avatar.
     *
     * @param dialog the message row to inspect.
     * @param isUser whether the row represents a user command.
     */
    private void verifyAvatarArrangement(DialogBox dialog, boolean isUser) {
        if (!isUser) {
            verifyAvatar((StackPane) dialog.getChildren().getFirst());
        }
    }

    /** Constructs each public message variant with identical multiline content.
     *
     * @param type the requested message variant.
     * @return the new message row.
     */
    private DialogBox createDialog(String type) {
        return switch (type) {
        case "user" -> DialogBox.getUserDialog("First line\n第二行");
        case "bot" -> DialogBox.getBotDialog("First line\n第二行");
        default -> DialogBox.getErrorDialog("First line\n第二行");
        };
    }

    /** Resizing the row must update the bound maximum message width.
     *
     * @param dialog the row being resized.
     * @param fraction the expected width fraction.
     */
    private void verifyResponsiveWidth(DialogBox dialog, double fraction) {
        Label message = (Label) dialog.getChildren().getLast();
        dialog.resize(500, 100);
        assertEquals(500 * fraction, message.getMaxWidth(), 0.001);
        dialog.resize(800, 100);
        assertEquals(800 * fraction, message.getMaxWidth(), 0.001);
    }

    /** Bot and error avatars retain the badge shape, initials, and sizing rules.
     *
     * @param avatar the badge preceding the reply.
     */
    private void verifyAvatar(StackPane avatar) {
        Circle circle = (Circle) avatar.getChildren().getFirst();
        Label initials = (Label) avatar.getChildren().getLast();
        assertEquals(14.0, circle.getRadius());
        assertTrue(circle.getStyleClass().contains("avatar-circle"));
        assertEquals("76", initials.getText());
        assertTrue(initials.getStyleClass().contains("avatar-text"));
        assertEquals(Region.USE_PREF_SIZE, avatar.getMinWidth());
        assertEquals(Region.USE_PREF_SIZE, avatar.getMaxHeight());
    }
}
