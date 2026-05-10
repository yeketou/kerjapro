export type Role = 'MAIN_CONTRACTOR' | 'SUBCONTRACTOR' | 'PLATFORM_ADMIN';

export type SubscriptionTier = 'FREE' | 'PRO' | 'PREMIUM';

export type TradeCategory =
  | 'PLUMBING' | 'ELECTRICAL' | 'TILING' | 'CARPENTRY'
  | 'PAINTING' | 'ROOFING' | 'HVAC' | 'LANDSCAPING'
  | 'FLOORING' | 'WATERPROOFING' | 'ALUMINUM_WORKS'
  | 'GLASS_WORKS' | 'RENOVATION' | 'BATHROOM' | 'KITCHEN'
  | 'CIVIL_WORKS' | 'OTHER';

export type BookingStatus = 'PENDING' | 'CONFIRMED' | 'DECLINED' | 'COMPLETED' | 'CANCELLED';

export type ProjectStatus = 'DRAFT' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';

export type WorkPackageStatus = 'OPEN' | 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export interface SubcontractorProfile {
  id: string;
  businessName: string;
  displayName: string;
  bio: string;
  phone: string;
  email: string;
  city: string;
  state: string;
  profilePhotoUrl?: string;
  cidbGrade?: string;
  subscriptionTier: SubscriptionTier;
  verified: boolean;
  available: boolean;
  averageRating?: string; // NUMERIC(3,2) from backend — use parseFloat() when displaying
  totalReviews: number;
  totalCompletedJobs: number;
  tradeSpecializations: TradeSpecialization[];
  brandCertifications: BrandCertification[];
  portfolio: PortfolioItem[];
}

export interface TradeSpecialization {
  id: string;
  tradeCategory: TradeCategory;
  yearsExperience?: number;
  description?: string;
}

export interface BrandCertification {
  id: string;
  brandName: string;
  certificationName: string;
  certificationNo?: string;
  issuedDate?: string;
  expiryDate?: string;
  certificateUrl?: string;
  verified: boolean;
}

export interface PortfolioItem {
  id: string;
  projectTitle: string;
  projectDescription?: string;
  location?: string;
  completedDate?: string;
  photoUrls: string[];
  brandUsed?: string;
}

export interface Project {
  id: string;
  title: string;
  description?: string;
  location?: string;
  startDate?: string;
  endDate?: string;
  status: ProjectStatus;
  workPackages: WorkPackage[];
}

export interface WorkPackage {
  id: string;
  projectId: string;
  title: string;
  tradeCategory: TradeCategory;
  description?: string;
  requiredBrand?: string;
  budgetMin?: number;
  budgetMax?: number;
  startDate?: string;
  endDate?: string;
  status: WorkPackageStatus;
  assignedSub?: SubcontractorProfile;
}

export interface Booking {
  id: string;
  workPackageId?: string;
  mainContractorId: string;
  subProfile: SubcontractorProfile;
  appointmentAt: string;
  durationMinutes: number;
  location?: string;
  notes?: string;
  status: BookingStatus;
}

export interface Review {
  id: string;
  bookingId: string;
  reviewerId: string;
  subProfileId: string;
  overallRating: number;
  workmanship?: number;
  punctuality?: number;
  communication?: number;
  brandKnowledge?: number;
  comment?: string;
  createdAt: string;
}
