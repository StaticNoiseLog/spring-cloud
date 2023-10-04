package com.example.demo.cat;

import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource
public interface CatRepository extends JpaRepository<Cat, Long> {
}