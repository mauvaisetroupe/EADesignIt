import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IApplicationComponent } from '@/shared/model/application-component.model';

import ApplicationComponentService from './application-component.service';
import AlertService from '@/shared/alert/alert.service';
import AccountService from '@/account/account.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class ApplicationComponent extends Vue {
  @Inject('applicationComponentService') private applicationComponentService: () => ApplicationComponentService;
  @Inject('alertService') private alertService: () => AlertService;
  @Inject('accountService') public accountService: () => AccountService;
  private removeId: number = null;

  public applicationComponents: IApplicationComponent[] = [];

  public isFetching = false;

  public fields = [
    // { key: 'id', sortable: false },
    { key: 'alias', sortable: true },
    { key: 'name', sortable: true },
    { key: 'application', sortable: false },
    { key: 'description', sortable: false, formatter: 'formatLongText' },
    { key: 'applicationType', sortable: false },
    { key: 'softwareType', sortable: false },
    { key: 'categories', sortable: false },
    { key: 'technologies', sortable: false },
  ];

  public currentPage = 1;
  public perPage = 10;
  public filter = '';

  public mounted(): void {
    this.retrieveAllApplicationComponents();
  }

  public clear(): void {
    this.retrieveAllApplicationComponents();
  }

  public retrieveAllApplicationComponents(): void {
    this.isFetching = true;
    this.applicationComponentService()
      .retrieve()
      .then(
        res => {
          this.applicationComponents = res.data;
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
          this.alertService().showHttpError(this, err.response);
        }
      );
  }

  public handleSyncList(): void {
    this.clear();
  }

  public prepareRemove(instance: IApplicationComponent): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeApplicationComponent(): void {
    this.applicationComponentService()
      .delete(this.removeId)
      .then(() => {
        const message = 'A ApplicationComponent is deleted with identifier ' + this.removeId;
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllApplicationComponents();
        this.closeDialog();
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
}
