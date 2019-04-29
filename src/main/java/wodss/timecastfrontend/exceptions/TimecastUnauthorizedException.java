package wodss.timecastfrontend.exceptions;

/**
 * Representation of a 401 error code
 *
 */
public class TimecastUnauthorizedException extends RuntimeException {
	public TimecastUnauthorizedException(String errorMessage) {
		super(errorMessage);
	}
}
