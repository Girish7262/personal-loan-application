import { createTheme } from '@mui/material/styles';

export const lightTheme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#0B2240', // Deep Banking Blue
      light: '#1B3B6F',
      dark: '#051329',
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: '#C5A880', // Premium Gold Accent
      light: '#DCC39F',
      dark: '#A3845C',
      contrastText: '#0B2240',
    },
    background: {
      default: '#F8FAFC',
      paper: '#FFFFFF',
    },
    text: {
      primary: '#0F172A',
      secondary: '#64748B',
    },
    success: {
      main: '#10B981', // Emerald green
    },
    error: {
      main: '#EF4444', // Ruby red
    },
    warning: {
      main: '#F59E0B', // Warm amber
    },
    info: {
      main: '#3B82F6', // Cobalt blue
    },
  },
  typography: {
    fontFamily: '"Inter", "Poppins", "Roboto", "Helvetica", sans-serif',
    h1: { fontWeight: 800, fontSize: '2.5rem', letterSpacing: '-0.02em', lineHeight: 1.2 },
    h2: { fontWeight: 700, fontSize: '1.875rem', letterSpacing: '-0.01em', lineHeight: 1.3 },
    h3: { fontWeight: 700, fontSize: '1.5rem', letterSpacing: '-0.01em', lineHeight: 1.4 },
    h4: { fontWeight: 600, fontSize: '1.25rem', lineHeight: 1.4 },
    h5: { fontWeight: 600, fontSize: '1.0625rem', lineHeight: 1.5 },
    h6: { fontWeight: 600, fontSize: '0.9375rem', lineHeight: 1.5 },
    body1: { fontSize: '1rem', lineHeight: 1.6 },
    body2: { fontSize: '0.875rem', lineHeight: 1.6 },
    button: { textTransform: 'none', fontWeight: 600, fontSize: '0.875rem' },
  },
  shape: {
    borderRadius: 12,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          padding: '10px 20px',
          boxShadow: 'none',
          transition: 'all 0.2s ease-in-out',
          '&:hover': {
            boxShadow: '0 4px 12px rgba(11, 34, 64, 0.15)',
            transform: 'translateY(-1px)',
          },
        },
        containedPrimary: {
          background: 'linear-gradient(135deg, #0B2240 0%, #1B3B6F 100%)',
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          boxShadow: '0 4px 20px -2px rgba(15, 23, 42, 0.05), 0 2px 6px -1px rgba(15, 23, 42, 0.03)',
          border: '1px solid rgba(226, 232, 240, 0.8)',
          background: '#FFFFFF',
          transition: 'all 0.25s ease-in-out',
          '&:hover': {
            boxShadow: '0 12px 30px -4px rgba(15, 23, 42, 0.08), 0 4px 12px -2px rgba(15, 23, 42, 0.04)',
          },
        },
      },
    },
  },
});

export const darkTheme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#3B82F6',
      light: '#60A5FA',
      dark: '#1D4ED8',
      contrastText: '#0F172A',
    },
    secondary: {
      main: '#DCC39F',
      contrastText: '#0B2240',
    },
    background: {
      default: '#0B1329',
      paper: '#111D3B',
    },
    text: {
      primary: '#F8FAFC',
      secondary: '#94A3B8',
    },
  },
  typography: {
    fontFamily: '"Inter", "Poppins", "Roboto", sans-serif',
  },
  shape: {
    borderRadius: 12,
  },
});

export default lightTheme;
