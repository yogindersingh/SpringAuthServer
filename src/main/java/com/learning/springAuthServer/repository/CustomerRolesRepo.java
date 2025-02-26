package com.learning.springAuthServer.repository;

import com.learning.springAuthServer.entity.CustomerRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface CustomerRolesRepo extends JpaRepository<CustomerRoles, UUID> {

  List<CustomerRoles> findAllByCustomerId(UUID customerId);
}
