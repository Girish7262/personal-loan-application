import { createTheme } from '@mui/material/styles';

export const lightTheme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#0B2E59', // Deep Banking Blue
      light: '#1E4C7A',
      dark: '#051833',
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: '#1D4ED8', // Royal Blue
      light: '#3B82F6',
      dark: '#1E3A8A',
      contrastText: '#FFFFFF',
    },
    background: {
      default: '#F8FAFC',
      paper: '#FFFFFF',
    },
    text: {
      primary: '#0F172A',
      secondary: '#64748B', // Standard Text Secondary
    },
    success: {
      main: '#16A34A',
    },
    error: {
      main: '#DC2626',
    },
    warning: {
      main: '#D4AF37', // Accent Gold
    },
    info: {
      main: '#0284C7',
    },
  },
  typography: {
    fontFamily: '"Poppins", "Inter", "Roboto", "Helvetica", sans-serif',
    h1: { 
      fontFamily: '"Poppins", "Inter", sans-serif',
      fontWeight: 800, 
      fontSize: '2.75rem', 
      letterSpacing: '-0.02em', 
      lineHeight: 1.2 
    },
    h2: { 
      fontFamily: '"Poppins", "Inter", sans-serif',
      fontWeight: 700, 
      fontSize: '2.25rem', 
      letterSpacing: '-0.015em', 
      lineHeight: 1.3 
    },
    h3: { 
      fontFamily: '"Poppins", "Inter", sans-serif',
      fontWeight: 700, 
      fontSize: '1.75rem', 
      letterSpacing: '-0.01em', 
      lineHeight: 1.4 
    },
    h4: { 
      fontFamily: '"Poppins", "Inter", sans-serif',
      fontWeight: 600, 
      fontSize: '1.45rem', 
      lineHeight: 1.4 
    },
    h5: { 
      fontFamily: '"Poppins", "Inter", sans-serif',
      fontWeight: 600, 
      fontSize: '1.15rem', 
      lineHeight: 1.5 
    },
    h6: { 
      fontFamily: '"Poppins", "Inter", sans-serif',
      fontWeight: 600, 
      fontSize: '0.95rem', 
      lineHeight: 1.5 
    },
    body1: { 
      fontFamily: '"Inter", sans-serif',
      fontSize: '1rem', 
      lineHeight: 1.6 
    },
    body2: { 
      fontFamily: '"Inter", sans-serif',
      fontSize: '0.875rem', 
      lineHeight: 1.6 
    },
    button: { 
      fontFamily: '"Poppins", "Inter", sans-serif',
      textTransform: 'none', 
      fontWeight: 600, 
      fontSize: '0.875rem' 
    },
  },
  shape: {
    borderRadius: 12,
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: `
        html {
          scroll-behavior: smooth;
        }
        ::selection {
          background-color: rgba(212, 175, 55, 0.2);
          color: #0B2E59;
        }
      `,
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          padding: '10px 24px',
          boxShadow: 'none',
          transition: 'all 0.25s cubic-bezier(0.4, 0, 0.2, 1)',
          '&:hover': {
            boxShadow: '0 6px 20px rgba(11, 46, 89, 0.12)',
            transform: 'translateY(-1.5px)',
          },
        },
        containedPrimary: {
          background: 'linear-gradient(135deg, #0B2E59 0%, #1D4ED8 100%)',
          color: '#FFFFFF',
          '&:hover': {
            background: 'linear-gradient(135deg, #051833 0%, #0B2E59 100%)',
            boxShadow: '0 6px 20px rgba(11, 46, 89, 0.22)',
          }
        },
        containedSecondary: {
          backgroundColor: '#D4AF37',
          color: '#051833',
          '&:hover': {
            backgroundColor: '#E5C158',
            boxShadow: '0 6px 20px rgba(212, 175, 55, 0.25)',
          }
        }
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          boxShadow: '0 4px 20px -2px rgba(15, 23, 42, 0.04), 0 2px 6px -1px rgba(15, 23, 42, 0.02)',
          border: '1px solid rgba(226, 232, 240, 0.8)',
          background: '#FFFFFF',
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          '&:hover': {
            boxShadow: '0 20px 40px -4px rgba(15, 23, 42, 0.08), 0 8px 16px -2px rgba(15, 23, 42, 0.04)',
            transform: 'translateY(-3px)',
            borderColor: 'rgba(212, 175, 55, 0.2)',
          },
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 8,
            backgroundColor: 'rgba(248, 250, 252, 0.7)',
            transition: 'all 0.2s',
            '&:hover': {
              backgroundColor: '#FFFFFF',
              '& .MuiOutlinedInput-notchedOutline': {
                borderColor: 'rgba(11, 46, 89, 0.3)',
              },
            },
            '&.Mui-focused': {
              backgroundColor: '#FFFFFF',
              '& .MuiOutlinedInput-notchedOutline': {
                borderWidth: '1.5px',
                borderColor: '#0B2E59',
                boxShadow: '0 0 0 4px rgba(11, 46, 89, 0.06)',
              },
            },
          },
        },
      },
    },
    MuiSlider: {
      styleOverrides: {
        root: {
          color: '#0B2E59',
          height: 6,
          '& .MuiSlider-thumb': {
            width: 20,
            height: 20,
            backgroundColor: '#FFFFFF',
            border: '3px solid #0B2E59',
            boxShadow: '0 4px 10px rgba(0,0,0,0.1)',
            transition: 'all 0.2s',
            '&:hover, &.Mui-focusVisible': {
              boxShadow: '0 0 0 8px rgba(11, 46, 89, 0.1)',
            },
          },
          '& .MuiSlider-track': {
            height: 6,
            borderRadius: 3,
          },
          '& .MuiSlider-rail': {
            height: 6,
            borderRadius: 3,
            backgroundColor: 'rgba(15, 23, 42, 0.08)',
          },
        },
      },
    },
    MuiAccordion: {
      styleOverrides: {
        root: {
          boxShadow: 'none',
          border: '1px solid rgba(226, 232, 240, 0.8)',
          borderRadius: 8,
          '&:before': {
            display: 'none',
          },
          '&.Mui-expanded': {
            margin: '0 0 16px 0',
            boxShadow: '0 12px 24px rgba(15, 23, 42, 0.04)',
            borderColor: 'rgba(11, 46, 89, 0.15)',
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
    fontFamily: '"Poppins", "Inter", "Roboto", sans-serif',
  },
  shape: {
    borderRadius: 12,
  },
});

export default lightTheme;
