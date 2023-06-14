/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import ApplicationUpdateComponent from '@/entities/application/application-update.vue';
import ApplicationClass from '@/entities/application/application-update.component';
import ApplicationService from '@/entities/application/application.service';

import OwnerService from '@/entities/owner/owner.service';

import ApplicationCategoryService from '@/entities/application-category/application-category.service';

import TechnologyService from '@/entities/technology/technology.service';

import ExternalReferenceService from '@/entities/external-reference/external-reference.service';

import ApplicationComponentService from '@/entities/application-component/application-component.service';

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
  describe('Application Management Update Component', () => {
    let wrapper: Wrapper<ApplicationClass>;
    let comp: ApplicationClass;
    let applicationServiceStub: SinonStubbedInstance<ApplicationService>;

    beforeEach(() => {
      applicationServiceStub = sinon.createStubInstance<ApplicationService>(ApplicationService);

      wrapper = shallowMount<ApplicationClass>(ApplicationUpdateComponent, {
        store,
        localVue,
        router,
        provide: {
          applicationService: () => applicationServiceStub,
          alertService: () => new AlertService(),

          ownerService: () =>
            sinon.createStubInstance<OwnerService>(OwnerService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

          applicationCategoryService: () =>
            sinon.createStubInstance<ApplicationCategoryService>(ApplicationCategoryService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

          technologyService: () =>
            sinon.createStubInstance<TechnologyService>(TechnologyService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

          externalReferenceService: () =>
            sinon.createStubInstance<ExternalReferenceService>(ExternalReferenceService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

          applicationComponentService: () =>
            sinon.createStubInstance<ApplicationComponentService>(ApplicationComponentService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

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
        comp.application = entity;
        applicationServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(applicationServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.application = entity;
        applicationServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(applicationServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundApplication = { id: 123 };
        applicationServiceStub.find.resolves(foundApplication);
        applicationServiceStub.retrieve.resolves([foundApplication]);

        // WHEN
        comp.beforeRouteEnter({ params: { applicationId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.application).toBe(foundApplication);
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
