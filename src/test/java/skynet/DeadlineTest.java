package skynet;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    void toString_formatsDeadlineDateAndTime() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0);
        Deadline deadline = new Deadline("return book", dateTime);
        // Act
        String result = deadline.toString();
        System.out.println(result);
        System.out.println("ACTUAL: [" + result + "]");
        System.out.println("EXPECTED PART: [Dec 02 2019, 06:00 pm]");
        // Assert
        assertTrue(result.contains("Dec 02 2019, 06:00 pm"));
    }

    @Test
    void toString_formatsMorningDeadlineCorrectly() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2025, 3, 15, 9, 30);
        Deadline deadline = new Deadline("submit report", dateTime);
        // Act
        String result = deadline.toString();
        // Assert
        assertTrue(result.contains("Mar 15 2025, 09:30 am"));
    }

    @Test
    void toString_formatsMidnightDeadlineCorrectly() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        Deadline deadline = new Deadline("new year task", dateTime);
        // Act
        String result = deadline.toString();
        // Assert
        assertTrue(result.contains("Jan 01 2025, 12:00 am"));
    }

}
