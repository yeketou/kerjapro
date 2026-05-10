export type TenantPlan = 'STARTER' | 'PROFESSIONAL' | 'ENTERPRISE';
export type TenantStatus = 'ACTIVE' | 'SUSPENDED' | 'CANCELLED';

export interface Tenant {
  id: string;
  slug: string;
  companyName: string;
  email: string;
  phone?: string;
  logoUrl?: string;
  plan: TenantPlan;
  status: TenantStatus;
}

export interface TenantSettings {
  timezone: string;
  currency: string;
  bookingLeadDays: number;
  autoReviewPush: boolean;
}
