import React, { useState, useEffect } from 'react';
import { Box, Typography, Slider, Grid, Paper, Divider, Stack } from '@mui/material';
import GlassCard from './GlassCard';

const EmiCalculatorWidget: React.FC = () => {
  const [amount, setAmount] = useState<number>(500000); // Default 5L
  const [tenure, setTenure] = useState<number>(36); // Default 3 years
  const [interestRate, setInterestRate] = useState<number>(11.5); // Default 11.5%
  const [emi, setEmi] = useState<number>(0);
  const [totalInterest, setTotalInterest] = useState<number>(0);
  const [totalPayable, setTotalPayable] = useState<number>(0);

  useEffect(() => {
    const monthlyRate = interestRate / 12 / 100;
    const emiCalc =
      (amount * monthlyRate * Math.pow(1 + monthlyRate, tenure)) /
      (Math.pow(1 + monthlyRate, tenure) - 1);
    
    if (isFinite(emiCalc)) {
      setEmi(Math.round(emiCalc));
      const totalPay = Math.round(emiCalc * tenure);
      setTotalPayable(totalPay);
      setTotalInterest(Math.max(0, totalPay - amount));
    }
  }, [amount, tenure, interestRate]);

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
          border: '1px solid rgba(226, 232, 240, 0.8)',
          boxShadow: '0 20px 40px -4px rgba(15, 23, 42, 0.06)',
          borderRadius: 4
        }}
      >
        <Stack direction="row" spacing={1.5} alignItems="center" sx={{ mb: 1 }}>
          <Box sx={{ width: 6, height: 24, borderRadius: 1, backgroundColor: '#D4AF37' }} />
          <Typography variant="h4" color="primary" sx={{ fontWeight: 800, fontFamily: '"Poppins", sans-serif' }}>
            EMI Calculator
          </Typography>
        </Stack>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 4, fontWeight: 500 }}>
          Plan your financial commitments with our real-time interest calculator.
        </Typography>

        <Grid container spacing={4}>
          <Grid item xs={12} md={7}>
            {/* Amount Slider */}
            <Box sx={{ mb: 4 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1.5 }}>
                <Typography variant="body2" sx={{ fontWeight: 700, color: 'text.secondary', textTransform: 'uppercase', letterSpacing: 0.5 }}>Loan Amount</Typography>
                <Typography variant="body1" color="primary" sx={{ fontWeight: 800, fontSize: '1.1rem' }}>
                  {formatCurrency(amount)}
                </Typography>
              </Box>
              <Slider
                value={amount}
                min={50000}
                max={5000000}
                step={10000}
                onChange={(_, val) => setAmount(val as number)}
                valueLabelDisplay="auto"
                valueLabelFormat={(val) => `${val / 100000} Lakh`}
              />
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 0.5 }}>
                <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>₹50K</Typography>
                <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>₹50 Lakh</Typography>
              </Box>
            </Box>

            {/* Tenure Slider */}
            <Box sx={{ mb: 4 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1.5 }}>
                <Typography variant="body2" sx={{ fontWeight: 700, color: 'text.secondary', textTransform: 'uppercase', letterSpacing: 0.5 }}>Tenure (Months)</Typography>
                <Typography variant="body1" color="primary" sx={{ fontWeight: 800, fontSize: '1.1rem' }}>
                  {tenure} Months ({Math.round((tenure / 12) * 10) / 10} Yrs)
                </Typography>
              </Box>
              <Slider
                value={tenure}
                min={6}
                max={84}
                step={6}
                onChange={(_, val) => setTenure(val as number)}
                valueLabelDisplay="auto"
              />
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 0.5 }}>
                <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>6 Months</Typography>
                <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>84 Months</Typography>
              </Box>
            </Box>

            {/* Interest Rate Slider */}
            <Box sx={{ mb: 2 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1.5 }}>
                <Typography variant="body2" sx={{ fontWeight: 700, color: 'text.secondary', textTransform: 'uppercase', letterSpacing: 0.5 }}>Interest Rate (% P.A.)</Typography>
                <Typography variant="body1" color="primary" sx={{ fontWeight: 800, fontSize: '1.1rem' }}>
                  {interestRate}%
                </Typography>
              </Box>
              <Slider
                value={interestRate}
                min={9}
                max={24}
                step={0.25}
                onChange={(_, val) => setInterestRate(val as number)}
                valueLabelDisplay="auto"
              />
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 0.5 }}>
                <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>9% p.a.</Typography>
                <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>24% p.a.</Typography>
              </Box>
            </Box>
          </Grid>

          <Grid item xs={12} md={5}>
            <Paper
              elevation={4}
              sx={{
                p: 3.5,
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                background: 'linear-gradient(135deg, #050D1A 0%, #0B2E59 100%)',
                color: '#FFFFFF',
                borderRadius: 4,
                boxShadow: '0 16px 36px rgba(11, 46, 89, 0.25)',
                border: '1px solid rgba(255, 255, 255, 0.08)',
              }}
            >
              <Box sx={{ mb: 3.5, textAlign: 'center' }}>
                <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.6)', textTransform: 'uppercase', letterSpacing: 1.5, fontWeight: 700, fontSize: '0.7rem' }}>
                  Proposed Monthly EMI
                </Typography>
                <Typography variant="h3" sx={{ fontWeight: 850, mt: 1, color: '#D4AF37', fontFamily: '"Poppins", sans-serif', letterSpacing: '-0.02em' }}>
                  {formatCurrency(emi)}
                </Typography>
              </Box>

              <Box sx={{ borderTop: '1px dashed rgba(255, 255, 255, 0.15)', pt: 3, display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.65)', fontWeight: 500 }}>Principal Amount</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 700 }}>{formatCurrency(amount)}</Typography>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.65)', fontWeight: 500 }}>Interest Component</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 700, color: 'rgba(255, 255, 255, 0.9)' }}>
                    {formatCurrency(totalInterest)}
                  </Typography>
                </Box>
                <Divider sx={{ borderColor: 'rgba(255, 255, 255, 0.1)', my: 0.5 }} />
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography variant="body1" sx={{ fontWeight: 700, color: 'rgba(255, 255, 255, 0.85)' }}>Total Payable</Typography>
                  <Typography variant="h5" sx={{ fontWeight: 800, color: '#D4AF37' }}>
                    {formatCurrency(totalPayable)}
                  </Typography>
                </Box>
              </Box>
            </Paper>
          </Grid>
        </Grid>
      </GlassCard>
    </Box>
  );
};

export default EmiCalculatorWidget;
