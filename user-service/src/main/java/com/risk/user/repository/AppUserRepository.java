package com.risk.user.repository;
import com.risk.user.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByLogin(String login);
    Optional<AppUser> findByEmail(String email);
    
}
