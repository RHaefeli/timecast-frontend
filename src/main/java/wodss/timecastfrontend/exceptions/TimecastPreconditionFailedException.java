package wodss.timecastfrontend.exceptions;

/**
 * Representation of a 412 error code
 *
 */
public class TimecastPreconditionFailedException extends RuntimeException {
	public TimecastPreconditionFailedException(String errorMessage) {
		super(errorMessage);
	}
}
