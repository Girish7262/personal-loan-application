export interface LoginCredentials {
  email: string;
  password?: string;
}

export interface RegisterCredentials {
  email: string;
  password?: string;
}

export interface UserDTO {
  userId: number;
  email: string;
  role: string;
  status: string;
}

export interface AuthResponse {
  token: string;
  user: UserDTO;
}
