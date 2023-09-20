import { Component, Vue, Inject } from 'vue-property-decorator';

import { maxLength } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import FunctionalFlowStepService from '@/entities/functional-flow-step/functional-flow-step.service';
import { IFunctionalFlowStep } from '@/shared/model/functional-flow-step.model';

import OwnerService from '@/entities/owner/owner.service';
import { IOwner } from '@/shared/model/owner.model';

import LandscapeViewService from '@/entities/landscape-view/landscape-view.service';
import { ILandscapeView } from '@/shared/model/landscape-view.model';

import DataFlowService from '@/entities/data-flow/data-flow.service';
import { IDataFlow } from '@/shared/model/data-flow.model';

import { IFunctionalFlow, FunctionalFlow } from '@/shared/model/functional-flow.model';
import FunctionalFlowService from './functional-flow.service';

import SequenceDiagramService from '@/eadesignit/sequence-diagram/import.service';
import ApplicationService from '../application/application.service';
import { IApplication } from '@/shared/model/application.model';

const validations: any = {
  functionalFlow: {
    alias: {},
    description: {
      maxLength: maxLength(1500),
    },
    comment: {
      maxLength: maxLength(1000),
    },
    status: {},
    documentationURL: {
      maxLength: maxLength(500),
    },
    documentationURL2: {
      maxLength: maxLength(500),
    },
    startDate: {},
    endDate: {},
  },
};

@Component({
  validations,
  watch: {
    nbLines: 'onLinesChnaged',
    plantuml: 'plantumlChange',
  },
})
export default class FunctionalFlowUpdate extends Vue {
  @Inject('functionalFlowService') private functionalFlowService: () => FunctionalFlowService;
  @Inject('alertService') private alertService: () => AlertService;

  public functionalFlow: IFunctionalFlow = new FunctionalFlow();

  @Inject('functionalFlowStepService') private functionalFlowStepService: () => FunctionalFlowStepService;

  public functionalFlowSteps: IFunctionalFlowStep[] = [];

  @Inject('ownerService') private ownerService: () => OwnerService;

  public owners: IOwner[] = [];

  @Inject('landscapeViewService') private landscapeViewService: () => LandscapeViewService;

  public landscapeViews: ILandscapeView[] = [];

  @Inject('dataFlowService') private dataFlowService: () => DataFlowService;

  @Inject('sequenceDiagramService') private sequenceDiagramService: () => SequenceDiagramService;

  @Inject('applicationService') private applicationService: () => ApplicationService;
  public applications: string[] = [];

