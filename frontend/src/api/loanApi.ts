import axiosClient from './axiosClient';

export interface LoanApplicationRequest {
  loanTypeId: number;
  loanAmount: number;
  loanTenureMonths: number;
  purpose: string;
  monthlyIncome: number;
  existingEmis: number;
}

export interface LoanApplicationResponse {
  loanId: number;
  customerId: number;
  applicationNumber: string;
  loanStatus: string;
  loanAmount: number;
  loanTenureMonths: number;
  interestRate: number;
  purpose: string;
  monthlyIncome: number;
  existingEmis: number;
  emi: number;
  createdAt: string;
}

export interface EligibilityResult {
  eligible: boolean;
  reasons: string[];
  maxEligibleAmount: number;
  recommendedInterestRate: number;
  foir: number;
}

export const loanApi = {
  submitApplication: async (application: LoanApplicationRequest): Promise<LoanApplicationResponse> => {
    const response = await axiosClient.post('/api/v1/loans', application);
    return response.data.data;
  },

  getMyLoans: async (): Promise<LoanApplicationResponse[]> => {
    const response = await axiosClient.get('/api/v1/loans/my');
    return response.data.data;
  },

  getLoanDetails: async (loanId: number): Promise<LoanApplicationResponse> => {
    const response = await axiosClient.get(`/api/v1/loans/${loanId}`);
    return response.data.data;
  },

  checkEligibility: async (params: LoanApplicationRequest): Promise<EligibilityResult> => {
    const response = await axiosClient.get('/api/v1/loans/eligibility', { params });
    return response.data.data;
  },
};
