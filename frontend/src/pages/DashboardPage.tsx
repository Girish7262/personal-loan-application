import React from 'react';
import {
  Container,
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Button,
  Chip,
  CircularProgress,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider,
} from '@mui/material';
import {
  ErrorOutline,
  AddOutlined,
  AccountBoxOutlined,
  AssignmentTurnedInOutlined,
} from '@mui/icons-material';

function DashboardPage() {
  // Mock customer metrics
  const profileComplete = false;
  const completionPercentage: number = 75;
  const missingFields = ['Aadhaar Number', 'Monthly Income Document'];

  // Mock active loans list
  const activeLoans = [
    {
      id: 1,
      number: 'PL-2026-00000001',
      amount: '1,50,000 INR',
      tenure: '12 months',
      type: 'Personal Loan',
      status: 'SUBMITTED',
      statusColor: 'info',
    },
    {
      id: 2,
      number: 'PL-2026-00000002',
      amount: '50,000 INR',
      tenure: '6 months',
      type: 'Personal Loan',
      status: 'APPROVED',
      statusColor: 'success',
    }
  ];

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" component="h1" sx={{ fontWeight: 'bold' }}>
            Welcome back, Girish!
          </Typography>
          <Typography variant="body1" sx={{ color: 'text.secondary', mt: 0.5 }}>
            Manage your personal loan applications and check eligibility
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddOutlined />}
          size="large"
          sx={{ fontWeight: 'bold' }}
        >
          New Loan Application
        </Button>
      </Box>

      <Grid container spacing={4}>
        {/* Onboarding Profile Status Card */}
        <Grid item xs={12} md={4}>
          <Card sx={{ height: '100%', boxShadow: 3, borderRadius: 2 }}>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
                <AccountBoxOutlined color="primary" /> Profile Completion
              </Typography>
              <Divider sx={{ mb: 3 }} />
              
              <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', mb: 3 }}>
                <Box sx={{ position: 'relative', display: 'inline-flex' }}>
                  <CircularProgress
                    variant="determinate"
                    value={completionPercentage}
                    size={100}
                    thickness={5}
                    color={completionPercentage === 100 ? 'success' : 'primary'}
                  />
                  <Box
                    sx={{
                      top: 0,
                      left: 0,
                      bottom: 0,
                      right: 0,
                      position: 'absolute',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                    }}
                  >
                    <Typography variant="h6" component="div" color="text.secondary">
                      {`${completionPercentage}%`}
                    </Typography>
                  </Box>
                </Box>
              </Box>

              <Typography variant="body2" sx={{ textAlign: 'center', color: 'text.secondary', mb: 3 }}>
                {completionPercentage === 100 
                  ? 'Your profile is fully verified and ready for approvals.' 
                  : 'Complete your profile requirements to enable loan disbursements.'}
              </Typography>

              {!profileComplete && missingFields.length > 0 && (
                <>
                  <Typography variant="subtitle2" sx={{ fontWeight: 'bold', color: 'error.main', mb: 1 }}>
                    Missing Requirements:
                  </Typography>
                  <List dense>
                    {missingFields.map((field, idx) => (
                      <ListItem key={idx} disableGutters>
                        <ListItemIcon sx={{ minWidth: 30 }}>
                          <ErrorOutline color="error" fontSize="small" />
                        </ListItemIcon>
                        <ListItemText primary={field} />
                      </ListItem>
                    ))}
                  </List>
                </>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Active Loan Applications List */}
        <Grid item xs={12} md={8}>
          <Card sx={{ height: '100%', boxShadow: 3, borderRadius: 2 }}>
            <CardContent sx={{ p: 3 }}>
              <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
                <AssignmentTurnedInOutlined color="primary" /> Active Loan Requests
              </Typography>
              <Divider sx={{ mb: 2 }} />

              {activeLoans.length === 0 ? (
                <Box sx={{ py: 6, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                  <Typography variant="body1" sx={{ color: 'text.secondary' }}>
                    No active loan applications found.
                  </Typography>
                  <Button variant="outlined" sx={{ mt: 2 }} startIcon={<AddOutlined />}>
                    Apply Now
                  </Button>
                </Box>
              ) : (
                <List>
                  {activeLoans.map((loan, idx) => (
                    <React.Fragment key={loan.id}>
                      <ListItem sx={{ py: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Box>
                          <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                            {loan.number}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            {loan.type} • Amount: {loan.amount} • Tenure: {loan.tenure}
                          </Typography>
                        </Box>
                        <Chip
                          label={loan.status}
                          color={loan.statusColor as any}
                          size="small"
                          sx={{ fontWeight: 'bold' }}
                        />
                      </ListItem>
                      {idx < activeLoans.length - 1 && <Divider />}
                    </React.Fragment>
                  ))}
                </List>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
}

export default DashboardPage;
