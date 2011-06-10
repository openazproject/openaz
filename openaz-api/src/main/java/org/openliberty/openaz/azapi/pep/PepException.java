package org.openliberty.openaz.azapi.pep;

/**
 * The PepException is used to provide additional
 * information to callers of the PepApi when
 * exception conditions occur.
 */
public class PepException extends Exception {

	private static final long serialVersionUID = 1L;

	/** 
	 * Create a PepException containing a Throwable that
	 * specifies the cause of this PepException.
	 * @param cause
	 */
	public PepException(Throwable cause) {
        super(cause);
    }

	/**
	 * Create a PepException containing the message provided
	 * and a Throwable containing further information as to
	 * the cause of the PepException.
	 * @param message
	 * @param cause
	 */
    public PepException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a PepException containing the message provided.
     * @param message
     */
    public PepException(String message) {
        super(message);
    }

	public PepException() {
		// TODO Auto-generated constructor stub
	}
}

