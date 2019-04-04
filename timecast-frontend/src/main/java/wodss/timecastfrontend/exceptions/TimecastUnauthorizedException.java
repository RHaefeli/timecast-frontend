package wodss.timecastfrontend.exceptions;

public class TimecastUnauthorizedException extends RuntimeException {
	public TimecastUnauthorizedException(String errorMessage) {
		super(errorMessage);
	}
}
