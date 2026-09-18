package com.fireinyu.themyth.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for deprecated {@link Util} class.
 */
@SuppressWarnings("deprecation")
public class UtilTest {

    /**
     * Tests instantiation of deprecated Util class.
     */
    @Test
    public void util_instantiation() {
        Util util = new Util();
        assertNotNull(util);
    }
}
