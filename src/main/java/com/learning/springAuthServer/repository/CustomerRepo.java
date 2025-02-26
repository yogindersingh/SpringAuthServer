package com.learning.springAuthServer.repository;

import com.learning.springAuthServer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, UUID> {

  //Post authorize example
  @PostAuthorize("returnObject.get().email==#email")
  Optional<Customer> findByEmail(String email);
}
