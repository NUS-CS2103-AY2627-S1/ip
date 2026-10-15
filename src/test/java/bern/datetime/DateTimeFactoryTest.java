package bern.datetime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Verifies that supported date and time formats produce equivalent values and that times may be omitted.
 */
public class DateTimeFactoryTest {
    private static final String[] dates = {
        "16/9/2026", "16/09/2026", "16-9-2026", "16-09-2026",
        "2026/9/16", "2026/09/16", "2026-9-16", "2026-09-16",
        "16 Sept 2026", "2026 Sept 16", "Sept 16 2026"
    };

    private static final String[] times = {
        "3pm", "3:00pm", "3.00pm",
        "15:00", "15.00"
    };

    @Test
    public void parseDateTime_acceptsMultipleFormatsAndGivesEquivalentOutput() {

        ArrayList<DateTime> dateTimeStrings = new ArrayList<>();
        for (String date : dates) {
            for (String time : times) {
                dateTimeStrings.add(DateTimeFactory.parseDateTime(date + " " + time));
                dateTimeStrings.add(DateTimeFactory.parseDateTime(time + " " + date));
            }
        }

        DateTime reference = dateTimeStrings.get(0);
        for (DateTime dateTime : dateTimeStrings) {
            assertEquals(dateTime.toString(), reference.toString());
        }
    }

    @Test
    public void parseDateTime_acceptsMissingTime() {
        for (String dateString : dates) {
            assertDoesNotThrow(() -> DateTimeFactory.parseDateTime(dateString));
        }
    }
}
