import React, { useState } from 'react';
import { Box, Typography, Button, TextField, Grid, Collapse, Alert, AlertTitle } from '@mui/material';
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
    <GlassCard sx={{ p: 4, height: '100%' }}>
      <Typography variant="h4" color="primary" gutterBottom sx={{ fontWeight: 700 }}>
        Eligibility Checker
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
        Check if you qualify for a personal loan in less than a minute.
      </Typography>

      <form onSubmit={checkEligibility}>
        <Grid container spacing={3}>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Gross Monthly Income (₹)"
              variant="outlined"
              fullWidth
              value={monthlyIncome}
              onChange={(e) => setMonthlyIncome(e.target.value.replace(/\D/g, ''))}
              required
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Existing Monthly EMIs (₹)"
              variant="outlined"
              fullWidth
              value={existingEmis}
              onChange={(e) => setExistingEmis(e.target.value.replace(/\D/g, ''))}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Requested Loan Amount (₹)"
              variant="outlined"
              fullWidth
              value={requestedAmount}
              onChange={(e) => setRequestedAmount(e.target.value.replace(/\D/g, ''))}
              required
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Tenure (Months)"
              variant="outlined"
              fullWidth
              value={tenure}
              onChange={(e) => setTenure(e.target.value.replace(/\D/g, ''))}
              required
            />
          </Grid>
          <Grid item xs={12}>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              fullWidth
              size="large"
              sx={{ py: 1.5, mt: 1 }}
            >
              Check My Eligibility
            </Button>
          </Grid>
        </Grid>
      </form>

      <Collapse in={result !== null}>
        {result && (
          <Box sx={{ mt: 4 }}>
            {result.eligible ? (
              <Alert
                icon={<CheckCircleOutline fontSize="inherit" />}
                severity="success"
                sx={{ borderRadius: 3, border: '1px solid rgba(16, 185, 129, 0.2)' }}
              >
                <AlertTitle sx={{ fontWeight: 700 }}>Congratulations! You are Eligible</AlertTitle>
                Based on your monthly income of **{formatCurrency(parseFloat(monthlyIncome))}**, you qualify for this loan request.
                <Box sx={{ mt: 2, display: 'flex', flexDirection: 'column', gap: 0.5 }}>
                  <Typography variant="body2">Proposed Monthly EMI: **{formatCurrency(result.proposedEmi)}**</Typography>
                  <Typography variant="body2">Debt-to-Income (FOIR): **{result.foir}%** (Limit: 50%)</Typography>
                  <Typography variant="body2">Max Eligible Limit: **{formatCurrency(result.maxAmount)}**</Typography>
                </Box>
              </Alert>
            ) : (
              <Alert
                icon={<ErrorOutline fontSize="inherit" />}
                severity="error"
                sx={{ borderRadius: 3, border: '1px solid rgba(239, 68, 68, 0.2)' }}
              >
                <AlertTitle sx={{ fontWeight: 700 }}>Eligibility Limit Exceeded</AlertTitle>
                Your total monthly debt commitments exceed 50% of your gross income.
                <Box sx={{ mt: 2, display: 'flex', flexDirection: 'column', gap: 0.5 }}>
                  <Typography variant="body2">Current FOIR: **{result.foir}%** (Max Allowed: 50%)</Typography>
                  <Typography variant="body2">Maximum eligible loan amount: **{formatCurrency(result.maxAmount)}**</Typography>
                  <Typography variant="body2" sx={{ mt: 1, fontStyle: 'italic' }}>
                    💡 Tip: Try increasing your tenure or reducing requested amount.
                  </Typography>
                </Box>
              </Alert>
            )}
          </Box>
        )}
      </Collapse>
    </GlassCard>
  );
};

export default EligibilityCheckerWidget;
