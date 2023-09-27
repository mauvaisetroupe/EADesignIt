package com.mauvaisetroupe.eadesignit.repository;

import com.mauvaisetroupe.eadesignit.domain.DataFormat;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DataFormat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DataFormatRepository extends JpaRepository<DataFormat, Long> {}
