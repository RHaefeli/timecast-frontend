package wodss.timecastfrontend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import wodss.timecastfrontend.domain.AbstractTimecastEntity;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;

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

    public List<T> getAll() throws TimecastNotFoundException, TimecastPreconditionFailedException,
            TimecastInternalServerErrorException {
        logger.debug("Request list for " + serviceEntityClass + " from api: " + apiURL);
        ResponseEntity<List<T>> response = restTemplate.exchange(apiURL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<T>>() {});
        checkResponseForException(response);
        List<T> entities = response.getBody();
        logger.debug("Received " + serviceEntityClass + " list: " + entities);
        return entities;
    }

    public T getById(int id) throws TimecastNotFoundException, TimecastPreconditionFailedException,
            TimecastInternalServerErrorException {
        logger.debug("Request " + serviceEntityClass + " entity with id " + id + " from api: " + apiURL);
        ResponseEntity<T> response = restTemplate.getForEntity(apiURL + "/" + id, serviceEntityClass);
        checkResponseForException(response);
        T entity = response.getBody();
        logger.debug("Received " + serviceEntityClass + " entity: " + entity);
        return entity;
    }

    public T create(T entity) throws TimecastNotFoundException, TimecastPreconditionFailedException,
            TimecastInternalServerErrorException {
        logger.debug("Create " + serviceEntityClass + " entity " + entity + " to api: " + apiURL);
        HttpEntity<T> request = new HttpEntity<>(entity);
        ResponseEntity<T> response = restTemplate.exchange(apiURL, HttpMethod.POST, request, serviceEntityClass);
        checkResponseForException(response);
        T newEntity = response.getBody();
        logger.debug("Received " + serviceEntityClass + " entity: " + newEntity);
        return newEntity;
    }

    public T update(T entity) throws TimecastNotFoundException, TimecastPreconditionFailedException,
            TimecastInternalServerErrorException {
        logger.debug("Update " + serviceEntityClass + " entity " + entity + " to api: " + apiURL);
        HttpEntity<T> requestEntity = new HttpEntity<>(entity);
        ResponseEntity<T> response = restTemplate.exchange(apiURL + "/" + entity.getId(), HttpMethod.PUT, requestEntity, serviceEntityClass);
        checkResponseForException(response);
        T newEntity = response.getBody();
        logger.debug("Received " + serviceEntityClass + " entity: " + newEntity);
        return newEntity;
    }

    public void deleteById(int id) throws TimecastNotFoundException, TimecastPreconditionFailedException,
            TimecastInternalServerErrorException {
        logger.debug("Delete " + serviceEntityClass + " entity with id " + id + " on api: " + apiURL);
        ResponseEntity<Void> response = restTemplate.exchange(apiURL + "/" + id, HttpMethod.DELETE, null, Void.class);
        checkResponseForException(response);
        logger.debug("Deleted " + serviceEntityClass + " entity with id: " + id);
    }


    protected <R> void checkResponseForException (ResponseEntity<R> response) {
        HttpStatus statusCode = response.getStatusCode();
        switch (statusCode) {
            case CREATED: break;
            case OK: break;
            case NOT_FOUND: throw new TimecastNotFoundException(response.getStatusCode().getReasonPhrase());
            // case FORBIDDEN: throw new TimecastForbiddenException(response.getStatusCode().getReasonPhrase());
            case PRECONDITION_FAILED: throw new TimecastPreconditionFailedException(response.getStatusCode().getReasonPhrase());
            case INTERNAL_SERVER_ERROR: throw new TimecastInternalServerErrorException(response.getStatusCode().getReasonPhrase());
            default: throw new IllegalStateException();
        }
    }
}
