package com.mauvaisetroupe.eadesignit.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mauvaisetroupe.eadesignit.domain.util.Ownershipable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A FlowInterface.
 */
@Entity
@Table(name = "interface")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FlowInterface implements Serializable, Comparable<FlowInterface>, Ownershipable, IFlowInterface {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "alias", nullable = false, unique = true)
    private String alias;

    @Column(name = "status")
    private String status;

    @Size(max = 500)
    @Column(name = "documentation_url", length = 500)
    private String documentationURL;

    @Size(max = 500)
    @Column(name = "documentation_url_2", length = 500)
    private String documentationURL2;

    @Size(max = 1500)
    @Column(name = "description", length = 1500)
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToMany(mappedBy = "flowInterface")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "functionalFlows", "flowInterface" }, allowSetters = true)
    private Set<DataFlow> dataFlows = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = {
            "owner", "itOwner", "businessOwner", "capabilityApplicationMappings", "capabilities", "externalIDS", "applicationsLists",
        },
        allowSetters = true
    )
    private Application source;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = {
            "owner", "itOwner", "businessOwner", "capabilityApplicationMappings", "capabilities", "externalIDS", "applicationsLists",
        },
        allowSetters = true
    )
    private Application target;

    @ManyToOne
    @JsonIgnoreProperties(value = { "application", "externalIDS" }, allowSetters = true)
    private ApplicationComponent sourceComponent;

    @ManyToOne
    @JsonIgnoreProperties(value = { "application", "externalIDS" }, allowSetters = true)
    private ApplicationComponent targetComponent;

    @ManyToOne
    private Protocol protocol;

    @ManyToOne
    private Owner owner;

    @OneToMany(mappedBy = "flowInterface")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "flowInterface", "group" }, allowSetters = true)
    private Set<FunctionalFlowStep> steps = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FlowInterface id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlias() {
        return this.alias;
    }

    public FlowInterface alias(String alias) {
        this.setAlias(alias);
        return this;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getStatus() {
        return this.status;
    }

    public FlowInterface status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDocumentationURL() {
        return this.documentationURL;
    }

    public FlowInterface documentationURL(String documentationURL) {
        this.setDocumentationURL(documentationURL);
        return this;
    }

    public void setDocumentationURL(String documentationURL) {
        this.documentationURL = documentationURL;
    }

    public String getDocumentationURL2() {
        return this.documentationURL2;
    }

    public FlowInterface documentationURL2(String documentationURL2) {
        this.setDocumentationURL2(documentationURL2);
        return this;
    }

    public void setDocumentationURL2(String documentationURL2) {
        this.documentationURL2 = documentationURL2;
    }

    public String getDescription() {
        return this.description;
    }

    public FlowInterface description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public FlowInterface startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public FlowInterface endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<DataFlow> getDataFlows() {
        return this.dataFlows;
    }

    public void setDataFlows(Set<DataFlow> dataFlows) {
        if (this.dataFlows != null) {
            this.dataFlows.forEach(i -> i.setFlowInterface(null));
        }
        if (dataFlows != null) {
            dataFlows.forEach(i -> i.setFlowInterface(this));
        }
        this.dataFlows = dataFlows;
    }

    public FlowInterface dataFlows(Set<DataFlow> dataFlows) {
        this.setDataFlows(dataFlows);
        return this;
    }

    public FlowInterface addDataFlows(DataFlow dataFlow) {
        this.dataFlows.add(dataFlow);
        dataFlow.setFlowInterface(this);
        return this;
    }

    public FlowInterface removeDataFlows(DataFlow dataFlow) {
        this.dataFlows.remove(dataFlow);
        dataFlow.setFlowInterface(null);
        return this;
    }

    public Application getSource() {
        return this.source;
    }

    public void setSource(Application application) {
        this.source = application;
    }

    public FlowInterface source(Application application) {
        this.setSource(application);
        return this;
    }

    public Application getTarget() {
        return this.target;
    }

    public void setTarget(Application application) {
        this.target = application;
    }

    public FlowInterface target(Application application) {
        this.setTarget(application);
        return this;
    }

    public ApplicationComponent getSourceComponent() {
        return this.sourceComponent;
    }

    public void setSourceComponent(ApplicationComponent applicationComponent) {
        this.sourceComponent = applicationComponent;
    }

    public FlowInterface sourceComponent(ApplicationComponent applicationComponent) {
        this.setSourceComponent(applicationComponent);
        return this;
    }

    public ApplicationComponent getTargetComponent() {
        return this.targetComponent;
    }

    public void setTargetComponent(ApplicationComponent applicationComponent) {
        this.targetComponent = applicationComponent;
    }

    public FlowInterface targetComponent(ApplicationComponent applicationComponent) {
        this.setTargetComponent(applicationComponent);
        return this;
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public FlowInterface protocol(Protocol protocol) {
        this.setProtocol(protocol);
        return this;
    }

    public Owner getOwner() {
        return this.owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public FlowInterface owner(Owner owner) {
        this.setOwner(owner);
        return this;
    }

    public Set<FunctionalFlowStep> getSteps() {
        return this.steps;
    }

    public void setSteps(Set<FunctionalFlowStep> functionalFlowSteps) {
        if (this.steps != null) {
            this.steps.forEach(i -> i.setFlowInterface(null));
        }
        if (functionalFlowSteps != null) {
            functionalFlowSteps.forEach(i -> i.setFlowInterface(this));
        }
        this.steps = functionalFlowSteps;
    }

    public FlowInterface steps(Set<FunctionalFlowStep> functionalFlowSteps) {
        this.setSteps(functionalFlowSteps);
        return this;
    }

    public FlowInterface addSteps(FunctionalFlowStep functionalFlowStep) {
        this.steps.add(functionalFlowStep);
        functionalFlowStep.setFlowInterface(this);
        return this;
    }

    public FlowInterface removeSteps(FunctionalFlowStep functionalFlowStep) {
        if (this.steps.contains(functionalFlowStep)) {
            this.steps.remove(functionalFlowStep);
        } else {
            // hibernate bug due to hashcode ?
            for (Iterator<FunctionalFlowStep> iterator = this.steps.iterator(); iterator.hasNext();) {
                FunctionalFlowStep step = iterator.next();
                if (step.getId() != null && step.getId().equals(functionalFlowStep.getId())) {
                    iterator.remove();
                }
            }
        }

        functionalFlowStep.setFlowInterface(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlowInterface)) {
            return false;
        }
        return id != null && id.equals(((FlowInterface) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlowInterface{" +
            "id=" + getId() +
            ", alias='" + getAlias() + "'" +
            ", status='" + getStatus() + "'" +
            ", documentationURL='" + getDocumentationURL() + "'" +
            ", documentationURL2='" + getDocumentationURL2() + "'" +
            ", description='" + getDescription() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }

    @Override
    public int compareTo(FlowInterface arg0) {
        int result = -1;
        if (arg0 == null) {
            result = -1;
        }
        // compare alias is one is not null
        else if (this.alias != null || arg0.alias != null) {
            result = ObjectUtils.compare(this.alias, arg0.alias, true);
        }
        // compare id is one is not null
        else if (this.id != null || arg0.id != null) {
            result = ObjectUtils.compare(this.id, arg0.id, true);
        } else {
            // alias and id are both null
            result = 0;
        }
        return result;
    }
}
