package student.project.bob.personality;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import student.project.bob.model.Todo;

/**
 * Tests Bob's personality profile.
 */
public class PersonalityProfileTest {
    /**
     * Verifies the default profile keeps Bob's name and uses the requested user image.
     */
    @Test
    public void defaultProfile_nameAndImages_useBobAndWendy() {
        PersonalityProfile profile = PersonalityProfile.DEFAULT;

        assertEquals("Bob", profile.getName());
        assertEquals("/images/Wendy.png", profile.getUserImagePath());
        assertEquals("/images/Bob_the_builder.jpg", profile.getBotImagePath());
    }

    /**
     * Verifies that the default profile provides its own chatbot phrases.
     */
    @Test
    public void defaultProfile_taskResponses_usePersonalityPhrases() {
        PersonalityProfile profile = PersonalityProfile.DEFAULT;
        Todo task = new Todo("read book");

        assertTrue(profile.getWelcomeMessage().contains("Bob"));
        assertTrue(profile.formatAddedTask(task, 1).startsWith("Can we build it?"));
        assertTrue(profile.formatMarkedTask(task).startsWith("Nice work!"));
        assertTrue(profile.formatUnmarkedTask(task).startsWith("All right"));
        assertTrue(profile.formatDeletedTask(task, 0).contains("worksite"));
    }
}
