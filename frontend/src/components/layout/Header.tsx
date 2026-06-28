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

  return (
    <AppBar position="sticky" sx={{ backgroundColor: '#0B2E59', boxShadow: '0 4px 20px rgba(0,0,0,0.08)' }}>
      <Container maxWidth="lg">
        <Toolbar disableGutters sx={{ justifyContent: 'space-between' }}>
          {/* Logo Brand */}
          <Stack direction="row" alignItems="center" spacing={1.5} component={RouterLink} to="/" sx={{ textDecoration: 'none', color: '#FFFFFF' }}>
            <AccountBalanceIcon sx={{ fontSize: 28, color: '#D4AF37' }} />
            <Typography variant="h5" sx={{ fontWeight: 800, letterSpacing: '-0.02em' }}>
              APEX<Box component="span" sx={{ color: '#D4AF37' }}>LOAN</Box>
            </Typography>
          </Stack>

          {/* Navigation links (visible on homepage) */}
          <Stack direction="row" spacing={3} sx={{ display: { xs: 'none', md: 'flex' } }}>
            <Link onClick={() => scrollToSection('hero')} sx={{ color: '#FFFFFF', fontWeight: 600, cursor: 'pointer', textDecoration: 'none', '&:hover': { color: '#D4AF37' } }}>
              Personal Loans
            </Link>
            <Link onClick={() => scrollToSection('eligibility')} sx={{ color: '#FFFFFF', fontWeight: 600, cursor: 'pointer', textDecoration: 'none', '&:hover': { color: '#D4AF37' } }}>
              Eligibility
            </Link>
            <Link onClick={() => scrollToSection('emi-calc')} sx={{ color: '#FFFFFF', fontWeight: 600, cursor: 'pointer', textDecoration: 'none', '&:hover': { color: '#D4AF37' } }}>
              EMI Calculator
            </Link>
            <Link onClick={() => scrollToSection('journey')} sx={{ color: '#FFFFFF', fontWeight: 600, cursor: 'pointer', textDecoration: 'none', '&:hover': { color: '#D4AF37' } }}>
              Loan Process
            </Link>
            <Link onClick={() => scrollToSection('faq')} sx={{ color: '#FFFFFF', fontWeight: 600, cursor: 'pointer', textDecoration: 'none', '&:hover': { color: '#D4AF37' } }}>
              FAQs
            </Link>
          </Stack>

          {/* Actions */}
          <Stack direction="row" spacing={2} alignItems="center">
            {token ? (
              <>
                <Button component={RouterLink} to="/dashboard" variant="outlined" sx={{ color: '#FFFFFF', borderColor: 'rgba(255,255,255,0.4)', '&:hover': { borderColor: '#D4AF37', color: '#D4AF37' } }}>
                  Dashboard
                </Button>
                <Button onClick={handleLogout} variant="contained" sx={{ backgroundColor: '#D4AF37', color: '#0B2E59', '&:hover': { backgroundColor: '#E5C158' } }}>
                  Logout
                </Button>
              </>
            ) : (
              <>
                <Button component={RouterLink} to="/login" variant="outlined" sx={{ color: '#FFFFFF', borderColor: 'rgba(255,255,255,0.4)', '&:hover': { borderColor: '#D4AF37', color: '#D4AF37' } }}>
                  Sign In
                </Button>
                <Button component={RouterLink} to="/register" variant="contained" sx={{ backgroundColor: '#D4AF37', color: '#0B2E59', fontWeight: 700, '&:hover': { backgroundColor: '#E5C158' } }}>
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
