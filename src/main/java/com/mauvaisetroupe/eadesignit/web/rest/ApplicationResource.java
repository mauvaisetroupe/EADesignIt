package com.mauvaisetroupe.eadesignit.web.rest;

import com.mauvaisetroupe.eadesignit.domain.Application;
import com.mauvaisetroupe.eadesignit.domain.Capability;
import com.mauvaisetroupe.eadesignit.repository.ApplicationRepository;
import com.mauvaisetroupe.eadesignit.service.dto.util.CapabilityUtil;
import com.mauvaisetroupe.eadesignit.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mauvaisetroupe.eadesignit.domain.Application}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ApplicationResource {

    private final Logger log = LoggerFactory.getLogger(ApplicationResource.class);

    private static final String ENTITY_NAME = "application";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApplicationRepository applicationRepository;

    private final CapabilityUtil capabilityUtil;

    public ApplicationResource(ApplicationRepository applicationRepository, CapabilityUtil capabilityUtil) {
        this.applicationRepository = applicationRepository;
        this.capabilityUtil = capabilityUtil;
    }

    /**
     * {@code POST  /applications} : Create a new application.
     *
     * @param application the application to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new application, or with status {@code 400 (Bad Request)} if the application has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/applications")
    @PreAuthorize("@ownershipChecker.check(#application)")
    public ResponseEntity<Application> createApplication(@Valid @RequestBody Application application) throws URISyntaxException {
        log.debug("REST request to save Application : {}", application);
        if (application.getId() != null) {
            throw new BadRequestAlertException("A new application cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Application result = applicationRepository.save(application);
        return ResponseEntity
            .created(new URI("/api/applications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /applications/:id} : Updates an existing application.
     *
     * @param id the id of the application to save.
     * @param application the application to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated application,
     * or with status {@code 400 (Bad Request)} if the application is not valid,
     * or with status {@code 500 (Internal Server Error)} if the application couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/applications/{id}")
    @PreAuthorize("@ownershipChecker.check(#application)")
    public ResponseEntity<Application> updateApplication(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Application application
    ) throws URISyntaxException {
        log.debug("REST request to update Application : {}, {}", id, application);
        if (application.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, application.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Application result = applicationRepository.save(application);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, application.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /applications/:id} : Partial updates given fields of an existing application, field will ignore if it is null
     *
     * @param id the id of the application to save.
     * @param application the application to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated application,
     * or with status {@code 400 (Bad Request)} if the application is not valid,
     * or with status {@code 404 (Not Found)} if the application is not found,
     * or with status {@code 500 (Internal Server Error)} if the application couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/applications/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@ownershipChecker.check(#application)")
    public ResponseEntity<Application> partialUpdateApplication(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Application application
    ) throws URISyntaxException {
        log.debug("REST request to partial update Application partially : {}, {}", id, application);
        if (application.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, application.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Application> result = applicationRepository
            .findById(application.getId())
            .map(existingApplication -> {
                if (application.getAlias() != null) {
                    existingApplication.setAlias(application.getAlias());
                }
                if (application.getName() != null) {
                    existingApplication.setName(application.getName());
                }
                if (application.getDescription() != null) {
                    existingApplication.setDescription(application.getDescription());
                }
                if (application.getComment() != null) {
                    existingApplication.setComment(application.getComment());
                }
                if (application.getDocumentationURL() != null) {
                    existingApplication.setDocumentationURL(application.getDocumentationURL());
                }
                if (application.getStartDate() != null) {
                    existingApplication.setStartDate(application.getStartDate());
                }
                if (application.getEndDate() != null) {
                    existingApplication.setEndDate(application.getEndDate());
                }
                if (application.getApplicationType() != null) {
                    existingApplication.setApplicationType(application.getApplicationType());
                }
                if (application.getSoftwareType() != null) {
                    existingApplication.setSoftwareType(application.getSoftwareType());
                }
                if (application.getNickname() != null) {
                    existingApplication.setNickname(application.getNickname());
                }

                return existingApplication;
            })
            .map(applicationRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, application.getId().toString())
        );
    }

    /**
     * {@code GET  /applications} : get all the applications.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of applications in body.
     */
    @GetMapping("/applications")
    public List<Application> getAllApplications(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Applications");
        return applicationRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /applications/:id} : get the "id" application.
     *
     * @param id the id of the application to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the application, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/applications/{id}")
    public ResponseEntity<Application> getApplication(@PathVariable Long id) {
        log.debug("REST request to get Application : {}", id);
        Optional<Application> application = applicationRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(application);
    }

    /**
     * {@code DELETE  /applications/:id} : delete the "id" application.
     *
     * @param id the id of the application to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/applications/{id}")
    @PreAuthorize("@ownershipChecker.check(#application)")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        log.debug("REST request to delete Application : {}", id);
        applicationRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/applications/{id}/capabilities")
    public Capability getApplicationCapabilities(@PathVariable Long id) {
        log.debug("REST request to get Application : {}", id);
        Optional<Application> application = applicationRepository.findOneWithEagerRelationships(id);
        Capability rootCapability = capabilityUtil.buildCapabilityTree(application.get().getCapabilities());
        return rootCapability;
    }
}
