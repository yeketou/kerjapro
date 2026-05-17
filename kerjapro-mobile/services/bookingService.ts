import apiClient from './apiClient';
import { Booking } from '../types';

export interface CreateBookingData {
  subProfileId: string;
  workPackageId?: string;
  appointmentAt: string;
  durationMinutes?: number;
  location?: string;
  notes?: string;
}

export interface CalendarSlot {
  bookingId: string;
  appointmentAt: string;
  appointmentEnd: string;
  durationMinutes: number;
  status: string;
  location?: string;
}

export const bookingService = {

  createBooking: (data: CreateBookingData): Promise<Booking> =>
    apiClient.post('/api/bookings', data).then(r => r.data),

  getBooking: (bookingId: string): Promise<Booking> =>
    apiClient.get(`/api/bookings/${bookingId}`).then(r => r.data),

  getOutgoing: (params?: { status?: string; page?: number; size?: number }) =>
    apiClient.get('/api/bookings/my/outgoing', { params }).then(r => r.data),

  getIncoming: (subProfileId: string, params?: { status?: string; page?: number }) =>
    apiClient.get(`/api/bookings/my/incoming/${subProfileId}`, { params }).then(r => r.data),

  confirm: (bookingId: string): Promise<Booking> =>
    apiClient.post(`/api/bookings/${bookingId}/confirm`).then(r => r.data),

  decline: (bookingId: string, reason?: string): Promise<Booking> =>
    apiClient.post(`/api/bookings/${bookingId}/decline`, { reason }).then(r => r.data),

  complete: (bookingId: string): Promise<Booking> =>
    apiClient.post(`/api/bookings/${bookingId}/complete`).then(r => r.data),

  cancel: (bookingId: string, reason?: string): Promise<Booking> =>
    apiClient.post(`/api/bookings/${bookingId}/cancel`, { reason }).then(r => r.data),

  getCalendar: (subProfileId: string, from: string, to: string): Promise<CalendarSlot[]> =>
    apiClient.get(`/api/bookings/calendar/${subProfileId}`, { params: { from, to } }).then(r => r.data),
};
