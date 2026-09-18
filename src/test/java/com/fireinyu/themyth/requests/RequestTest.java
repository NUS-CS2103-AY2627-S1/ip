package com.fireinyu.themyth.requests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.exceptions.ArugmentMismatchException;
import com.fireinyu.themyth.exceptions.WrongTypeException;

/**
 * Unit tests for {@link Request} argument validation and retrieval mechanisms.
 */
public class RequestTest {

    private static class DummyRequest extends Request {
        DummyRequest(List<String> posArgs, Map<String, String> kwargs) {
            super(posArgs, kwargs);
        }

        DummyRequest(Request other) {
            super(other);
        }

        @Override
        public List<InputFieldParser<?>> getPosArgTypes() {
            return List.of(InputFieldParser.STRING, InputFieldParser.STRING, InputFieldParser.STRING);
        }

        @Override
        public Map<String, InputFieldParser<?>> getKwargTypes() {
            return Map.of("kw1", InputFieldParser.STRING, "kw2", InputFieldParser.STRING);
        }
    }

    /**
     * Tests that supplying an invalid number of positional arguments throws an {@link ArugmentMismatchException}.
     */
    @Test
    public void constructor_wrongPosArgs() {
        assertThrows(ArugmentMismatchException.class, () -> new DummyRequest(
                List.of("a"),
                Map.of("kw1", "val1", "kw2", "val2")
        ));

        assertThrows(ArugmentMismatchException.class, () -> new DummyRequest(
                List.of("a", "b", "c", "d"),
                Map.of("kw1", "val1", "kw2", "val2")
        ));
    }

    /**
     * Tests that supplying invalid keyword arguments throws an {@link ArugmentMismatchException}.
     */
    @Test
    public void constructor_wrongKwArgs() {
        assertThrows(ArugmentMismatchException.class, () -> new DummyRequest(
                List.of("a", "b", "c"),
                Map.of("kw1", "val1")
        ));

        assertThrows(ArugmentMismatchException.class, () -> new DummyRequest(
                List.of("a", "b", "c"),
                Map.of("kw1", "val1", "wrongKw", "val2")
        ));
    }

    /**
     * Tests that constructing a Request with valid arguments completes without errors.
     */
    @Test
    public void constructor_correctArgs() {
        new DummyRequest(
                List.of("a", "b", "c"),
                Map.of("kw1", "val1", "kw2", "val2")
        );
    }

    /**
     * Tests that positional arguments can be retrieved correctly by index.
     */
    @Test
    public void getArg_posArg() {
        Request req = new DummyRequest(
                List.of("a", "b", "c"),
                Map.of("kw1", "val1", "kw2", "val2")
        );
        assertEquals("b", req.getArg(1, String.class));
        assertEquals("c", req.getArg(2, String.class));
    }

    /**
     * Tests that keyword arguments can be retrieved correctly by keyword name.
     */
    @Test
    public void getArg_kwArg() {
        Request req = new DummyRequest(
                List.of("a", "b", "c"),
                Map.of("kw1", "val1", "kw2", "val2")
        );
        assertEquals("val1", req.getArg("kw1", String.class));
    }

    private static class OptionalKwargRequest extends Request {
        OptionalKwargRequest(List<String> posArgs, Map<String, String> kwargs) {
            super(posArgs, kwargs, Map.of("opt", "defaultVal"));
        }

        @Override
        public List<InputFieldParser<?>> getPosArgTypes() {
            return List.of(InputFieldParser.STRING);
        }

        @Override
        public Map<String, InputFieldParser<?>> getKwargTypes() {
            return Map.of("opt", InputFieldParser.STRING);
        }
    }

    /**
     * Tests that requests with default keyword arguments apply default values when omitted.
     */
    @Test
    public void constructor_defaultKwargs_appliesDefault() {
        Request req = new OptionalKwargRequest(List.of("cmd"), Map.of());
        assertEquals("defaultVal", req.getArg("opt", String.class));
    }

    /**
     * Tests that Request.of constructs a simple single-token request.
     */
    @Test
    public void requestOf_constructsSingleArgRequest() {
        Request req = Request.of("hello");
        assertEquals("hello", req.getArg(0, String.class));
    }

    /**
     * Tests that retrieving an argument with the wrong requested type throws a {@link WrongTypeException}.
     */
    @Test
    public void getArg_wrongType_throwsWrongTypeException() {
        Request req = Request.of("hello");
        assertThrows(WrongTypeException.class, () -> req.getArg(0, Integer.class));
    }

    /**
     * Tests the copy constructor of {@link Request}.
     */
    @Test
    public void constructor_copyConstructor_copiesArguments() {
        Request original = new DummyRequest(
                List.of("a", "b", "c"),
                Map.of("kw1", "val1", "kw2", "val2")
        );
        Request copy = new DummyRequest(original);
        assertEquals("a", copy.getArg(0, String.class));
        assertEquals("b", copy.getArg(1, String.class));
        assertEquals("val1", copy.getArg("kw1", String.class));
    }

    /**
     * Tests {@link AtRequest} argument specifications.
     */
    @Test
    public void atRequest_argumentTypes() {
        AtRequest req = new AtRequest(List.of("at", "2025-06-01-09-00-00"), Map.of());
        assertEquals(List.of(InputFieldParser.STRING, InputFieldParser.DATETIME), req.getPosArgTypes());
        assertEquals(Map.of("sort", InputFieldParser.ORDER), req.getKwargTypes());
    }

    /**
     * Tests {@link DueRequest} argument specifications.
     */
    @Test
    public void dueRequest_argumentTypes() {
        DueRequest req = new DueRequest(List.of("due", "2025-10-15-18-00-00"), Map.of());
        assertEquals(List.of(InputFieldParser.STRING, InputFieldParser.DATETIME), req.getPosArgTypes());
        assertEquals(Map.of("sort", InputFieldParser.ORDER), req.getKwargTypes());
    }
}
