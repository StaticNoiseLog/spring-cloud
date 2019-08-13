package com.example.demo.car;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface CarRepository extends CrudRepository<Car, Long> {
    Iterable<Car> findByMakeIgnoringCase(String make);
}
