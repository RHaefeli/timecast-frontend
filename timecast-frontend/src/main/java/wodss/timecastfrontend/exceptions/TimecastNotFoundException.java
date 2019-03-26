package wodss.timecastfrontend.exceptions;

public class TimecastNotFoundException extends RuntimeException {
    public TimecastNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
