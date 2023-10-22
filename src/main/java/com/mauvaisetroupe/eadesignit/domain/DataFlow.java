package com.mauvaisetroupe.eadesignit.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mauvaisetroupe.eadesignit.domain.enumeration.Frequency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * DataFlow represents
 * - A file when Protocol=FILE
 * - A topic when Protocol=Event
 * - A Swagger when Protocol=API
 * - A WSDL when Protocol=SOAP
 * - An XSD when Protocol=ESB, MESSAGING
 */
@Schema(
    description = "DataFlow represents\n- A file when Protocol=FILE\n- A topic when Protocol=Event\n- A Swagger when Protocol=API\n- A WSDL when Protocol=SOAP\n- An XSD when Protocol=ESB, MESSAGING"
)
@Entity
@Table(name = "dataflow")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DataFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "resource_name", nullable = false)
    private String resourceName;

    @Column(name = "resource_type")
    private String resourceType;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency")
    private Frequency frequency;

    @Size(max = 500)
    @Column(name = "contract_url", length = 500)
    private String contractURL;

    @Size(max = 500)
    @Column(name = "documentation_url", length = 500)
    private String documentationURL;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dataFlow")
    @JsonIgnoreProperties(value = { "dataFlow" }, allowSetters = true)
    private Set<DataFlowItem> items = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private DataFormat format;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_dataflow__functional_flows",
        joinColumns = @JoinColumn(name = "dataflow_id"),
        inverseJoinColumns = @JoinColumn(name = "functional_flows_id")
    )
    @JsonIgnoreProperties(value = { "steps", "owner", "landscapes", "dataFlows" }, allowSetters = true)
    private Set<FunctionalFlow> functionalFlows = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "dataFlows", "source", "target", "sourceComponent", "targetComponent", "owner", "steps" },
        allowSetters = true
    )
    private FlowInterface flowInterface;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DataFlow id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public DataFlow resourceName(String resourceName) {
        this.setResourceName(resourceName);
        return this;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceType() {
        return this.resourceType;
    }

    public DataFlow resourceType(String resourceType) {
        this.setResourceType(resourceType);
        return this;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getDescription() {
        return this.description;
    }

    public DataFlow description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Frequency getFrequency() {
        return this.frequency;
    }

    public DataFlow frequency(Frequency frequency) {
        this.setFrequency(frequency);
        return this;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public String getContractURL() {
        return this.contractURL;
    }

    public DataFlow contractURL(String contractURL) {
        this.setContractURL(contractURL);
        return this;
    }

    public void setContractURL(String contractURL) {
        this.contractURL = contractURL;
    }

    public String getDocumentationURL() {
        return this.documentationURL;
    }

    public DataFlow documentationURL(String documentationURL) {
        this.setDocumentationURL(documentationURL);
        return this;
    }

    public void setDocumentationURL(String documentationURL) {
        this.documentationURL = documentationURL;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public DataFlow startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public DataFlow endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<DataFlowItem> getItems() {
        return this.items;
    }

    public void setItems(Set<DataFlowItem> dataFlowItems) {
        if (this.items != null) {
            this.items.forEach(i -> i.setDataFlow(null));
        }
        if (dataFlowItems != null) {
            dataFlowItems.forEach(i -> i.setDataFlow(this));
        }
        this.items = dataFlowItems;
    }

    public DataFlow items(Set<DataFlowItem> dataFlowItems) {
        this.setItems(dataFlowItems);
        return this;
    }

    public DataFlow addItems(DataFlowItem dataFlowItem) {
        this.items.add(dataFlowItem);
        dataFlowItem.setDataFlow(this);
        return this;
    }

    public DataFlow removeItems(DataFlowItem dataFlowItem) {
        this.items.remove(dataFlowItem);
        dataFlowItem.setDataFlow(null);
        return this;
    }

    public DataFormat getFormat() {
        return this.format;
    }

    public void setFormat(DataFormat dataFormat) {
        this.format = dataFormat;
    }

    public DataFlow format(DataFormat dataFormat) {
        this.setFormat(dataFormat);
        return this;
    }

    public Set<FunctionalFlow> getFunctionalFlows() {
        return this.functionalFlows;
    }

    public void setFunctionalFlows(Set<FunctionalFlow> functionalFlows) {
        this.functionalFlows = functionalFlows;
    }

    public DataFlow functionalFlows(Set<FunctionalFlow> functionalFlows) {
        this.setFunctionalFlows(functionalFlows);
        return this;
    }

    public DataFlow addFunctionalFlows(FunctionalFlow functionalFlow) {
        this.functionalFlows.add(functionalFlow);
        functionalFlow.getDataFlows().add(this);
        return this;
    }

    public DataFlow removeFunctionalFlows(FunctionalFlow functionalFlow) {
        this.functionalFlows.remove(functionalFlow);
        functionalFlow.getDataFlows().remove(this);
        return this;
    }

    public FlowInterface getFlowInterface() {
        return this.flowInterface;
    }

    public void setFlowInterface(FlowInterface flowInterface) {
        this.flowInterface = flowInterface;
    }

    public DataFlow flowInterface(FlowInterface flowInterface) {
        this.setFlowInterface(flowInterface);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataFlow)) {
            return false;
        }
        return getId() != null && getId().equals(((DataFlow) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DataFlow{" +
            "id=" + getId() +
            ", resourceName='" + getResourceName() + "'" +
            ", resourceType='" + getResourceType() + "'" +
            ", description='" + getDescription() + "'" +
            ", frequency='" + getFrequency() + "'" +
            ", contractURL='" + getContractURL() + "'" +
            ", documentationURL='" + getDocumentationURL() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
