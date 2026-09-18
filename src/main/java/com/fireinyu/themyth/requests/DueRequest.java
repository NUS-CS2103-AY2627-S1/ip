package com.fireinyu.themyth.requests;

import java.util.List;
import java.util.Map;

import com.fireinyu.themyth.Defaults;
import com.fireinyu.themyth.responses.Response;

/**
 * User request to list all DeadlineTasks due by a given datetime.
 *
 * @see com.fireinyu.themyth.tasks.DeadlineTask
 * @see Response
 */
public class DueRequest extends Request {
    /**
     * Creates a request to list tasks due by a specific date and time.
     *
     * @param posArgs Positional arguments.
     * @param kwargs Keyword arguments.
     */
    public DueRequest(List<String> posArgs, Map<String, String> kwargs) {
        super(posArgs, kwargs, Map.of("sort", Defaults.TASK_ORDER));
    }

    @Override
    public List<InputFieldParser<?>> getPosArgTypes() {
        return List.of(InputFieldParser.STRING, InputFieldParser.DATETIME);
    }

    @Override
    public Map<String, InputFieldParser<?>> getKwargTypes() {
        return Map.of("sort", InputFieldParser.ORDER);
    }
}
