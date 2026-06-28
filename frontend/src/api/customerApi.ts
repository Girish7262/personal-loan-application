import axiosClient from './axiosClient';

export interface CustomerProfileDTO {
  firstName: string;
  lastName: string;
  dob: string;
  panNumber: string;
  aadhaarNumber: string;
  monthlyIncome: number;
  employmentType: string;
  address: string;
}

export const customerApi = {
  getProfile: async (): Promise<CustomerProfileDTO> => {
    const response = await axiosClient.get('/api/v1/customer/profile');
    return response.data.data;
  },

  createProfile: async (profile: CustomerProfileDTO): Promise<CustomerProfileDTO> => {
    const response = await axiosClient.post('/api/v1/customer/profile', profile);
    return response.data.data;
  },

  updateProfile: async (profile: Partial<CustomerProfileDTO>): Promise<CustomerProfileDTO> => {
    const response = await axiosClient.put('/api/v1/customer/profile', profile);
    return response.data.data;
  },
};
