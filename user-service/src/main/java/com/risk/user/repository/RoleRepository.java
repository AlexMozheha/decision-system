package com.risk.user.repository;
import com.risk.enums.UserRole;
import com.risk.user.model.Role;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Cacheable(value = "RoleName", key = "#name")
    Optional<Role> findByName(UserRole name);

}
