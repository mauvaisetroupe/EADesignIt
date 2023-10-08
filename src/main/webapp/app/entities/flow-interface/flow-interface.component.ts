import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IFlowInterface } from '@/shared/model/flow-interface.model';

import FlowInterfaceService from './flow-interface.service';
import AlertService from '@/shared/alert/alert.service';
import AccountService from '@/account/account.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class FlowInterface extends Vue {
  @Inject('flowInterfaceService') private flowInterfaceService: () => FlowInterfaceService;
  @Inject('alertService') private alertService: () => AlertService;
  @Inject('accountService') public accountService: () => AccountService;

  public perPage = 20;
  public currentPage = 1;

  get filteredRows() {
    return this.flowInterfaces.filter(row => {
      return (
        (!this.filterAlias || row.alias?.toLowerCase().includes(this.filterAlias?.toLowerCase())) &&
        (!this.filterSource ||
          (row.source?.name?.toLowerCase() + row.sourceComponent?.name?.toLowerCase()).includes(this.filterSource?.toLowerCase())) &&
        (!this.filterTarget ||
          (row.target?.name?.toLowerCase() + row.targetComponent?.name?.toLowerCase()).includes(this.filterTarget?.toLowerCase())) &&
        (!this.filterProtocol || row.protocol?.name?.toLowerCase().includes(this.filterProtocol?.toLowerCase()))
      );
    });
  }

  private removeId: number = null;

  public flowInterfaces: IFlowInterface[] = [];

  public isFetching = false;

  public filterAlias = '';
  public filterSource = '';
  public filterTarget = '';
  public filterProtocol = '';

  public mounted(): void {
    this.retrieveAllFlowInterfaces();
    if (this.$route && this.$route.query && this.$route.query.searchTerm) {
      this.filterProtocol = this.$route.query.searchTerm as string;
    }
  }

  public clear(): void {
    this.retrieveAllFlowInterfaces();
  }

  public retrieveAllFlowInterfaces(): void {
    this.isFetching = true;
    this.flowInterfaceService()
      .retrieve()
      .then(
        res => {
          this.flowInterfaces = res.data;
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

  public prepareRemove(instance: IFlowInterface): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeFlowInterface(): void {
    this.flowInterfaceService()
      .delete(this.removeId)
      .then(() => {
        const message = 'A FlowInterface is deleted with identifier ' + this.removeId;
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllFlowInterfaces();
        this.closeDialog();
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }

  public isOwner(flowInterface: IFlowInterface): boolean {
    const username = this.$store.getters.account?.login ?? '';
    if (this.accountService().writeAuthorities) {
      return true;
    }
    if (flowInterface.owner && flowInterface.owner.users) {
      for (const user of flowInterface.owner.users) {
        if (user.login === username) {
          return true;
        }
      }
    }
    return false;
  }
}
