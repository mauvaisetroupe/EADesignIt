import { defineComponent, inject, ref, type Ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import OwnerService from './owner.service';
import { type IOwner } from '@/shared/model/owner.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OwnerDetails',
  setup() {
    const ownerService = inject('ownerService', () => new OwnerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const owner: Ref<IOwner> = ref({});

    const retrieveOwner = async ownerId => {
      try {
        const res = await ownerService().find(ownerId);
        owner.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.ownerId) {
      retrieveOwner(route.params.ownerId);
    }

    return {
      alertService,
      owner,

      previousState,
    };
  },
});
