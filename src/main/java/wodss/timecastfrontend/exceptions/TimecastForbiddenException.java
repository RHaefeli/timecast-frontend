package wodss.timecastfrontend.exceptions;

/**
 * Representation of a 403 error code
 *
 */
public class TimecastForbiddenException extends RuntimeException {
	public TimecastForbiddenException(String errorMessage) {
		super(errorMessage);
	}

}
