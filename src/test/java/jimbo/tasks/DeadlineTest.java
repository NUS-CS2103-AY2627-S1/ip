package jimbo.tasks;

import jimbo.JimboException;
import jimbo.task.Deadline;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeadlineTest {
    @Test
    public void deadlineInvalidByDateTest() {
        String data = "TEST";
        String by = "INVALID DATE";
        assertThrows(JimboException.class, () -> {
           new Deadline(data, by);
        });
    }
}
