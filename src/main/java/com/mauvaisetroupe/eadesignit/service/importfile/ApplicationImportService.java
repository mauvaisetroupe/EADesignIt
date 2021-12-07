package com.mauvaisetroupe.eadesignit.service.importfile;

import com.mauvaisetroupe.eadesignit.domain.Application;
import com.mauvaisetroupe.eadesignit.domain.ApplicationCategory;
import com.mauvaisetroupe.eadesignit.domain.ApplicationImport;
import com.mauvaisetroupe.eadesignit.domain.Technology;
import com.mauvaisetroupe.eadesignit.domain.enumeration.ImportStatus;
import com.mauvaisetroupe.eadesignit.repository.ApplicationCategoryRepository;
import com.mauvaisetroupe.eadesignit.repository.ApplicationRepository;
import com.mauvaisetroupe.eadesignit.repository.TechnologyRepository;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.poi.EncryptedDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
public class ApplicationImportService {

    private static final String APPLICATION_ID = "application.id";
    private static final String APPLICATION_NAME = "application.name";
    private static final String APPLICATION_DESCRIPTION = "application.description";
    private static final String APPLICATION_COMMENT = "application.comment";
    private static final String APPLICATION_TYPE = "application.type";
    private static final String APPLICATION_TECHNOLOGY = "application.technology";
    private static final String APPLICATION_OWNER = "application.owner";

    private final List<String> columnsArray = new ArrayList<String>();

    private static final String APPLICATION_SHEET_NAME = "Application";

    private final Logger log = LoggerFactory.getLogger(ApplicationImportService.class);

    private final ApplicationRepository applicationRepository;
    private final ApplicationCategoryRepository applicationCategoryRepository;
    private final TechnologyRepository technologyRepository;

    public ApplicationImportService(
        ApplicationRepository applicationRepository,
        ApplicationCategoryRepository applicationCategoryRepository,
        TechnologyRepository technologyRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.applicationCategoryRepository = applicationCategoryRepository;
        this.technologyRepository = technologyRepository;

        this.columnsArray.add(APPLICATION_ID);
        this.columnsArray.add(APPLICATION_NAME);
        this.columnsArray.add(APPLICATION_DESCRIPTION);
        this.columnsArray.add(APPLICATION_COMMENT);
        this.columnsArray.add(APPLICATION_TYPE);
        this.columnsArray.add(APPLICATION_TECHNOLOGY);
        this.columnsArray.add(APPLICATION_OWNER);
    }

    public List<ApplicationImport> importExcel(InputStream inputStream, String originalFilename)
        throws EncryptedDocumentException, IOException {
        ExcelReader excelReader = new ExcelReader(inputStream);
        List<Map<String, Object>> applicationDF = excelReader.getSheet(APPLICATION_SHEET_NAME);
        log.info("Found Excel sheet " + applicationDF);

        String importID = (new SimpleDateFormat("YYYYMMddhhmmss")).format(new Date());

        List<ApplicationImport> result = new ArrayList<ApplicationImport>();
        Long i = 0L;
        for (Map<String, Object> map : applicationDF) {
            ApplicationImport applicationImport = mapArrayToImportApplication(map);
            applicationImport.setImportId(importID);
            applicationImport.setExcelFileName(originalFilename);
            applicationImport.setId(i++);

            Application application = mapImportToApplication(applicationImport);

            if (application.getId() != null) {
                applicationImport.setImportStatus(ImportStatus.EXISTING);
            } else {
                applicationImport.setImportStatus(ImportStatus.NEW);
                application.setAlias(applicationImport.getIdFromExcel());
            }
            applicationRepository.save(application);
            result.add(applicationImport);
        }

        return result;
    }

    private ApplicationImport mapArrayToImportApplication(Map<String, Object> map) {
        ApplicationImport applicationImport = new ApplicationImport();
        applicationImport.setIdFromExcel((String) map.get(APPLICATION_ID));
        applicationImport.setName((String) map.get(APPLICATION_NAME));
        applicationImport.setDescription((String) map.get(APPLICATION_DESCRIPTION));
        applicationImport.setComment((String) map.get(APPLICATION_COMMENT));
        applicationImport.setType((String) map.get(APPLICATION_TYPE));
        applicationImport.setTechnology((String) map.get(APPLICATION_TECHNOLOGY));
        return applicationImport;
    }

    public Application mapImportToApplication(ApplicationImport applicationImport) {
        // Check if alias not used for another application
        Optional<Application> optional = applicationRepository.findByAlias(applicationImport.getIdFromExcel());
        final Application application;
        if (optional.isPresent()) {
            application = optional.get();
            Assert.isTrue(
                application.getName().toLowerCase().equals(applicationImport.getName().toLowerCase()),
                "Cannot change application name (" +
                application.getName() +
                "/" +
                applicationImport.getName() +
                "), please  correrct your Excel file"
            );
        } else {
            application = new Application();
        }

        Application appliWithSameName = applicationRepository.findByNameIgnoreCase(applicationImport.getName());
        if (appliWithSameName != null) {
            Assert.isTrue(
                appliWithSameName.getAlias().toLowerCase().equals(applicationImport.getIdFromExcel()),
                "Cannot have same application name for two aliases '" +
                appliWithSameName.getName() +
                "' : (" +
                appliWithSameName.getAlias() +
                "/" +
                applicationImport.getIdFromExcel() +
                "), please  correrct your Excel file"
            );
        }

        application.setComment(applicationImport.getComment());
        application.setDescription(applicationImport.getDescription());
        application.setName(applicationImport.getName());

        if (StringUtils.hasText(applicationImport.getTechnology())) {
            Technology technology = technologyRepository.findByNameIgnoreCase(applicationImport.getTechnology());
            if (technology == null) {
                technology = new Technology();
                technology.setName(applicationImport.getTechnology());
                technologyRepository.save(technology);
            }
            application.addTechnologies(technology);
        }

        if (StringUtils.hasText(applicationImport.getType())) {
            ApplicationCategory applicationCategory = applicationCategoryRepository.findByNameIgnoreCase(applicationImport.getType());
            if (applicationCategory == null) {
                applicationCategory = new ApplicationCategory();
                applicationCategory.setName(applicationImport.getType());
                applicationCategoryRepository.save(applicationCategory);
            }
            application.addCategories(applicationCategory);
        }

        return application;
    }
}
