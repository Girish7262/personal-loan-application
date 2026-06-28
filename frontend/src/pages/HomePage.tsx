import React from 'react';
import { Box, Container, Grid, Typography, Button, Stack, Accordion, AccordionSummary, AccordionDetails } from '@mui/material';
import { ExpandMore, Security, FlashOn, VerifiedUser, CloudUpload } from '@mui/icons-material';
import EmiCalculatorWidget from '@/components/ui/EmiCalculatorWidget';
import EligibilityCheckerWidget from '@/components/ui/EligibilityCheckerWidget';
import LoanTimeline from '@/components/ui/LoanTimeline';
import GlassCard from '@/components/ui/GlassCard';
import { Link as RouterLink } from 'react-router-dom';

const HomePage: React.FC = () => {
  return (
    <Box sx={{ backgroundColor: '#F8FAFC', minHeight: '100vh', pb: 10 }}>
      {/* Hero Section */}
      <Box
        sx={{
          background: 'radial-gradient(circle at 10% 20%, rgba(11, 34, 64, 1) 0%, rgba(15, 45, 80, 1) 90.1%)',
          color: '#FFFFFF',
          pt: { xs: 8, md: 12 },
          pb: { xs: 10, md: 16 },
          clipPath: 'ellipse(140% 100% at 50% 0%)',
          mb: -6,
        }}
      >
        <Container maxWidth="lg">
          <Grid container spacing={6} alignItems="center">
            <Grid item xs={12} md={7}>
              <Typography variant="h1" gutterBottom sx={{ fontWeight: 800 }}>
                Instant Personal Loans. <br />
                <Box component="span" sx={{ color: 'secondary.main' }}>
                  Decisions in Minutes.
                </Box>
              </Typography>
              <Typography variant="h5" sx={{ mb: 4, opacity: 0.85, fontWeight: 400, lineHeight: 1.6 }}>
                Experience India's most secure digital loan portal. Up to ₹50 Lakhs with minimum paperwork, end-to-end encryption, and transparent approvals.
              </Typography>
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                <Button
                  component={RouterLink}
                  to="/register"
                  variant="contained"
                  color="secondary"
                  size="large"
                  sx={{ py: 1.8, px: 4, fontSize: '1rem', color: '#0B2240' }}
                >
                  Apply Now
                </Button>
                <Button
                  component={RouterLink}
                  to="/login"
                  variant="outlined"
                  size="large"
                  sx={{
                    py: 1.8,
                    px: 4,
                    fontSize: '1rem',
                    color: '#FFFFFF',
                    borderColor: 'rgba(255, 255, 255, 0.4)',
                    '&:hover': { borderColor: '#FFFFFF', backgroundColor: 'rgba(255, 255, 255, 0.05)' },
                  }}
                >
                  Member Login
                </Button>
              </Stack>
            </Grid>
          </Grid>
        </Container>
      </Box>

      {/* Reusable Process Timeline */}
      <Container maxWidth="lg" sx={{ mb: 10, position: 'relative', zIndex: 2 }}>
        <GlassCard sx={{ p: 4 }}>
          <Typography variant="h3" color="primary" align="center" gutterBottom sx={{ fontWeight: 800 }}>
            Simplified 4-Step Process
          </Typography>
          <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 6 }}>
            Track your loan application journey in real-time from draft to final bank disbursement.
          </Typography>
          <LoanTimeline currentStatus="SUBMITTED" />
        </GlassCard>
      </Container>

      {/* Feature Cards Section */}
      <Container maxWidth="lg" sx={{ mb: 10 }}>
        <Typography variant="h2" color="primary" align="center" gutterBottom sx={{ fontWeight: 800 }}>
          Why Choose Digital Loans?
        </Typography>
        <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 6 }}>
          Designed for maximum security, speed, and complete transparent tracking.
        </Typography>

        <Grid container spacing={4}>
          <Grid item xs={12} sm={6} md={3}>
            <GlassCard sx={{ p: 3, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(197,168,128,0.1)', color: 'secondary.dark', mb: 2 }}>
                <FlashOn />
              </Box>
              <Typography variant="h5" sx={{ mb: 1, fontWeight: 700 }}>Instant Decisions</Typography>
              <Typography variant="body2" color="text.secondary">
                Automated eligibility matching checks evaluate your limits in real-time.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <GlassCard sx={{ p: 3, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(11,34,64,0.05)', color: 'primary.main', mb: 2 }}>
                <Security />
              </Box>
              <Typography variant="h5" sx={{ mb: 1, fontWeight: 700 }}>AES-256 Encryption</Typography>
              <Typography variant="body2" color="text.secondary">
                Aadhaar and PAN details are encrypted at-rest using military-grade security.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <GlassCard sx={{ p: 3, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(16,185,129,0.1)', color: 'success.main', mb: 2 }}>
                <VerifiedUser />
              </Box>
              <Typography variant="h5" sx={{ mb: 1, fontWeight: 700 }}>Maker-Checker flow</Typography>
              <Typography variant="body2" color="text.secondary">
                Multi-level approval verification safeguards checks and compliance audits.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <GlassCard sx={{ p: 3, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(59,130,246,0.1)', color: 'info.main', mb: 2 }}>
                <CloudUpload />
              </Box>
              <Typography variant="h5" sx={{ mb: 1, fontWeight: 700 }}>Paperless Uploads</Typography>
              <Typography variant="body2" color="text.secondary">
                Upload PDFs, PNGs, and JPEGs. Instant duplicate files checking using SHA-256.
              </Typography>
            </GlassCard>
          </Grid>
        </Grid>
      </Container>

      {/* Interactive Widgets Section */}
      <Container maxWidth="lg" sx={{ mb: 10 }}>
        <Grid container spacing={4}>
          <Grid item xs={12} md={6}>
            <EligibilityCheckerWidget />
          </Grid>
          <Grid item xs={12} md={6}>
            <EmiCalculatorWidget />
          </Grid>
        </Grid>
      </Container>

      {/* FAQ Section */}
      <Container maxWidth="md">
        <Typography variant="h2" color="primary" align="center" gutterBottom sx={{ fontWeight: 800 }}>
          Frequently Asked Questions
        </Typography>
        <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 6 }}>
          Quick answers to clarify security questions, limits, and timelines.
        </Typography>

        <Accordion sx={{ mb: 2, borderRadius: '8px !important' }}>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography sx={{ fontWeight: 600 }}>What documents are required to apply?</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Typography color="text.secondary">
              You only need your Aadhaar Card (PDF/Image) and PAN Card. The details are encrypted at-rest and masked on visual portals to guarantee privacy.
            </Typography>
          </AccordionDetails>
        </Accordion>

        <Accordion sx={{ mb: 2, borderRadius: '8px !important' }}>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography sx={{ fontWeight: 600 }}>How long does the approval take?</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Typography color="text.secondary">
              Once submitted, Maker Officers verify documents, Credit Checkers approve values, and Finance Officers disburse funds—all within 24 to 48 business hours.
            </Typography>
          </AccordionDetails>
        </Accordion>

        <Accordion sx={{ mb: 2, borderRadius: '8px !important' }}>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography sx={{ fontWeight: 600 }}>Is my data safe on this portal?</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Typography color="text.secondary">
              Absolutely. We enforce AES-256-GCM field encryption on identity fields. Additionally, document checksums prevent files tampering, and virus scan guards block malicious uploads.
            </Typography>
          </AccordionDetails>
        </Accordion>
      </Container>
    </Box>
  );
};

export default HomePage;
