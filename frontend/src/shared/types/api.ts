export interface UserResponse {
  id: number;
  email: string;
  firstName: string;
  lastName?: string;
  fullName: string;
  active: boolean;
  roles: string[];
  createdAt: string;
  updatedAt: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  expiresIn: number;
  user: UserResponse;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  statusDescription: string;
  message: string;
  path: string;
  fieldErrors?: Record<string, string>;
  errors?: string[];
}

export interface MovieResponse {
  id: number;
  title: string;
  description: string;
  genre: string;
  director?: string;
  movieType?: string;
  releaseDate: string;
  durationMinutes: number;
  price: number;
  imageUrl?: string;
  available: boolean;
  categories: string[];
  createdAt: string;
  updatedAt: string;
}

export interface RentalResponse {
  id: number;
  userId: number;
  userName: string;
  movieId: number;
  movieTitle: string;
  rentalDate: string;
  dueDate: string;
  returnDate?: string;
  totalPrice: number;
  status: "ACTIVE" | "COMPLETED" | "OVERDUE" | "CANCELLED";
  isOverdue: boolean;
  daysRented: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateRentalRequest {
  movieId: number;
  daysToRent: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface CategoryResponse {
  id: number;
  name: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCategoryRequest {
  name: string;
  description?: string;
}

export interface CreateMovieRequest {
  title: string;
  description?: string;
  genre?: string;
  director?: string;
  movieType?: string;
  releaseDate?: string;
  durationMinutes?: number;
  price: number;
  imageUrl?: string;
  available?: boolean;
}

export interface VideoResponse {
  id: number;
  movieId: number;
  movieTitle: string;
  status: "PENDING" | "PROCESSING" | "READY" | "ERROR";
  statusDisplay: string;
  streamUrl?: string;
  durationSeconds?: number;
  fileSizeBytes?: number;
  errorMessage?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ExternalMovieResponse {
  externalId: string;
  title: string;
  description?: string;
  genre?: string;
  director?: string;
  movieType?: string;
  releaseDate?: string;
  releaseYear?: string;
  durationMinutes?: number;
  imageUrl?: string;
}

