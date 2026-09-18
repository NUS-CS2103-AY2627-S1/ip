package com.fireinyu.themyth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.tasks.TaskOrder;

/**
 * Unit tests for default values in {@link Defaults}.
 */
public class DefaultsTest {

    /**
     * Tests instantiation of Defaults class and verifies default values.
     */
    @Test
    public void defaults_constructorAndConstants() {
        Defaults defaults = new Defaults();
        assertNotNull(defaults);

        assertEquals("resources/tasks.csv", Defaults.TASKFILE);
        assertEquals("data/questions.csv", Defaults.QUIZFILE);
        assertEquals(100, Defaults.LINEWIDTH);
        assertEquals(">>> ", Defaults.USERPROMPT);
        assertEquals("The Myth declares, honey 💅✨: ", Defaults.BOTPROMPT);
        assertEquals("The Myth is clutching pearls 🙄💅: ", Defaults.TWEAKPROMPT);
        assertEquals(
                "The Myth dramatically faints into the abyss! The rhinestone tombstone reads 🪦✨: ",
                Defaults.DEATHPROMPT);
        assertEquals("yyyy-MM-dd-HH-mm-ss", Defaults.DATE_INPUTFORMAT);
        assertEquals(TaskOrder.MODIFIED, Defaults.TASK_ORDER);
        assertNotNull(Defaults.STARTMODE);
    }
}
