import React, { useState, useEffect } from 'react';
import { Box, Typography, Slider, Grid, Paper } from '@mui/material';
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
    <GlassCard sx={{ p: 4 }}>
      <Typography variant="h4" color="primary" gutterBottom sx={{ fontWeight: 700 }}>
        EMI Calculator
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
        Plan your finances with our intuitive loan installment calculator.
      </Typography>

      <Grid container spacing={4}>
        <Grid item xs={12} md={7}>
          {/* Amount Slider */}
          <Box sx={{ mb: 4 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography variant="body1" sx={{ fontWeight: 600 }}>Loan Amount</Typography>
              <Typography variant="body1" color="primary" sx={{ fontWeight: 700 }}>
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
            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
              <Typography variant="caption" color="text.secondary">₹50K</Typography>
              <Typography variant="caption" color="text.secondary">₹50 Lakh</Typography>
            </Box>
          </Box>

          {/* Tenure Slider */}
          <Box sx={{ mb: 4 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography variant="body1" sx={{ fontWeight: 600 }}>Tenure (Months)</Typography>
              <Typography variant="body1" color="primary" sx={{ fontWeight: 700 }}>
                {tenure} Months ({Math.round((tenure / 12) * 10) / 10} Years)
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
            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
              <Typography variant="caption" color="text.secondary">6 Months</Typography>
              <Typography variant="caption" color="text.secondary">84 Months</Typography>
            </Box>
          </Box>

          {/* Interest Rate Slider */}
          <Box sx={{ mb: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography variant="body1" sx={{ fontWeight: 600 }}>Interest Rate (% P.A.)</Typography>
              <Typography variant="body1" color="primary" sx={{ fontWeight: 700 }}>
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
            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
              <Typography variant="caption" color="text.secondary">9%</Typography>
              <Typography variant="caption" color="text.secondary">24%</Typography>
            </Box>
          </Box>
        </Grid>

        <Grid item xs={12} md={5}>
          <Paper
            elevation={0}
            sx={{
              p: 3,
              height: '100%',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'center',
              backgroundColor: 'rgba(11, 34, 64, 0.03)',
              borderRadius: 4,
              border: '1px dashed rgba(11, 34, 64, 0.1)',
            }}
          >
            <Box sx={{ mb: 3, textAlign: 'center' }}>
              <Typography variant="body2" color="text.secondary" sx={{ textTransform: 'uppercase', letterSpacing: 1, fontWeight: 600 }}>
                Monthly Installment (EMI)
              </Typography>
              <Typography variant="h3" color="primary" sx={{ fontWeight: 800, mt: 1 }}>
                {formatCurrency(emi)}
              </Typography>
            </Box>

            <Box sx={{ borderTop: '1px solid rgba(15, 23, 42, 0.08)', pt: 2 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1.5 }}>
                <Typography variant="body2" color="text.secondary">Principal Amount</Typography>
                <Typography variant="body2" sx={{ fontWeight: 600 }}>{formatCurrency(amount)}</Typography>
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1.5 }}>
                <Typography variant="body2" color="text.secondary">Interest Payable</Typography>
                <Typography variant="body2" color="secondary.dark" sx={{ fontWeight: 600 }}>
                  {formatCurrency(totalInterest)}
                </Typography>
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', borderTop: '1px solid rgba(15, 23, 42, 0.08)', pt: 1.5 }}>
                <Typography variant="body1" sx={{ fontWeight: 700 }}>Total Payable</Typography>
                <Typography variant="body1" color="primary" sx={{ fontWeight: 800 }}>
                  {formatCurrency(totalPayable)}
                </Typography>
              </Box>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </GlassCard>
  );
};

export default EmiCalculatorWidget;
