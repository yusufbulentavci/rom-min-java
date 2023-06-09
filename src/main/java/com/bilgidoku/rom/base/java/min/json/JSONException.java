package com.bilgidoku.rom.base.java.min.json;

/**
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 * @author JSON.org
 * @version 2010-12-24
 */
public class JSONException extends Exception {
	private static final long serialVersionUID = 0;
	private Throwable cause;

    public JSONException(String message) {
        super(message);
    }
	
    /**
     * Constructs a JSONException with an explanatory message.
     * @param message Detail about the reason for the exception.
     */
    public JSONException(String message, Throwable t) {
        super(message, t);
    }

    public JSONException(Throwable cause) {
        super(cause);
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }
}
