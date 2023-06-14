import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import CapabilityApplicationMappingService from '@/entities/capability-application-mapping/capability-application-mapping.service';
import { ICapabilityApplicationMapping } from '@/shared/model/capability-application-mapping.model';

import { ICapability, Capability } from '@/shared/model/capability.model';
import CapabilityService from './capability.service';

const validations: any = {
  capability: {
    name: {
      required,
    },
    description: {
      maxLength: maxLength(1500),
    },
    comment: {
      maxLength: maxLength(1500),
    },
    level: {},
  },
};

@Component({
  validations,
})
export default class CapabilityUpdate extends Vue {
  @Inject('capabilityService') private capabilityService: () => CapabilityService;
  @Inject('alertService') private alertService: () => AlertService;

  public capability: ICapability = new Capability();

  public capabilities: ICapability[] = [];

  @Inject('capabilityApplicationMappingService') private capabilityApplicationMappingService: () => CapabilityApplicationMappingService;

  public capabilityApplicationMappings: ICapabilityApplicationMapping[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.capabilityId) {
        vm.retrieveCapability(to.params.capabilityId);
      }
      vm.initRelationships();
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.capability.id) {
      this.capabilityService()
        .update(this.capability)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Capability is updated with identifier ' + param.id;
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      this.capabilityService()
        .create(this.capability)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Capability is created with identifier ' + param.id;
          this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public retrieveCapability(capabilityId): void {
    this.capabilityService()
      .find(capabilityId)
      .then(res => {
        this.capability = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.capabilityService()
      .retrieve()
      .then(res => {
        this.capabilities = res.data;
      });
    this.capabilityApplicationMappingService()
      .retrieve()
      .then(res => {
        this.capabilityApplicationMappings = res.data;
      });
  }
}
