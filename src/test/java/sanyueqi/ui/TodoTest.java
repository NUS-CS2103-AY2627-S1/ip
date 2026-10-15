package sanyueqi.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TodoTest {
    @Test
    void normalTestPrintMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            String s = SanYueQi.masterTaskList.printNewTask(ToDo.makeToDo("todo 3 4".split("\\s+")));

            assertNotNull(s);
            assertTrue(s.contains("[T][ ] 3 4"));

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

            String s = SanYueQi.masterTaskList.printNewTask(ToDo.makeToDo("todo".split("\\s+")));

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

            String s = SanYueQi.masterTaskList.printNewTask(ToDo.makeToDo("todo  deadline  event  !#$ /   /from/from /by /from \\1\" /to   /to 2\\\"".split("\\s+")));

            assertNotNull(s);
            assertTrue(s.contains("[T][ ] deadline event !#$ / /from/from /by /from \\1\" /to /to 2\\\""));

        } catch (SYQException e) {
            fail();

        } finally {
            System.setOut(originalOut);
        }
    }
}
