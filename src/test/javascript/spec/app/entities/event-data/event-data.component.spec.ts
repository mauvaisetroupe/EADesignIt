/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';

import * as config from '@/shared/config/config';
import EventDataComponent from '@/entities/event-data/event-data.vue';
import EventDataClass from '@/entities/event-data/event-data.component';
import EventDataService from '@/entities/event-data/event-data.service';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('b-badge', {});
localVue.directive('b-modal', {});
localVue.component('b-button', {});
localVue.component('router-link', {});

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  describe('EventData Management Component', () => {
    let wrapper: Wrapper<EventDataClass>;
    let comp: EventDataClass;
    let eventDataServiceStub: SinonStubbedInstance<EventDataService>;

    beforeEach(() => {
      eventDataServiceStub = sinon.createStubInstance<EventDataService>(EventDataService);
      eventDataServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<EventDataClass>(EventDataComponent, {
        store,
        localVue,
        stubs: { bModal: bModalStub as any },
        provide: {
          eventDataService: () => eventDataServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      eventDataServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllEventDatas();
      await comp.$nextTick();

      // THEN
      expect(eventDataServiceStub.retrieve.called).toBeTruthy();
      expect(comp.eventData[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      eventDataServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      comp.removeEventData();
      await comp.$nextTick();

      // THEN
      expect(eventDataServiceStub.delete.called).toBeTruthy();
      expect(eventDataServiceStub.retrieve.callCount).toEqual(1);
    });
  });
});
