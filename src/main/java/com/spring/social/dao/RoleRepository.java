package com.spring.social.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.spring.social.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
}
