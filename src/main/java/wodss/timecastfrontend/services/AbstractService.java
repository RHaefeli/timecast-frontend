package wodss.timecastfrontend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import wodss.timecastfrontend.dto.TimecastDto;
import wodss.timecastfrontend.domain.TimecastEntity;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract Service which all services should extend. Contains basic methods to
 * fetch data from the backend.
 *
 * @param <E> Domain entity to fetch
 * @param <DTO> DTO of entity
 */
public abstract class AbstractService<E extends TimecastEntity, DTO extends TimecastDto> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final String apiURL;
    protected final RestTemplate restTemplate;
    private final Class<DTO> serviceEntityClass;
    private final ParameterizedTypeReference<List<DTO>> listParameterizedTypeReference;

    /**
     * Constructor
     * @param restTemplate
     * @param apiURL
     * @param serviceEntityClass
     * @param listParameterizedTypeReference
     */
    protected AbstractService(RestTemplate restTemplate, String apiURL, Class<DTO> serviceEntityClass,
                              ParameterizedTypeReference<List<DTO>> listParameterizedTypeReference) {
        this.restTemplate = restTemplate;
        this.apiURL = apiURL;
        this.serviceEntityClass = serviceEntityClass;
        this.listParameterizedTypeReference = listParameterizedTypeReference;
    }

    /**
     * Fetches all entities
     * @param token
     * @return List of found entities
     * @throws TimecastUnauthorizedException
     * @throws TimecastForbiddenException
     * @throws TimecastNotFoundException
     * @throws TimecastInternalServerErrorException
     */
    public List<E> getAll(Token token) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastInternalServerErrorException {
        logger.debug("Request list for " + serviceEntityClass + " from api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<List<DTO>> response = restTemplate.exchange(apiURL, HttpMethod.GET, request, listParameterizedTypeReference);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            // Other status codes are mapped by the RestTemplate Error Handler
            throw new IllegalStateException(statusCode.toString());
        }

        List<DTO> dtos = response.getBody();
        logger.debug("Received " + serviceEntityClass + " list: " + dtos);
        if (dtos == null) {
            return null;
        }
        return dtos.stream().map(dto -> mapDtoToEntity(token, dto)).collect(Collectors.toList());
    }

    /**
     * Fetches one entity by its id
     * @param token
     * @param id
     * @return
     * @throws TimecastUnauthorizedException
     * @throws TimecastForbiddenException
     * @throws TimecastNotFoundException
     * @throws TimecastInternalServerErrorException
     */
    public E getById(Token token, long id) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastInternalServerErrorException {
        logger.debug("Request " + serviceEntityClass + " entity with id " + id + " from api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<DTO> response = restTemplate.exchange(apiURL + "/" + id, HttpMethod.GET, request, serviceEntityClass);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            // Other status codes are mapped by the RestTemplate Error Handler
            throw new IllegalStateException(statusCode.toString());
        }

        DTO dto = response.getBody();
        logger.debug("Received " + serviceEntityClass + " entity: " + dto);
        return mapDtoToEntity(token, dto);
    }

    /**
     * Create entity
     * @param token
     * @param entity
     * @return
     * @throws TimecastUnauthorizedException
     * @throws TimecastForbiddenException
     * @throws TimecastNotFoundException
     * @throws TimecastPreconditionFailedException
     * @throws TimecastInternalServerErrorException
     */
    public E create(Token token, E entity) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastPreconditionFailedException, TimecastInternalServerErrorException {
        logger.debug("Create " + serviceEntityClass + " entity " + entity + " to api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<DTO> request = new HttpEntity<>(mapEntityToDto(token, entity), headers);
        ResponseEntity<DTO> response = restTemplate.exchange(apiURL, HttpMethod.POST, request, serviceEntityClass);
        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.CREATED) {
            // Other status codes are mapped by the RestTemplate Error Handler
            throw new IllegalStateException(statusCode.toString());
        }

        DTO dto = response.getBody();
        logger.debug("Received " + serviceEntityClass + " entity: " + dto);
        return mapDtoToEntity(token, dto);
    }

    /**
     * Update entity
     * @param token
     * @param entity
     * @return
     * @throws TimecastUnauthorizedException
     * @throws TimecastForbiddenException
     * @throws TimecastNotFoundException
     * @throws TimecastPreconditionFailedException
     * @throws TimecastInternalServerErrorException
     */
    public E update(Token token, E entity) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastPreconditionFailedException, TimecastInternalServerErrorException {
        logger.debug("Update " + serviceEntityClass + " entity " + entity + " to api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<DTO> requestEntity = new HttpEntity<>(mapEntityToDto(token, entity), headers);
        ResponseEntity<DTO> response = restTemplate.exchange(apiURL + "/" + entity.getId(), HttpMethod.PUT, requestEntity, serviceEntityClass);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            // Other status codes are mapped by the RestTemplate Error Handler
            throw new IllegalStateException(statusCode.toString());
        }

        DTO dto = response.getBody();
        logger.debug("Received " + serviceEntityClass + " entity: " + dto);
        return mapDtoToEntity(token, dto);
    }

    /**
     * Delete entity
     * @param token
     * @param id
     * @throws TimecastUnauthorizedException
     * @throws TimecastForbiddenException
     * @throws TimecastNotFoundException
     * @throws TimecastInternalServerErrorException
     */
    public void deleteById(Token token, long id) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastInternalServerErrorException {
        logger.debug("Delete " + serviceEntityClass + " entity with id " + id + " on api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(apiURL + "/" + id, HttpMethod.DELETE, request, Void.class);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.NO_CONTENT) {
            // Other status codes are mapped by the RestTemplate Error Handler
            throw new IllegalStateException(statusCode.toString());
        }

        logger.debug("Deleted " + serviceEntityClass + " entity with id: " + id);
    }

    /**
     * Override to define how to map the entity to a dto
     * @param token
     * @param entity
     * @return
     */
    protected abstract DTO mapEntityToDto(Token token, E entity);
    /**
     * Override to define how to map the dto to an entity
     * @param token
     * @param dto
     * @return
     */
    protected abstract E mapDtoToEntity(Token token, DTO dto);
}
