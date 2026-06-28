// @vitest-environment jsdom
import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import DashboardPage from '../DashboardPage';

Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation((query) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
});

describe('DashboardPage Component', () => {
  it('renders welcome greetings and loan application progress trackers correctly', () => {
    render(<DashboardPage />);

    expect(screen.getByRole('heading', { name: /Welcome back, Girish!/i })).toBeDefined();
    expect(screen.getByText(/Profile Completion/i)).toBeDefined();
    expect(screen.getByText(/Active Loan Requests/i)).toBeDefined();
  });
});
