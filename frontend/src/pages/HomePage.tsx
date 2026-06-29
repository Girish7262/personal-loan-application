import React, { useState, useEffect } from 'react';
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
  DialogContent,
  DialogActions,
  TextField,
  Avatar,
  AvatarGroup,
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
  CheckCircle,
  Fingerprint as FingerprintIcon,
  LocalActivity,
  Group,
  ThumbUp,
  HeadsetMic,
  Assignment as AssignmentIcon,
  FactCheck as FactCheckIcon,
  Description as DescriptionIcon,
  MonetizationOn as MonetizationOnIcon,
  Star,
} from '@mui/icons-material';
import EmiCalculatorWidget from '@/components/ui/EmiCalculatorWidget';
import EligibilityCheckerWidget from '@/components/ui/EligibilityCheckerWidget';
import GlassCard from '@/components/ui/GlassCard';
import { useNavigate, useLocation } from 'react-router-dom';
import { authApi } from '@/api/authApi';

const processSteps = [
  { 
    title: 'Apply Online', 
    desc: 'Fill basic details in 2 minutes', 
    icon: <AssignmentIcon sx={{ fontSize: 26 }} /> 
  },
  { 
    title: 'Verify Identity', 
    desc: 'Secure KYC with Aadhaar & PAN', 
    icon: <FingerprintIcon sx={{ fontSize: 26 }} /> 
  },
  { 
    title: 'Get Approved', 
    desc: 'Instant verification by our system', 
    icon: <FactCheckIcon sx={{ fontSize: 26 }} /> 
  },
  { 
    title: 'Receive Offer', 
    desc: 'View loan offer and accept digitally', 
    icon: <DescriptionIcon sx={{ fontSize: 26 }} /> 
  },
  { 
    title: 'Get Disbursed', 
    desc: 'Amount credited to your account', 
    icon: <MonetizationOnIcon sx={{ fontSize: 26 }} /> 
  }
];

const testimonials = [
  {
    name: 'Rahul Sharma',
    role: 'Software Architect',
    avatar: 'https://i.pravatar.cc/100?img=33',
    feedback: 'The Aadhaar KYC verification took under 2 minutes. The loan amount was credited to my account the same afternoon. Absolutely seamless fintech portal.',
    rating: 5,
  },
  {
    name: 'Priya Patel',
    role: 'Business Director',
    avatar: 'https://i.pravatar.cc/100?img=47',
    feedback: 'Apex Loan offered me a competitive interest rate of 10.25% p.a. for my expansion needs. The interface is highly transparent and informative.',
    rating: 5,
  },
  {
    name: 'Amit Verma',
    role: 'Financial Consultant',
    avatar: 'https://i.pravatar.cc/100?img=12',
    feedback: 'Outstanding customer support. The monthly EMIs are customized perfectly to my budget, and there are absolutely no hidden service fees.',
    rating: 5,
  }
];

