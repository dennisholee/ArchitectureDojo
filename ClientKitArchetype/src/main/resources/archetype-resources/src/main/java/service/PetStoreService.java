package ${package}.service;

import ${package}.config.ClientConfiguration;
import ${package}.service.resilience.ResilientService;
import ${package}.petstore.api.DefaultApi;
import ${package}.petstore.model.*;
import ${package}.validation.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Service class for Pet Store operations.
 * Focuses solely on business operations, delegating cross-cutting concerns
 * to specialized components.
 */
@Slf4j
public class PetStoreService extends ResilientService {
    private final DefaultApi api;

    public PetStoreService(DefaultApi api, ClientConfiguration config) {
        super(config);
        this.api = api;
    }

    public List<Pet> listPets(int limit) {
        return executeWithResilience(() -> {
            ValidationUtils.validate(limit, "Limit must be a positive integer");
            PetsResponse response = api.listPets(limit);
            return response.getPets();
        });
    }

    public Pet createPet(PetRequest request) {
        return executeWithResilience(() -> {
            ValidationUtils.validate(request, "PetRequest must not be null");
            return api.createPet(request);
        });
    }
}