  public dataFlows: IDataFlow[] = [];
  public isSaving = false;
  public currentLanguage = '';
  public plantuml = '';
  public plantUMLImage = '';
  public isFetching = false;
  public importError = '';
  public previewError = '';
  public functionalFlowImport = null;

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.functionalFlowId) {
        vm.retrieveFunctionalFlow(to.params.functionalFlowId);
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

  mounted() {
    this.applicationService()
      .retrieve()
      .then(res => {
        this.applications = res.data.map(appli => appli.name);
      });
  }

  ////////////////////////////////////////////////
  // On load, retrieve
  // - FunctionFlow,
  // - plantUML source fron flowID
  // - plantUML image
  // - table with potential interfaces (should be the same than flow detail)
  /////////////////////////////////////////////////

  // STEP 1 - Retrieve FunctionalFlow
  public retrieveFunctionalFlow(functionalFlowId) {
    this.functionalFlowService()
      .find(functionalFlowId)
      .then(res => {
        this.functionalFlow = res;
        this.getPlantUMLSourceFromFlowId(functionalFlowId);
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  // STEP 2 - Retrieve plantuml source from flow ID
  public getPlantUMLSourceFromFlowId(functionalFlowId) {
    this.functionalFlowService()
      .getPlantUMLSource(functionalFlowId, true, true)
      .then(
        res => {
          this.plantuml = res.data;
          this.isFetching = false;
          this.getPlantUMLImageFromString();
        },
        err => {
          console.log(err);
        }
      );
  }

  // STEP 3 : Retrieve plantuml Image from plantuml source
  public getPlantUMLImageFromString() {
    this.sequenceDiagramService()
      .getPlantUMLFromString(this.plantuml)
      .then(
        res => {
          this.plantUMLImage = res.data;
          this.plantumlModified = false;
          this.isFetching = false;
          this.importPlantuml();
        },
        err => {
          console.log(err);
          this.plantUMLImage = '';
        }
      );
  }

  // STEP 3 : Retrieve interface list from plantuml Source
  public importPlantuml() {
    this.sequenceDiagramService()
      .importPlantuml(this.plantuml)
      .then(
        res => {
          this.functionalFlowImport = res.data;
          this.isFetching = false;
          this.previewError = '';
        },
        err => {
          this.plantUMLImage = '';
          this.functionalFlowImport = {};
          this.previewError = err;
          this.plantumlModified = false;
        }
      );
  }

  public saveImport() {
    // this.getPlantUML();
    // this.sequenceDiagramService()
    //   .saveImport(this.functionalFlow, this.selectedLandscape)
    //   .then(
    //     res => {
    //       this.$router.push({ name: 'FunctionalFlowView', params: { functionalFlowId: res.data.id } });
    //     },
    //     err => {
    //       this.plantUMLImage = '';
    //       this.functionalFlow = '';
    //       this.importError = err;
    //     }
    //   );
  }

  public changeInterface(flowimportLine) {
    if (flowimportLine.selectedInterface && flowimportLine.selectedInterface.protocol) {
      flowimportLine.protocol = flowimportLine.selectedInterface.protocol;
    }
  }

  //////////////////////////////////////////////////
  // SAVE
  //////////////////////////////////////////////////

  public assignLandscape(): ILandscapeView {
    // is landcaspe ID pass as param
    let landscapeToSave: ILandscapeView;
    if (this.$route.query.landscapeViewId) {
      this.landscapeViews.forEach(landscape => {
        console.log(landscape.id + ' ...[' + this.$route.query.landscapeViewId + ']...');
        console.log('----[' + parseInt(this.$route.query.landscapeViewId as string) + ']---');
        if (landscape.id === parseInt(this.$route.query.landscapeViewId as string)) {
          landscapeToSave = landscape;
        }
      });
    }
    return landscapeToSave;
  }

  public save(): void {
    this.isSaving = true;
    if (this.functionalFlow.id) {
      this.functionalFlowService()
        .update(this.functionalFlow)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A FunctionalFlow is updated with identifier ' + param.id;
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
      this.functionalFlowService()
        .create(this.functionalFlow)
        .then(param => {
          const createdFunctionalFlow: IFunctionalFlow = param;
          // add to landscape if exist then save landscape
          const landscapeToSave = this.assignLandscape();
          console.log(landscapeToSave);
          if (landscapeToSave != null) {
            landscapeToSave.flows.push(createdFunctionalFlow);
            console.log('About to save landcsape : ');
            console.log(landscapeToSave);
            this.landscapeViewService()
              .update(landscapeToSave)
              .then(param2 => {
                this.isSaving = false;
                this.$router.go(-1);
                const message = 'A FunctionalFlow is created with identifier ' + param.id + ' for landscape ' + param2.id;
                this.$root.$bvToast.toast(message.toString(), {
                  toaster: 'b-toaster-top-center',
                  title: 'Success',
                  variant: 'success',
                  solid: true,
                  autoHideDelay: 5000,
                });
              });
          } else {
            this.isSaving = false;
            this.$router.go(-1);
            const message = 'A FunctionalFlow is created with identifier ' + param.id;
            this.$root.$bvToast.toast(message.toString(), {
              toaster: 'b-toaster-top-center',
              title: 'Success',
              variant: 'success',
              solid: true,
              autoHideDelay: 5000,
            });
          }
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.functionalFlowStepService()
      .retrieve()
      .then(res => {
        this.functionalFlowSteps = res.data;
      });
    this.ownerService()
      .retrieve()
      .then(res => {
        this.owners = res.data;
      });
    this.landscapeViewService()
      .retrieve()
      .then(res => {
        this.landscapeViews = res.data;
      });
    this.dataFlowService()
      .retrieve()
      .then(res => {
        this.dataFlows = res.data;
      });
  }

  ///////////////////////////////////////////////
  // appliction autocmplete
  ///////////////////////////////////////////////

  public plantumlModified = false;

  public get nbLines() {
    return this.plantuml.split(/\r\n|\r|\n/).length;
  }

  public get lastLine() {
    return this.plantuml.split(/\r\n|\r|\n/).slice(-1)[0];
  }

  public onLinesChnaged() {
    this.getPlantUMLImageFromString();
  }

  public plantumlChange() {
    this.plantumlModified = true;

    //console.log(this.inputSplitted);
    this.selectedIndex = 0;
    this.wordIndex = this.inputSplitted.length - 1;
    this.focus();
  }

  public wordIndex = 0;
  public selectedIndex = 0;
  public searchMatch: string[] = [];
  public clickedChooseItem = false;

  get listToSearch() {
    const standardItems = this.applications;
    return standardItems;
  }

  get currentWord() {
    return this.plantuml.replace(/(\r\n|\n|\r)/gm, ' ').split(' ')[this.wordIndex];
  }

  get inputSplitted() {
    return this.plantuml.replace(/(\r\n|\n|\r)/gm, ' ').split(' ');
  }

  highlightWord(word) {
    const regex = new RegExp('(' + this.currentWord + ')', 'g');
    return word.replace(regex, '<mark>$1</mark>');
  }
  setWord(word) {
    let currentWords = this.plantuml.replace(/(\r\n|\n|\r)/gm, '__br__ ').split(' ');
    currentWords[this.wordIndex] = currentWords[this.wordIndex].replace(this.currentWord, '"' + word + '" ');
    this.wordIndex += 1;
    this.plantuml = currentWords.join(' ').replace(/__br__\s/g, '\n');
  }
  moveDown() {
    if (this.selectedIndex < this.searchMatch.length - 1) {
      this.selectedIndex++;
    }
  }
  moveUp() {
    if (this.selectedIndex !== -1) {
      this.selectedIndex--;
    }
  }
  selectItem(index) {
    this.selectedIndex = index;
    this.chooseItem();
  }
  chooseItem(e) {
    this.clickedChooseItem = true;

    if (this.selectedIndex !== -1 && this.searchMatch.length > 0) {
      if (e) {
        e.preventDefault();
      }
      this.setWord(this.searchMatch[this.selectedIndex]);
      this.selectedIndex = -1;
    }
  }
  focusout(e) {
    setTimeout(() => {
      if (!this.clickedChooseItem) {
        this.searchMatch = [];
        this.selectedIndex = -1;
      }
      this.clickedChooseItem = false;
    }, 100);
  }
  focus() {
    this.searchMatch = [];
    if (this.lastLine.includes(':')) {
      this.searchMatch = [];
    } else if (!this.currentWord || this.currentWord === '') {
      console.log(this.currentWord);
      this.searchMatch = [];
    } else if (this.currentWord.length > 2) {
      console.log(this.currentWord);
      this.searchMatch = this.listToSearch.filter(el => el.toLowerCase().indexOf(this.currentWord.toLowerCase()) >= 0);
    } else if (this.searchMatch.length === 1 && this.currentWord === this.searchMatch[0]) {
      this.searchMatch = [];
    }
  }
}
