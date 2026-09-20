package goop.ui;

/**
 * Captures one command response for display in a graphical interface.
 */
public class ResponseUi extends Ui {
    private String response;

    /**
     * Returns the most recently generated response.
     *
     * @return Response text without console decoration.
     */
    public String getResponse() {
        return response;
    }

    @Override
    protected void showResponse(String message) {
        response = message;
    }
}
