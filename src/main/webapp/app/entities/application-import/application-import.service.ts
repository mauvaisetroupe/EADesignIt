import axios from 'axios';

import { type IApplicationImport } from '@/shared/model/application-import.model';

const baseApiUrl = 'api/application-imports';
const apiForImportUrl = 'api/import';
const apiForExportUrl = 'api/export';

export default class ApplicationImportService {
  public find(id: number): Promise<IApplicationImport> {
    return new Promise<IApplicationImport>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public retrieve(): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(baseApiUrl)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public delete(id: number): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .delete(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public create(entity: IApplicationImport): Promise<IApplicationImport> {
    return new Promise<IApplicationImport>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public update(entity: IApplicationImport): Promise<IApplicationImport> {
    return new Promise<IApplicationImport>((resolve, reject) => {
      axios
        .put(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public partialUpdate(entity: IApplicationImport): Promise<IApplicationImport> {
    return new Promise<IApplicationImport>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public uploadFile(file: File): Promise<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    return new Promise<any>((resolve, reject) => {
      axios
        .post(`${apiForImportUrl}/application/upload-file`, formData)
        .then(res => {
          console.log(res);
          resolve(res);
        })
        .catch(err => {
          console.log(err);
          reject(err);
        });
    });
  }

  public downloadFile(): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(`${apiForExportUrl}/applications`, {
          responseType: 'blob',
        })
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
