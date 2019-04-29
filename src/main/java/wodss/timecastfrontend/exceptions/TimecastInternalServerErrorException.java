package wodss.timecastfrontend.exceptions;

/**
 * Representation of a 500 error code
 *
 */
public class TimecastInternalServerErrorException extends RuntimeException {
	public TimecastInternalServerErrorException(String errorMessage) {
		super(errorMessage);
	}

}
