import apiClient from './apiClient';
import { Project, WorkPackage } from '../types';

export const projectService = {

  getProjects: (params?: { status?: string; page?: number; size?: number }) =>
    apiClient.get('/api/projects', { params }).then(r => r.data),

  getProject: (projectId: string): Promise<Project> =>
    apiClient.get(`/api/projects/${projectId}`).then(r => r.data),

  createProject: (data: Partial<Project>): Promise<Project> =>
    apiClient.post('/api/projects', data).then(r => r.data),

  updateProject: (projectId: string, data: Partial<Project>): Promise<Project> =>
    apiClient.patch(`/api/projects/${projectId}`, data).then(r => r.data),

  activateProject: (projectId: string): Promise<Project> =>
    apiClient.post(`/api/projects/${projectId}/activate`).then(r => r.data),

  cancelProject: (projectId: string): Promise<Project> =>
    apiClient.post(`/api/projects/${projectId}/cancel`).then(r => r.data),

  getWorkPackages: (projectId: string): Promise<WorkPackage[]> =>
    apiClient.get(`/api/projects/${projectId}/packages`).then(r => r.data),

  addWorkPackage: (projectId: string, data: Partial<WorkPackage>): Promise<WorkPackage> =>
    apiClient.post(`/api/projects/${projectId}/packages`, data).then(r => r.data),

  assignSubcontractor: (projectId: string, packageId: string, subProfileId: string) =>
    apiClient.post(`/api/projects/${projectId}/packages/${packageId}/assign/${subProfileId}`)
      .then(r => r.data),
};
