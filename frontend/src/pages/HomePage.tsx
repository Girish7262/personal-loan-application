import React, { useState } from 'react';
import {
  Box,
  Container,
  Grid,
  Typography,
  Button,
  Stack,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Paper,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Avatar,
  AvatarGroup,
  Stepper,
  Step,
  StepLabel,
  InputAdornment,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  ExpandMore,
  Security,
  FlashOn,
  VerifiedUser,
  CloudUpload,
  AccountBalanceWallet,
  Stars,
  ArrowForward,
  PhoneIphone,
  LockOutlined,
  CheckCircle,
  Fingerprint,
  LocalActivity,
  CheckCircleOutline,
} from '@mui/icons-material';
import EmiCalculatorWidget from '@/components/ui/EmiCalculatorWidget';
import EligibilityCheckerWidget from '@/components/ui/EligibilityCheckerWidget';
import LoanTimeline from '@/components/ui/LoanTimeline';
import GlassCard from '@/components/ui/GlassCard';
import { useNavigate } from 'react-router-dom';
import { authApi } from '@/api/authApi';

const HomePage: React.FC = () => {
  const navigate = useNavigate();

  // Multi-step Apply Now Wizard state
  const [wizardOpen, setWizardOpen] = useState(false);
  const [wizardStep, setWizardStep] = useState(0);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Form states inside Wizard
  const [incomeInput, setIncomeInput] = useState('75000');
  const [emiInput, setEmiInput] = useState('15000');
  const [amountInput, setAmountInput] = useState('500000');
  const [tenureInput, setTenureInput] = useState('36');

  const [mobileNum, setMobileNum] = useState('');
  const [otpCode, setOtpCode] = useState('');
  const [isExistingUser, setIsExistingUser] = useState(false);

  // Auth fields inside Wizard
  const [emailInput, setEmailInput] = useState('');
  const [passwordInput, setPasswordInput] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const [wizardError, setWizardError] = useState<string | null>(null);

  const scrollToSection = (id: string) => {
    const el = document.getElementById(id);
    if (el) el.scrollIntoView({ behavior: 'smooth' });
  };

  // Wizard action handlers
  const handleOpenWizard = () => {
    setWizardOpen(true);
    setWizardStep(0);
    setWizardError(null);
  };

  const handleCloseWizard = () => {
    setWizardOpen(false);
    setWizardStep(0);
    setWizardError(null);
  };

  const handleProceedEligibility = () => {
    const inc = parseFloat(incomeInput) || 0;
    const emi = parseFloat(emiInput) || 0;
    const amt = parseFloat(amountInput) || 0;
    const ten = parseFloat(tenureInput) || 36;

    const monthlyRate = 11.5 / 12 / 100;
    const proposedEmi = (amt * monthlyRate * Math.pow(1 + monthlyRate, ten)) /
      (Math.pow(1 + monthlyRate, ten) - 1);

    const foirMax = inc * 0.50;
    if (proposedEmi + emi > foirMax) {
      setWizardError('Pre-evaluation limit exceeded. Try applying for a lower amount or longer tenure.');
      return;
    }

    setWizardError(null);
    setWizardStep(1); // Proceed to mobile step
  };

  const handleSendOtp = () => {
    if (!/^\d{10}$/.test(mobileNum)) {
      setWizardError('Please enter a valid 10-digit mobile number.');
      return;
    }
    // Simulate check: odd numbers represent existing users for demo validation flow
    const isOdd = parseInt(mobileNum.slice(-1)) % 2 !== 0;
    setIsExistingUser(isOdd);

    setWizardError(null);
    setWizardStep(2); // Proceed to OTP verification step
  };

  const handleVerifyOtp = () => {
    if (otpCode !== '123456' && otpCode.length !== 6) {
      setWizardError('Invalid OTP code. Please enter 123456 or a valid 6-digit code.');
      return;
    }
    setWizardError(null);
    setWizardStep(3); // Proceed to Login/Register details step
  };

  const handleAuthSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setWizardError(null);

    try {
      if (isExistingUser) {
        // Log in user
        const response = await authApi.login({
          email: emailInput,
          password: passwordInput,
        });
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(response.user));
        setWizardStep(4); // Success step
      } else {
        // Register user
        if (passwordInput !== confirmPassword) {
          setWizardError('Passwords do not match.');
          setIsSubmitting(false);
          return;
        }
        await authApi.register({
          email: emailInput,
          mobileNumber: mobileNum,
          password: passwordInput,
        });
        // Auto-login after registration
        const loginRes = await authApi.login({
          email: emailInput,
          password: passwordInput,
        });
        localStorage.setItem('token', loginRes.token);
        localStorage.setItem('user', JSON.stringify(loginRes.user));
        setWizardStep(4); // Success step
      }
    } catch (err: any) {
      setWizardError(err?.response?.data?.message || 'Authentication failed. Please verify fields.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Box sx={{ backgroundColor: '#F8FAFC', minHeight: '100vh', pb: 10 }}>
      {/* Hero Section */}
      <Box
        id="hero"
        sx={{
          background: 'radial-gradient(circle at 10% 20%, #0B2E59 0%, #051833 90%)',
          color: '#FFFFFF',
          pt: { xs: 8, md: 10 },
          pb: { xs: 12, md: 16 },
          clipPath: 'ellipse(140% 100% at 50% 0%)',
          position: 'relative',
          overflow: 'hidden',
        }}
      >
        {/* Glow circles behind text */}
        <Box sx={{ position: 'absolute', top: '-10%', right: '5%', width: 450, height: 450, borderRadius: '50%', background: 'radial-gradient(circle, rgba(29,78,216,0.18) 0%, transparent 70%)', filter: 'blur(50px)' }} />

        <Container maxWidth="lg" sx={{ position: 'relative', zIndex: 1 }}>
          <Grid container spacing={6} alignItems="center">
            {/* Left side text */}
            <Grid item xs={12} md={7}>
              <Typography variant="h1" gutterBottom sx={{ fontWeight: 800, fontSize: { xs: '2.5rem', md: '3.5rem' }, lineHeight: 1.15 }}>
                Instant Personal Loans. <br />
                <Box component="span" sx={{ color: '#D4AF37' }}>
                  Decisions in Minutes.
                </Box>
              </Typography>
              <Typography variant="body1" sx={{ mb: 4, opacity: 0.9, fontSize: '1.15rem', lineHeight: 1.6, maxWidth: 620 }}>
                Get up to ₹50 Lakhs with minimal paperwork, 100% digital process and transparent approvals.
              </Typography>
              
              {/* Trust features row */}
              <Grid container spacing={2} sx={{ mb: 4, maxWidth: 620 }}>
                <Grid item xs={6}>
                  <Stack direction="row" alignItems="center" spacing={1}>
                    <CheckCircleOutline sx={{ color: '#D4AF37', fontSize: 20 }} />
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>Secure & Trusted (AES-256)</Typography>
                  </Stack>
                </Grid>
                <Grid item xs={6}>
                  <Stack direction="row" alignItems="center" spacing={1}>
                    <CheckCircleOutline sx={{ color: '#D4AF37', fontSize: 20 }} />
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>Instant Approval (In Minutes)</Typography>
                  </Stack>
                </Grid>
                <Grid item xs={6}>
                  <Stack direction="row" alignItems="center" spacing={1}>
                    <CheckCircleOutline sx={{ color: '#D4AF37', fontSize: 20 }} />
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>Low Interest (From 9.5% p.a.)</Typography>
                  </Stack>
                </Grid>
                <Grid item xs={6}>
                  <Stack direction="row" alignItems="center" spacing={1}>
                    <CheckCircleOutline sx={{ color: '#D4AF37', fontSize: 20 }} />
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>No Hidden Charges (Transparent)</Typography>
                  </Stack>
                </Grid>
              </Grid>

              {/* CTAs */}
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2.5} sx={{ mb: 4 }}>
                <Button
                  onClick={handleOpenWizard}
                  variant="contained"
                  size="large"
                  endIcon={<ArrowForward />}
                  sx={{
                    py: 2,
                    px: 5,
                    fontSize: '1.05rem',
                    backgroundColor: '#D4AF37',
                    color: '#051833',
                    fontWeight: 700,
                    borderRadius: 3,
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
                    borderRadius: 3,
                    '&:hover': { borderColor: '#D4AF37', color: '#D4AF37', transform: 'translateY(-2px)' },
                  }}
                >
                  Check Eligibility
                </Button>
              </Stack>

              {/* Rating group */}
              <Stack direction="row" alignItems="center" spacing={2}>
                <AvatarGroup max={4}>
                  <Avatar alt="User 1" src="https://i.pravatar.cc/100?img=11" />
                  <Avatar alt="User 2" src="https://i.pravatar.cc/100?img=12" />
                  <Avatar alt="User 3" src="https://i.pravatar.cc/100?img=13" />
                  <Avatar alt="User 4" src="https://i.pravatar.cc/100?img=14" />
                </AvatarGroup>
                <Box>
                  <Stack direction="row" spacing={0.2} sx={{ color: '#D4AF37' }}>
                    <Stars fontSize="small" /><Stars fontSize="small" /><Stars fontSize="small" /><Stars fontSize="small" /><Stars fontSize="small" />
                  </Stack>
                  <Typography variant="caption" sx={{ opacity: 0.8, fontWeight: 600 }}>
                    4.9/5 (50,000+ happy customers)
                  </Typography>
                </Box>
              </Stack>
            </Grid>

            {/* Right side Illustration Card Stack */}
            <Grid item xs={12} md={5} sx={{ display: { xs: 'none', md: 'flex' }, justifyContent: 'center' }}>
              <Box sx={{ position: 'relative', width: '100%', height: 380 }}>
                {/* Visual Card 1 */}
                <Paper
                  elevation={8}
                  sx={{
                    position: 'absolute',
                    top: '5%',
                    left: '5%',
                    width: '85%',
                    height: 220,
                    borderRadius: 4,
                    background: 'linear-gradient(135deg, #0B2E59 0%, #1D4ED8 100%)',
                    color: '#FFFFFF',
                    p: 4,
                    boxShadow: '0 20px 45px rgba(11,46,89,0.3)',
                    border: '1px solid rgba(255,255,255,0.15)',
                    zIndex: 2,
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'space-between',
                  }}
                >
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <Box>
                      <Typography variant="body2" sx={{ opacity: 0.7, letterSpacing: 2, fontSize: '0.75rem', fontWeight: 700 }}>
                        APEX PRIVILEGE CARD
                      </Typography>
                      <Typography variant="h4" sx={{ fontWeight: 800, mt: 0.5, color: '#D4AF37' }}>
                        ₹50,00,000
                      </Typography>
                    </Box>
                    <Security sx={{ color: '#D4AF37', fontSize: 32 }} />
                  </Box>
                  <Box>
                    <Typography variant="caption" sx={{ display: 'block', opacity: 0.6, fontWeight: 700 }}>APPROVED LIMIT</Typography>
                    <Typography variant="body1" sx={{ fontWeight: 600, letterSpacing: 3, mt: 0.5 }}>**** **** **** 2026</Typography>
                  </Box>
                </Paper>

                {/* Visual Card 2 (behind Card 1) */}
                <Paper
                  elevation={4}
                  sx={{
                    position: 'absolute',
                    top: '25%',
                    left: '20%',
                    width: '80%',
                    height: 190,
                    borderRadius: 4,
                    background: 'rgba(255, 255, 255, 0.9)',
                    border: '1px solid rgba(11,46,89,0.1)',
                    p: 3.5,
                    zIndex: 1,
                    display: 'flex',
                    alignItems: 'flex-end',
                    justifyContent: 'space-between',
                    boxShadow: '0 12px 30px rgba(0,0,0,0.06)',
                  }}
                >
                  <Box sx={{ color: '#0B2E59' }}>
                    <Typography variant="caption" sx={{ display: 'block', fontWeight: 700, color: 'text.secondary' }}>INTEREST RATE</Typography>
                    <Typography variant="h5" sx={{ fontWeight: 800, color: '#0B2E59' }}>11.5% p.a.</Typography>
                  </Box>
                  <Chip label="PRE-APPROVED" color="success" size="small" sx={{ fontWeight: 700, px: 1 }} />
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
            <Grid container spacing={4} justifyContent="center" alignItems="center">
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
          Why Choose Apex Loan?
        </Typography>
        <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 8, maxWidth: 650, mx: 'auto' }}>
          Technology, transparency and trust come together for the best loan experience.
        </Typography>

        <Grid container spacing={4}>
          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(29,78,216,0.06)', color: 'secondary.main', mb: 2.5 }}>
                <FlashOn sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700 }}>Instant Approval</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7 }}>
                Get approval in minutes with smart algorithms.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(212,175,55,0.08)', color: '#D4AF37', mb: 2.5 }}>
                <CloudUpload sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700 }}>Paperless Process</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7 }}>
                Upload documents digitally with secure verification.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(22,163,74,0.06)', color: 'success.main', mb: 2.5 }}>
                <Security sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700 }}>Bank-grade Security</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7 }}>
                Your data is protected with AES-256 encryption.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(2,132,199,0.06)', color: 'info.main', mb: 2.5 }}>
                <AccountBalanceWallet sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700 }}>Lowest Interest Rate</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7 }}>
                Competitive rates starting from 9.5% p.a.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(220,38,38,0.06)', color: 'error.main', mb: 2.5 }}>
                <VerifiedUser sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700 }}>Flexible Repayment</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7 }}>
                Choose EMIs that fit your budget.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%' }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(11,46,89,0.06)', color: 'primary.main', mb: 2.5 }}>
                <LocalActivity sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700 }}>Transparent Charges</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7 }}>
                No hidden charges. No surprises.
              </Typography>
            </GlassCard>
          </Grid>
        </Grid>
      </Container>

      {/* Loan Journey Timeline */}
      <Box id="journey" sx={{ backgroundColor: 'rgba(11,46,89,0.02)', py: 10, mb: 12 }}>
        <Container maxWidth="lg">
          <Typography variant="h2" color="primary" align="center" gutterBottom sx={{ fontWeight: 800 }}>
            Our Simple 5-Step Loan Process
          </Typography>
          <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 8 }}>
            From application to disbursement - completely digital and transparent.
          </Typography>
          <GlassCard sx={{ p: 4 }}>
            <LoanTimeline currentStatus="SUBMITTED" />
          </GlassCard>
        </Container>
      </Box>

      {/* Interactive Calculator Widgets */}
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
          Hear from our happy customers about their experience.
        </Typography>

        <Grid container spacing={4}>
          <Grid item xs={12} md={4}>
            <GlassCard sx={{ p: 4 }}>
              <Typography variant="body1" sx={{ fontStyle: 'italic', mb: 3, color: 'text.secondary', lineHeight: 1.7 }}>
                "The entire process was digital and hassle-free. I got my loan approved in just 10 minutes!"
              </Typography>
              <Stack direction="row" spacing={2} alignItems="center">
                <Box sx={{ width: 48, height: 48, borderRadius: '50%', backgroundColor: '#0B2E59', color: '#FFFFFF', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700 }}>
                  RS
                </Box>
                <Box>
                  <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>Rahul Sharma</Typography>
                  <Typography variant="caption" color="text.secondary">Pune, Maharashtra</Typography>
                </Box>
              </Stack>
            </GlassCard>
          </Grid>

          <Grid item xs={12} md={4}>
            <GlassCard sx={{ p: 4 }}>
              <Typography variant="body1" sx={{ fontStyle: 'italic', mb: 3, color: 'text.secondary', lineHeight: 1.7 }}>
                "Best interest rates and 100% transparent process. Highly recommended!"
              </Typography>
              <Stack direction="row" spacing={2} alignItems="center">
                <Box sx={{ width: 48, height: 48, borderRadius: '50%', backgroundColor: '#1D4ED8', color: '#FFFFFF', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700 }}>
                  PM
                </Box>
                <Box>
                  <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>Priya Mehta</Typography>
                  <Typography variant="caption" color="text.secondary">Bangalore, Karnataka</Typography>
                </Box>
              </Stack>
            </GlassCard>
          </Grid>

          <Grid item xs={12} md={4}>
            <GlassCard sx={{ p: 4 }}>
              <Typography variant="body1" sx={{ fontStyle: 'italic', mb: 3, color: 'text.secondary', lineHeight: 1.7 }}>
                "Amazing customer support and quick disbursement. Truly a great experience."
              </Typography>
              <Stack direction="row" spacing={2} alignItems="center">
                <Box sx={{ width: 48, height: 48, borderRadius: '50%', backgroundColor: '#D4AF37', color: '#051833', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700 }}>
                  SP
                </Box>
                <Box>
                  <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>Sandeep Patel</Typography>
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
          Got questions? We have answers.
        </Typography>

        <Accordion sx={{ mb: 2, borderRadius: '8px !important' }}>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography sx={{ fontWeight: 600 }}>What documents are required for a personal loan?</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Typography color="text.secondary">
              Generally, you need PAN Card, Aadhaar Card, and income proof (bank statement or salary slip).
            </Typography>
          </AccordionDetails>
        </Accordion>

        <Accordion sx={{ mb: 2, borderRadius: '8px !important' }}>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography sx={{ fontWeight: 600 }}>How long does the approval take?</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Typography color="text.secondary">
              It typically takes a few minutes for digital pre-evaluation, and final sanction within 24-48 business hours.
            </Typography>
          </AccordionDetails>
        </Accordion>

        <Accordion sx={{ mb: 2, borderRadius: '8px !important' }}>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography sx={{ fontWeight: 600 }}>What is the minimum credit score required?</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Typography color="text.secondary">
              A credit score of 700 or above is ideal, though we evaluate multiple indicators to match your limit requests.
            </Typography>
          </AccordionDetails>
        </Accordion>

        <Accordion sx={{ mb: 2, borderRadius: '8px !important' }}>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography sx={{ fontWeight: 600 }}>Can I foreclose my loan?</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Typography color="text.secondary">
              Yes, foreclosure is supported. Check the Terms of Service for specific foreclosure charges and guidelines.
            </Typography>
          </AccordionDetails>
        </Accordion>
      </Container>

      {/* MULTI-STEP APPLY NOW MODAL WIZARD */}
      <Dialog open={wizardOpen} onClose={handleCloseWizard} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ backgroundColor: '#0B2E59', color: '#FFFFFF', py: 2.5 }}>
          <Stack direction="row" alignItems="center" spacing={1}>
            <Fingerprint sx={{ color: '#D4AF37' }} />
            <Typography variant="h5" sx={{ fontWeight: 700, color: '#FFFFFF' }}>
              Apex Fast Loan Application Wizard
            </Typography>
          </Stack>
        </DialogTitle>

        <DialogContent sx={{ mt: 3 }}>
          {/* Stepper progress */}
          <Stepper activeStep={wizardStep} alternativeLabel sx={{ mb: 4 }}>
            <Step><StepLabel>Eligibility</StepLabel></Step>
            <Step><StepLabel>Mobile</StepLabel></Step>
            <Step><StepLabel>OTP</StepLabel></Step>
            <Step><StepLabel>Security</StepLabel></Step>
          </Stepper>

          {wizardError && (
            <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
              {wizardError}
            </Alert>
          )}

          {/* STEP 0: ELIGIBILITY CHECK */}
          {wizardStep === 0 && (
            <Box>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                Let's perform a fast pre-eligibility evaluation to determine your primary credit matching limits.
              </Typography>
              <Grid container spacing={2.5}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Gross Monthly Income (₹)"
                    value={incomeInput}
                    onChange={(e) => setIncomeInput(e.target.value.replace(/\D/g, ''))}
                    fullWidth
                    required
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Existing Monthly EMIs (₹)"
                    value={emiInput}
                    onChange={(e) => setEmiInput(e.target.value.replace(/\D/g, ''))}
                    fullWidth
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Requested Loan Amount (₹)"
                    value={amountInput}
                    onChange={(e) => setAmountInput(e.target.value.replace(/\D/g, ''))}
                    fullWidth
                    required
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    label="Requested Tenure (Months)"
                    value={tenureInput}
                    onChange={(e) => setTenureInput(e.target.value.replace(/\D/g, ''))}
                    fullWidth
                    required
                  />
                </Grid>
              </Grid>
            </Box>
          )}

          {/* STEP 1: MOBILE NUMBER INPUT */}
          {wizardStep === 1 && (
            <Box sx={{ textAlign: 'center', py: 2 }}>
              <PhoneIphone sx={{ fontSize: 56, color: 'primary.main', mb: 2 }} />
              <Typography variant="h5" sx={{ fontWeight: 700, mb: 1 }}>Enter Mobile Number</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                Provide your mobile number to trigger an OTP authorization verification code.
              </Typography>
              <TextField
                label="Mobile Number (10 digits)"
                value={mobileNum}
                onChange={(e) => setMobileNum(e.target.value.replace(/\D/g, ''))}
                fullWidth
                required
                InputProps={{
                  startAdornment: <InputAdornment position="start">+91 </InputAdornment>,
                }}
                sx={{ maxWidth: 360, mx: 'auto' }}
              />
            </Box>
          )}

          {/* STEP 2: OTP VERIFICATION */}
          {wizardStep === 2 && (
            <Box sx={{ textAlign: 'center', py: 2 }}>
              <LockOutlined sx={{ fontSize: 56, color: 'primary.main', mb: 2 }} />
              <Typography variant="h5" sx={{ fontWeight: 700, mb: 1 }}>Verify OTP Code</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                Enter the 6-digit verification code sent to <strong>+91 {mobileNum}</strong>. (Use demo code: <strong>123456</strong>)
              </Typography>
              <TextField
                label="6-Digit OTP Code"
                value={otpCode}
                onChange={(e) => setOtpCode(e.target.value.replace(/\D/g, ''))}
                fullWidth
                required
                inputProps={{ maxLength: 6, style: { textAlign: 'center', fontSize: '1.25rem', letterSpacing: '4px' } }}
                sx={{ maxWidth: 280, mx: 'auto' }}
              />
            </Box>
          )}

          {/* STEP 3: LOGIN / REGISTER BASED ON USER RECOGNITION */}
          {wizardStep === 3 && (
            <Box>
              <Typography variant="h5" sx={{ fontWeight: 700, mb: 1, textAlign: 'center' }}>
                {isExistingUser ? 'Welcome Back! Secure Login' : 'Secure Registration Profile'}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3, textAlign: 'center' }}>
                {isExistingUser
                  ? 'We recognized your mobile number in our system. Please sign in to link your application.'
                  : 'Let\'s create a new secure portal account to complete your loan checklist.'}
              </Typography>

              <form onSubmit={handleAuthSubmit}>
                <Stack spacing={2.5}>
                  <TextField
                    label="Email Address"
                    type="email"
                    value={emailInput}
                    onChange={(e) => setEmailInput(e.target.value)}
                    fullWidth
                    required
                  />
                  <TextField
                    label="Secure Account Password"
                    type="password"
                    value={passwordInput}
                    onChange={(e) => setPasswordInput(e.target.value)}
                    fullWidth
                    required
                  />
                  {!isExistingUser && (
                    <TextField
                      label="Confirm Password"
                      type="password"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      fullWidth
                      required
                    />
                  )}
                  
                  <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    size="large"
                    disabled={isSubmitting}
                    sx={{ py: 1.5, mt: 2 }}
                  >
                    {isSubmitting ? <CircularProgress size={24} color="inherit" /> : (isExistingUser ? 'Sign In & Apply' : 'Register & Apply')}
                  </Button>
                </Stack>
              </form>
            </Box>
          )}

          {/* STEP 4: SUCCESS SUMMARY */}
          {wizardStep === 4 && (
            <Box sx={{ textAlign: 'center', py: 4 }}>
              <CheckCircle color="success" sx={{ fontSize: 72, mb: 2 }} />
              <Typography variant="h4" sx={{ fontWeight: 800, color: 'success.main', mb: 2 }}>
                Authentication Success!
              </Typography>
              <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
                Your fast eligibility checks matched successfully. We are redirecting you to your customer portal workspace to finalize document uploads and complete your application.
              </Typography>
              <Button
                variant="contained"
                color="success"
                size="large"
                onClick={() => {
                  handleCloseWizard();
                  navigate('/dashboard');
                }}
                sx={{ py: 1.5, px: 6, borderRadius: 3 }}
              >
                Go to Workspace
              </Button>
            </Box>
          )}
        </DialogContent>

        {wizardStep < 3 && (
          <DialogActions sx={{ p: 3, borderTop: '1px solid rgba(0,0,0,0.06)' }}>
            <Button onClick={handleCloseWizard} variant="outlined">
              Cancel
            </Button>
            {wizardStep === 0 && (
              <Button onClick={handleProceedEligibility} variant="contained" color="primary">
                Proceed Check
              </Button>
            )}
            {wizardStep === 1 && (
              <Button onClick={handleSendOtp} variant="contained" color="primary">
                Send OTP Code
              </Button>
            )}
            {wizardStep === 2 && (
              <Button onClick={handleVerifyOtp} variant="contained" color="primary">
                Verify OTP
              </Button>
            )}
          </DialogActions>
        )}
      </Dialog>
    </Box>
  );
};

export default HomePage;
