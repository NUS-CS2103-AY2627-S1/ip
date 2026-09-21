package maggigorengayam.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import maggigorengayam.MaggiGorengAyamException;

/**
 * toValidIndex is protected static, so this test class lives in the same
 * package (maggigorengayam.command) to call it directly - Command itself
 * has no other state-free, IO-free logic worth unit testing on its own.
 */
public class CommandTest {

    @Test
    public void toValidIndex_lowestValidNumber_returnsZeroBasedIndex() throws MaggiGorengAyamException {
        assertEquals(0, Command.toValidIndex(1, 5));
    }

    @Test
    public void toValidIndex_highestValidNumber_returnsZeroBasedIndex() throws MaggiGorengAyamException {
        assertEquals(4, Command.toValidIndex(5, 5));
    }

    @Test
    public void toValidIndex_zero_exceptionThrown() {
        assertThrows(MaggiGorengAyamException.class, () -> Command.toValidIndex(0, 5));
    }

    @Test
    public void toValidIndex_negativeNumber_exceptionThrown() {
        assertThrows(MaggiGorengAyamException.class, () -> Command.toValidIndex(-1, 5));
    }

    @Test
    public void toValidIndex_numberGreaterThanTaskCount_exceptionThrown() {
        assertThrows(MaggiGorengAyamException.class, () -> Command.toValidIndex(6, 5));
    }

    @Test
    public void toValidIndex_emptyTaskList_exceptionThrownForAnyNumber() {
        assertThrows(MaggiGorengAyamException.class, () -> Command.toValidIndex(1, 0));
    }
}
