package com.mauvaisetroupe.eadesignit.service.importfile;

import com.mauvaisetroupe.eadesignit.domain.Application;
import com.mauvaisetroupe.eadesignit.domain.Capability;
import com.mauvaisetroupe.eadesignit.domain.CapabilityApplicationMapping;
import com.mauvaisetroupe.eadesignit.domain.LandscapeView;
import com.mauvaisetroupe.eadesignit.domain.enumeration.ImportStatus;
import com.mauvaisetroupe.eadesignit.repository.ApplicationRepository;
import com.mauvaisetroupe.eadesignit.repository.CapabilityApplicationMappingRepository;
import com.mauvaisetroupe.eadesignit.repository.CapabilityRepository;
import com.mauvaisetroupe.eadesignit.repository.LandscapeViewRepository;
import com.mauvaisetroupe.eadesignit.service.dto.CapabilityDTO;
import com.mauvaisetroupe.eadesignit.service.importfile.dto.ApplicationCapabilityItemDTO;
import com.mauvaisetroupe.eadesignit.service.importfile.dto.CapabilityImportDTO;
import com.mauvaisetroupe.eadesignit.service.importfile.util.SummaryImporterService;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.poi.EncryptedDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationCapabilityImportService {

    private final Logger log = LoggerFactory.getLogger(ApplicationCapabilityImportService.class);

    @Autowired
    private CapabilityRepository capabilityRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CapabilityApplicationMappingRepository capabilityApplicationMappingRepository;

    @Autowired
    private LandscapeViewRepository landscapeViewRepository;

    @Autowired
    private SummaryImporterService summaryImporterService;

    public static final String APP_NAME_1 = "application.name.1";
    public static final String APP_NAME_2 = "application.name.2";
    public static final String APP_NAME_3 = "application.name.3";
    public static final String APP_NAME_4 = "application.name.4";
    public static final String APP_NAME_5 = "application.name.5";

    public static final String L0_NAME = "capability.L0";
    public static final String L1_NAME = "capability.L1";
    public static final String L2_NAME = "capability.L2";
    public static final String L3_NAME = "capability.L3";
    public static final String FULL_PATH = "full.path";

    public List<ApplicationCapabilityItemDTO> importExcel(InputStream excel, String sheetname)
        throws EncryptedDocumentException, IOException {
        ExcelReader capabilityFlowExcelReader = new ExcelReader(excel);
        List<ApplicationCapabilityItemDTO> result = new ArrayList<ApplicationCapabilityItemDTO>();

        String diagramName = summaryImporterService.findLandscape(capabilityFlowExcelReader, sheetname);
        LandscapeView landscape = landscapeViewRepository.findByDiagramNameIgnoreCase(diagramName);

        List<Map<String, Object>> capabilitiesDF = capabilityFlowExcelReader.getSheet(sheetname);
        for (Map<String, Object> map : capabilitiesDF) {
            ApplicationCapabilityItemDTO itemDTO = new ApplicationCapabilityItemDTO();

            CapabilityDTO l0Import = null, l1Import = null, l2Import = null, l3Import = null;
            String domain = null;
            if (map.containsKey(FULL_PATH)) {
                String fullPath = (String) map.get(FULL_PATH);
                String[] capabilitiesName = fullPath.split(" > ");
                if (capabilitiesName.length > 0) domain = capabilitiesName[0];
                if (capabilitiesName.length > 1) l0Import = new CapabilityDTO(capabilitiesName[1], 0);
                if (capabilitiesName.length > 2) l1Import = new CapabilityDTO(capabilitiesName[2], 1);
                if (capabilitiesName.length > 3) l2Import = new CapabilityDTO(capabilitiesName[3], 2);
                if (capabilitiesName.length > 4) l3Import = new CapabilityDTO(capabilitiesName[4], 3);
            } else {
                if (map.get(L0_NAME) != null) l0Import = new CapabilityDTO((String) map.get(L0_NAME), 0);
                if (map.get(L1_NAME) != null) l1Import = new CapabilityDTO((String) map.get(L1_NAME), 1);
                if (map.get(L2_NAME) != null) l2Import = new CapabilityDTO((String) map.get(L2_NAME), 2);
                if (map.get(L3_NAME) != null) l3Import = new CapabilityDTO((String) map.get(L3_NAME), 3);
            }
            CapabilityImportDTO capabilityImportDTO = new CapabilityImportDTO(l0Import, l1Import, l2Import, l3Import);
            itemDTO.setCapabilityImportDTO(capabilityImportDTO);
            itemDTO.setApplicationNames(mapArrayToString(map));

            Optional<Capability> capabilityOptional = findCapability(l0Import, l1Import, l2Import, l3Import, itemDTO);
            List<Application> applications = findApplication(map, itemDTO);

            if (applications.isEmpty()) {
                String error = "No application found : " + map;
                log.error(error);
            } else if (capabilityOptional.isEmpty()) {
                String error = "No Capaility found : " + map;
                log.error(error);
            } else {
                Capability capability = capabilityOptional.get();
                for (Application application : applications) {
                    CapabilityApplicationMapping capabilityApplicationMapping = capabilityApplicationMappingRepository.findByApplicationAndCapability(
                        application,
                        capability
                    );
                    if (capabilityApplicationMapping == null) {
                        capabilityApplicationMapping = new CapabilityApplicationMapping();
                        capabilityApplicationMappingRepository.save(capabilityApplicationMapping);
                    }
                    application.addCapabilityApplicationMapping(capabilityApplicationMapping);
                    capability.addCapabilityApplicationMapping(capabilityApplicationMapping);
                    if (landscape != null) {
                        landscape.addCapabilityApplicationMapping(capabilityApplicationMapping);
                    }
                }
                if (itemDTO.getImportStatus() == null) {
                    itemDTO.setImportStatus(ImportStatus.NEW);
                }
            }
            result.add(itemDTO);
        }
        return result;
    }

    private List<Application> findApplication(Map<String, Object> map, ApplicationCapabilityItemDTO applicationCapabilityDTO) {
        String[] apps = { APP_NAME_1, APP_NAME_2, APP_NAME_3, APP_NAME_4, APP_NAME_5 };
        List<Application> result = new ArrayList<>();
        for (String appName : apps) {
            String app1 = getApplicationName(map, appName);
            if (app1 != null) {
                Application application = applicationRepository.findByNameIgnoreCase(app1);
                if (application != null) {
                    result.add(application);
                } else {
                    processError(applicationCapabilityDTO, "Can not find application : [" + app1 + "]");
                }
            }
        }
        return result;
    }

    private List<String> mapArrayToString(Map<String, Object> map) {
        String[] apps = { APP_NAME_1, APP_NAME_2, APP_NAME_3, APP_NAME_4, APP_NAME_5 };
        List<String> result = new ArrayList<>();
        for (String appName : apps) {
            result.add(getApplicationName(map, appName));
        }
        return result;
    }

    private String getApplicationName(Map<String, Object> map, String appName1) {
        Object obj = map.get(appName1);
        if (obj != null) {
            return obj.toString().trim();
        }
        return null;
    }

    private Optional<Capability> findCapability(
        CapabilityDTO l0Import,
        CapabilityDTO l1Import,
        CapabilityDTO l2Import,
        CapabilityDTO l3Import,
        ApplicationCapabilityItemDTO applicationCapabilityDTO
    ) {
        List<Capability> potentials = null;
        Capability capability = null;
        if (l3Import != null && l2Import != null) {
            potentials =
                capabilityRepository.findByNameIgnoreCaseAndParentNameIgnoreCaseAndLevel(l3Import.getName(), l2Import.getName(), 3);
        } else if (l2Import != null && l1Import != null) {
            potentials =
                capabilityRepository.findByNameIgnoreCaseAndParentNameIgnoreCaseAndLevel(l2Import.getName(), l1Import.getName(), 2);
        } else if (l1Import != null && l0Import != null) {
            potentials =
                capabilityRepository.findByNameIgnoreCaseAndParentNameIgnoreCaseAndLevel(l1Import.getName(), l0Import.getName(), 1);
        } else if (l0Import != null) {
            potentials = capabilityRepository.findByNameIgnoreCaseAndParentNameIgnoreCaseAndLevel(l0Import.getName(), null, 0);
        } else {
            processError(
                applicationCapabilityDTO,
                "At least one capability should not be null : " + capa(l0Import, l1Import, l2Import, l3Import)
            );
        }
        if (potentials == null || potentials.size() != 1) {
            processError(applicationCapabilityDTO, "Capability could not be found : " + capa(l0Import, l1Import, l2Import, l3Import));
        } else {
            capability = potentials.get(0);
        }
        return Optional.ofNullable(capability);
    }

    private String capa(CapabilityDTO l0Import, CapabilityDTO l1Import, CapabilityDTO l2Import, CapabilityDTO l3Import) {
        String sep = " > ";
        return l0Import + sep + l1Import + sep + l2Import + ((l3Import == null) ? "" : sep + l3Import);
    }

    private void processError(ApplicationCapabilityItemDTO applicationCapabilityDTO, String error) {
        log.error(error);
        applicationCapabilityDTO.setImportStatus(ImportStatus.ERROR);
        applicationCapabilityDTO.setErrorMessage(error);
    }
}
