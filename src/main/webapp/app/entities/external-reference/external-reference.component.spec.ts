/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import ExternalReference from './external-reference.vue';
import ExternalReferenceService from './external-reference.service';
import AlertService from '@/shared/alert/alert.service';

type ExternalReferenceComponentType = InstanceType<typeof ExternalReference>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('ExternalReference Management Component', () => {
    let externalReferenceServiceStub: SinonStubbedInstance<ExternalReferenceService>;
    let mountOptions: MountingOptions<ExternalReferenceComponentType>['global'];

    beforeEach(() => {
      externalReferenceServiceStub = sinon.createStubInstance<ExternalReferenceService>(ExternalReferenceService);
      externalReferenceServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          externalReferenceService: () => externalReferenceServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        externalReferenceServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(ExternalReference, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(externalReferenceServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.externalReferences[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: ExternalReferenceComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(ExternalReference, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        externalReferenceServiceStub.retrieve.reset();
        externalReferenceServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        externalReferenceServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeExternalReference();
        await comp.$nextTick(); // clear components

        // THEN
        expect(externalReferenceServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(externalReferenceServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
