package sunnyexception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TooManyKeywordsExceptionTest {

    @Test
    public void exceptionStoresMessage() {
        TooManyKeywordsException exception = new TooManyKeywordsException("I'll tolerate at most one keyword!");

        assertEquals("I'll tolerate at most one keyword!", exception.getMessage());
    }
}
