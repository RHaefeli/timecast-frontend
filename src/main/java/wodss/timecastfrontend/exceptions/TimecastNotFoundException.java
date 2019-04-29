package wodss.timecastfrontend.exceptions;
/**
 * Representation of a 404 error code
 *
 */
public class TimecastNotFoundException extends RuntimeException {
	public TimecastNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
