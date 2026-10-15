package chudgpt.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import chudgpt.exception.ChudException;

/** Tests keyword validation and exact fixed-width priority labels. */
public class PriorityTest {
    @Test
    public void parse_supportedKeywords_ignoresCase() throws ChudException {
        for (Priority priority : Priority.values()) {
            assertEquals(priority, Priority.parse(priority.name()));
            assertEquals(priority, Priority.parse(priority.name().toLowerCase(Locale.ROOT)));
        }
        assertEquals(Priority.EXTREME, Priority.parse("ExTrEmE"));
    }

    @Test
    public void parse_unsupportedKeywords_throwsUsefulError() {
        for (String value : new String[]{"", "1", "0", "urgent", "h", "med", "high-priority"}) {
            ChudException exception = assertThrows(ChudException.class, () -> Priority.parse(value));
            assertEquals("Priority must be one of: EXTREME, HIGH, MEDIUM, LOW, NONE.", exception.getMessage());
        }
    }

    @Test
    public void getDisplayLabel_everyPriority_hasExactPadding() {
        assertEquals("[P:EXTREME]", Priority.EXTREME.getDisplayLabel());
        assertEquals("[P:HIGH   ]", Priority.HIGH.getDisplayLabel());
        assertEquals("[P:MEDIUM ]", Priority.MEDIUM.getDisplayLabel());
        assertEquals("[P:LOW    ]", Priority.LOW.getDisplayLabel());
        assertEquals("[P:NONE   ]", Priority.NONE.getDisplayLabel());
    }
}
