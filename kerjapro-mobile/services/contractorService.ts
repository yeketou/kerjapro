import apiClient from './apiClient';
import { SubcontractorProfile, TradeSpecialization, BrandCertification, PortfolioItem } from '../types';

export interface SearchParams {
  tradeCategory?: string;
  brandName?: string;
  city?: string;
  state?: string;
  minRating?: number;
  verified?: boolean;
  page?: number;
  size?: number;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export const contractorService = {

  search: (params: SearchParams): Promise<PagedResponse<SubcontractorProfile>> =>
    apiClient.get('/api/contractors/search', { params }).then(r => r.data),

  getProfile: (profileId: string): Promise<SubcontractorProfile> =>
    apiClient.get(`/api/contractors/profiles/${profileId}`).then(r => r.data),

  getMyProfile: (): Promise<SubcontractorProfile> =>
    apiClient.get('/api/contractors/profiles/me').then(r => r.data),

  updateProfile: (data: Partial<SubcontractorProfile>): Promise<SubcontractorProfile> =>
    apiClient.patch('/api/contractors/profiles/me', data).then(r => r.data),

  addTrade: (data: { tradeCategory: string; yearsExperience?: number; description?: string }) =>
    apiClient.post('/api/contractors/profiles/me/trades', data).then(r => r.data),

  removeTrade: (tradeId: string) =>
    apiClient.delete(`/api/contractors/profiles/me/trades/${tradeId}`),

  addCertification: (data: Partial<BrandCertification>) =>
    apiClient.post('/api/contractors/profiles/me/certifications', data).then(r => r.data),

  removeCertification: (certId: string) =>
    apiClient.delete(`/api/contractors/profiles/me/certifications/${certId}`),

  addPortfolioItem: (data: Partial<PortfolioItem>) =>
    apiClient.post('/api/contractors/profiles/me/portfolio', data).then(r => r.data),

  removePortfolioItem: (itemId: string) =>
    apiClient.delete(`/api/contractors/profiles/me/portfolio/${itemId}`),

  onboardSubcontractor: (data: any) =>
    apiClient.post('/api/contractors/onboarding/subcontractor', data).then(r => r.data),
};
