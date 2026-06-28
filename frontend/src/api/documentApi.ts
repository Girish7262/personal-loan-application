import axiosClient from './axiosClient';

export interface DocumentResponse {
  documentId: number;
  loanId: number;
  fileName: string;
  fileSize: number;
  fileType: string;
  documentType: string;
  downloadUrl: string;
  createdAt: string;
}

export const documentApi = {
  uploadDocument: async (file: File, loanId: number, documentType: string): Promise<DocumentResponse> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('loanId', loanId.toString());
    formData.append('documentType', documentType);

    const response = await axiosClient.post('/api/v1/documents/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data.data;
  },

  getLoanDocuments: async (loanId: number): Promise<DocumentResponse[]> => {
    const response = await axiosClient.get(`/api/v1/documents/loan/${loanId}`);
    return response.data.data;
  },

  deleteDocument: async (documentId: number): Promise<void> => {
    await axiosClient.delete(`/api/v1/documents/${documentId}`);
  },
};
