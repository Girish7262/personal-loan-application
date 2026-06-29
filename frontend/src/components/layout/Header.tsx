import React from 'react';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import { AppBar, Container, Toolbar, Typography, Button, Stack, Link, Box } from '@mui/material';
import { Link as RouterLink, useNavigate, useLocation } from 'react-router-dom';

const Header: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const token = localStorage.getItem('token');

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  const scrollToSection = (id: string) => {
    if (location.pathname !== '/') {
      navigate('/');
      setTimeout(() => {
        const el = document.getElementById(id);
        if (el) el.scrollIntoView({ behavior: 'smooth' });
      }, 100);
    } else {
      const el = document.getElementById(id);
      if (el) el.scrollIntoView({ behavior: 'smooth' });
    }
  };

  const linkStyle = {
    color: 'rgba(255, 255, 255, 0.75)',
    fontWeight: 600,
    fontSize: '0.9rem',
    cursor: 'pointer',
    textDecoration: 'none',
    position: 'relative',
    transition: 'all 0.2s ease-in-out',
    '&::after': {
      content: '""',
      position: 'absolute',
      width: '0%',
      height: '2.5px',
      bottom: '-6px',
      left: '0',
      backgroundColor: '#D4AF37',
      transition: 'width 0.25s cubic-bezier(0.4, 0, 0.2, 1)',
    },
    '&:hover': {
      color: '#FFFFFF',
      '&::after': {
        width: '100%',
      }
    }
  };

  return (
    <AppBar position="sticky" sx={{ backgroundColor: 'rgba(10, 17, 40, 0.85)', backdropFilter: 'blur(20px)', WebkitBackdropFilter: 'blur(20px)', boxShadow: '0 8px 32px rgba(5, 24, 51, 0.2)', borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
      <Container maxWidth="lg">
        <Toolbar disableGutters sx={{ justifyContent: 'space-between', height: 72 }}>
          {/* Logo Brand */}
          <Stack direction="row" alignItems="center" spacing={1.5} component={RouterLink} to="/" sx={{ textDecoration: 'none' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', p: 0.8, borderRadius: 2, backgroundColor: 'rgba(212, 175, 55, 0.1)', border: '1px solid rgba(212, 175, 55, 0.2)' }}>
              <AccountBalanceIcon sx={{ fontSize: 22, color: '#D4AF37' }} />
            </Box>
            <Typography variant="h5" sx={{ fontWeight: 800, fontFamily: '"Poppins", sans-serif', letterSpacing: '0.02em', display: 'flex', alignItems: 'center', color: '#FFFFFF' }}>
              APEX
              <Box component="span" sx={{ color: '#D4AF37', fontWeight: 300, ml: 0.5 }}>
                LOAN
              </Box>
            </Typography>
          </Stack>

          {/* Navigation links (visible on homepage) */}
          <Stack direction="row" spacing={4} sx={{ display: { xs: 'none', md: 'flex' } }}>
            <Link onClick={() => scrollToSection('hero')} sx={linkStyle}>
              Personal Loans
            </Link>
            <Link onClick={() => scrollToSection('eligibility')} sx={linkStyle}>
              Eligibility
            </Link>
            <Link onClick={() => scrollToSection('emi-calc')} sx={linkStyle}>
              EMI Calculator
            </Link>
            <Link onClick={() => scrollToSection('journey')} sx={linkStyle}>
              Loan Process
            </Link>
            <Link onClick={() => scrollToSection('faq')} sx={linkStyle}>
              FAQs
            </Link>
            <Link onClick={() => scrollToSection('footer')} sx={linkStyle}>
              Contact
            </Link>
          </Stack>

          {/* Actions */}
          <Stack direction="row" spacing={2} alignItems="center">
            {token ? (
              <>
                <Button component={RouterLink} to="/dashboard" variant="outlined" sx={{ color: '#FFFFFF', borderColor: 'rgba(255,255,255,0.3)', borderRadius: 2.5, px: 3, '&:hover': { borderColor: '#D4AF37', color: '#D4AF37', backgroundColor: 'rgba(212, 175, 55, 0.05)' } }}>
                  Dashboard
                </Button>
                <Button onClick={handleLogout} variant="contained" sx={{ backgroundColor: '#D4AF37', color: '#051833', fontWeight: 700, borderRadius: 2.5, px: 3, '&:hover': { backgroundColor: '#E5C158' } }}>
                  Logout
                </Button>
              </>
            ) : (
              <>
                <Button component={RouterLink} to="/login" variant="outlined" sx={{ color: '#FFFFFF', borderColor: 'rgba(255,255,255,0.3)', borderRadius: 2.5, px: 3, '&:hover': { borderColor: '#D4AF37', color: '#D4AF37', backgroundColor: 'rgba(212, 175, 55, 0.05)' } }}>
                  Sign In
                </Button>
                <Button onClick={() => navigate('/?apply=true')} variant="contained" sx={{ backgroundColor: '#D4AF37', color: '#051833', fontWeight: 700, borderRadius: 2.5, px: 3.5, boxShadow: '0 4px 14px rgba(212, 175, 55, 0.25)', '&:hover': { backgroundColor: '#E5C158', boxShadow: '0 6px 20px rgba(212, 175, 55, 0.35)', transform: 'translateY(-1px)' } }}>
                  Apply Now
                </Button>
              </>
            )}
          </Stack>
        </Toolbar>
      </Container>
    </AppBar>
  );
};

export default Header;
