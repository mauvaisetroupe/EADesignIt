package com.mauvaisetroupe.eadesignit.repository;

import com.mauvaisetroupe.eadesignit.domain.Application;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Application entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Application findByNameIgnoreCase(String name);
    Optional<Application> findByAlias(String alias);
}
