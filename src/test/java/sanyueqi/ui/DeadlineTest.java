package sanyueqi.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeadlineTest {
    @Test
    void normalUnformattedTestPrintMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            String s = SanYueQi.masterTaskList.printNewTask(Deadline.makeDeadline("deadline 3 /by 4".split("\\s+")));

            assertNotNull(s);
            assertTrue(s.contains("[D][ ] 3 (by: 4)"));

        } catch (SYQException e) {
            fail();

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void normalFormattedTestPrintMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            String s = SanYueQi.masterTaskList.printNewTask(Deadline.makeDeadline("deadline 3 /by 2026-06-13".split("\\s+")));

            assertNotNull(s);
            assertTrue(s.contains("[D][ ] 3 (by: 13-Jun-2026)"));

        } catch (SYQException e) {
            fail();

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void overflowFormattedTestPrintMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            String s = SanYueQi.masterTaskList.printNewTask(Deadline.makeDeadline("deadline 3 /by 2026-06-31".split("\\s+")));

            assertNotNull(s);
            assertTrue(s.contains("[D][ ] 3 (by: 30-Jun-2026)"));

        } catch (SYQException e) {
            fail();

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void invalidDateUnformattedTestPrintMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            String s = SanYueQi.masterTaskList.printNewTask(Deadline.makeDeadline("deadline 3 /by 2026-06-32".split("\\s+")));

            assertNotNull(s);
            assertTrue(s.contains("[D][ ] 3 (by: 2026-06-32)"));

        } catch (SYQException e) {
            fail();

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void noDescriptionErrorTestPrintMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            String s = SanYueQi.masterTaskList.printNewTask(Deadline.makeDeadline("deadline".split("\\s+")));

            assertNull(s);

        } catch (SYQException e) {
            assertTrue(true);

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void noDeadlineErrorTestPrintMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            String s = SanYueQi.masterTaskList.printNewTask(Deadline.makeDeadline("deadline 3 by 4".split("\\s+")));

            assertNull(s);

        } catch (SYQException e) {
            assertTrue(true);

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void stressTestPrintMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            String s = SanYueQi.masterTaskList.printNewTask(Deadline.makeDeadline("deadline  event  todo  !#$ /   /from/from /by /from \\1\" /to   /to 2\\\"".split("\\s+")));

            assertNotNull(s);
            assertTrue(s.contains("[D][ ] event todo !#$ / /from/from (by: /from \\1\" /to /to 2\\\")"));

        } catch (SYQException e) {
            fail();

        } finally {
            System.setOut(originalOut);
        }
    }
}
