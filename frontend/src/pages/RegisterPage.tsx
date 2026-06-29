import React, { useState } from 'react';
import { Box, Typography, Button, TextField, InputAdornment, IconButton, Link, Alert, CircularProgress } from '@mui/material';
import { Visibility, VisibilityOff, LockOutlined, EmailOutlined, PhoneOutlined } from '@mui/icons-material';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { authApi } from '@/api/authApi';
import GlassCard from '@/components/ui/GlassCard';

const registerSchema = z
  .object({
    email: z.string().email('Please enter a valid email address'),
    mobileNumber: z.string().regex(/^\d{10}$/, 'Mobile number must be exactly 10 digits'),
    password: z
      .string()
      .min(6, 'Password must be at least 6 characters')
      .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
      .regex(/[a-z]/, 'Password must contain at least one lowercase letter')
      .regex(/[0-9]/, 'Password must contain at least one number')
      .regex(/[^A-Za-z0-9]/, 'Password must contain at least one special character'),
    confirmPassword: z.string().min(6, 'Confirm password must be at least 6 characters'),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Passwords do not match',
    path: ['confirmPassword'],
  });

type RegisterFormInputs = z.infer<typeof registerSchema>;

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormInputs>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterFormInputs) => {
    setIsLoading(true);
    setErrorMsg(null);
    setSuccessMsg(null);
    try {
      await authApi.register({
        email: data.email,
        mobileNumber: data.mobileNumber,
        password: data.password,
      });
      setSuccessMsg('Registration successful! Verification email sent.');
      setTimeout(() => {
        navigate('/login');
      }, 3000);
    } catch (err: any) {
      setErrorMsg(err?.response?.data?.message || 'Registration failed. Mobile or email already registered.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Box
      sx={{
        background: 'radial-gradient(circle at 50% 50%, rgba(29, 78, 216, 0.15) 0%, rgba(10, 17, 40, 1) 70%), #0A1128',
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        p: 2,
      }}
    >
      <GlassCard sx={{ width: '100%', maxWidth: 500, p: 4, display: 'flex', flexDirection: 'column', alignItems: 'center', borderRadius: 5 }}>
        {/* Banking Brand Header */}
        <Box sx={{ mb: 4, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', p: 0.8, borderRadius: 2, backgroundColor: 'rgba(212, 175, 55, 0.1)', border: '1px solid rgba(212, 175, 55, 0.2)', mb: 2 }}>
            <AccountBalanceIcon sx={{ fontSize: 24, color: '#D4AF37' }} />
          </Box>
          <Typography variant="h5" sx={{ fontWeight: 800, fontFamily: '"Poppins", sans-serif', letterSpacing: '0.02em', display: 'flex', alignItems: 'center', color: '#0B2E59', mb: 1.5 }}>
            APEX
            <Box component="span" sx={{ color: '#D4AF37', fontWeight: 300, ml: 0.5 }}>
              LOAN
            </Box>
          </Typography>
          <Typography variant="h3" color="primary" sx={{ fontWeight: 800, fontFamily: '"Poppins", sans-serif' }}>
            Create Account
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1, textAlign: 'center' }}>
            Sign up to check customized pre-approved loan options.
          </Typography>
        </Box>

        {errorMsg && (
          <Alert severity="error" sx={{ mb: 3, width: '100%', textAlign: 'left', borderRadius: 3 }}>
            {errorMsg}
          </Alert>
        )}
        {successMsg && (
          <Alert severity="success" sx={{ mb: 3, width: '100%', textAlign: 'left', borderRadius: 3 }}>
            {successMsg}
          </Alert>
        )}

        <form onSubmit={handleSubmit(onSubmit)} style={{ width: '100%' }}>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
            <TextField
              label="Email Address"
              variant="outlined"
              fullWidth
              {...register('email')}
              error={!!errors.email}
              helperText={errors.email?.message}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <EmailOutlined color="action" />
                  </InputAdornment>
                ),
              }}
            />

            <TextField
              label="Mobile Number (10 digits)"
              variant="outlined"
              fullWidth
              {...register('mobileNumber')}
              error={!!errors.mobileNumber}
              helperText={errors.mobileNumber?.message}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <PhoneOutlined color="action" />
                  </InputAdornment>
                ),
              }}
            />

            <TextField
              label="Password"
              type={showPassword ? 'text' : 'password'}
              variant="outlined"
              fullWidth
              {...register('password')}
              error={!!errors.password}
              helperText={errors.password?.message}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockOutlined color="action" />
                  </InputAdornment>
                ),
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />

            <TextField
              label="Confirm Password"
              type={showPassword ? 'text' : 'password'}
              variant="outlined"
              fullWidth
              {...register('confirmPassword')}
              error={!!errors.confirmPassword}
              helperText={errors.confirmPassword?.message}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockOutlined color="action" />
                  </InputAdornment>
                ),
              }}
            />

            <Button
              type="submit"
              variant="contained"
              color="primary"
              fullWidth
              size="large"
              disabled={isLoading}
              sx={{ 
                py: 1.8, 
                fontWeight: 700, 
                boxShadow: '0 4px 14px rgba(11, 46, 89, 0.2)',
                background: 'linear-gradient(135deg, #0B2E59 0%, #1D4ED8 100%)',
                '&:hover': {
                  background: 'linear-gradient(135deg, #051833 0%, #0B2E59 100%)',
                  boxShadow: '0 6px 20px rgba(11, 46, 89, 0.3)',
                }
              }}
            >
              {isLoading ? <CircularProgress size={24} color="inherit" /> : 'Register Account'}
            </Button>

            <Typography variant="body2" color="text.secondary" sx={{ textAlign: 'center' }}>
              Already have an account?{' '}
              <Link component={RouterLink} to="/login" color="primary" sx={{ fontWeight: 600, textDecoration: 'none', '&:hover': { color: '#D4AF37' } }}>
                Sign In here
              </Link>
            </Typography>
          </Box>
        </form>
      </GlassCard>
    </Box>
  );
};

export default RegisterPage;
