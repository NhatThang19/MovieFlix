package com.vn.movie_flix.repository;

import com.vn.movie_flix.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository  extends JpaRepository<Role, Long> {
    Role findRoleByName(String name);
}