const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();

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

  // Hook up redirect trigger parameter from navbar
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    if (params.get('apply') === 'true') {
      handleOpenWizard();
      navigate('/', { replace: true });
    }
  }, [location.search, navigate]);

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
      {/* Immersive Dark Blue Hero Section */}
      <Box
        id="hero"
        sx={{
          background: 'radial-gradient(circle at 80% 20%, rgba(29, 78, 216, 0.15) 0%, rgba(10, 17, 40, 1) 70%), #0A1128',
          color: '#FFFFFF',
          pt: { xs: 8, md: 12 },
          pb: { xs: 12, md: 16 },
          position: 'relative',
          overflow: 'hidden',
          borderBottom: '1px solid rgba(255, 255, 255, 0.05)',
        }}
      >
        <Container maxWidth="lg" sx={{ position: 'relative', zIndex: 2 }}>
          <Grid container spacing={6} alignItems="center">
            {/* Left side content */}
            <Grid item xs={12} md={7}>
              <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 3 }}>
                <Box sx={{ display: 'inline-flex', px: 2, py: 0.6, borderRadius: 5, backgroundColor: 'rgba(212, 175, 55, 0.1)', border: '1px solid rgba(212, 175, 55, 0.2)' }}>
                  <Typography variant="caption" sx={{ color: '#D4AF37', fontWeight: 700, letterSpacing: 0.5 }}>
                    🏆 India's Most Trusted Digital Lending Partner
                  </Typography>
                </Box>
              </Stack>

              <Typography variant="h1" sx={{ color: '#FFFFFF', fontWeight: 850, fontSize: { xs: '2.75rem', md: '3.75rem' }, lineHeight: 1.15, mb: 2.5, letterSpacing: '-0.02em' }}>
                Instant Personal Loans. <br />
                <Box component="span" sx={{ background: 'linear-gradient(135deg, #F0C243 0%, #D4AF37 100%)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
                  Decisions in Minutes.
                </Box>
              </Typography>
              <Typography variant="body1" sx={{ mb: 4.5, color: '#94A3B8', fontSize: '1.15rem', lineHeight: 1.7, maxWidth: 600 }}>
                Get up to ₹50 Lakhs with zero manual paperwork, a 100% encrypted digital pipeline, and instant approval evaluation.
              </Typography>

              {/* Trust Indicators */}
              <Grid container spacing={2.5} sx={{ mb: 5, maxWidth: 600 }}>
                <Grid item xs={6}>
                  <Stack direction="row" alignItems="center" spacing={1.5}>
                    <CheckCircle sx={{ fontSize: 18, color: '#D4AF37' }} />
                    <Typography variant="body2" sx={{ fontWeight: 600, color: '#E2E8F0' }}>Secure & Trusted (AES-256)</Typography>
                  </Stack>
                </Grid>
                <Grid item xs={6}>
                  <Stack direction="row" alignItems="center" spacing={1.5}>
                    <CheckCircle sx={{ fontSize: 18, color: '#D4AF37' }} />
                    <Typography variant="body2" sx={{ fontWeight: 600, color: '#E2E8F0' }}>Instant Evaluation (2 Mins)</Typography>
                  </Stack>
                </Grid>
                <Grid item xs={6}>
                  <Stack direction="row" alignItems="center" spacing={1.5}>
                    <CheckCircle sx={{ fontSize: 18, color: '#D4AF37' }} />
                    <Typography variant="body2" sx={{ fontWeight: 600, color: '#E2E8F0' }}>Low Interest (9.5% p.a.)</Typography>
                  </Stack>
                </Grid>
                <Grid item xs={6}>
                  <Stack direction="row" alignItems="center" spacing={1.5}>
                    <CheckCircle sx={{ fontSize: 18, color: '#D4AF37' }} />
                    <Typography variant="body2" sx={{ fontWeight: 600, color: '#E2E8F0' }}>Transparent Processing</Typography>
                  </Stack>
                </Grid>
              </Grid>

              {/* CTAs */}
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2.5} sx={{ mb: 5 }}>
                <Button
                  onClick={handleOpenWizard}
                  variant="contained"
                  size="large"
                  endIcon={<ArrowForward />}
                  sx={{
                    py: 2.2,
                    px: 5,
                    fontSize: '1rem',
                    backgroundColor: '#D4AF37',
                    color: '#050D1A',
                    fontWeight: 700,
                    borderRadius: 2.5,
                    boxShadow: '0 8px 24px rgba(212, 175, 55, 0.35)',
                    '&:hover': {
                      backgroundColor: '#E5C158',
                      boxShadow: '0 12px 30px rgba(212, 175, 55, 0.5)',
                      transform: 'translateY(-2px)'
                    },
                  }}
                >
                  Apply Now
                </Button>
                <Button
                  onClick={() => scrollToSection('eligibility')}
                  variant="outlined"
                  size="large"
                  sx={{
                    py: 2.2,
                    px: 5,
                    fontSize: '1rem',
                    color: '#FFFFFF',
                    borderColor: 'rgba(255, 255, 255, 0.25)',
                    fontWeight: 650,
                    borderRadius: 2.5,
                    backdropFilter: 'blur(4px)',
                    '&:hover': {
                      borderColor: '#FFFFFF',
                      backgroundColor: 'rgba(255, 255, 255, 0.08)',
                      transform: 'translateY(-2px)'
                    },
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
                  <Typography variant="caption" sx={{ color: '#94A3B8', fontWeight: 600 }}>
                    4.9/5 (50,000+ happy clients)
                  </Typography>
                </Box>
              </Stack>
            </Grid>

            {/* Right side Illustration Portrait Stack */}
            <Grid item xs={12} md={5} sx={{ display: { xs: 'none', md: 'flex' }, justifyContent: 'center', position: 'relative' }}>
              <Box sx={{ position: 'relative', width: '100%', maxWidth: 440, display: 'flex', justifyContent: 'center', alignItems: 'flex-end', height: 420 }}>
                {/* Man Image */}
                <Box
                  component="img"
                  src="/hero_man.png"
                  alt="Premium Banking Customer"
                  sx={{
                    width: '85%',
                    height: 'auto',
                    maxHeight: 400,
                    objectFit: 'contain',
                    zIndex: 2,
                    position: 'relative',
                    filter: 'drop-shadow(0 20px 35px rgba(11, 46, 89, 0.35))',
                  }}
                />
                
                {/* Floating Blue Card 1 */}
                <Paper
                  elevation={8}
                  sx={{
                    position: 'absolute',
                    top: '10%',
                    right: '-8%',
                    width: 250,
                    height: 150,
                    borderRadius: 3.5,
                    background: 'linear-gradient(135deg, #0B2E59 0%, #1D4ED8 100%)',
                    color: '#FFFFFF',
                    p: 2.5,
                    boxShadow: '0 20px 40px rgba(11, 46, 89, 0.4)',
                    border: '1px solid rgba(212, 175, 55, 0.35)',
                    zIndex: 3,
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'space-between',
                    animation: 'floatCard1 6s ease-in-out infinite',
                    '@keyframes floatCard1': {
                      '0%, 100%': { transform: 'translateY(0px)' },
                      '50%': { transform: 'translateY(-10px)' }
                    }
                  }}
                >
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <Box>
                      <Typography variant="caption" sx={{ opacity: 0.7, letterSpacing: 1.5, fontSize: '0.6rem', fontWeight: 700 }}>
                        APEX PRIVILEGE CARD
                      </Typography>
                      <Typography variant="body1" sx={{ fontWeight: 800, mt: 0.2, color: '#D4AF37', fontSize: '1.1rem' }}>
                        ₹50,00,000
                      </Typography>
                    </Box>
                    <Security sx={{ color: '#D4AF37', fontSize: 22 }} />
                  </Box>
                  <Box>
                    <Typography variant="caption" sx={{ display: 'block', opacity: 0.6, fontSize: '0.55rem', fontWeight: 700 }}>APPROVED LIMIT</Typography>
                    <Typography variant="body2" sx={{ fontWeight: 600, letterSpacing: 2, fontSize: '0.75rem', mt: 0.2 }}>**** **** **** 2026</Typography>
                  </Box>
                </Paper>

                {/* Floating Dark Card 2 */}
                <Paper
                  elevation={6}
                  sx={{
                    position: 'absolute',
                    bottom: '15%',
                    left: '-8%',
                    width: 220,
                    height: 95,
                    borderRadius: 3.5,
                    background: 'rgba(5, 13, 26, 0.95)',
                    border: '1px solid rgba(255, 255, 255, 0.1)',
                    p: 2.2,
                    zIndex: 3,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    boxShadow: '0 12px 28px rgba(0, 0, 0, 0.4)',
                    animation: 'floatCard2 6s ease-in-out infinite',
                    animationDelay: '1.5s',
                    '@keyframes floatCard2': {
                      '0%, 100%': { transform: 'translateY(0px)' },
                      '50%': { transform: 'translateY(-8px)' }
                    }
                  }}
                >
                  <Box sx={{ color: '#FFFFFF' }}>
                    <Typography variant="caption" sx={{ display: 'block', fontWeight: 600, color: 'rgba(255, 255, 255, 0.6)', fontSize: '0.6rem', letterSpacing: 0.5 }}>INTEREST RATE</Typography>
                    <Typography variant="body1" sx={{ fontWeight: 800, color: '#FFFFFF', mt: 0.2 }}>11.5% p.a.</Typography>
                  </Box>
                  <Chip label="PRE-APPROVED" color="success" size="small" sx={{ fontWeight: 700, fontSize: '0.65rem', height: 22, backgroundColor: '#16A34A' }} />
                </Paper>
              </Box>
            </Grid>
          </Grid>
        </Container>
      </Box>

      {/* Floating Statistics Dashboard */}
      <Box sx={{ mt: -6, mb: 10, position: 'relative', zIndex: 3 }}>
        <Container maxWidth="lg">
          <Paper
            elevation={6}
            sx={{
              borderRadius: 4,
              py: 4,
              px: { xs: 3, md: 5 },
              backgroundColor: '#FFFFFF',
              border: '1px solid rgba(226, 232, 240, 0.8)',
              boxShadow: '0 20px 45px -4px rgba(15, 23, 42, 0.08)',
            }}
          >
            <Grid container spacing={3} justifyContent="center" alignItems="center">
              <Grid item xs={12} sm={6} md={3}>
                <Box sx={{ 
                  display: 'flex', 
                  flexDirection: 'column', 
                  alignItems: 'center', 
                  textAlign: 'center',
                  p: 2.5,
                  borderRadius: 3,
                  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                    backgroundColor: 'rgba(11, 46, 89, 0.02)',
                  }
                }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', width: 52, height: 52, borderRadius: '50%', backgroundColor: 'rgba(11, 46, 89, 0.06)', color: '#0B2E59', mb: 2 }}>
                    <AccountBalanceWallet sx={{ fontSize: 24 }} />
                  </Box>
                  <Typography variant="h2" color="primary" sx={{ fontWeight: 800, fontSize: { xs: '1.8rem', md: '2.3rem' } }}>₹500Cr+</Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, fontWeight: 700 }}>Loans Disbursed</Typography>
                </Box>
              </Grid>
              <Grid item xs={12} sm={6} md={3} sx={{ borderLeft: { md: '1px solid #E2E8F0' } }}>
                <Box sx={{ 
                  display: 'flex', 
                  flexDirection: 'column', 
                  alignItems: 'center', 
                  textAlign: 'center',
                  p: 2.5,
                  borderRadius: 3,
                  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                    backgroundColor: 'rgba(11, 46, 89, 0.02)',
                  }
                }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', width: 52, height: 52, borderRadius: '50%', backgroundColor: 'rgba(29, 78, 216, 0.06)', color: '#1D4ED8', mb: 2 }}>
                    <Group sx={{ fontSize: 24 }} />
                  </Box>
                  <Typography variant="h2" color="primary" sx={{ fontWeight: 800, fontSize: { xs: '1.8rem', md: '2.3rem' } }}>50K+</Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, fontWeight: 700 }}>Happy Clients</Typography>
                </Box>
              </Grid>
              <Grid item xs={12} sm={6} md={3} sx={{ borderLeft: { md: '1px solid #E2E8F0' } }}>
                <Box sx={{ 
                  display: 'flex', 
                  flexDirection: 'column', 
                  alignItems: 'center', 
                  textAlign: 'center',
                  p: 2.5,
                  borderRadius: 3,
                  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                    backgroundColor: 'rgba(11, 46, 89, 0.02)',
                  }
                }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', width: 52, height: 52, borderRadius: '50%', backgroundColor: 'rgba(212, 175, 55, 0.08)', color: '#D4AF37', mb: 2 }}>
                    <ThumbUp sx={{ fontSize: 22 }} />
                  </Box>
                  <Typography variant="h2" color="primary" sx={{ fontWeight: 800, fontSize: { xs: '1.8rem', md: '2.3rem' } }}>99%</Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, fontWeight: 700 }}>Approval Rate</Typography>
                </Box>
              </Grid>
              <Grid item xs={12} sm={6} md={3} sx={{ borderLeft: { md: '1px solid #E2E8F0' } }}>
                <Box sx={{ 
                  display: 'flex', 
                  flexDirection: 'column', 
                  alignItems: 'center', 
                  textAlign: 'center',
                  p: 2.5,
                  borderRadius: 3,
                  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                    backgroundColor: 'rgba(11, 46, 89, 0.02)',
                  }
                }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', width: 52, height: 52, borderRadius: '50%', backgroundColor: 'rgba(22, 163, 74, 0.06)', color: '#16A34A', mb: 2 }}>
                    <HeadsetMic sx={{ fontSize: 24 }} />
                  </Box>
                  <Typography variant="h2" color="primary" sx={{ fontWeight: 800, fontSize: { xs: '1.8rem', md: '2.3rem' } }}>24x7</Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, fontWeight: 700 }}>Institutional Support</Typography>
                </Box>
              </Grid>
            </Grid>
          </Paper>
        </Container>
      </Box>

      {/* Feature Cards Section */}
      <Container maxWidth="lg" sx={{ mb: 12 }}>
        <Typography variant="h2" color="primary" align="center" gutterBottom sx={{ fontWeight: 800, fontFamily: '"Poppins", sans-serif' }}>
          Why Choose Apex Loan?
        </Typography>
        <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 6, maxWidth: 650, mx: 'auto', fontWeight: 500 }}>
          Technology, transparency and institutional trust integrated to deliver the ultimate loan ecosystem.
        </Typography>

        <Grid container spacing={4}>
          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%', border: '1px solid rgba(226, 232, 240, 0.8)', borderLeft: '4px solid #1D4ED8', transition: 'all 0.3s', '&:hover': { transform: 'scale(1.02)' } }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(29,78,216,0.06)', color: 'secondary.main', mb: 2.5 }}>
                <FlashOn sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700, color: '#0B2E59', fontFamily: '"Poppins", sans-serif' }}>Instant Approval</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7, fontWeight: 500 }}>
                Automatic credit risk verification resolves loan applications in under 2 minutes.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%', border: '1px solid rgba(226, 232, 240, 0.8)', borderLeft: '4px solid #D4AF37', transition: 'all 0.3s', '&:hover': { transform: 'scale(1.02)' } }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(212,175,55,0.08)', color: '#D4AF37', mb: 2.5 }}>
                <CloudUpload sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700, color: '#0B2E59', fontFamily: '"Poppins", sans-serif' }}>Paperless Process</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7, fontWeight: 500 }}>
                100% digital KYC pipeline with Aadhaar, PAN, and secure e-Sign documentation.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%', border: '1px solid rgba(226, 232, 240, 0.8)', borderLeft: '4px solid #16A34A', transition: 'all 0.3s', '&:hover': { transform: 'scale(1.02)' } }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(22,163,74,0.06)', color: 'success.main', mb: 2.5 }}>
                <Security sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700, color: '#0B2E59', fontFamily: '"Poppins", sans-serif' }}>Bank-grade Security</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7, fontWeight: 500 }}>
                End-to-end data encryption secured via industry-standard SSL and AES-256 protocols.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%', border: '1px solid rgba(226, 232, 240, 0.8)', borderLeft: '4px solid #0284C7', transition: 'all 0.3s', '&:hover': { transform: 'scale(1.02)' } }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(2,132,199,0.06)', color: 'info.main', mb: 2.5 }}>
                <AccountBalanceWallet sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700, color: '#0B2E59', fontFamily: '"Poppins", sans-serif' }}>Competitive Rates</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7, fontWeight: 500 }}>
                Attractive interest rate thresholds starting as low as 9.5% p.a.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%', border: '1px solid rgba(226, 232, 240, 0.8)', borderLeft: '4px solid #DC2626', transition: 'all 0.3s', '&:hover': { transform: 'scale(1.02)' } }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(220,38,38,0.06)', color: 'error.main', mb: 2.5 }}>
                <VerifiedUser sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700, color: '#0B2E59', fontFamily: '"Poppins", sans-serif' }}>Flexible Tenures</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7, fontWeight: 500 }}>
                Structured repayment models offering options from 6 to 84 months.
              </Typography>
            </GlassCard>
          </Grid>

          <Grid item xs={12} sm={6} md={4}>
            <GlassCard sx={{ p: 4, height: '100%', border: '1px solid rgba(226, 232, 240, 0.8)', borderLeft: '4px solid #0B2E59', transition: 'all 0.3s', '&:hover': { transform: 'scale(1.02)' } }}>
              <Box sx={{ display: 'inline-flex', p: 1.5, borderRadius: 3, backgroundColor: 'rgba(11,46,89,0.06)', color: 'primary.main', mb: 2.5 }}>
                <LocalActivity sx={{ fontSize: 28 }} />
              </Box>
              <Typography variant="h5" sx={{ mb: 1.5, fontWeight: 700, color: '#0B2E59', fontFamily: '"Poppins", sans-serif' }}>Zero Hidden Charges</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7, fontWeight: 500 }}>
                Complete transparency. Every fee component is detailed clearly.
              </Typography>
            </GlassCard>
          </Grid>
        </Grid>
      </Container>

      {/* Rebuilt Process Stepper Section */}
      <Box id="journey" sx={{ backgroundColor: 'rgba(11,46,89,0.02)', py: 10, mb: 12, borderTop: '1px solid rgba(11,46,89,0.04)', borderBottom: '1px solid rgba(11,46,89,0.04)' }}>
        <Container maxWidth="lg">
          <Typography variant="h2" color="primary" align="center" gutterBottom sx={{ fontWeight: 800, fontFamily: '"Poppins", sans-serif' }}>
            Our Seamless Step-by-Step Journey
          </Typography>
          <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 8, fontWeight: 500 }}>
            Submit details, authenticate, verify limits, and receive disbursal completely online.
          </Typography>

          <Box sx={{ position: 'relative' }}>
            {/* Horizontal Line behind icons (Desktop) */}
            <Box
              sx={{
                display: { xs: 'none', md: 'block' },
                position: 'absolute',
                top: 35,
                left: '10%',
                right: '10%',
                height: '2.5px',
                backgroundImage: 'linear-gradient(to right, rgba(11, 46, 89, 0.2) 33%, rgba(255,255,255,0) 0%)',
                backgroundSize: '12px 100%',
                backgroundRepeat: 'repeat-x',
                zIndex: 1,
              }}
            />
            {/* Vertical Line behind icons (Mobile) */}
            <Box
              sx={{
                display: { xs: 'block', md: 'none' },
                position: 'absolute',
                top: 35,
                bottom: 35,
                left: '50%',
                width: '2px',
                backgroundImage: 'linear-gradient(to bottom, rgba(11, 46, 89, 0.2) 33%, rgba(255,255,255,0) 0%)',
                backgroundSize: '100% 12px',
                backgroundRepeat: 'repeat-y',
                zIndex: 1,
                transform: 'translateX(-50%)',
              }}
            />

            <Grid container spacing={4} justifyContent="space-between" sx={{ position: 'relative', zIndex: 2 }}>
              {processSteps.map((step, idx) => (
                <Grid item xs={12} sm={6} md={2.4} key={idx}>
                  <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center' }}>
                    <Box sx={{
                      width: 70,
                      height: 70,
                      borderRadius: '50%',
                      backgroundColor: '#FFFFFF',
                      border: '2px solid #E2E8F0',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      color: '#0B2E59',
                      boxShadow: '0 8px 24px rgba(15, 23, 42, 0.05)',
                      position: 'relative',
                      transition: 'all 0.3s',
                      '&:hover': {
                        borderColor: '#D4AF37',
                        color: '#D4AF37',
                        transform: 'scale(1.08)',
                        boxShadow: '0 12px 30px rgba(212, 175, 55, 0.2)',
                      }
                    }}>
                      {step.icon}
                      <Box sx={{
                        position: 'absolute',
                        top: -4,
                        right: -4,
                        width: 24,
                        height: 24,
                        borderRadius: '50%',
                        backgroundColor: '#D4AF37',
                        color: '#050D1A',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontWeight: 800,
                        fontSize: '0.75rem',
                        border: '2px solid #FFFFFF'
                      }}>
                        {idx + 1}
                      </Box>
                    </Box>
                    <Typography variant="h6" sx={{ mt: 3, fontWeight: 700, color: '#0B2E59', fontFamily: '"Poppins", sans-serif' }}>{step.title}</Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1, fontSize: '0.85rem', lineHeight: 1.6, px: 2, fontWeight: 500 }}>{step.desc}</Typography>
                  </Box>
                </Grid>
              ))}
            </Grid>
          </Box>
        </Container>
      </Box>

      {/* Interactive Calculators Section */}
      <Container maxWidth="lg" sx={{ mb: 12 }}>
        <Typography variant="h2" color="primary" align="center" gutterBottom sx={{ fontWeight: 800, fontFamily: '"Poppins", sans-serif' }}>
          Evaluate Your Financials
        </Typography>
        <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 8, fontWeight: 500 }}>
          Check limits and plan monthly installments instantly.
        </Typography>

        <Grid container spacing={5}>
          <Grid item xs={12} md={6} id="eligibility">
            <EligibilityCheckerWidget />
          </Grid>
          <Grid item xs={12} md={6} id="emi-calc">
            <EmiCalculatorWidget />
          </Grid>
        </Grid>
      </Container>

      {/* Customer Testimonials Section */}
      <Box sx={{ backgroundColor: 'rgba(212, 175, 55, 0.02)', py: 10, mb: 12, borderTop: '1px solid rgba(212,175,55,0.04)', borderBottom: '1px solid rgba(212,175,55,0.04)' }}>
        <Container maxWidth="lg">
          <Typography variant="h2" color="primary" align="center" gutterBottom sx={{ fontWeight: 800, fontFamily: '"Poppins", sans-serif' }}>
            What Our Customers Say
          </Typography>
          <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 8, fontWeight: 500 }}>
            Hear from our verified clients who secured personal loans through our digital platform.
          </Typography>

          <Grid container spacing={4}>
            {testimonials.map((t, idx) => (
              <Grid item xs={12} md={4} key={idx}>
                <Paper
                  elevation={3}
                  sx={{
                    p: 4,
                    height: '100%',
                    borderRadius: 4,
                    backgroundColor: '#FFFFFF',
                    border: '1px solid rgba(226, 232, 240, 0.8)',
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'space-between',
                    transition: 'all 0.3s',
                    '&:hover': {
                      transform: 'translateY(-5px)',
                      boxShadow: '0 16px 36px rgba(15, 23, 42, 0.08)'
                    }
                  }}
                >
                  <Box>
                    <Stack direction="row" spacing={0.3} sx={{ color: '#D4AF37', mb: 2.5 }}>
                      {[...Array(t.rating)].map((_, i) => (
                        <Star key={i} fontSize="small" />
                      ))}
                    </Stack>
                    <Typography variant="body2" color="text.primary" sx={{ fontStyle: 'italic', lineHeight: 1.7, mb: 3, fontSize: '0.92rem', fontWeight: 500 }}>
                      "{t.feedback}"
                    </Typography>
                  </Box>
                  <Stack direction="row" spacing={2} alignItems="center">
                    <Avatar alt={t.name} src={t.avatar} sx={{ width: 48, height: 48, border: '2px solid #E2E8F0' }} />
                    <Box>
                      <Typography variant="subtitle2" sx={{ fontWeight: 800, color: '#0B2E59' }}>{t.name}</Typography>
                      <Stack direction="row" spacing={0.8} alignItems="center">
                        <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>{t.role}</Typography>
                        <Chip label="VERIFIED" color="success" variant="outlined" size="small" sx={{ fontSize: '0.55rem', height: 16, fontWeight: 800, borderRadius: 1 }} />
                      </Stack>
                    </Box>
                  </Stack>
                </Paper>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Box>

      {/* Accordion FAQs Section */}
      <Container maxWidth="md" sx={{ mb: 12 }}>
        <Typography variant="h2" color="primary" align="center" gutterBottom sx={{ fontWeight: 800, fontFamily: '"Poppins", sans-serif' }}>
          Frequently Asked Questions
        </Typography>
        <Typography variant="body1" color="text.secondary" align="center" sx={{ mb: 6, fontWeight: 500 }}>
          Find answers to common queries regarding application, documents, and disbursal.
        </Typography>

        <Box sx={{ mt: 4 }}>
          <Accordion
            sx={{
              mb: 2,
              borderRadius: '12px !important',
              border: '1px solid rgba(226, 232, 240, 0.8)',
              boxShadow: '0 4px 12px rgba(15, 23, 42, 0.02)',
              '&:before': { display: 'none' },
              '&.Mui-expanded': {
                boxShadow: '0 12px 24px rgba(15, 23, 42, 0.06)',
                borderColor: 'rgba(11, 46, 89, 0.15)',
              }
            }}
          >
            <AccordionSummary expandIcon={<ExpandMore color="primary" />}>
              <Typography variant="h6" sx={{ fontWeight: 700, color: '#0B2E59', py: 0.5 }}>What are the eligibility criteria for a personal loan?</Typography>
            </AccordionSummary>
            <AccordionDetails sx={{ borderTop: '1px solid #F1F5F9', pt: 2.5 }}>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7, fontWeight: 500 }}>
                Applicants must be Indian citizens aged between 21 and 60, with a regular source of monthly income. The minimum salary threshold is ₹25,000 per month, and total monthly debts (proposed loan EMI + current outstanding commitments) must not exceed 50% of monthly gross income.
              </Typography>
            </AccordionDetails>
          </Accordion>

          <Accordion
            sx={{
              mb: 2,
              borderRadius: '12px !important',
              border: '1px solid rgba(226, 232, 240, 0.8)',
              boxShadow: '0 4px 12px rgba(15, 23, 42, 0.02)',
              '&:before': { display: 'none' },
              '&.Mui-expanded': {
                boxShadow: '0 12px 24px rgba(15, 23, 42, 0.06)',
                borderColor: 'rgba(11, 46, 89, 0.15)',
              }
            }}
          >
            <AccordionSummary expandIcon={<ExpandMore color="primary" />}>
              <Typography variant="h6" sx={{ fontWeight: 700, color: '#0B2E59', py: 0.5 }}>What documentation is required for evaluation?</Typography>
            </AccordionSummary>
            <AccordionDetails sx={{ borderTop: '1px solid #F1F5F9', pt: 2.5 }}>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7, fontWeight: 500 }}>
                We utilize a completely digital verification pipeline. You only need your Aadhaar card linked to a mobile number for identity verification, a PAN card for automatic credit bureau evaluations, and access to online banking for income statements.
              </Typography>
            </AccordionDetails>
          </Accordion>

          <Accordion
            sx={{
              mb: 2,
              borderRadius: '12px !important',
              border: '1px solid rgba(226, 232, 240, 0.8)',
              boxShadow: '0 4px 12px rgba(15, 23, 42, 0.02)',
              '&:before': { display: 'none' },
              '&.Mui-expanded': {
                boxShadow: '0 12px 24px rgba(15, 23, 42, 0.06)',
                borderColor: 'rgba(11, 46, 89, 0.15)',
              }
            }}
          >
            <AccordionSummary expandIcon={<ExpandMore color="primary" />}>
              <Typography variant="h6" sx={{ fontWeight: 700, color: '#0B2E59', py: 0.5 }}>How long does it take for loan disbursement?</Typography>
            </AccordionSummary>
            <AccordionDetails sx={{ borderTop: '1px solid #F1F5F9', pt: 2.5 }}>
              <Typography variant="body2" color="text.secondary" sx={{ lineHeight: 1.7, fontWeight: 500 }}>
                Once approved by the system and digital agreements are e-signed, disbursal evaluation is triggered. Under normal operating conditions, the approved loan amount is directly credited to your registered salary account within 2 hours.
              </Typography>
            </AccordionDetails>
          </Accordion>
        </Box>
      </Container>

      {/* Multi-step Apply Now Flow Dialog Wizard */}
      <Dialog 
        open={wizardOpen} 
        onClose={handleCloseWizard} 
        maxWidth="sm" 
        fullWidth 
        PaperProps={{ sx: { borderRadius: 4, overflow: 'hidden', boxShadow: '0 24px 60px rgba(0,0,0,0.15)' } }}
      >
        {/* Header Block */}
        <Box sx={{ background: 'linear-gradient(135deg, #050D1A 0%, #0B2E59 100%)', color: '#FFFFFF', px: 4, py: 3.5, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Box>
            <Typography variant="h5" sx={{ fontWeight: 800, fontFamily: '"Poppins", sans-serif' }}>
              Apply For Personal Loan
            </Typography>
            <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.6)', fontWeight: 600 }}>
              Step {wizardStep + 1} of 5 • Fast Track Pipeline
            </Typography>
          </Box>
          <Box sx={{ display: 'flex', p: 1, borderRadius: 2, backgroundColor: 'rgba(212, 175, 55, 0.1)' }}>
            <Security sx={{ color: '#D4AF37', fontSize: 22 }} />
          </Box>
        </Box>

        <DialogContent sx={{ px: 4, py: 4 }}>
          {wizardError && (
            <Alert severity="error" sx={{ mb: 3, borderRadius: 2, fontWeight: 500 }}>
              {wizardError}
            </Alert>
          )}

          {/* STEP 0: Pre-Evaluation (Inputs) */}
          {wizardStep === 0 && (
            <Box>
              <Typography variant="subtitle1" sx={{ fontWeight: 800, color: '#0B2E59', mb: 1.5 }}>
                Enter Financial Details
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3, fontWeight: 500 }}>
                Please provide accurate estimates to verify standard eligibility criteria.
              </Typography>

              <Stack spacing={3}>
                <TextField
                  label="Gross Monthly Income"
                  variant="outlined"
                  fullWidth
                  value={incomeInput}
                  onChange={(e) => setIncomeInput(e.target.value.replace(/\D/g, ''))}
                  required
                  InputProps={{
                    startAdornment: <InputAdornment position="start" sx={{ color: 'primary.main', fontWeight: 700 }}>₹</InputAdornment>
                  }}
                />
                <TextField
                  label="Existing Monthly EMIs"
                  variant="outlined"
                  fullWidth
                  value={emiInput}
                  onChange={(e) => setEmiInput(e.target.value.replace(/\D/g, ''))}
                  InputProps={{
                    startAdornment: <InputAdornment position="start" sx={{ color: 'primary.main', fontWeight: 700 }}>₹</InputAdornment>
                  }}
                />
                <TextField
                  label="Requested Loan Amount"
                  variant="outlined"
                  fullWidth
                  value={amountInput}
                  onChange={(e) => setAmountInput(e.target.value.replace(/\D/g, ''))}
                  required
                  InputProps={{
                    startAdornment: <InputAdornment position="start" sx={{ color: 'primary.main', fontWeight: 700 }}>₹</InputAdornment>
                  }}
                />
                <TextField
                  label="Requested Tenure (Months)"
                  variant="outlined"
                  fullWidth
                  value={tenureInput}
                  onChange={(e) => setTenureInput(e.target.value.replace(/\D/g, ''))}
                  required
                  InputProps={{
                    endAdornment: <InputAdornment position="end" sx={{ color: 'text.secondary', fontWeight: 700, fontSize: '0.8rem' }}>Months</InputAdornment>
                  }}
                />
              </Stack>
            </Box>
          )}

          {/* STEP 1: Mobile Number Validation */}
          {wizardStep === 1 && (
            <Box>
              <Typography variant="subtitle1" sx={{ fontWeight: 800, color: '#0B2E59', mb: 1.5 }}>
                Enter Mobile Number
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3, fontWeight: 500 }}>
                Please provide your Aadhaar-linked phone number to initiate verification.
              </Typography>

              <TextField
                label="Mobile Number"
                variant="outlined"
                fullWidth
                value={mobileNum}
                onChange={(e) => setMobileNum(e.target.value.replace(/\D/g, ''))}
                required
                placeholder="9876543210"
                InputProps={{
                  startAdornment: <InputAdornment position="start" sx={{ color: 'primary.main', fontWeight: 700 }}>+91</InputAdornment>
                }}
              />
              <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 2.5, fontWeight: 500 }}>
                ℹ️ Demo Note: Odd numbers (e.g. 9876543211) simulate existing accounts; even numbers simulate fresh registrations.
              </Typography>
            </Box>
          )}

          {/* STEP 2: OTP Entry Verification */}
          {wizardStep === 2 && (
            <Box sx={{ textAlign: 'center' }}>
              <Typography variant="subtitle1" sx={{ fontWeight: 800, color: '#0B2E59', mb: 1.5, textAlign: 'left' }}>
                Verify OTP
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 4, textAlign: 'left', fontWeight: 500 }}>
                We sent a security code to **+91 {mobileNum}**. Please enter it below.
              </Typography>

              <TextField
                variant="outlined"
                value={otpCode}
                onChange={(e) => setOtpCode(e.target.value.replace(/\D/g, ''))}
                required
                placeholder="••••••"
                inputProps={{
                  maxLength: 6,
                  style: { textAlign: 'center', letterSpacing: '0.8rem', fontSize: '1.5rem', fontWeight: 800, color: '#0B2E59' }
                }}
                sx={{ width: '80%', maxWidth: 280, mx: 'auto', mb: 3 }}
              />
              <Typography variant="caption" color="text.secondary" sx={{ display: 'block', fontWeight: 600 }}>
                💡 Tip: Use verification code **123456** to proceed.
              </Typography>
            </Box>
          )}

          {/* STEP 3: Register/Login details */}
          {wizardStep === 3 && (
            <Box>
              <Typography variant="subtitle1" sx={{ fontWeight: 800, color: '#0B2E59', mb: 1.5 }}>
                {isExistingUser ? 'Welcome Back! Log In' : 'Create Secure Account'}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3, fontWeight: 500 }}>
                {isExistingUser 
                  ? 'We found an account under this number. Enter credentials to log in.' 
                  : 'Establish credentials to encrypt and preserve your application session.'}
              </Typography>

              <form onSubmit={handleAuthSubmit}>
                <Stack spacing={3}>
                  <TextField
                    label="Email Address"
                    variant="outlined"
                    type="email"
                    fullWidth
                    value={emailInput}
                    onChange={(e) => setEmailInput(e.target.value)}
                    required
                  />
                  <TextField
                    label="Password"
                    variant="outlined"
                    type="password"
                    fullWidth
                    value={passwordInput}
                    onChange={(e) => setPasswordInput(e.target.value)}
                    required
                  />
                  {!isExistingUser && (
                    <TextField
                      label="Confirm Password"
                      variant="outlined"
                      type="password"
                      fullWidth
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      required
                    />
                  )}
                  <Button
                    type="submit"
                    variant="contained"
                    fullWidth
                    disabled={isSubmitting}
                    sx={{ py: 1.8, mt: 1, fontWeight: 700, borderRadius: 2 }}
                  >
                    {isSubmitting ? <CircularProgress size={24} color="inherit" /> : (isExistingUser ? 'Log In & Continue' : 'Create Account & Continue')}
                  </Button>
                </Stack>
              </form>
            </Box>
          )}

          {/* STEP 4: Success Message */}
          {wizardStep === 4 && (
            <Box sx={{ textAlign: 'center', py: 2 }}>
              <CheckCircle color="success" sx={{ fontSize: 64, mb: 2 }} />
              <Typography variant="h5" sx={{ fontWeight: 800, color: '#0B2E59', mb: 1.5 }}>
                Identity Verified Successfully!
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 4, px: 2, fontWeight: 500, lineHeight: 1.7 }}>
                Welcome, **{emailInput}**. Your authentication credentials have been stored securely. Let's head over to the workspace dashboard.
              </Typography>
              <Button
                onClick={() => {
                  handleCloseWizard();
                  navigate('/dashboard');
                }}
                variant="contained"
                size="large"
                endIcon={<ArrowForward />}
                sx={{ py: 1.8, px: 5, borderRadius: 2, fontWeight: 700 }}
              >
                Go To My Dashboard
              </Button>
            </Box>
          )}
        </DialogContent>

        {/* Footer controls for steps 0 to 2 */}
        {wizardStep < 3 && (
          <DialogActions sx={{ px: 4, pb: 4, pt: 0, justifyContent: 'space-between' }}>
            <Button 
              onClick={() => {
                if (wizardStep === 0) handleCloseWizard();
                else setWizardStep(wizardStep - 1);
              }}
              sx={{ fontWeight: 700, color: 'text.secondary' }}
            >
              Back
            </Button>
            <Button
              variant="contained"
              onClick={() => {
                if (wizardStep === 0) handleProceedEligibility();
                else if (wizardStep === 1) handleSendOtp();
                else if (wizardStep === 2) handleVerifyOtp();
              }}
              sx={{ px: 4, py: 1.3, fontWeight: 700, borderRadius: 2 }}
            >
              Continue
            </Button>
          </DialogActions>
        )}
      </Dialog>
    </Box>
  );
};

export default HomePage;
