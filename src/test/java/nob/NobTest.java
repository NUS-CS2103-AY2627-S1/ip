package nob;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Basic JUnit setup checks for the Nob application.
 */
public class NobTest {
    /**
     * Confirms that JUnit assertions run from Gradle's test source set.
     */
    @Test
    public void basicAssertionPasses() {
        assertEquals(2, 2);
    }

    /**
     * Confirms that multiple JUnit test methods are discovered.
     */
    @Test
    public void anotherBasicAssertionPasses() {
        assertEquals(4, 4);
    }
}
