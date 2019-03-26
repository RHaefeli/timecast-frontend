package wodss.timecastfrontend.exceptions;

public class TimecastPreconditionFailedException extends Exception{
	public TimecastPreconditionFailedException(String errorMessage) {
		super(errorMessage);
	}
}
