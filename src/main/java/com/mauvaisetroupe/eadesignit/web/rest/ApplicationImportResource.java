package com.mauvaisetroupe.eadesignit.web.rest;

import com.mauvaisetroupe.eadesignit.domain.ApplicationImport;
import com.mauvaisetroupe.eadesignit.repository.ApplicationImportRepository;
import com.mauvaisetroupe.eadesignit.web.rest.errors.BadRequestAlertException;
import io.jsonwebtoken.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mauvaisetroupe.eadesignit.domain.ApplicationImport}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ApplicationImportResource {

    private final Logger log = LoggerFactory.getLogger(ApplicationImportResource.class);

    private static final String ENTITY_NAME = "applicationImport";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApplicationImportRepository applicationImportRepository;

    public ApplicationImportResource(ApplicationImportRepository applicationImportRepository) {
        this.applicationImportRepository = applicationImportRepository;
    }

    /**
     * {@code POST  /application-imports} : Create a new applicationImport.
     *
     * @param applicationImport the applicationImport to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new applicationImport, or with status {@code 400 (Bad Request)} if the applicationImport has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/application-imports")
    public ResponseEntity<ApplicationImport> createApplicationImport(@RequestBody ApplicationImport applicationImport)
        throws URISyntaxException {
        log.debug("REST request to save ApplicationImport : {}", applicationImport);
        if (applicationImport.getId() != null) {
            throw new BadRequestAlertException("A new applicationImport cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ApplicationImport result = applicationImportRepository.save(applicationImport);
        return ResponseEntity
            .created(new URI("/api/application-imports/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /application-imports/:id} : Updates an existing applicationImport.
     *
     * @param id the id of the applicationImport to save.
     * @param applicationImport the applicationImport to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated applicationImport,
     * or with status {@code 400 (Bad Request)} if the applicationImport is not valid,
     * or with status {@code 500 (Internal Server Error)} if the applicationImport couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/application-imports/{id}")
    public ResponseEntity<ApplicationImport> updateApplicationImport(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ApplicationImport applicationImport
    ) throws URISyntaxException {
        log.debug("REST request to update ApplicationImport : {}, {}", id, applicationImport);
        if (applicationImport.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, applicationImport.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicationImportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ApplicationImport result = applicationImportRepository.save(applicationImport);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, applicationImport.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /application-imports/:id} : Partial updates given fields of an existing applicationImport, field will ignore if it is null
     *
     * @param id the id of the applicationImport to save.
     * @param applicationImport the applicationImport to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated applicationImport,
     * or with status {@code 400 (Bad Request)} if the applicationImport is not valid,
     * or with status {@code 404 (Not Found)} if the applicationImport is not found,
     * or with status {@code 500 (Internal Server Error)} if the applicationImport couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/application-imports/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApplicationImport> partialUpdateApplicationImport(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ApplicationImport applicationImport
    ) throws URISyntaxException {
        log.debug("REST request to partial update ApplicationImport partially : {}, {}", id, applicationImport);
        if (applicationImport.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, applicationImport.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicationImportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ApplicationImport> result = applicationImportRepository
            .findById(applicationImport.getId())
            .map(existingApplicationImport -> {
                if (applicationImport.getImportId() != null) {
                    existingApplicationImport.setImportId(applicationImport.getImportId());
                }
                if (applicationImport.getExcelFileName() != null) {
                    existingApplicationImport.setExcelFileName(applicationImport.getExcelFileName());
                }
                if (applicationImport.getIdFromExcel() != null) {
                    existingApplicationImport.setIdFromExcel(applicationImport.getIdFromExcel());
                }
                if (applicationImport.getName() != null) {
                    existingApplicationImport.setName(applicationImport.getName());
                }
                if (applicationImport.getDescription() != null) {
                    existingApplicationImport.setDescription(applicationImport.getDescription());
                }
                if (applicationImport.getType() != null) {
                    existingApplicationImport.setType(applicationImport.getType());
                }
                if (applicationImport.getTechnology() != null) {
                    existingApplicationImport.setTechnology(applicationImport.getTechnology());
                }
                if (applicationImport.getComment() != null) {
                    existingApplicationImport.setComment(applicationImport.getComment());
                }

                return existingApplicationImport;
            })
            .map(applicationImportRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, applicationImport.getId().toString())
        );
    }

    /**
     * {@code GET  /application-imports} : get all the applicationImports.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of applicationImports in body.
     */
    @GetMapping("/application-imports")
    public List<ApplicationImport> getAllApplicationImports() {
        log.debug("REST request to get all ApplicationImports");
        return applicationImportRepository.findAll();
    }

    /**
     * {@code GET  /application-imports/:id} : get the "id" applicationImport.
     *
     * @param id the id of the applicationImport to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the applicationImport, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/application-imports/{id}")
    public ResponseEntity<ApplicationImport> getApplicationImport(@PathVariable Long id) {
        log.debug("REST request to get ApplicationImport : {}", id);
        Optional<ApplicationImport> applicationImport = applicationImportRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(applicationImport);
    }

    /**
     * {@code DELETE  /application-imports/:id} : delete the "id" applicationImport.
     *
     * @param id the id of the applicationImport to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/application-imports/{id}")
    public ResponseEntity<Void> deleteApplicationImport(@PathVariable Long id) {
        log.debug("REST request to delete ApplicationImport : {}", id);
        applicationImportRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/application-imports/upload-file")
    public List<ApplicationImport> uploadFile(@RequestPart MultipartFile file) throws URISyntaxException, IOException, java.io.IOException {
        Workbook offices;
        String lowerCaseFileName = file.getOriginalFilename().toLowerCase();
        if (lowerCaseFileName.endsWith(".xlsx")) {
            offices = new XSSFWorkbook(file.getInputStream());
        } else {
            offices = new HSSFWorkbook(file.getInputStream());
        }
        Sheet sheet = offices.getSheetAt(0);

        Instant instant = Instant.now();

        int index = 0;
        Map<String, Integer> map = new HashMap<String, Integer>(); //Create map

        List<ApplicationImport> result = new ArrayList<ApplicationImport>();
        Long i = 0L;
        for (Row row : sheet) {
            if (index == 0) {
                for (Cell cell : row) {
                    // map column name and column index
                    map.put(cell.getStringCellValue(), cell.getColumnIndex());
                }
            } else {
                //application.name application.description application.comment application.type
                ApplicationImport applicationImport = new ApplicationImport();
                applicationImport.setIdFromExcel(CellUtil.getCell(row, map.get("application.id")).getStringCellValue());
                applicationImport.setName(CellUtil.getCell(row, map.get("application.name")).getStringCellValue());
                applicationImport.setDescription(CellUtil.getCell(row, map.get("application.description")).getStringCellValue());
                applicationImport.setComment(CellUtil.getCell(row, map.get("application.comment")).getStringCellValue());
                applicationImport.setType(CellUtil.getCell(row, map.get("application.type")).getStringCellValue());
                applicationImport.setImportId(instant);
                applicationImport.setExcelFileName(lowerCaseFileName);
                applicationImport.setId(i++);

                result.add(applicationImport);
            }
            index++;
        }
        offices.close();
        return result;
    }
}
