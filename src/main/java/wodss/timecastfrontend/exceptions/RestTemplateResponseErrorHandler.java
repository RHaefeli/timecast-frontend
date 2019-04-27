package wodss.timecastfrontend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {

        return (
                httpResponse.getStatusCode().series() == CLIENT_ERROR
                        || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse)
            throws IOException {

        if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            // handle SERVER_ERROR
        } else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            // handle CLIENT_ERROR
            HttpStatus statusCode = httpResponse.getStatusCode();
            switch(statusCode) {
                case NOT_FOUND: throw new TimecastNotFoundException(statusCode.getReasonPhrase());
                case FORBIDDEN: throw new TimecastForbiddenException(statusCode.getReasonPhrase());
                case PRECONDITION_FAILED: throw new TimecastPreconditionFailedException(statusCode.getReasonPhrase());
                case UNAUTHORIZED: throw new TimecastUnauthorizedException(statusCode.getReasonPhrase());
                case INTERNAL_SERVER_ERROR: throw new TimecastInternalServerErrorException(statusCode.getReasonPhrase());
            }
        }
    }
}
