package wodss.timecastfrontend.exceptions;

public class TimecastPreconditionFailedException extends RuntimeException {
	public TimecastPreconditionFailedException(String errorMessage) {
		super(errorMessage);
	}
}
