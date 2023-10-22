package com.mauvaisetroupe.eadesignit.service.reporting;

import com.mauvaisetroupe.eadesignit.domain.DataFlow;
import com.mauvaisetroupe.eadesignit.domain.FlowInterface;
import com.mauvaisetroupe.eadesignit.domain.FunctionalFlowStep;
import com.mauvaisetroupe.eadesignit.domain.util.DataFlowComparator;
import com.mauvaisetroupe.eadesignit.repository.DataFlowRepository;
import com.mauvaisetroupe.eadesignit.repository.FlowInterfaceRepository;
import com.mauvaisetroupe.eadesignit.repository.FunctionalFlowRepository;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
public class ReportingService {

    @Autowired
    private FlowInterfaceRepository flowInterfaceRepository;

    @Autowired
    private FunctionalFlowRepository functionalFlowRepository;

    @Autowired
    private DataFlowRepository dataFlowRepository;

    private final Logger log = LoggerFactory.getLogger(ReportingService.class);

    public void mergeInterfaces(Long id, @NotNull List<String> aliasToMerge) {
        DataFlowComparator comparator = new DataFlowComparator();

        // INPUTS
        FlowInterface interfaceToKeep = flowInterfaceRepository.findById(id).get();
        Set<FlowInterface> interfacesToMerge = flowInterfaceRepository.findByAliasIn(aliasToMerge);

        for (FlowInterface interfaceToMerge : interfacesToMerge) {
            log.debug(" ### ### ### About to merge interface :" + interfaceToMerge.getAlias());

            //application
            Assert.isTrue(
                interfaceToMerge.getSource().equals(interfaceToKeep.getSource()) &&
                interfaceToMerge.getTarget().equals(interfaceToKeep.getTarget()),
                "Should have same source & taget"
            );

            //application component
            Assert.isNull(interfaceToMerge.getSourceComponent(), "Not implemented");
            Assert.isNull(interfaceToMerge.getTargetComponent(), "Not implemented");

            //protocol
            Assert.isTrue(interfaceToMerge.getProtocol().equals(interfaceToKeep.getProtocol()), "Not implemented");

            //owner not check, could potentially be different

            // Documentation URL
            addIfNecessaryDocumenation(interfaceToKeep, interfaceToMerge.getDocumentationURL());
            addIfNecessaryDocumenation(interfaceToKeep, interfaceToMerge.getDocumentationURL2());

            //dataflow
            // make copy to avoid concurrent modification exception
            Set<DataFlow> dataFlowToprocess = new HashSet<>();
            dataFlowToprocess.addAll(interfaceToMerge.getDataFlows());

            for (DataFlow dataFlowToMerge : dataFlowToprocess) {
                log.debug(" ### ### Examining DataFlow :" + dataFlowToMerge.getId());

                // remove dataSet from interface
                log.debug(" ###  Detach DataFlow {} from interface {} ", dataFlowToMerge.getId(), interfaceToMerge.getAlias());
                interfaceToMerge.removeDataFlows(dataFlowToMerge);
                log.debug(" ### Save DataFlow : " + dataFlowToMerge.getId());
                dataFlowRepository.save(dataFlowToMerge);
                log.debug(" ### Save Interface : " + interfaceToMerge.getAlias());
                flowInterfaceRepository.save(interfaceToMerge);

                // Check if need to keep or delete (other dataflow equivalent?)
                boolean shouldKeepDataFlow = true;
                for (DataFlow dataFlowToKeep : interfaceToKeep.getDataFlows()) {
                    log.debug(" ### Comparing with DataFlow :" + dataFlowToKeep.getId());
                    if (comparator.areEquivalent(dataFlowToMerge, dataFlowToKeep)) {
                        shouldKeepDataFlow = false;
                        log.debug(" ### Equality with existing DataFlow {}.", dataFlowToKeep.getId());
                        log.debug(" ### Delete DataFlow : " + dataFlowToMerge.getId());
                        dataFlowRepository.delete(dataFlowToMerge);
                        break;
                    }
                }
                if (shouldKeepDataFlow) {
                    if (!interfaceToKeep.getDataFlows().contains(dataFlowToMerge)) {
                        // Move if necessary
                        log.debug(" ### Adding to interface : " + interfaceToKeep.getAlias() + " DataFlow :  " + dataFlowToMerge.getId());
                        interfaceToKeep.addDataFlows(dataFlowToMerge);
                        log.debug(" ### Save DataFlow : " + dataFlowToMerge.getId());
                        dataFlowRepository.save(dataFlowToMerge);
                        log.debug(" ### Save Interface : " + interfaceToKeep.getAlias());
                        flowInterfaceRepository.save(interfaceToKeep);
                    } else {
                        log.debug(
                            "### DataFlow :  " + dataFlowToMerge.getId() + " already in " + interfaceToKeep.getAlias() + " (not added)"
                        );
                    }
                }
            }

            // Functional Flows to move (avoid ConcurrentMofificationException)
            Set<FunctionalFlowStep> stepsToModify = new HashSet<>();
            stepsToModify.addAll(interfaceToMerge.getSteps());
            for (FunctionalFlowStep step : stepsToModify) {
                step.setFlowInterface(interfaceToKeep);
            }

            // Delete interface
            log.debug(" ### ### Delete " + interfaceToMerge.getAlias());
            flowInterfaceRepository.delete(interfaceToMerge);
        }
    }

    private void addIfNecessaryDocumenation(FlowInterface interfaceToKeep, String documentationURL) {
        if (!StringUtils.hasText(documentationURL)) return;
        if (documentationURL.equals(interfaceToKeep.getDocumentationURL())) return;
        if (documentationURL.equals(interfaceToKeep.getDocumentationURL2())) return;
        if (!StringUtils.hasText(interfaceToKeep.getDocumentationURL())) {
            interfaceToKeep.setDocumentationURL(documentationURL);
        }
        if (!StringUtils.hasText(interfaceToKeep.getDocumentationURL2())) {
            interfaceToKeep.setDocumentationURL2(documentationURL);
        }
        throw new NotImplementedException("Cannot add documentation URL");
    }
}
