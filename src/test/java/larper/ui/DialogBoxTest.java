package larper.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class DialogBoxTest {
    @Test
    public void getTagChipStyle_tagCaseAndName_expectedColorRules() {
        assertEquals(DialogBox.getTagChipStyle("school"), DialogBox.getTagChipStyle("SCHOOL"));
        assertNotEquals(DialogBox.getTagChipStyle("school"), DialogBox.getTagChipStyle("work"));
    }
}
