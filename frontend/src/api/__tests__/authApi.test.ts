import { describe, it, expect, vi } from 'vitest';
import axiosClient from '../axiosClient';
import { authApi } from '../authApi';

// Mock the axios client base instance
vi.mock('../axiosClient', () => {
  return {
    default: {
      post: vi.fn(),
    },
  };
});

describe('authApi client mapping', () => {
  it('sends login POST payload correctly', async () => {
    // Arrange
    const responsePayload = { token: 'MOCK_TOKEN', user: { userId: 100, email: 'girish@gmail.com', role: 'CUSTOMER' } };
    vi.mocked(axiosClient.post).mockResolvedValue({ data: responsePayload });

    // Act
    const result = await authApi.login({ email: 'girish@gmail.com', password: 'Password@123' });

    // Assert
    expect(axiosClient.post).toHaveBeenCalledWith('/api/auth/login', {
      email: 'girish@gmail.com',
      password: 'Password@123',
    });
    expect(result).toEqual(responsePayload);
  });
});
