import React from 'react';
import { Box, Container, Grid, Typography, Stack, Link, Divider } from '@mui/material';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import SecurityIcon from '@mui/icons-material/Security';

const Footer: React.FC = () => {
  return (
    <Box
      component="footer"
      sx={{
        py: 6,
        backgroundColor: '#051833',
        color: '#94A3B8',
        borderTop: '1px solid rgba(255,255,255,0.06)',
      }}
    >
      <Container maxWidth="lg">
        <Grid container spacing={4} sx={{ mb: 4 }}>
          {/* Brand Info */}
          <Grid item xs={12} md={4}>
            <Stack direction="row" alignItems="center" spacing={1.5} sx={{ color: '#FFFFFF', mb: 2 }}>
              <AccountBalanceIcon sx={{ fontSize: 28, color: '#D4AF37' }} />
              <Typography variant="h5" sx={{ fontWeight: 800, letterSpacing: '-0.02em' }}>
                APEX<Box component="span" sx={{ color: '#D4AF37' }}>LOAN</Box>
              </Typography>
            </Stack>
            <Typography variant="body2" sx={{ mb: 2.5, lineHeight: 1.7 }}>
              Apex Loan System is a secure, digitized lending platform providing quick approvals and transparent interest rates for credit needs.
            </Typography>
            <Stack direction="row" alignItems="center" spacing={1} sx={{ color: '#FFFFFF' }}>
              <SecurityIcon sx={{ color: '#D4AF37', fontSize: 20 }} />
              <Typography variant="caption" sx={{ fontWeight: 600 }}>
                Military-Grade AES-256 Secured
              </Typography>
            </Stack>
          </Grid>

          {/* Products Column */}
          <Grid item xs={6} md={2.5}>
            <Typography variant="subtitle2" sx={{ color: '#FFFFFF', fontWeight: 700, mb: 2 }}>
              Loan Products
            </Typography>
            <Stack spacing={1.5} sx={{ fontSize: '0.875rem' }}>
              <Link href="#" color="inherit" underline="none" sx={{ '&:hover': { color: '#D4AF37' } }}>Personal Loans</Link>
              <Link href="#" color="inherit" underline="none" sx={{ '&:hover': { color: '#D4AF37' } }}>Home Loans</Link>
              <Link href="#" color="inherit" underline="none" sx={{ '&:hover': { color: '#D4AF37' } }}>Education Loans</Link>
            </Stack>
          </Grid>

          {/* Calculators Column */}
          <Grid item xs={6} md={2.5}>
            <Typography variant="subtitle2" sx={{ color: '#FFFFFF', fontWeight: 700, mb: 2 }}>
              Calculators
            </Typography>
            <Stack spacing={1.5} sx={{ fontSize: '0.875rem' }}>
              <Link href="#" color="inherit" underline="none" sx={{ '&:hover': { color: '#D4AF37' } }}>EMI Calculator</Link>
              <Link href="#" color="inherit" underline="none" sx={{ '&:hover': { color: '#D4AF37' } }}>Eligibility Calculator</Link>
            </Stack>
          </Grid>

          {/* Legal / Policy Column */}
          <Grid item xs={12} md={3}>
            <Typography variant="subtitle2" sx={{ color: '#FFFFFF', fontWeight: 700, mb: 2 }}>
              Legal & Support
            </Typography>
            <Stack spacing={1.5} sx={{ fontSize: '0.875rem' }}>
              <Link href="#" color="inherit" underline="none" sx={{ '&:hover': { color: '#D4AF37' } }}>Privacy Policy</Link>
              <Link href="#" color="inherit" underline="none" sx={{ '&:hover': { color: '#D4AF37' } }}>Terms of Service</Link>
              <Link href="#" color="inherit" underline="none" sx={{ '&:hover': { color: '#D4AF37' } }}>Customer Support</Link>
            </Stack>
          </Grid>
        </Grid>

        <Divider sx={{ borderColor: 'rgba(255,255,255,0.06)', mb: 3 }} />

        <Box sx={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="caption">
            &copy; {new Date().getFullYear()} Apex Loan System. All rights reserved.
          </Typography>
          <Typography variant="caption" sx={{ mt: { xs: 1, sm: 0 } }}>
            Subject to credit assessment parameters and verification rules.
          </Typography>
        </Box>
      </Container>
    </Box>
  );
};

export default Footer;
