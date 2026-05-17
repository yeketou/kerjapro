import apiClient from './apiClient';

export interface CreateReviewData {
  bookingId: string;
  overallRating: number;
  workmanship?: number;
  punctuality?: number;
  communication?: number;
  brandKnowledge?: number;
  comment?: string;
}

export interface Review {
  id: string;
  bookingId: string;
  reviewerId: string;
  subProfileId: string;
  overallRating: string;
  workmanship?: string;
  punctuality?: string;
  communication?: string;
  brandKnowledge?: string;
  comment?: string;
  createdAt: string;
}

export interface RatingSummary {
  subProfileId: string;
  averageRating: string;
  averageWorkmanship?: string;
  averagePunctuality?: string;
  averageCommunication?: string;
  averageBrandKnowledge?: string;
  totalReviews: number;
}

export const reviewService = {

  createReview: (data: CreateReviewData): Promise<Review> =>
    apiClient.post('/api/reviews', data).then(r => r.data),

  getReviewsForSub: (subProfileId: string, page = 0): Promise<{ content: Review[] }> =>
    apiClient.get(`/api/reviews/subcontractor/${subProfileId}`, { params: { page } }).then(r => r.data),

  getRatingSummary: (subProfileId: string): Promise<RatingSummary> =>
    apiClient.get(`/api/reviews/subcontractor/${subProfileId}/summary`).then(r => r.data),

  getMyReviews: (page = 0): Promise<{ content: Review[] }> =>
    apiClient.get('/api/reviews/my', { params: { page } }).then(r => r.data),
};
