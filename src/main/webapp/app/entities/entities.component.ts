import { Component, Provide, Vue } from 'vue-property-decorator';

import UserService from '@/entities/user/user.service';
import LandscapeViewService from './landscape-view/landscape-view.service';
import OwnerService from './owner/owner.service';
import FunctionalFlowService from './functional-flow/functional-flow.service';
import FlowInterfaceService from './flow-interface/flow-interface.service';
import ApplicationService from './application/application.service';
import DataFlowService from './data-flow/data-flow.service';
import ApplicationComponentService from './application-component/application-component.service';
import ProtocolService from './protocol/protocol.service';
import DataFlowItemService from './data-flow-item/data-flow-item.service';
import DataFormatService from './data-format/data-format.service';
import ApplicationCategoryService from './application-category/application-category.service';
import TechnologyService from './technology/technology.service';
import CapabilityService from './capability/capability.service';
import FunctionalFlowStepService from './functional-flow-step/functional-flow-step.service';
import FlowGroupService from './flow-group/flow-group.service';
import ExternalReferenceService from './external-reference/external-reference.service';
import ExternalSystemService from './external-system/external-system.service';
import CapabilityApplicationMappingService from './capability-application-mapping/capability-application-mapping.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

@Component
export default class Entities extends Vue {
  @Provide('userService') private userService = () => new UserService();
  @Provide('landscapeViewService') private landscapeViewService = () => new LandscapeViewService();
  @Provide('ownerService') private ownerService = () => new OwnerService();
  @Provide('functionalFlowService') private functionalFlowService = () => new FunctionalFlowService();
  @Provide('flowInterfaceService') private flowInterfaceService = () => new FlowInterfaceService();
  @Provide('applicationService') private applicationService = () => new ApplicationService();
  @Provide('dataFlowService') private dataFlowService = () => new DataFlowService();
  @Provide('applicationComponentService') private applicationComponentService = () => new ApplicationComponentService();
  @Provide('protocolService') private protocolService = () => new ProtocolService();
  @Provide('dataFlowItemService') private dataFlowItemService = () => new DataFlowItemService();
  @Provide('dataFormatService') private dataFormatService = () => new DataFormatService();
  @Provide('applicationCategoryService') private applicationCategoryService = () => new ApplicationCategoryService();
  @Provide('technologyService') private technologyService = () => new TechnologyService();
  @Provide('capabilityService') private capabilityService = () => new CapabilityService();
  @Provide('functionalFlowStepService') private functionalFlowStepService = () => new FunctionalFlowStepService();
  @Provide('flowGroupService') private flowGroupService = () => new FlowGroupService();
  @Provide('externalReferenceService') private externalReferenceService = () => new ExternalReferenceService();
  @Provide('externalSystemService') private externalSystemService = () => new ExternalSystemService();
  @Provide('capabilityApplicationMappingService') private capabilityApplicationMappingService = () =>
    new CapabilityApplicationMappingService();
  // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
}
