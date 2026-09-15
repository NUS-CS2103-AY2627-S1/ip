package ekud.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link DialogStyle} CSS class names used by reply bubbles.
 */
public class DialogStyleTest {
    @Test
    public void getCssClass_knownStyles_returnExpectedNames() {
        assertEquals("add-label", DialogStyle.ADD.getCssClass());
        assertEquals("marked-label", DialogStyle.CHANGE_MARK.getCssClass());
        assertEquals("delete-label", DialogStyle.DELETE.getCssClass());
        assertEquals("error-label", DialogStyle.ERROR.getCssClass());
        assertEquals("", DialogStyle.NONE.getCssClass());
    }
}
