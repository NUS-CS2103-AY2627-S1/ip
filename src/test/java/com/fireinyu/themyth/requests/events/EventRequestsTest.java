package com.fireinyu.themyth.requests.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.requests.InputFieldParser;

/**
 * Unit tests for interrupt event requests in {@link com.fireinyu.themyth.requests.events}.
 */
public class EventRequestsTest {

    private static class ConcreteInterruptEvent extends InterruptEvent {
        ConcreteInterruptEvent(String... params) {
            super(params);
        }

        @Override
        public List<InputFieldParser<?>> getPosArgTypes() {
            return List.of(InputFieldParser.STRING, InputFieldParser.STRING);
        }

        @Override
        public Map<String, InputFieldParser<?>> getKwargTypes() {
            return Map.of();
        }
    }

    /**
     * Tests {@link InitRequest} instantiation and argument specifications.
     */
    @Test
    public void initRequest_structure() {
        InitRequest req = new InitRequest();
        assertEquals("interrupt", req.getArg(0, String.class));
        assertEquals(List.of(InputFieldParser.STRING), req.getPosArgTypes());
        assertTrue(req.getKwargTypes().isEmpty());
    }

    /**
     * Tests {@link CloseRequest} instantiation and argument specifications.
     */
    @Test
    public void closeRequest_structure() {
        CloseRequest req = new CloseRequest();
        assertEquals("interrupt", req.getArg(0, String.class));
        assertEquals(List.of(InputFieldParser.STRING), req.getPosArgTypes());
        assertTrue(req.getKwargTypes().isEmpty());
    }

    /**
     * Tests custom {@link InterruptEvent} subclasses with parameter propagation.
     */
    @Test
    public void interruptEvent_withParams_prependsInterruptKeyword() {
        ConcreteInterruptEvent event = new ConcreteInterruptEvent("param1");
        assertEquals("interrupt", event.getArg(0, String.class));
        assertEquals("param1", event.getArg(1, String.class));
    }
}
