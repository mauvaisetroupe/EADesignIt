package com.mauvaisetroupe.eadesignit.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A FunctionalFlowStep.
 */
@Entity
@Table(name = "REL_FLOW__INTERFACES", uniqueConstraints = { @UniqueConstraint(columnNames = { "interfaces_id", "flow_id" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FunctionalFlowStep implements Serializable, Comparable<FunctionalFlowStep> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "interfaces_id")
    @NotNull
    @JsonIgnoreProperties(value = { "sourceComponent", "targetComponent", "owner", "steps" }, allowSetters = true)
    private FlowInterface flowInterface;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "steps", "owner", "landscapes", "dataFlows" }, allowSetters = true)
    private FunctionalFlow flow;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FunctionalFlowStep id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public FunctionalFlowStep description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FlowInterface getFlowInterface() {
        return this.flowInterface;
    }

    public void setFlowInterface(FlowInterface flowInterface) {
        this.flowInterface = flowInterface;
    }

    public FunctionalFlowStep flowInterface(FlowInterface flowInterface) {
        this.setFlowInterface(flowInterface);
        return this;
    }

    public FunctionalFlow getFlow() {
        return this.flow;
    }

    public void setFlow(FunctionalFlow functionalFlow) {
        this.flow = functionalFlow;
    }

    public FunctionalFlowStep flow(FunctionalFlow functionalFlow) {
        this.setFlow(functionalFlow);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FunctionalFlowStep)) {
            return false;
        }
        return id != null && id.equals(((FunctionalFlowStep) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FunctionalFlowStep{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }

    @Override
    public int compareTo(FunctionalFlowStep arg0) {
        int result = -1;
        if (arg0 == null) {
            result = -1;
        } else if (arg0 == this) {
            result = 0;
        } else if (arg0.getId() != null && arg0.getId() == this.getId()) {
            result = 0;
        } else if (arg0.getFlowInterface() == null || arg0.getFlowInterface().getAlias() == null) {
            result = -1;
        } else if (this.getFlowInterface() == null || this.getFlowInterface().getAlias() == null) {
            result = 1;
        } else {
            result = this.getFlowInterface().getAlias().compareTo(arg0.getFlowInterface().getAlias());
        }
        return result;
    }
}
