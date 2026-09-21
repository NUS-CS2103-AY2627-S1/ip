package friday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests deadline date construction and matching independently of completion and time.
 */
class DeadlineTest {
    @Test
    void constructor_supportedDateTime_storesParsedValue() {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
    }

    @ParameterizedTest
    @CsvSource({"2019-12-01,false", "2019-12-02,true", "2019-12-03,false"})
    void occursOn_dueDate_onlyMatchesThatDateRegardlessOfStatus(String date, boolean isExpectedMatch) {
        Deadline deadline = new Deadline("return book", "2019-12-02 18:00");
        assertEquals(isExpectedMatch, deadline.occursOn(LocalDate.parse(date)));
        deadline.markAsDone();
        assertEquals(isExpectedMatch, deadline.occursOn(LocalDate.parse(date)));
    }
}
