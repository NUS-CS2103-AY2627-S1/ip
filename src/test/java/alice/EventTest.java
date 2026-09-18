package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class EventTest {
    private Event sampleEvent() {
        return new Event("project meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 16, 0));
    }

    @Test
    public void getTypeName_event_returnsEvent() {
        assertEquals("Event", sampleEvent().getTypeName());
    }

    @Test
    public void getScheduleSummary_event_spansStartToEnd() {
        assertEquals("Dec 2 2019, 2:00 PM – Dec 2 2019, 4:00 PM", sampleEvent().getScheduleSummary());
    }

    @Test
    public void toString_notDone_showsTypeAndBothTimes() {
        assertEquals("[E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)",
                sampleEvent().toString());
    }

    @Test
    public void toFileFormat_notDone_usesIsoDatesAndZeroFlag() {
        assertEquals("E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00",
                sampleEvent().toFileFormat());
    }

    @Test
    public void toFileFormat_markedDone_recordsTheDoneFlag() {
        Event event = sampleEvent();
        event.markAsDone();

        assertEquals("E | 1 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00", event.toFileFormat());
    }
}
