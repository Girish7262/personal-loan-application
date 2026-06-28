import React from 'react';
import { Box, Stepper, Step, StepLabel, Typography } from '@mui/material';

export interface LoanTimelineProps {
  currentStatus: string;
}

const steps = [
  { label: 'DRAFT', desc: 'Application initiated' },
  { label: 'SUBMITTED', desc: 'Submitted for verification' },
  { label: 'VERIFIED', desc: 'Maker officer verified' },
  { label: 'APPROVED', desc: 'Credit checker approved' },
  { label: 'SANCTIONED', desc: 'Sanction letter generated' },
  { label: 'DISBURSED', desc: 'Funds released to customer' }
];

const LoanTimeline: React.FC<LoanTimelineProps> = ({ currentStatus }) => {
  const normalizedStatus = (currentStatus || 'DRAFT').toUpperCase();
  
  let activeStep = 0;
  if (normalizedStatus === 'SUBMITTED') activeStep = 1;
  else if (normalizedStatus === 'VERIFIED') activeStep = 2;
  else if (normalizedStatus === 'APPROVED') activeStep = 3;
  else if (normalizedStatus === 'SANCTIONED') activeStep = 4;
  else if (normalizedStatus === 'DISBURSED') activeStep = 5;
  else if (normalizedStatus === 'CLOSED') activeStep = 6;
  else if (normalizedStatus === 'REJECTED') activeStep = -1;
  else if (normalizedStatus === 'CANCELLED') activeStep = -1;

  if (activeStep === -1) {
    return (
      <Box sx={{ width: '100%', py: 2, textAlign: 'center' }}>
        <Typography variant="body1" color="error" sx={{ fontWeight: 700 }}>
          ❌ Application status is currently {normalizedStatus}
        </Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ width: '100%', py: 2 }}>
      <Stepper activeStep={activeStep} alternativeLabel>
        {steps.map((step) => {
          return (
            <Step key={step.label}>
              <StepLabel>
                <Typography variant="body2" sx={{ fontWeight: 700 }}>
                  {step.label}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {step.desc}
                </Typography>
              </StepLabel>
            </Step>
          );
        })}
      </Stepper>
    </Box>
  );
};

export default LoanTimeline;
