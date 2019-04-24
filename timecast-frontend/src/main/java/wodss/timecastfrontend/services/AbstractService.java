package wodss.timecastfrontend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import wodss.timecastfrontend.domain.AbstractTimecastEntity;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.*;

import java.util.List;

public abstract class AbstractService<T extends AbstractTimecastEntity> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final String apiURL;
    protected final RestTemplate restTemplate;
    private final Class<T> serviceEntityClass;

    protected AbstractService(RestTemplate restTemplate, String apiURL, Class<T> serviceEntityClass) {
        this.restTemplate = restTemplate;
        this.apiURL = apiURL;
        this.serviceEntityClass = serviceEntityClass;
    }

    public List<T> getAll(Token token) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastInternalServerErrorException {
        logger.debug("Request list for " + serviceEntityClass + " from api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<List<T>> response = restTemplate.exchange(apiURL, HttpMethod.GET, request,
                new ParameterizedTypeReference<List<T>>() {});

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            throwStatusCodeException(statusCode);
        }

        List<T> entities = response.getBody();
        logger.debug("Received " + serviceEntityClass + " list: " + entities);
        return entities;
    }

    public T getById(Token token, long id) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastInternalServerErrorException {
        logger.debug("Request " + serviceEntityClass + " entity with id " + id + " from api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<T> response = restTemplate.exchange(apiURL + "/" + id, HttpMethod.GET, request, serviceEntityClass);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            throwStatusCodeException(statusCode);
        }

        T entity = response.getBody();
        logger.debug("Received " + serviceEntityClass + " entity: " + entity);
        return entity;
    }

    public T create(Token token, T entity) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastPreconditionFailedException, TimecastInternalServerErrorException {
        logger.debug("Create " + serviceEntityClass + " entity " + entity + " to api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        HttpEntity<T> request = new HttpEntity<>(entity, headers);
        ResponseEntity<T> response = restTemplate.exchange(apiURL, HttpMethod.POST, request, serviceEntityClass);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            throwStatusCodeException(statusCode);
        }

        T newEntity = response.getBody();
        logger.debug("Received " + serviceEntityClass + " entity: " + newEntity);
        return newEntity;
    }

    public T update(Token token, T entity) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastPreconditionFailedException, TimecastInternalServerErrorException {
        logger.debug("Update " + serviceEntityClass + " entity " + entity + " to api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        HttpEntity<T> requestEntity = new HttpEntity<>(entity, headers);
        ResponseEntity<T> response = restTemplate.exchange(apiURL + "/" + entity.getId(), HttpMethod.PUT, requestEntity, serviceEntityClass);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            throwStatusCodeException(statusCode);
        }

        T newEntity = response.getBody();
        logger.debug("Received " + serviceEntityClass + " entity: " + newEntity);
        return newEntity;
    }

    public void deleteById(Token token, long id) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastInternalServerErrorException {
        logger.debug("Delete " + serviceEntityClass + " entity with id " + id + " on api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(apiURL + "/" + id, HttpMethod.DELETE, request, Void.class);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            throwStatusCodeException(statusCode);
        }

        logger.debug("Deleted " + serviceEntityClass + " entity with id: " + id);
    }

    public static void throwStatusCodeException(HttpStatus statusCode) throws TimecastUnauthorizedException,
            TimecastForbiddenException, TimecastNotFoundException, TimecastPreconditionFailedException,
            TimecastInternalServerErrorException, IllegalStateException {
        switch (statusCode) {
            case UNAUTHORIZED: throw new TimecastUnauthorizedException(statusCode.getReasonPhrase());
            case FORBIDDEN: throw new TimecastForbiddenException(statusCode.getReasonPhrase());
            case NOT_FOUND: throw new TimecastNotFoundException(statusCode.getReasonPhrase());
            case PRECONDITION_FAILED: throw new TimecastPreconditionFailedException(statusCode.getReasonPhrase());
            case INTERNAL_SERVER_ERROR: throw new TimecastInternalServerErrorException(statusCode.getReasonPhrase());
            default: throw new IllegalStateException();
        }
    }
}
