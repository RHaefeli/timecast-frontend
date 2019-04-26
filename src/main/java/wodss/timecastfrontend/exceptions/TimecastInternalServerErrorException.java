package wodss.timecastfrontend.exceptions;

public class TimecastInternalServerErrorException extends RuntimeException {
	public TimecastInternalServerErrorException(String errorMessage) {
		super(errorMessage);
	}

}
