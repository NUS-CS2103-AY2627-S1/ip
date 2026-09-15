package choo.task;

/**
 * Checks the display symbols represented by each task status.
 */
public class TaskStatusTest {
    /**
     * Verifies that each enum constant exposes the expected status symbol.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        assertEquals("X", TaskStatus.DONE.getIcon());
        assertEquals(" ", TaskStatus.NOT_DONE.getIcon());
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected
                    + System.lineSeparator() + "Actual: " + actual);
        }
    }
}
