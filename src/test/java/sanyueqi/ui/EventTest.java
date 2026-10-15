package sanyueqi.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {
    @Test
    void normalUnformattedTestPrintMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            String s = SanYueQi.masterTaskList.printNewTask(Event.makeEvent("event 3 /from 4 /to 5".split("\\s+")));

            assertNotNull(s);
            assertTrue(s.contains("[E][ ] 3 (from: 4 to: 5)"));

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

            String s = SanYueQi.masterTaskList.printNewTask(Event.makeEvent("event 3 /from 2026-04-29 /to 2026-04-30".split("\\s+")));

            assertNotNull(s);
            assertTrue(s.contains("[E][ ] 3 (from: 29-Apr-2026 to: 30-Apr-2026)"));

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

            String s = SanYueQi.masterTaskList.printNewTask(Event.makeEvent("event 3 /from 2026-02-29 /to 2026-02-30".split("\\s+")));

            assertNotNull(s);
            assertTrue(s.contains("[E][ ] 3 (from: 28-Feb-2026 to: 28-Feb-2026)"));

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

            String s = SanYueQi.masterTaskList.printNewTask(Event.makeEvent("event 3 /from 2026-02-31 /to 2026-02-32".split("\\s+")));

            assertNotNull(s);
            assertTrue(s.contains("[E][ ] 3 (from: 28-Feb-2026 to: 2026-02-32)"));

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

            String s = SanYueQi.masterTaskList.printNewTask(Event.makeEvent("event".split("\\s+")));

            assertNull(s);

        } catch (SYQException e) {
            assertTrue(true);

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void noToErrorTestPrintMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            String s = SanYueQi.masterTaskList.printNewTask(Event.makeEvent("event 3 /from 4".split("\\s+")));

            assertNull(s);

        } catch (SYQException e) {
            assertTrue(true);

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void toBeforeFromErrorTestPrintMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            String s = SanYueQi.masterTaskList.printNewTask(Event.makeEvent("event 3 /to 4 /from 5".split("\\s+")));

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

            String s = SanYueQi.masterTaskList.printNewTask(Event.makeEvent("event  todo  deadline  !#$ /   /from/from /by /from \\1\" /to   /to 2\\\"".split("\\s+")));

            assertNotNull(s);
            assertTrue(s.contains("[E][ ] todo deadline !#$ / /from/from /by (from: \\1\" to: /to 2\\\")"));

        } catch (SYQException e) {
            fail();

        } finally {
            System.setOut(originalOut);
        }
    }
}
