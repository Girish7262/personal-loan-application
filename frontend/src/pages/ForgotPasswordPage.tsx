import React, { useState } from 'react';
import { Box, Typography, Button, TextField, InputAdornment, Link, Alert, CircularProgress } from '@mui/material';
import { LockResetOutlined, EmailOutlined, ArrowBack } from '@mui/icons-material';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link as RouterLink } from 'react-router-dom';
import { authApi } from '@/api/authApi';
import GlassCard from '@/components/ui/GlassCard';

const forgotPasswordSchema = z.object({
  email: z.string().email('Please enter a valid email address'),
});

type ForgotPasswordFormInputs = z.infer<typeof forgotPasswordSchema>;

const ForgotPasswordPage: React.FC = () => {
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ForgotPasswordFormInputs>({
    resolver: zodResolver(forgotPasswordSchema),
  });

  const onSubmit = async (data: ForgotPasswordFormInputs) => {
    setIsLoading(true);
    setErrorMsg(null);
    setSuccessMsg(null);
    try {
      await authApi.forgotPassword(data.email);
      setSuccessMsg('Reset instruction email successfully sent to your registered address.');
    } catch (err: any) {
      setErrorMsg(err?.response?.data?.message || 'Failed to send recovery email. Please check the address.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Box
      sx={{
        background: 'radial-gradient(circle at 10% 20%, rgba(11, 34, 64, 1) 0%, rgba(15, 45, 80, 1) 90.1%)',
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        p: 2,
      }}
    >
      <GlassCard sx={{ width: '100%', maxWidth: 450, p: 4, textAlign: 'center', borderRadius: 5 }}>
        {/* Banking Brand Header */}
        <Box sx={{ mb: 4, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <Box
            sx={{
              display: 'inline-flex',
              p: 1.5,
              borderRadius: 3,
              backgroundColor: 'rgba(197, 168, 128, 0.1)',
              color: 'secondary.main',
              mb: 2,
            }}
          >
            <LockResetOutlined sx={{ fontSize: 32 }} />
          </Box>
          <Typography variant="h3" color="primary" sx={{ fontWeight: 800 }}>
            Reset Password
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            Enter your email to receive recovery instructions
          </Typography>
        </Box>

        {errorMsg && (
          <Alert severity="error" sx={{ mb: 3, textAlign: 'left', borderRadius: 3 }}>
            {errorMsg}
          </Alert>
        )}
        {successMsg && (
          <Alert severity="success" sx={{ mb: 3, textAlign: 'left', borderRadius: 3 }}>
            {successMsg}
          </Alert>
        )}

        <form onSubmit={handleSubmit(onSubmit)}>
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

            <Button
              type="submit"
              variant="contained"
              color="primary"
              fullWidth
              size="large"
              disabled={isLoading}
              sx={{ py: 1.5 }}
            >
              {isLoading ? <CircularProgress size={24} color="inherit" /> : 'Send Recovery Link'}
            </Button>

            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1 }}>
              <ArrowBack color="action" fontSize="small" />
              <Link component={RouterLink} to="/login" color="primary" sx={{ fontWeight: 600, fontSize: '0.875rem' }}>
                Back to Sign In
              </Link>
            </Box>
          </Box>
        </form>
      </GlassCard>
    </Box>
  );
};

export default ForgotPasswordPage;
