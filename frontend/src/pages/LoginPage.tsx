import React, { useState } from 'react';
import { Box, Typography, Button, TextField, InputAdornment, IconButton, Link, Alert, CircularProgress } from '@mui/material';
import { Visibility, VisibilityOff, LockOutlined, EmailOutlined } from '@mui/icons-material';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { authApi } from '@/api/authApi';
import GlassCard from '@/components/ui/GlassCard';

const loginSchema = z.object({
  email: z.string().email('Please enter a valid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginFormInputs = z.infer<typeof loginSchema>;

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormInputs>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormInputs) => {
    setIsLoading(true);
    setErrorMsg(null);
    try {
      const response = await authApi.login({
        email: data.email,
        password: data.password,
      });
      localStorage.setItem('token', response.token);
      localStorage.setItem('user', JSON.stringify(response.user));
      navigate('/dashboard');
    } catch (err: any) {
      setErrorMsg(err?.response?.data?.message || 'Login failed. Invalid email or password.');
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
      <GlassCard sx={{ width: '100%', maxWidth: 450, p: 4, display: 'flex', flexDirection: 'column', alignItems: 'center', borderRadius: 5 }}>
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
            Portal Sign In
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1, textAlign: 'center' }}>
            Access your secure personal loan digital account workspace.
          </Typography>
        </Box>

        {errorMsg && (
          <Alert severity="error" sx={{ mb: 3, width: '100%', textAlign: 'left', borderRadius: 3 }}>
            {errorMsg}
          </Alert>
        )}

        <form onSubmit={handleSubmit(onSubmit)} style={{ width: '100%' }}>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
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

            <Box sx={{ textAlign: 'right' }}>
              <Link component={RouterLink} to="/forgot-password" variant="body2" color="primary" sx={{ fontWeight: 600, textDecoration: 'none', '&:hover': { color: '#D4AF37' } }}>
                Forgot password?
              </Link>
            </Box>

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
              {isLoading ? <CircularProgress size={24} color="inherit" /> : 'Secure Sign In'}
            </Button>

            <Typography variant="body2" color="text.secondary" sx={{ textAlign: 'center' }}>
              Don't have an account?{' '}
              <Link component={RouterLink} to="/register" color="primary" sx={{ fontWeight: 600, textDecoration: 'none', '&:hover': { color: '#D4AF37' } }}>
                Register here
              </Link>
            </Typography>
          </Box>
        </form>
      </GlassCard>
    </Box>
  );
};

export default LoginPage;
