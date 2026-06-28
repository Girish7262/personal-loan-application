import React from 'react';
import { Box, Container, Grid, Typography, Button, Stack, Accordion, AccordionSummary, AccordionDetails, Paper, Chip } from '@mui/material';
import { ExpandMore, Security, FlashOn, VerifiedUser, CloudUpload, AccountBalanceWallet, LocalActivity, Stars } from '@mui/icons-material';
import EmiCalculatorWidget from '@/components/ui/EmiCalculatorWidget';
import EligibilityCheckerWidget from '@/components/ui/EligibilityCheckerWidget';
import LoanTimeline from '@/components/ui/LoanTimeline';
import GlassCard from '@/components/ui/GlassCard';
import { Link as RouterLink } from 'react-router-dom';

const HomePage: React.FC = () => {
  const scrollToSection = (id: string) => {
    const el = document.getElementById(id);
    if (el) el.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <Box sx={{ backgroundColor: '#F8FAFC', minHeight: '100vh', pb: 10 }}>
      {/* Hero Section */}
      <Box
        id="hero"
        sx={{
          background: 'radial-gradient(circle at 10% 20%, #0B2E59 0%, #051833 90%)',
          color: '#FFFFFF',
          pt: { xs: 8, md: 12 },
          pb: { xs: 12, md: 18 },
          clipPath: 'ellipse(140% 100% at 50% 0%)',
          position: 'relative',
          overflow: 'hidden',
        }}
      >
        {/* Glow circles behind text */}
        <Box sx={{ position: 'absolute', top: '-10%', right: '5%', width: 400, height: 400, borderRadius: '50%', background: 'radial-gradient(circle, rgba(29,78,216,0.15) 0%, transparent 70%)', filter: 'blur(50px)' }} />
        <Box sx={{ position: 'absolute', bottom: '10%', left: '10%', width: 300, height: 300, borderRadius: '50%', background: 'radial-gradient(circle, rgba(212,175,55,0.08) 0%, transparent 70%)', filter: 'blur(40px)' }} />

        <Container maxWidth="lg" sx={{ position: 'relative', zIndex: 1 }}>
          <Grid container spacing={6} alignItems="center">
            {/* Left side text */}
            <Grid item xs={12} md={7}>
              <Typography variant="h1" gutterBottom sx={{ fontWeight: 800, fontSize: { xs: '2.5rem', md: '3.5rem' } }}>
                Instant Personal Loans. <br />
                <Box component="span" sx={{ color: '#D4AF37' }}>
                  Decisions in Minutes.
                </Box>
              </Typography>
              <Typography variant="h5" sx={{ mb: 4, opacity: 0.9, fontWeight: 400, lineHeight: 1.6, maxWidth: 600 }}>
                Experience India's premier digital loan portal. Access up to ₹50 Lakhs with minimum documentation, complete security, and transparent processing.
              </Typography>
              
              <Typography variant="body2" sx={{ mb: 4, display: 'flex', alignItems: 'center', gap: 1, color: '#D4AF37', fontWeight: 600 }}>
                <Stars fontSize="small" /> Trusted by over 50,000+ satisfied customers nationwide
              </Typography>

              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2.5}>
                <Button
                  component={RouterLink}
                  to="/register"
                  variant="contained"
                  size="large"
                  sx={{
                    py: 2,
                    px: 5,
                    fontSize: '1.05rem',
                    backgroundColor: '#D4AF37',
                    color: '#051833',
                    fontWeight: 700,
                    '&:hover': { backgroundColor: '#E5C158', transform: 'translateY(-2px)' },
                  }}
                >
                  Apply Now
                </Button>
                <Button
                  onClick={() => scrollToSection('eligibility')}
                  variant="outlined"
                  size="large"
                  sx={{
                    py: 2,
                    px: 5,
                    fontSize: '1.05rem',
                    color: '#FFFFFF',
                    borderColor: 'rgba(255, 255, 255, 0.4)',
                    fontWeight: 600,
                    '&:hover': { borderColor: '#D4AF37', color: '#D4AF37', transform: 'translateY(-2px)' },
                  }}
                >
                  Check Eligibility
                </Button>
              </Stack>
            </Grid>

            {/* Right side CSS Illustration */}
            <Grid item xs={12} md={5} sx={{ display: { xs: 'none', md: 'flex' }, justifyContent: 'center' }}>
              <Box sx={{ position: 'relative', width: '100%', height: 360 }}>
                {/* Visual Card 1 */}
                <Paper
                  elevation={6}
                  sx={{
                    position: 'absolute',
                    top: '10%',
                    left: '5%',
                    width: '85%',
                    height: 220,
                    borderRadius: 4,
                    background: 'linear-gradient(135deg, #1E3A8A 0%, #1D4ED8 100%)',
                    color: '#FFFFFF',
                    p: 3.5,
                    boxShadow: '0 20px 40px rgba(0,0,0,0.3)',
                    border: '1px solid rgba(255,255,255,0.1)',
                    zIndex: 2,
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'space-between',
                  }}
                >
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <Box>
                      <Typography variant="body2" sx={{ opacity: 0.7, letterSpacing: 1.5, fontSize: '0.75rem', fontWeight: 700 }}>
                        APEX PRIVILEGE CARD
                      </Typography>
                      <Typography variant="h4" sx={{ fontWeight: 800, mt: 0.5, color: '#D4AF37' }}>
                        ₹50,000,00
                      </Typography>
                    </Box>
                    <Security sx={{ color: '#D4AF37', fontSize: 32 }} />
                  </Box>
                  <Box>
                    <Typography variant="caption" sx={{ display: 'block', opacity: 0.6 }}>APPROVED LIMIT</Typography>
                    <Typography variant="body1" sx={{ fontWeight: 600, letterSpacing: 2, mt: 0.5 }}>**** **** **** 2026</Typography>
                  </Box>
                </Paper>

                {/* Visual Card 2 (behind Card 1) */}
                <Paper
                  elevation={3}
                  sx={{
                    position: 'absolute',
                    top: '25%',
                    left: '20%',
                    width: '80%',
                    height: 200,
                    borderRadius: 4,
                    background: 'rgba(255,255,255,0.1)',
                    backdropFilter: 'blur(10px)',
                    border: '1px solid rgba(255,255,255,0.2)',
                    p: 3,
                    zIndex: 1,
                    display: 'flex',
                    alignItems: 'flex-end',
                    justifyContent: 'space-between',
                  }}
                >
                  <Box sx={{ color: '#FFFFFF', opacity: 0.8 }}>
                    <Typography variant="caption" sx={{ display: 'block' }}>EMI INTEREST RATE</Typography>
                    <Typography variant="h5" sx={{ fontWeight: 800 }}>9.5% P.A.</Typography>
                  </Box>
                  <Chip label="CLEAN SCANNED" color="success" size="small" sx={{ fontWeight: 700, backgroundColor: '#16A34A' }} />
                </Paper>
              </Box>
            </Grid>
          </Grid>
        </Container>
      </Box>

      {/* Trusted Statistics Section */}
      <Box sx={{ mt: -6, mb: 10, position: 'relative', zIndex: 3 }}>
        <Container maxWidth="lg">
          <Paper
            elevation={4}
            sx={{
              borderRadius: 4,
              py: 4,
              px: { xs: 3, md: 6 },
              backgroundColor: '#FFFFFF',
              border: '1px solid rgba(226,232,240,0.8)',
            }}
          >
            <Grid container spacing={4} justifyContent="center">
              <Grid item xs={6} md={3} sx={{ textAlign: 'center', borderRight: { md: '1px solid #E2E8F0' } }}>
                <Typography variant="h2" color="primary" sx={{ fontWeight: 800 }}>₹500Cr+</Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, fontWeight: 600 }}>Loans Disbursed</Typography>
              </Grid>
              <Grid item xs={6} md={3} sx={{ textAlign: 'center', borderRight: { md: '1px solid #E2E8F0' } }}>
                <Typography variant="h2" color="primary" sx={{ fontWeight: 800 }}>50K+</Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, fontWeight: 600 }}>Happy Customers</Typography>
              </Grid>
              <Grid item xs={6} md={3} sx={{ textAlign: 'center', borderRight: { md: '1px solid #E2E8F0' } }}>
                <Typography variant="h2" color="primary" sx={{ fontWeight: 800 }}>99%</Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, fontWeight: 600 }}>Approval Satisfaction</Typography>
              </Grid>
              <Grid item xs={6} md={3} sx={{ textAlign: 'center' }}>
                <Typography variant="h2" color="primary" sx={{ fontWeight: 800 }}>24x7</Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, fontWeight: 600 }}>Support Services</Typography>
              </Grid>
            </Grid>
          </Paper>
        </Container>
      </Box>

      {/* Feature Cards Section */}
      <Container maxWidth="lg" sx={{ mb: 12 }}>
        <Typography variant="h2" color="primary" align="center" gutterBottom sx={{ fontWeight: 800 }}>
          Redefining Digital Lending
        </Typography>
        <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 8, maxWidth: 650, mx: 'auto' }}>
          Our personal loan portal combines lightning-fast checks with strict security audits.
        </Typography>

        <Grid container spacing={4}>
          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(29,78,216,0.06)', color: 'secondary.main', mb: 2.5 }}>
                <FlashOn sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700 }}>Instant Approval</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7 }}>
                Automated eligibility matching algorithms check calculations and limits instantly.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(212,175,55,0.08)', color: '#D4AF37', mb: 2.5 }}>
                <CloudUpload sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700 }}>Paperless Uploads</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7 }}>
                Upload scans and images directly. We check magic bytes and prevent duplicate files dynamically.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(22,163,74,0.06)', color: 'success.main', mb: 2.5 }}>
                <Security sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700 }}>Military Grade Security</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7 }}>
                Aadhaar IDs, PAN numbers, and documents are encrypted at-rest using AES-256 keys.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(2,132,199,0.06)', color: 'info.main', mb: 2.5 }}>
                <AccountBalanceWallet sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700 }}>Transparent Charges</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7 }}>
                Zero hidden charges. Complete breakdown of processing fees, principal rates, and EMI balances.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(220,38,38,0.06)', color: 'error.main', mb: 2.5 }}>
                <VerifiedUser sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700 }}>Lowest Interest</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7 }}>
                Personal Loan interest rates starting from 9.5% P.A. with custom rate overrides for profile matches.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(11,46,89,0.06)', color: 'primary.main', mb: 2.5 }}>
                <LocalActivity sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700 }}>Digital Process</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7 }}>
                Apply, track, verify, approve, and disburse digitally. Complete workflow transparency.
              </Typography>
            </GlassCard>
          </Grid>
        </Grid>
      </Container>

      {/* Reusable Process Timeline */}
      <Box id="journey" sx={{ backgroundColor: 'rgba(11,46,89,0.02)', py: 10, mb: 12 }}>
        <Container maxWidth="lg">
          <Typography variant="h2" color="primary" align="center" gutterBottom sx={{ fontWeight: 800 }}>
            Simplified 5-Step Process
          </Typography>
          <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 8 }}>
            Our dynamic, multi-role validation workflow ensures complete transparent processing.
          </Typography>
          <GlassCard sx={{ p: 4 }}>
            <LoanTimeline currentStatus="SUBMITTED" />
          </GlassCard>
        </Container>
      </Box>

      {/* Interactive Widgets Section */}
      <Container maxWidth="lg" sx={{ mb: 12 }}>
        <Grid container spacing={5}>
          <Grid item xs={12} md={6} id="eligibility">
            <EligibilityCheckerWidget />
          </Grid>
          <Grid item xs={12} md={6} id="emi-calc">
            <EmiCalculatorWidget />
          </Grid>
        </Grid>
      </Container>

      {/* Testimonials */}
      <Container maxWidth="lg" sx={{ mb: 12 }}>
        <Typography variant="h2" color="primary" align="center" gutterBottom sx={{ fontWeight: 800 }}>
          What Our Customers Say
        </Typography>
        <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 8 }}>
          Real feedback from verified clients who got instant disbursements.
        </Typography>

        <Grid container spacing={4}>
          <Grid item xs={12} md={4}>
            <GlassCard sx={{ p: 4 }}>
              <Typography variant="body1" sx={{ fontStyle: 'italic', mb: 3, color: 'text.secondary', lineHeight: 1.7 }}>
                "The digitised verification was smooth. I uploaded my files on a Monday, and by Tuesday afternoon the sanction letter was issued!"
              </Typography>
              <Stack direction="row" spacing={2} alignItems="center">
                <Box sx={{ width: 48, height: 48, borderRadius: '50%', backgroundColor: '#0B2E59', color: '#FFFFFF', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700 }}>
                  VS
                </Box>
                <Box>
                  <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>Vivek Sharma</Typography>
                  <Typography variant="caption" color="text.secondary">Pune, Maharashtra</Typography>
                </Box>
              </Stack>
            </GlassCard>
          </Grid>

          <Grid item xs={12} md={4}>
            <GlassCard sx={{ p: 4 }}>
              <Typography variant="body1" sx={{ fontStyle: 'italic', mb: 3, color: 'text.secondary', lineHeight: 1.7 }}>
                "Apex Loan portal is simple to navigate. I checked my limits using the checker tool, requested ₹3,00,000 and got disbursed exactly as requested."
              </Typography>
              <Stack direction="row" spacing={2} alignItems="center">
                <Box sx={{ width: 48, height: 48, borderRadius: '50%', backgroundColor: '#1D4ED8', color: '#FFFFFF', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700 }}>
                  AR
                </Box>
                <Box>
                  <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>Ananya Rao</Typography>
                  <Typography variant="caption" color="text.secondary">Bangalore, Karnataka</Typography>
                </Box>
              </Stack>
            </GlassCard>
          </Grid>

          <Grid item xs={12} md={4}>
            <GlassCard sx={{ p: 4 }}>
              <Typography variant="body1" sx={{ fontStyle: 'italic', mb: 3, color: 'text.secondary', lineHeight: 1.7 }}>
                "I was skeptical about at-rest data safety initially. But after seeing their detailed security policies and SHA-256 uploads, I felt completely secure."
              </Typography>
              <Stack direction="row" spacing={2} alignItems="center">
                <Box sx={{ width: 48, height: 48, borderRadius: '50%', backgroundColor: '#D4AF37', color: '#051833', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700 }}>
                  MD
                </Box>
                <Box>
                  <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>Manoj Desai</Typography>
                  <Typography variant="caption" color="text.secondary">Mumbai, Maharashtra</Typography>
                </Box>
              </Stack>
            </GlassCard>
          </Grid>
        </Grid>
      </Container>

      {/* FAQ Section */}
      <Container maxWidth="md" id="faq">
        <Typography variant="h2" color="primary" align="center" gutterBottom sx={{ fontWeight: 800 }}>
          Frequently Asked Questions
        </Typography>
        <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 8 }}>
          Need details about processing fee, interest rates, or eligibility limits?
        </Typography>

        <Accordion sx={{ mb: 2, borderRadius: '8px !important' }}>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography sx={{ fontWeight: 600 }}>What is a FOIR check and how is it calculated?</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Typography color="text.secondary">
              FOIR stands for Fixed Obligation to Income Ratio. It evaluates your total active monthly debt commitments (existing EMIs + proposed new loan EMI) relative to your monthly gross income. To safeguard users from credit strain, we enforce a maximum 50% limit for Personal Loans and 60% for Home Loans.
            </Typography>
          </AccordionDetails>
        </Accordion>

        <Accordion sx={{ mb: 2, borderRadius: '8px !important' }}>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography sx={{ fontWeight: 600 }}>Can the Credit Manager change the interest rate or approved limit?</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Typography color="text.secondary">
              Yes. During checker verification reviews, Credit Managers evaluate your repayment history. They can perform an interest rate override or propose a revised loan amount. All adjustments are logged in the secure audit log history database.
            </Typography>
          </AccordionDetails>
        </Accordion>

        <Accordion sx={{ mb: 2, borderRadius: '8px !important' }}>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography sx={{ fontWeight: 600 }}>What forms of document files are verified?</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Typography color="text.secondary">
              We verify Aadhaar Card IDs and PAN Cards in PDF, PNG, and JPG file extensions. The files are checked for magic byte mismatches, duplicate hashes, and scanned for upload validity.
            </Typography>
          </AccordionDetails>
        </Accordion>
      </Container>
    </Box>
  );
};

export default HomePage;
