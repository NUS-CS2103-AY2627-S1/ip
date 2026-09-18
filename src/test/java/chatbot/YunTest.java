package chatbot;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests end-to-end command dispatch without using the application's real task file. */
class YunTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void commandLifecycle_addListFindMarkUnmarkDeleteAndBye() {
        Yun yun = new Yun(new Storage(temporaryDirectory.resolve("tasks.txt").toString()), new Ui());

        assertTrue(yun.getResponse("todo read book").contains("I've added"));
        assertTrue(yun.getResponse("find BOOK").contains("read book"));
        assertTrue(yun.getResponse("mark 1").contains("[X]"));
        assertTrue(yun.getResponse("unmark 1").contains("[ ]"));
        assertTrue(yun.getResponse("list").contains("read book"));
        assertTrue(yun.getResponse("delete 1").contains("removed"));
        assertTrue(yun.getResponse("bye").contains("Buh bai"));
    }

    @Test
    void invalidCommandsAndDuplicatesBecomeUserFacingErrors() {
        Yun yun = new Yun(new Storage(temporaryDirectory.resolve("tasks.txt").toString()), new Ui());

        yun.getResponse("todo read book");
        assertTrue(yun.getResponse("todo read book").contains("duplicate"));
        assertTrue(yun.getResponse("mark 2").contains("invalid"));
        assertTrue(yun.getResponse("unknown command").contains("Invalid input"));
    }
}
