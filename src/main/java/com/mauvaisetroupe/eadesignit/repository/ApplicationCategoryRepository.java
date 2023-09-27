package com.mauvaisetroupe.eadesignit.repository;

import com.mauvaisetroupe.eadesignit.domain.ApplicationCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ApplicationCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApplicationCategoryRepository extends JpaRepository<ApplicationCategory, Long> {}
