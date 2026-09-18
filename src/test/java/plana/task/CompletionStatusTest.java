package plana.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CompletionStatusTest {

    @Test
    void getStatusIcon_notDoneStatus_returnsBlankIcon() {
        assertEquals(" ", CompletionStatus.NOT_DONE.getStatusIcon());
    }

    @Test
    void getStatusIcon_doneStatus_returnsCrossIcon() {
        assertEquals("X", CompletionStatus.DONE.getStatusIcon());
    }
}
