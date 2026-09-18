package com.fireinyu.themyth.requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fireinyu.themyth.exceptions.ArugmentMismatchException;
import com.fireinyu.themyth.exceptions.WrongTypeException;
import com.fireinyu.themyth.responses.Response;

/**
 * User request which triggers a user cycle.
 * Each Request is handled by the active ChatMode, which produces a Response.
 * The app then handles the Response and then waits for the next Request.
 *
 * @see com.fireinyu.themyth.chatmodes.ChatMode
 * @see Response
 */
public abstract class Request {

    private final List<String> posArgs;
    private final Map<String, String> kwargs;
    private final List<Object> posArgObjects;
    private final Map<String, Object> kwargObjects;

    /**
     * Initializes a Request from the arguments of another Request.
     *
     * @param request request to copy arguments from.
     * @throws ArugmentMismatchException If the arguments do not match what is expected.
     */
    public Request(Request request) {
        this(request.posArgs, request.kwargs);
    }

    /**
     * Initializes a Request with optional keyword arguments.
     *
     * @param posArgs Positional arguments.
     * @param kwargs Provided keyword arguments (both compulsory and optional).
     * @param optionalKwargs default values of optional keyword arguments.
     * @throws ArugmentMismatchException If the arguments do not match what is expected.
     */
    protected Request(
            List<String> posArgs,
            Map<String, String> kwargs,
            Map<String, Object> optionalKwargs) {
        this.posArgs = posArgs;
        this.kwargs = kwargs;
        posArgObjects = new ArrayList<>();
        kwargObjects = new HashMap<>(optionalKwargs);
        List<InputFieldParser<?>> posArgTypes = getPosArgTypes();
        Map<String, InputFieldParser<?>> kwargTypes = getKwargTypes();
        validateArguments(posArgTypes, kwargTypes, optionalKwargs);

        for (int i = 0; i < posArgs.size(); i++) {
            posArgObjects.add(posArgTypes.get(i).parse(posArgs.get(i)));
        }
        for (String keyword : kwargs.keySet()) {
            kwargObjects.put(keyword, kwargTypes.get(keyword).parse(kwargs.get(keyword)));
        }
    }

    /**
     * Initializes a Request with no optional keyword arguments.
     *
     * @param posArgs Positional arguments.
     * @param kwargs Keyword arguments.
     */
    protected Request(List<String> posArgs, Map<String, String> kwargs) {
        this(posArgs, kwargs, Map.of());
    }

    /**
     * Validates argument counts and names before any argument values are parsed.
     */
    private void validateArguments(List<InputFieldParser<?>> posArgTypes,
            Map<String, InputFieldParser<?>> kwargTypes, Map<String, Object> optionalKwargs) {
        if (posArgTypes.size() != posArgs.size()) {
            throw new ArugmentMismatchException(posArgTypes.size(), posArgs.size());
        }
        Set<String> allKeywords = new HashSet<>(kwargs.keySet());
        allKeywords.addAll(optionalKwargs.keySet());
        if (!kwargTypes.keySet().equals(allKeywords)) {
            throw new ArugmentMismatchException(kwargTypes.keySet(), kwargs.keySet());
        }
    }

    /**
     * Creates a simple request with only a command.
     *
     * @param command The command string.
     * @return A new Request object.
     */
    public static Request of(String command) {
        return new Request(List.of(command), Map.of()) {
            @Override
            public List<InputFieldParser<?>> getPosArgTypes() {
                return List.of(InputFieldParser.STRING);
            }

            @Override
            public Map<String, InputFieldParser<?>> getKwargTypes() {
                return Map.of();
            }
        };
    }

    /**
     * Returns the value of a positional argument.
     *
     * @param at the argument's position (0 is the command).
     * @param type argument type.
     * @return the value of the positional argument.
     * @see String
     */
    public <T> T getArg(int at, Class<T> type) {
        Object argument = posArgObjects.get(at);
        if (type.isInstance(argument)) {
            return type.cast(argument);
        }
        throw new WrongTypeException(type);
    }

    /**
     * Returns the value of a keyword argument.
     *
     * @param key the argument's keyword.
     * @param type argument type.
     * @return the value of the keyword argument.
     * @see String
     */
    public <T> T getArg(String key, Class<T> type) {
        Object argument = kwargObjects.get(key);
        if (type.isInstance(argument)) {
            return type.cast(argument);
        }
        throw new WrongTypeException(type);
    }

    /**
     * Gets the expected types for positional arguments.
     *
     * @return A list of parsers for positional arguments.
     */
    public abstract List<InputFieldParser<?>> getPosArgTypes();

    /**
     * Gets the expected types for keyword arguments.
     *
     * @return A map of keyword to parser for keyword arguments.
     */
    public abstract Map<String, InputFieldParser<?>> getKwargTypes();
}
