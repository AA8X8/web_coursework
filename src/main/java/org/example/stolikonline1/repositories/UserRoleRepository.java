package org.example.stolikonline1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.stolikonline1.models.entities.Role;
import org.example.stolikonline1.models.enums.UserRoles;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(UserRoles role);
}