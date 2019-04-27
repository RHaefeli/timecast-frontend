package wodss.timecastfrontend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import wodss.timecastfrontend.domain.TimecastDto;
import wodss.timecastfrontend.domain.TimecastEntity;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.*;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractService<E extends TimecastEntity, DTO extends TimecastDto> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final String apiURL;
    protected final RestTemplate restTemplate;
    private final Class<DTO> serviceEntityClass;
    private final ParameterizedTypeReference<List<DTO>> listParameterizedTypeReference;

    protected AbstractService(RestTemplate restTemplate, String apiURL, Class<DTO> serviceEntityClass,
                              ParameterizedTypeReference<List<DTO>> listParameterizedTypeReference) {
        this.restTemplate = restTemplate;
        this.apiURL = apiURL;
        this.serviceEntityClass = serviceEntityClass;
        this.listParameterizedTypeReference = listParameterizedTypeReference;
    }

    public List<E> getAll(Token token) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastInternalServerErrorException {
        logger.debug("Request list for " + serviceEntityClass + " from api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<List<DTO>> response = restTemplate.exchange(apiURL, HttpMethod.GET, request, listParameterizedTypeReference);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            throwStatusCodeException(statusCode);
        }

        List<DTO> dtos = response.getBody();
        logger.debug("Received " + serviceEntityClass + " list: " + dtos);
        if (dtos == null) {
            return null;
        }
        return dtos.stream().map(dto -> mapDtoToEntity(token, dto)).collect(Collectors.toList());
    }

    public E getById(Token token, long id) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastInternalServerErrorException {
        logger.debug("Request " + serviceEntityClass + " entity with id " + id + " from api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<DTO> response = restTemplate.exchange(apiURL + "/" + id, HttpMethod.GET, request, serviceEntityClass);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            throwStatusCodeException(statusCode);
        }

        DTO dto = response.getBody();
        logger.debug("Received " + serviceEntityClass + " entity: " + dto);
        return mapDtoToEntity(token, dto);
    }

    public E create(Token token, E entity) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastPreconditionFailedException, TimecastInternalServerErrorException {
        logger.debug("Create " + serviceEntityClass + " entity " + entity + " to api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        HttpEntity<DTO> request = new HttpEntity<>(mapEntityToDto(token, entity), headers);
        ResponseEntity<DTO> response = restTemplate.exchange(apiURL, HttpMethod.POST, request, serviceEntityClass);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.CREATED) {
            throwStatusCodeException(statusCode);
        }

        DTO dto = response.getBody();
        logger.debug("Received " + serviceEntityClass + " entity: " + dto);
        return mapDtoToEntity(token, dto);
    }

    public E update(Token token, E entity) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastPreconditionFailedException, TimecastInternalServerErrorException {
        logger.debug("Update " + serviceEntityClass + " entity " + entity + " to api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        HttpEntity<DTO> requestEntity = new HttpEntity<>(mapEntityToDto(token, entity), headers);
        ResponseEntity<DTO> response = restTemplate.exchange(apiURL + "/" + entity.getId(), HttpMethod.PUT, requestEntity, serviceEntityClass);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            throwStatusCodeException(statusCode);
        }

        DTO dto = response.getBody();
        logger.debug("Received " + serviceEntityClass + " entity: " + dto);
        return mapDtoToEntity(token, dto);
    }

    public void deleteById(Token token, long id) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastInternalServerErrorException {
        logger.debug("Delete " + serviceEntityClass + " entity with id " + id + " on api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(apiURL + "/" + id, HttpMethod.DELETE, request, Void.class);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.NO_CONTENT) {
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

    protected abstract DTO mapEntityToDto(Token token, E entity);
    protected abstract E mapDtoToEntity(Token token, DTO dto);
}
