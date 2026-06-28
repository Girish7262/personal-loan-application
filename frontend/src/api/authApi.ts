import axiosClient from './axiosClient';
import { LoginCredentials, RegisterCredentials, AuthResponse } from '@/types/auth';

export const authApi = {
  login: async (credentials: LoginCredentials): Promise<AuthResponse> => {
    const response = await axiosClient.post('/api/v1/auth/login', credentials);
    return response.data;
  },

  register: async (credentials: RegisterCredentials): Promise<AuthResponse> => {
    const response = await axiosClient.post('/api/v1/auth/register', credentials);
    return response.data;
  },

  forgotPassword: async (email: string): Promise<{ message: string }> => {
    const response = await axiosClient.post('/api/v1/auth/forgot-password', { email });
    return response.data;
  },
};
