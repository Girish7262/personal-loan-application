import React from 'react';
import { Box, Container, Grid, Typography, Stack, Link, Divider, IconButton } from '@mui/material';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import SecurityIcon from '@mui/icons-material/Security';
import FacebookIcon from '@mui/icons-material/Facebook';
import TwitterIcon from '@mui/icons-material/Twitter';
import LinkedInIcon from '@mui/icons-material/LinkedIn';
import InstagramIcon from '@mui/icons-material/Instagram';
import PhoneIcon from '@mui/icons-material/Phone';
import EmailIcon from '@mui/icons-material/Email';
import RoomIcon from '@mui/icons-material/Room';

const Footer: React.FC = () => {
  return (
    <Box
      id="footer"
      component="footer"
      sx={{
        py: 8,
        backgroundColor: '#050D1A', // Darker institutional blue
        color: '#94A3B8',
        borderTop: '1px solid rgba(255,255,255,0.06)',
      }}
    >
      <Container maxWidth="lg">
        <Grid container spacing={4} sx={{ mb: 6 }}>
          {/* Brand Info */}
          <Grid item xs={12} md={4}>
            <Stack direction="row" alignItems="center" spacing={1.5} sx={{ color: '#FFFFFF', mb: 2.5 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', p: 0.8, borderRadius: 2, backgroundColor: 'rgba(212, 175, 55, 0.1)', border: '1px solid rgba(212, 175, 55, 0.2)' }}>
                <AccountBalanceIcon sx={{ fontSize: 20, color: '#D4AF37' }} />
              </Box>
              <Typography variant="h5" sx={{ fontWeight: 800, fontFamily: '"Poppins", sans-serif', letterSpacing: '0.02em', display: 'flex', alignItems: 'center' }}>
                APEX
                <Box component="span" sx={{ color: '#D4AF37', fontWeight: 300, ml: 0.5 }}>
                  LOAN
                </Box>
              </Typography>
            </Stack>
            <Typography variant="body2" sx={{ mb: 3, lineHeight: 1.7, color: '#94A3B8' }}>
              Apex Loan is a premium digital lending platform offering customized, instant financial solutions with transparent terms, low interest rates, and bank-grade security.
            </Typography>
            <Stack direction="row" spacing={1}>
              <IconButton size="small" sx={{ color: '#94A3B8', '&:hover': { color: '#D4AF37', backgroundColor: 'rgba(212,175,55,0.05)' } }}>
                <FacebookIcon fontSize="small" />
              </IconButton>
              <IconButton size="small" sx={{ color: '#94A3B8', '&:hover': { color: '#D4AF37', backgroundColor: 'rgba(212,175,55,0.05)' } }}>
                <TwitterIcon fontSize="small" />
              </IconButton>
              <IconButton size="small" sx={{ color: '#94A3B8', '&:hover': { color: '#D4AF37', backgroundColor: 'rgba(212,175,55,0.05)' } }}>
                <LinkedInIcon fontSize="small" />
              </IconButton>
              <IconButton size="small" sx={{ color: '#94A3B8', '&:hover': { color: '#D4AF37', backgroundColor: 'rgba(212,175,55,0.05)' } }}>
                <InstagramIcon fontSize="small" />
              </IconButton>
            </Stack>
          </Grid>

          {/* Products Column */}
          <Grid item xs={6} md={2.5}>
            <Typography variant="subtitle2" sx={{ color: '#FFFFFF', fontWeight: 700, mb: 2.5, letterSpacing: '0.05em', textTransform: 'uppercase', fontSize: '0.8rem' }}>
              Loan Products
            </Typography>
            <Stack spacing={1.8} sx={{ fontSize: '0.875rem' }}>
              <Link href="#" color="inherit" underline="none" sx={{ transition: 'color 0.2s', '&:hover': { color: '#D4AF37' } }}>Personal Loans</Link>
              <Link href="#" color="inherit" underline="none" sx={{ transition: 'color 0.2s', '&:hover': { color: '#D4AF37' } }}>Business Loans</Link>
              <Link href="#" color="inherit" underline="none" sx={{ transition: 'color 0.2s', '&:hover': { color: '#D4AF37' } }}>Home Loans</Link>
              <Link href="#" color="inherit" underline="none" sx={{ transition: 'color 0.2s', '&:hover': { color: '#D4AF37' } }}>Education Loans</Link>
            </Stack>
          </Grid>

          {/* Calculators Column */}
          <Grid item xs={6} md={2.5}>
            <Typography variant="subtitle2" sx={{ color: '#FFFFFF', fontWeight: 700, mb: 2.5, letterSpacing: '0.05em', textTransform: 'uppercase', fontSize: '0.8rem' }}>
              Calculators
            </Typography>
            <Stack spacing={1.8} sx={{ fontSize: '0.875rem' }}>
              <Link href="#" color="inherit" underline="none" sx={{ transition: 'color 0.2s', '&:hover': { color: '#D4AF37' } }}>EMI Calculator</Link>
              <Link href="#" color="inherit" underline="none" sx={{ transition: 'color 0.2s', '&:hover': { color: '#D4AF37' } }}>Eligibility Calculator</Link>
              <Link href="#" color="inherit" underline="none" sx={{ transition: 'color 0.2s', '&:hover': { color: '#D4AF37' } }}>Interest Rates Checker</Link>
            </Stack>
          </Grid>

          {/* Contact / Support Column */}
          <Grid item xs={12} md={3}>
            <Typography variant="subtitle2" sx={{ color: '#FFFFFF', fontWeight: 700, mb: 2.5, letterSpacing: '0.05em', textTransform: 'uppercase', fontSize: '0.8rem' }}>
              Contact Support
            </Typography>
            <Stack spacing={2} sx={{ fontSize: '0.875rem', color: '#94A3B8' }}>
              <Stack direction="row" spacing={1.5} alignItems="flex-start">
                <RoomIcon sx={{ color: '#D4AF37', fontSize: 20, mt: 0.2 }} />
                <Typography variant="body2" sx={{ color: 'inherit' }}>
                  Corporate Office, BKC, Bandra East, Mumbai, Maharashtra 400051
                </Typography>
              </Stack>
              <Stack direction="row" spacing={1.5} alignItems="center">
                <PhoneIcon sx={{ color: '#D4AF37', fontSize: 18 }} />
                <Typography variant="body2" sx={{ color: 'inherit' }}>
                  1800-123-4567 (Toll-Free)
                </Typography>
              </Stack>
              <Stack direction="row" spacing={1.5} alignItems="center">
                <EmailIcon sx={{ color: '#D4AF37', fontSize: 18 }} />
                <Link href="mailto:support@apexloan.com" color="inherit" underline="none" sx={{ transition: 'color 0.2s', '&:hover': { color: '#D4AF37' } }}>
                  support@apexloan.com
                </Link>
              </Stack>
            </Stack>
          </Grid>
        </Grid>

        <Divider sx={{ borderColor: 'rgba(255,255,255,0.06)', mb: 4 }} />

        {/* Banking trust badges */}
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={3} alignItems="center" sx={{ mb: 4, flexWrap: 'wrap', justifyContent: 'center' }}>
          <Box sx={{ border: '1px solid rgba(255,255,255,0.08)', borderRadius: 2, px: 2.5, py: 1, display: 'flex', alignItems: 'center', gap: 1.5, backgroundColor: 'rgba(255,255,255,0.02)' }}>
            <Typography variant="caption" sx={{ fontWeight: 800, color: '#D4AF37', letterSpacing: 1, fontSize: '0.75rem' }}>RBI</Typography>
            <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.65rem', fontWeight: 600 }}>REGULATED ENTITY</Typography>
          </Box>
          <Box sx={{ border: '1px solid rgba(255,255,255,0.08)', borderRadius: 2, px: 2.5, py: 1, display: 'flex', alignItems: 'center', gap: 1.5, backgroundColor: 'rgba(255,255,255,0.02)' }}>
            <SecurityIcon sx={{ color: '#D4AF37', fontSize: 16 }} />
            <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.65rem', fontWeight: 600 }}>AES-256 BIT ENCRYPTED</Typography>
          </Box>
          <Box sx={{ border: '1px solid rgba(255,255,255,0.08)', borderRadius: 2, px: 2.5, py: 1, display: 'flex', alignItems: 'center', gap: 1.5, backgroundColor: 'rgba(255,255,255,0.02)' }}>
            <Typography variant="caption" sx={{ fontWeight: 800, color: '#16A34A', fontSize: '0.75rem' }}>SSL</Typography>
            <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.65rem', fontWeight: 600 }}>SECURED CONNECTION</Typography>
          </Box>
          <Box sx={{ border: '1px solid rgba(255,255,255,0.08)', borderRadius: 2, px: 2.5, py: 1, display: 'flex', alignItems: 'center', gap: 1.5, backgroundColor: 'rgba(255,255,255,0.02)' }}>
            <Typography variant="caption" sx={{ fontWeight: 800, color: '#0284C7', fontSize: '0.75rem' }}>ISO 27001</Typography>
            <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.65rem', fontWeight: 600 }}>INFORMATION SECURITY</Typography>
          </Box>
        </Stack>

        <Box sx={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'center', pt: 2, borderTop: '1px solid rgba(255,255,255,0.04)' }}>
          <Typography variant="caption" sx={{ color: '#64748B' }}>
            &copy; {new Date().getFullYear()} Apex Loan System (APEXLOAN Corporate Bank). All rights reserved.
          </Typography>
          <Typography variant="caption" sx={{ mt: { xs: 1.5, sm: 0 }, color: '#64748B', display: 'block' }}>
            Subject to credit assessment parameters, underwriting policies and KYC verification guidelines.
          </Typography>
        </Box>
      </Container>
    </Box>
  );
};

export default Footer;
