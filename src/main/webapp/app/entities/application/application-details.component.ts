import { Component, Vue, Inject, Watch } from 'vue-property-decorator';

import { IApplication } from '@/shared/model/application.model';
import ApplicationService from './application.service';
import AccountService from '@/account/account.service';
import AlertService from '@/shared/alert/alert.service';
import { IFlowInterface } from '@/shared/model/flow-interface.model';
import { ICapability } from '@/shared/model/capability.model';
import CapabilityComponent from '@/entities/capability/component/capability.vue';
import { IFunctionalFlow } from '@/shared/model/functional-flow.model';

@Component({
  components: {
    CapabilityComponent,
  },
})
export default class ApplicationDetails extends Vue {
  @Inject('applicationService') private applicationService: () => ApplicationService;
  @Inject('alertService') private alertService: () => AlertService;
  @Inject('accountService') public accountService: () => AccountService;
  public application: IApplication = {};
  public plantUMLImage = '';
  public capabilitiesPlantUMLImage = '';
  public interfaces: IFlowInterface[] = [];
  public flows: IFunctionalFlow[] = [];
  public consolidatedCapabilities: ICapability[] = [];

  public layout = 'elk';
  public refreshingPlantuml = false;
  public groupComponents = true;

  public lco: ICapability = {};
  public showLabels = false;
  public interfaceCurrentPage = 1;
  public interfaceFilter = '';
  public interfacePerPage = 10;
  public flowCurrentPage = 1;
  public flowFilter = '';
  public flowPerPage = 10;

  public tabIndex = 0;
  public applicationId = -1;
  public sessionKey = 'application.detail.tab';

  @Watch('tabIndex')
  public onTabChange(newtab) {
    if (this.application && this.application.id) {
      this.tabIndex = newtab;
      sessionStorage.setItem(this.sessionKey, this.application.id + '#' + this.tabIndex);
    }
  }

  public created() {
    // https://github.com/bootstrap-vue/bootstrap-vue/issues/2803
    this.$nextTick(() => {
      this.loadTab(this.applicationId);
    });
  }

  public loadTab(_applicationID) {
    if (sessionStorage.getItem(this.sessionKey)) {
      const parts = sessionStorage.getItem(this.sessionKey).split('#');
      const appliId = parseInt(parts[0]);
      const tabIndex = parseInt(parts[1]);
      const applicationID = parseInt(_applicationID);
      if (applicationID === appliId) {
        this.tabIndex = tabIndex;
      } else {
        sessionStorage.removeItem(this.sessionKey);
      }
    } else {
      this.tabIndex = 1;
    }
  }

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.applicationId) {
        vm.retrieveApplication(to.params.applicationId);
        vm.applicationId = parseInt(to.params.applicationId);
      }
    });
  }

  public retrieveApplication(applicationId) {
    this.plantUMLImage = '';
    this.capabilitiesPlantUMLImage = '';
    this.applicationService()
      .find(applicationId)
      .then(res => {
        this.application = res;
        this.getPlantUML(applicationId);
        this.retrieveCapabilities(applicationId);
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }

  public getPlantUML(applicationId) {
    this.applicationService()
      .getPlantUML(applicationId, this.layout, this.groupComponents, this.showLabels)
      .then(
        res => {
          this.plantUMLImage = res.data.svg;
          this.interfaces = res.data.interfaces;
          this.flows = res.data.flows;
          this.refreshingPlantuml = false;
        },
        err => {
          console.log(err);
        }
      );
  }

  public isOwner(application: IApplication): boolean {
    const username = this.$store.getters.account?.login ?? '';
    if (this.accountService().writeAuthorities) {
      return true;
    }
    if (application.owner && application.owner.users) {
      for (const user of application.owner.users) {
        if (user.login === username) {
          return true;
        }
      }
    }
    return false;
  }

  public retrieveCapabilities(applicationId) {
    this.applicationService()
      .getCapabilities(applicationId)
      .then(res => {
        this.consolidatedCapabilities = res;
        this.lco = this.consolidatedCapabilities[0];
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public routeToCapability(capId: string) {
    this.$router.push({ name: 'CapabilityNavigate', params: { capabilityId: capId } });
  }

  public changeLayout() {
    if (this.layout == 'smetana') {
      this.layout = 'elk';
    } else {
      this.layout = 'smetana';
    }
    this.getPlantUML(this.application.id);
  }

  public doGroupComponents() {
    this.refreshingPlantuml = true;
    this.groupComponents = !this.groupComponents;
    this.getPlantUML(this.application.id);
  }

  public doShowLabels() {
    this.refreshingPlantuml = true;
    this.showLabels = !this.showLabels;
    this.getPlantUML(this.application.id);
  }
}
