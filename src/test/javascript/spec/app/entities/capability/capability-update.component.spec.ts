/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import CapabilityUpdateComponent from '@/entities/capability/capability-update.vue';
import CapabilityClass from '@/entities/capability/capability-update.component';
import CapabilityService from '@/entities/capability/capability.service';

import CapabilityApplicationMappingService from '@/entities/capability-application-mapping/capability-application-mapping.service';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.use(ToastPlugin);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('Capability Management Update Component', () => {
    let wrapper: Wrapper<CapabilityClass>;
    let comp: CapabilityClass;
    let capabilityServiceStub: SinonStubbedInstance<CapabilityService>;

    beforeEach(() => {
      capabilityServiceStub = sinon.createStubInstance<CapabilityService>(CapabilityService);

      wrapper = shallowMount<CapabilityClass>(CapabilityUpdateComponent, {
        store,
        localVue,
        router,
        provide: {
          capabilityService: () => capabilityServiceStub,
          alertService: () => new AlertService(),

          capabilityApplicationMappingService: () =>
            sinon.createStubInstance<CapabilityApplicationMappingService>(CapabilityApplicationMappingService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.capability = entity;
        capabilityServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(capabilityServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.capability = entity;
        capabilityServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(capabilityServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundCapability = { id: 123 };
        capabilityServiceStub.find.resolves(foundCapability);
        capabilityServiceStub.retrieve.resolves([foundCapability]);

        // WHEN
        comp.beforeRouteEnter({ params: { capabilityId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.capability).toBe(foundCapability);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
