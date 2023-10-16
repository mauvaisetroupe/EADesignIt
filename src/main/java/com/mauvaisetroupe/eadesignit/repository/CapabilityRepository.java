package com.mauvaisetroupe.eadesignit.repository;

import com.mauvaisetroupe.eadesignit.domain.Capability;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Capability entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CapabilityRepository extends JpaRepository<Capability, Long> {
    void deleteByCapabilityApplicationMappingsIsEmpty();
    List<Capability> findByLevel(int i);

    @Query(
        value = "select c from Capability c" +
        " left join fetch c.subCapabilities s1 " +
        " left join fetch s1.subCapabilities s2 " +
        " left join fetch s2.subCapabilities s3 " +
        " left join fetch s3.subCapabilities s4 " +
        " left join fetch s4.subCapabilities s5 " +
        " left join fetch s5.subCapabilities s6 " +
        " left join fetch c.capabilityApplicationMappings  m1" +
        " left join fetch s1.capabilityApplicationMappings m2" +
        " left join fetch s2.capabilityApplicationMappings m3" +
        " left join fetch s3.capabilityApplicationMappings m4 " +
        " left join fetch s4.capabilityApplicationMappings m5 " +
        " left join fetch s5.capabilityApplicationMappings m6 " +
        " left join fetch s6.capabilityApplicationMappings m7 " +
        " left join fetch m1.application a1" +
        " left join fetch m2.application a2" +
        " left join fetch m3.application a3" +
        " left join fetch m4.application a4" +
        " left join fetch m5.application a5" +
        " left join fetch m6.application a6" +
        " left join fetch m7.application a7" +
        " where c.id=:id"
    )
    // c = ROOT level =-2
    // s1 l = -1 (surdomain)
    // s2 l = 0
    // s3 l = 1
    // s4 l = 2
    Optional<Capability> findById(@Param("id") Long id);

    @Query(
        value = "select c from Capability c" +
        " left join fetch c.subCapabilities s1 " +
        " left join fetch s1.subCapabilities s2 "
    )    
    List<Capability> findAllWithSubCapabilities();
}
