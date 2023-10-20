package com.mauvaisetroupe.eadesignit.repository;

import com.mauvaisetroupe.eadesignit.domain.ApplicationComponent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ApplicationComponentRepositoryWithBagRelationshipsImpl implements ApplicationComponentRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ApplicationComponent> fetchBagRelationships(Optional<ApplicationComponent> applicationComponent) {
        return applicationComponent.map(this::fetchCategories).map(this::fetchTechnologies).map(this::fetchExternalIDS);
    }

    @Override
    public Page<ApplicationComponent> fetchBagRelationships(Page<ApplicationComponent> applicationComponents) {
        return new PageImpl<>(
            fetchBagRelationships(applicationComponents.getContent()),
            applicationComponents.getPageable(),
            applicationComponents.getTotalElements()
        );
    }

    @Override
    public List<ApplicationComponent> fetchBagRelationships(List<ApplicationComponent> applicationComponents) {
        return Optional
            .of(applicationComponents)
            .map(this::fetchCategories)
            .map(this::fetchTechnologies)
            .map(this::fetchExternalIDS)
            .orElse(Collections.emptyList());
    }

    ApplicationComponent fetchCategories(ApplicationComponent result) {
        return entityManager
            .createQuery(
                "select applicationComponent from ApplicationComponent applicationComponent left join fetch applicationComponent.categories where applicationComponent.id = :id",
                ApplicationComponent.class
            )
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<ApplicationComponent> fetchCategories(List<ApplicationComponent> applicationComponents) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, applicationComponents.size()).forEach(index -> order.put(applicationComponents.get(index).getId(), index));
        List<ApplicationComponent> result = entityManager
            .createQuery(
                "select applicationComponent from ApplicationComponent applicationComponent left join fetch applicationComponent.categories where applicationComponent in :applicationComponents",
                ApplicationComponent.class
            )
            .setParameter("applicationComponents", applicationComponents)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    ApplicationComponent fetchTechnologies(ApplicationComponent result) {
        return entityManager
            .createQuery(
                "select applicationComponent from ApplicationComponent applicationComponent left join fetch applicationComponent.technologies where applicationComponent.id = :id",
                ApplicationComponent.class
            )
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<ApplicationComponent> fetchTechnologies(List<ApplicationComponent> applicationComponents) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, applicationComponents.size()).forEach(index -> order.put(applicationComponents.get(index).getId(), index));
        List<ApplicationComponent> result = entityManager
            .createQuery(
                "select applicationComponent from ApplicationComponent applicationComponent left join fetch applicationComponent.technologies where applicationComponent in :applicationComponents",
                ApplicationComponent.class
            )
            .setParameter("applicationComponents", applicationComponents)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    ApplicationComponent fetchExternalIDS(ApplicationComponent result) {
        return entityManager
            .createQuery(
                "select applicationComponent from ApplicationComponent applicationComponent left join fetch applicationComponent.externalIDS where applicationComponent.id = :id",
                ApplicationComponent.class
            )
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<ApplicationComponent> fetchExternalIDS(List<ApplicationComponent> applicationComponents) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, applicationComponents.size()).forEach(index -> order.put(applicationComponents.get(index).getId(), index));
        List<ApplicationComponent> result = entityManager
            .createQuery(
                "select applicationComponent from ApplicationComponent applicationComponent left join fetch applicationComponent.externalIDS where applicationComponent in :applicationComponents",
                ApplicationComponent.class
            )
            .setParameter("applicationComponents", applicationComponents)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
