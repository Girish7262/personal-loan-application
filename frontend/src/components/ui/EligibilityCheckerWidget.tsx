import React, { useState } from 'react';
import { Box, Typography, Button, TextField, Grid, Collapse, InputAdornment, LinearProgress, Stack } from '@mui/material';
import GlassCard from './GlassCard';
import { CheckCircleOutline, ErrorOutline } from '@mui/icons-material';

const EligibilityCheckerWidget: React.FC = () => {
  const [monthlyIncome, setMonthlyIncome] = useState<string>('75000');
  const [existingEmis, setExistingEmis] = useState<string>('15000');
  const [requestedAmount, setRequestedAmount] = useState<string>('500000');
  const [tenure, setTenure] = useState<string>('36');
  const [result, setResult] = useState<{
    eligible: boolean;
    maxAmount: number;
    proposedEmi: number;
    foir: number;
  } | null>(null);

  const checkEligibility = (e: React.FormEvent) => {
    e.preventDefault();
    const income = parseFloat(monthlyIncome) || 0;
    const currentEmi = parseFloat(existingEmis) || 0;
    const reqAmount = parseFloat(requestedAmount) || 0;
    const months = parseFloat(tenure) || 36;

    // Standard base interest rate 11.5%
    const monthlyRate = 11.5 / 12 / 100;
    const newEmi = (reqAmount * monthlyRate * Math.pow(1 + monthlyRate, months)) /
      (Math.pow(1 + monthlyRate, months) - 1);

    const foirMax = income * 0.50; // FOIR limit 50%
    const totalNewEmi = newEmi + currentEmi;
    const foirRatio = (totalNewEmi / income) * 100;

    const isEligible = totalNewEmi <= foirMax;
    const maxAllowedEmi = Math.max(0, foirMax - currentEmi);
    // Back-calculate max eligible loan amount based on allowed emi
    const maxAmountCalc = (maxAllowedEmi * (Math.pow(1 + monthlyRate, months) - 1)) /
      (monthlyRate * Math.pow(1 + monthlyRate, months));

    setResult({
      eligible: isEligible,
      maxAmount: Math.round(maxAmountCalc),
      proposedEmi: Math.round(newEmi),
      foir: Math.round(foirRatio),
    });
  };

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0,
    }).format(val);
  };

  return (
    <Box sx={{ height: '100%' }}>
      <GlassCard 
        sx={{ 
          p: 4.5, 
          height: '100%', 
          display: 'flex', 
          flexDirection: 'column',
          border: '1px solid rgba(226, 232, 240, 0.8)',
          boxShadow: '0 20px 40px -4px rgba(15, 23, 42, 0.06)',
          borderRadius: 4
        }}
      >
        <Stack direction="row" spacing={1.5} alignItems="center" sx={{ mb: 1 }}>
          <Box sx={{ width: 6, height: 24, borderRadius: 1, backgroundColor: '#D4AF37' }} />
          <Typography variant="h4" color="primary" sx={{ fontWeight: 800, fontFamily: '"Poppins", sans-serif' }}>
            Check Eligibility
          </Typography>
        </Stack>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 4, fontWeight: 500 }}>
          Check your loan qualification limits instantly under safe bank-grade evaluation.
        </Typography>

        <form onSubmit={checkEligibility} style={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
          <Grid container spacing={3} sx={{ mb: 4 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Gross Monthly Income"
                variant="outlined"
                fullWidth
                value={monthlyIncome}
                onChange={(e) => setMonthlyIncome(e.target.value.replace(/\D/g, ''))}
                required
                InputProps={{
                  startAdornment: <InputAdornment position="start" sx={{ color: 'primary.main', fontWeight: 700 }}>₹</InputAdornment>,
                }}
                sx={{
                  '& .MuiInputLabel-root': { fontWeight: 600 },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Existing Monthly EMIs"
                variant="outlined"
                fullWidth
                value={existingEmis}
                onChange={(e) => setExistingEmis(e.target.value.replace(/\D/g, ''))}
                InputProps={{
                  startAdornment: <InputAdornment position="start" sx={{ color: 'primary.main', fontWeight: 700 }}>₹</InputAdornment>,
                }}
                sx={{
                  '& .MuiInputLabel-root': { fontWeight: 600 },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Requested Loan Amount"
                variant="outlined"
                fullWidth
                value={requestedAmount}
                onChange={(e) => setRequestedAmount(e.target.value.replace(/\D/g, ''))}
                required
                InputProps={{
                  startAdornment: <InputAdornment position="start" sx={{ color: 'primary.main', fontWeight: 700 }}>₹</InputAdornment>,
                }}
                sx={{
                  '& .MuiInputLabel-root': { fontWeight: 600 },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Requested Tenure"
                variant="outlined"
                fullWidth
                value={tenure}
                onChange={(e) => setTenure(e.target.value.replace(/\D/g, ''))}
                required
                InputProps={{
                  endAdornment: <InputAdornment position="end" sx={{ color: 'text.secondary', fontWeight: 700, fontSize: '0.8rem' }}>Months</InputAdornment>,
                }}
                sx={{
                  '& .MuiInputLabel-root': { fontWeight: 600 },
                }}
              />
            </Grid>
          </Grid>
          <Button
            type="submit"
            variant="contained"
            color="primary"
            fullWidth
            size="large"
            sx={{ 
              py: 2.2, 
              fontWeight: 700, 
              fontSize: '1rem',
              borderRadius: 2.5,
              textTransform: 'uppercase',
              letterSpacing: 1,
              background: 'linear-gradient(135deg, #0B2E59 0%, #1D4ED8 100%)',
              boxShadow: '0 8px 24px rgba(11, 46, 89, 0.2)',
              '&:hover': {
                background: 'linear-gradient(135deg, #051833 0%, #0B2E59 100%)',
                boxShadow: '0 12px 30px rgba(11, 46, 89, 0.3)',
              }
            }}
          >
            Check My Eligibility
          </Button>
        </form>

        <Collapse in={result !== null}>
          {result && (
            <Box sx={{ mt: 4 }}>
              {result.eligible ? (
                <Box
                  sx={{
                    borderRadius: 4,
                    p: 3.5,
                    backgroundColor: 'rgba(22, 163, 74, 0.03)',
                    border: '1px dashed rgba(22, 163, 74, 0.3)',
                    color: '#0F172A',
                  }}
                >
                  <Stack direction="row" spacing={1.5} alignItems="flex-start" sx={{ mb: 2 }}>
                    <CheckCircleOutline sx={{ color: '#16A34A', fontSize: 26, mt: 0.2 }} />
                    <Box>
                      <Typography variant="body1" sx={{ fontWeight: 800, color: '#16A34A', fontSize: '1.1rem' }}>
                        Congratulations! You qualify for the loan.
                      </Typography>
                      <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 0.5 }}>
                        Based on an industry standard Debt-To-Income Limit (FOIR: 50%)
                      </Typography>
                    </Box>
                  </Stack>

                  <Box sx={{ mt: 3, pt: 3, borderTop: '1px solid rgba(22, 163, 74, 0.1)' }}>
                    <Grid container spacing={3}>
                      <Grid item xs={6} sm={4}>
                        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', fontWeight: 700, letterSpacing: 0.5 }}>PROPOSED EMI</Typography>
                        <Typography variant="h5" sx={{ fontWeight: 800, color: '#0B2E59', mt: 0.5 }}>{formatCurrency(result.proposedEmi)}/mo</Typography>
                      </Grid>
                      <Grid item xs={6} sm={4}>
                        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', fontWeight: 700, letterSpacing: 0.5 }}>DEBT RATIO (FOIR)</Typography>
                        <Typography variant="h5" sx={{ fontWeight: 800, color: result.foir > 40 ? '#D4AF37' : '#16A34A', mt: 0.5 }}>{result.foir}%</Typography>
                      </Grid>
                      <Grid item xs={12} sm={4}>
                        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', fontWeight: 700, letterSpacing: 0.5 }}>MAX APPROVED LIMIT</Typography>
                        <Typography variant="h5" sx={{ fontWeight: 800, color: '#16A34A', mt: 0.5 }}>{formatCurrency(result.maxAmount)}</Typography>
                      </Grid>
                    </Grid>
                  </Box>

                  <Box sx={{ mt: 3 }}>
                    <Stack direction="row" justifyContent="space-between" sx={{ mb: 1 }}>
                      <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700 }}>Income Allocation</Typography>
                      <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 700 }}>{result.foir}% Used</Typography>
                    </Stack>
                    <LinearProgress variant="determinate" value={result.foir} color={result.foir > 40 ? "warning" : "success"} sx={{ height: 6, borderRadius: 3 }} />
                  </Box>
                </Box>
              ) : (
                <Box
                  sx={{
                    borderRadius: 4,
                    p: 3.5,
                    backgroundColor: 'rgba(220, 38, 38, 0.03)',
                    border: '1px dashed rgba(220, 38, 38, 0.3)',
                    color: '#0F172A',
                  }}
                >
                  <Stack direction="row" spacing={1.5} alignItems="flex-start" sx={{ mb: 2 }}>
                    <ErrorOutline sx={{ color: '#DC2626', fontSize: 26, mt: 0.2 }} />
                    <Box>
                      <Typography variant="body1" sx={{ fontWeight: 800, color: '#DC2626', fontSize: '1.1rem' }}>
                        Eligibility Criteria Not Met
                      </Typography>
                      <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 0.5 }}>
                        Debt obligations exceed 50% limit threshold.
                      </Typography>
                    </Box>
                  </Stack>

                  <Box sx={{ mt: 3, pt: 3, borderTop: '1px solid rgba(220, 38, 38, 0.1)' }}>
                    <Grid container spacing={3}>
                      <Grid item xs={6} sm={6}>
                        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', fontWeight: 700, letterSpacing: 0.5 }}>DEBT RATIO</Typography>
                        <Typography variant="h5" sx={{ fontWeight: 800, color: '#DC2626', mt: 0.5 }}>{result.foir}% <Typography variant="caption" color="text.secondary">(Max: 50%)</Typography></Typography>
                      </Grid>
                      <Grid item xs={6} sm={6}>
                        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', fontWeight: 700, letterSpacing: 0.5 }}>MAX LOAN CAPACITY</Typography>
                        <Typography variant="h5" sx={{ fontWeight: 800, color: '#0F172A', mt: 0.5 }}>{formatCurrency(result.maxAmount)}</Typography>
                      </Grid>
                    </Grid>
                  </Box>

                  <Typography variant="body2" sx={{ mt: 3, display: 'flex', alignItems: 'center', gap: 0.8, fontStyle: 'italic', color: '#64748B', fontWeight: 500 }}>
                    💡 Tip: Try increasing the requested loan tenure or lowering the loan principal.
                  </Typography>
                </Box>
              )}
            </Box>
          )}
        </Collapse>
      </GlassCard>
    </Box>
  );
};

export default EligibilityCheckerWidget;
