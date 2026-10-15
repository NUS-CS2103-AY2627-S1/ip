package sunnyexception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SunnyExceptionTest {

    @Test
    public void exceptionStoresMessage() {
        SunnyException exception = new SunnyException("A %$^@#&!^@ DATE???");

        assertEquals("A %$^@#&!^@ DATE???", exception.getMessage());
    }
}