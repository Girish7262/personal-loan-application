import React, { useState } from 'react';
import {
  Container,
  Grid,
  Typography,
  Box,
  Button,
  Chip,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider,
  Tabs,
  Tab,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Slider,
  Snackbar,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  LinearProgress,
} from '@mui/material';
import {
  Dashboard,
  Person,
  Description,
  Calculate,
  Notifications,
  FolderOpen,
  CloudUpload,
  Visibility,
  ThumbUpAlt,
  Cancel,
} from '@mui/icons-material';
import GlassCard from '@/components/ui/GlassCard';
import LoanTimeline from '@/components/ui/LoanTimeline';

// ==========================================
// MOCK DATA FOR ENTERPRISE DEMONSTRATION
// ==========================================
const INITIAL_LOANS = [
  {
    id: 1,
    number: 'PL-2026-00000001',
    amount: 150000,
    approvedAmount: 140000,
    tenure: 12,
    interestRate: 11.5,
    type: 'Personal Loan',
    status: 'APPROVED',
    purpose: 'Debt Consolidation',
    customerName: 'Girish Patil',
    income: 75000,
    existingEmis: 15000,
    submittedAt: '2026-06-25T10:30:00Z',
    verifiedAt: '2026-06-26T14:20:00Z',
    approvedAt: '2026-06-27T09:15:00Z',
    remarks: 'Approved with minor amount limit adjustment.',
    documents: [
      { id: 1, name: 'Aadhaar_Card.pdf', size: '1.2 MB', hash: 'sha256-a94f82c3...', version: 2 },
      { id: 2, name: 'PAN_Card.png', size: '850 KB', hash: 'sha256-e3b0c442...', version: 1 }
    ]
  },
  {
    id: 2,
    number: 'PL-2026-00000002',
    amount: 500000,
    approvedAmount: 0,
    tenure: 36,
    interestRate: 12.5,
    type: 'Personal Loan',
    status: 'SUBMITTED',
    purpose: 'Home Improvements',
    customerName: 'Aishwarya Sen',
    income: 90000,
    existingEmis: 20000,
    submittedAt: '2026-06-28T11:45:00Z',
    remarks: '',
    documents: [
      { id: 3, name: 'Aadhaar_Front.pdf', size: '2.1 MB', hash: 'sha256-9a2f1c84...', version: 1 },
      { id: 4, name: 'Income_Proof.pdf', size: '3.4 MB', hash: 'sha256-4b8c9d1a...', version: 1 }
    ]
  },
  {
    id: 3,
    number: 'PL-2026-00000003',
    amount: 3000000,
    approvedAmount: 0,
    tenure: 60,
    interestRate: 9.5,
    type: 'Home Loan',
    status: 'VERIFIED',
    purpose: 'Property Purchase',
    customerName: 'Rohan Sharma',
    income: 150000,
    existingEmis: 35000,
    submittedAt: '2026-06-27T16:10:00Z',
    verifiedAt: '2026-06-28T09:30:00Z',
    remarks: 'Documents matched and successfully verified.',
    documents: [
      { id: 5, name: 'Income_Statement.pdf', size: '4.2 MB', hash: 'sha256-ff88b13c...', version: 1 }
    ]
  }
];

const INITIAL_AUDIT_LOGS = [
  { id: 1, action: 'LOGIN_SUCCESS', actor: 'Girish Patil', role: 'CUSTOMER', time: '2026-06-28T22:15:00Z', ip: '192.168.1.5' },
  { id: 2, action: 'LOAN_VERIFICATION', actor: 'Officer Sameer', role: 'LOAN_OFFICER', time: '2026-06-28T09:30:00Z', ip: '192.168.1.12' },
  { id: 3, action: 'LOAN_APPROVAL', actor: 'Manager Anita', role: 'CREDIT_MANAGER', time: '2026-06-27T09:15:00Z', ip: '192.168.1.18' }
];

const DashboardPage: React.FC = () => {
  const [role, setRole] = useState<'CUSTOMER' | 'LOAN_OFFICER' | 'CREDIT_MANAGER' | 'FINANCE_OFFICER' | 'ADMIN'>('CUSTOMER');
  const [activeTab, setActiveTab] = useState(0);
  const [loans, setLoans] = useState<any[]>(INITIAL_LOANS);
  const [auditLogs, setAuditLogs] = useState(INITIAL_AUDIT_LOGS);

  // Apply Loan fields
  const [loanType, setLoanType] = useState('Personal Loan');
  const [reqAmount, setReqAmount] = useState(300000);
  const [tenure, setTenure] = useState(24);
  const [income, setIncome] = useState(65000);
  const [emis, setEmis] = useState(10000);
  const [purpose, setPurpose] = useState('Education');

  // Manager action inputs
  const [selectedLoan, setSelectedLoan] = useState<typeof INITIAL_LOANS[0] | null>(null);
  const [approvedAmtInput, setApprovedAmtInput] = useState(0);
  const [rateInput, setRateInput] = useState(11.5);
  const [remarksInput, setRemarksInput] = useState('');

  // Dialog/Toast state
  const [toast, setToast] = useState<{ open: boolean; message: string; severity: 'success' | 'error' | 'warning' } | null>(null);
  const [detailsOpen, setDetailsOpen] = useState(false);

  // Document Upload Mock file state
  const [, setUploadFile] = useState<File | null>(null);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadedDocs, setUploadedDocs] = useState([
    { id: 1, name: 'PAN_Card.png', size: '850 KB', status: 'CLEAN', hash: 'sha256-e3b0c442...', version: 1 },
    { id: 2, name: 'Aadhaar_Card.pdf', size: '1.2 MB', status: 'CLEAN', hash: 'sha256-a94f82c3...', version: 2 }
  ]);

  const showToast = (message: string, severity: 'success' | 'error' | 'warning' = 'success') => {
    setToast({ open: true, message, severity });
  };

  const handleApplyLoan = (e: React.FormEvent) => {
    e.preventDefault();

    // Verification check for local validation FOIR limits
    const monthlyRate = 11.5 / 12 / 100;
    const proposedEmi = (reqAmount * monthlyRate * Math.pow(1 + monthlyRate, tenure)) /
      (Math.pow(1 + monthlyRate, tenure) - 1);
    
    if (proposedEmi + emis > income * 0.5) {
      showToast('Pre-evaluation warning: Loan exceeds standard debt-to-income limits!', 'warning');
    }

    const newLoan = {
      id: loans.length + 1,
      number: `PL-2026-0000000${loans.length + 1}`,
      amount: reqAmount,
      approvedAmount: 0,
      tenure: tenure,
      interestRate: 11.5,
      type: loanType,
      status: 'SUBMITTED',
      purpose: purpose,
      customerName: 'Girish Patil',
      income: income,
      existingEmis: emis,
      submittedAt: new Date().toISOString(),
      remarks: '',
      documents: [
        { id: Date.now(), name: 'Aadhaar_Front.pdf', size: '1.5 MB', hash: 'sha256-8a7e3d1c...', version: 1 }
      ]
    };

    setLoans([newLoan, ...loans]);
    showToast('Loan Application submitted successfully!');
    setActiveTab(0); // Back to dashboard tab
  };

  const handleVerify = (loanId: number) => {
    setLoans(loans.map(l => l.id === loanId ? { ...l, status: 'VERIFIED', verifiedAt: new Date().toISOString(), remarks: 'Verified by Officer Sameer' } : l));
    showToast('Application successfully marked as VERIFIED.');
  };

  const handleOpenApproval = (loan: typeof INITIAL_LOANS[0]) => {
    setSelectedLoan(loan);
    setApprovedAmtInput(loan.amount);
    setRateInput(loan.interestRate);
    setRemarksInput('');
    setDetailsOpen(true);
  };

  const handleApprove = () => {
    if (!selectedLoan) return;
    setLoans(loans.map(l => l.id === selectedLoan.id ? {
      ...l,
      status: 'APPROVED',
      approvedAmount: approvedAmtInput,
      interestRate: rateInput,
      approvedAt: new Date().toISOString(),
      remarks: remarksInput || 'Approved by Credit Manager.'
    } : l));

    setAuditLogs([
      {
        id: auditLogs.length + 1,
        action: 'LOAN_APPROVAL',
        actor: 'Manager Anita',
        role: 'CREDIT_MANAGER',
        time: new Date().toISOString(),
        ip: '192.168.1.18'
      },
      ...auditLogs
    ]);

    setDetailsOpen(false);
    showToast(`Application approved for ₹${approvedAmtInput.toLocaleString()} at ${rateInput}% interest.`);
  };

  const handleReject = () => {
    if (!selectedLoan) return;
    if (!remarksInput.trim()) {
      showToast('Error: Mandatory remarks are required for rejection!', 'error');
      return;
    }
    setLoans(loans.map(l => l.id === selectedLoan.id ? {
      ...l,
      status: 'REJECTED',
      remarks: remarksInput
    } : l));
    setDetailsOpen(false);
    showToast('Application marked as REJECTED.', 'warning');
  };

  const handleSanction = (loanId: number) => {
    setLoans(loans.map(l => l.id === loanId ? { ...l, status: 'SANCTIONED' } : l));
    showToast('Sanction letter generated and sent.');
  };

  const handleDisburse = (loanId: number) => {
    setLoans(loans.map(l => l.id === loanId ? { ...l, status: 'DISBURSED' } : l));
    showToast('Funds successfully disbursed to customer bank account!');
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      if (file.size > 5 * 1024 * 1024) {
        showToast('Error: Maximum file upload size limit is 5MB!', 'error');
        return;
      }
      setUploadFile(file);
      setIsUploading(true);
      setTimeout(() => {
        setIsUploading(false);
        setUploadedDocs([
          {
            id: Date.now(),
            name: file.name,
            size: `${Math.round(file.size / 1024)} KB`,
            status: 'CLEAN',
            hash: 'sha256-' + Math.random().toString(36).substring(7) + '...',
            version: 1
          },
          ...uploadedDocs
        ]);
        showToast('Document uploaded successfully. Virus scan completed (CLEAN).');
        setUploadFile(null);
      }, 2000);
    }
  };

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0,
    }).format(val);
  };

  const getStatusChipColor = (status: string) => {
    switch (status) {
      case 'DRAFT': return 'default';
      case 'SUBMITTED': return 'info';
      case 'VERIFIED': return 'warning';
      case 'APPROVED': return 'success';
      case 'SANCTIONED': return 'secondary';
      case 'DISBURSED': return 'success';
      default: return 'error';
    }
  };

  return (
    <Container maxWidth="xl" sx={{ mt: 4, mb: 8 }}>
      {/* Top Simulator Header */}
      <GlassCard sx={{ p: 3, mb: 4 }}>
        <Grid container spacing={3} alignItems="center" justifyContent="space-between">
          <Grid item xs={12} md={6}>
            <Typography variant="h3" color="primary" sx={{ fontWeight: 800 }}>
              Enterprise Dashboard
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Review systems, check loan files, verify data, or sanction disbursements.
            </Typography>
          </Grid>
          <Grid item xs={12} md={4} sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            <FormControl size="small" fullWidth sx={{ maxWidth: 280 }}>
              <InputLabel id="role-select-label">Interactive Role Simulator</InputLabel>
              <Select
                labelId="role-select-label"
                value={role}
                label="Interactive Role Simulator"
                onChange={(e) => {
                  setRole(e.target.value as any);
                  setActiveTab(0);
                }}
              >
                <MenuItem value="CUSTOMER">Customer View</MenuItem>
                <MenuItem value="LOAN_OFFICER">Loan Officer (Maker)</MenuItem>
                <MenuItem value="CREDIT_MANAGER">Credit Manager (Checker)</MenuItem>
                <MenuItem value="FINANCE_OFFICER">Finance Officer (Treasury)</MenuItem>
                <MenuItem value="ADMIN">System Administrator</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      </GlassCard>

      {/* CUSTOMER PORTAL */}
      {role === 'CUSTOMER' && (
        <Grid container spacing={4}>
          <Grid item xs={12} md={3}>
            <Paper elevation={0} sx={{ borderRadius: 4, overflow: 'hidden', border: '1px solid rgba(226,232,240,0.8)' }}>
              <Tabs
                orientation="vertical"
                value={activeTab}
                onChange={(_, val) => setActiveTab(val)}
                sx={{
                  borderRight: 1,
                  borderColor: 'divider',
                  backgroundColor: '#FFFFFF',
                  '& .MuiTab-root': {
                    alignItems: 'flex-start',
                    textAlign: 'left',
                    py: 2,
                    px: 3,
                    fontWeight: 600,
                  }
                }}
              >
                <Tab icon={<Dashboard sx={{ mr: 1, verticalAlign: 'middle' }} />} iconPosition="start" label="Overview" />
                <Tab icon={<Person sx={{ mr: 1, verticalAlign: 'middle' }} />} iconPosition="start" label="My Profile" />
                <Tab icon={<FolderOpen sx={{ mr: 1, verticalAlign: 'middle' }} />} iconPosition="start" label="Apply for Loan" />
                <Tab icon={<CloudUpload sx={{ mr: 1, verticalAlign: 'middle' }} />} iconPosition="start" label="Upload Documents" />
                <Tab icon={<Calculate sx={{ mr: 1, verticalAlign: 'middle' }} />} iconPosition="start" label="EMI Schedule" />
                <Tab icon={<Notifications sx={{ mr: 1, verticalAlign: 'middle' }} />} iconPosition="start" label="Notifications" />
              </Tabs>
            </Paper>
          </Grid>

          <Grid item xs={12} md={9}>
            {/* Overview Tab */}
            {activeTab === 0 && (
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                <Grid container spacing={3}>
                  <Grid item xs={12} sm={4}>
                    <GlassCard sx={{ p: 3, textAlign: 'center' }}>
                      <Typography variant="body2" color="text.secondary">Total Active Loans</Typography>
                      <Typography variant="h2" color="primary" sx={{ fontWeight: 800, mt: 1 }}>
                        {loans.filter(l => l.status === 'APPROVED' || l.status === 'DISBURSED').length}
                      </Typography>
                    </GlassCard>
                  </Grid>
                  <Grid item xs={12} sm={4}>
                    <GlassCard sx={{ p: 3, textAlign: 'center' }}>
                      <Typography variant="body2" color="text.secondary">Disbursed Amount</Typography>
                      <Typography variant="h2" color="success.main" sx={{ fontWeight: 800, mt: 1 }}>
                        {formatCurrency(loans.filter(l => l.status === 'DISBURSED').reduce((acc, curr) => acc + curr.approvedAmount, 0))}
                      </Typography>
                    </GlassCard>
                  </Grid>
                  <Grid item xs={12} sm={4}>
                    <GlassCard sx={{ p: 3, textAlign: 'center' }}>
                      <Typography variant="body2" color="text.secondary">Profile Verification</Typography>
                      <Chip label="75% Complete" color="warning" sx={{ mt: 2, fontWeight: 700 }} />
                    </GlassCard>
                  </Grid>
                </Grid>

                <GlassCard sx={{ p: 3 }}>
                  <Typography variant="h4" color="primary" gutterBottom sx={{ fontWeight: 700 }}>
                    My Loan Applications
                  </Typography>
                  <TableContainer component={Paper} elevation={0} sx={{ mt: 2 }}>
                    <Table>
                      <TableHead sx={{ backgroundColor: 'rgba(11,34,64,0.03)' }}>
                        <TableRow>
                          <TableCell sx={{ fontWeight: 700 }}>Application No.</TableCell>
                          <TableCell sx={{ fontWeight: 700 }}>Type</TableCell>
                          <TableCell sx={{ fontWeight: 700 }}>Requested Amount</TableCell>
                          <TableCell sx={{ fontWeight: 700 }}>Interest Rate</TableCell>
                          <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                          <TableCell sx={{ fontWeight: 700 }}>Actions</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {loans.map((row) => (
                          <React.Fragment key={row.id}>
                            <TableRow>
                              <TableCell sx={{ fontWeight: 600 }}>{row.number}</TableCell>
                              <TableCell>{row.type}</TableCell>
                              <TableCell>{formatCurrency(row.amount)}</TableCell>
                              <TableCell>{row.interestRate}%</TableCell>
                              <TableCell>
                                <Chip label={row.status} color={getStatusChipColor(row.status)} size="small" sx={{ fontWeight: 700 }} />
                              </TableCell>
                              <TableCell>
                                <Button
                                  variant="outlined"
                                  size="small"
                                  startIcon={<Visibility />}
                                  onClick={() => {
                                    setSelectedLoan(row);
                                    setDetailsOpen(true);
                                  }}
                                >
                                  Details
                                </Button>
                              </TableCell>
                            </TableRow>
                          </React.Fragment>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </GlassCard>
              </Box>
            )}

            {/* Profile Tab */}
            {activeTab === 1 && (
              <GlassCard sx={{ p: 4 }}>
                <Typography variant="h4" color="primary" gutterBottom sx={{ fontWeight: 700 }}>
                  Profile Information
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
                  Ensure your physical files and monthly numbers match your active database records.
                </Typography>
                <Grid container spacing={3}>
                  <Grid item xs={12} sm={6}>
                    <TextField label="Full Name" defaultValue="Girish Patil" fullWidth />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField label="Mobile Number" defaultValue="9999999999" fullWidth disabled />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField label="PAN Identity" value="XXXXXX452A" fullWidth disabled helperText="Encrypted using AES-256" />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField label="Aadhaar ID" value="XXXXXXXX8950" fullWidth disabled helperText="Encrypted using AES-256" />
                  </Grid>
                  <Grid item xs={12}>
                    <Button variant="contained" color="primary" onClick={() => showToast('Profile details updated successfully.')}>
                      Save Changes
                    </Button>
                  </Grid>
                </Grid>
              </GlassCard>
            )}

            {/* Apply Loan Tab */}
            {activeTab === 2 && (
              <GlassCard sx={{ p: 4 }}>
                <Typography variant="h4" color="primary" gutterBottom sx={{ fontWeight: 700 }}>
                  Apply For A Personal Loan
                </Typography>
                <form onSubmit={handleApplyLoan}>
                  <Grid container spacing={3}>
                    <Grid item xs={12} sm={6}>
                      <FormControl fullWidth>
                        <InputLabel>Loan Type</InputLabel>
                        <Select value={loanType} onChange={(e) => setLoanType(e.target.value)}>
                          <MenuItem value="Personal Loan">Personal Loan (FOIR: 50%)</MenuItem>
                          <MenuItem value="Home Loan">Home Loan (FOIR: 60%)</MenuItem>
                        </Select>
                      </FormControl>
                    </Grid>
                    <Grid item xs={12} sm={6}>
                      <TextField label="Monthly Gross Income (₹)" value={income} onChange={(e) => setIncome(Number(e.target.value))} fullWidth />
                    </Grid>
                    <Grid item xs={12}>
                      <Box sx={{ mb: 1 }}>
                        <Typography variant="body2" sx={{ fontWeight: 600 }}>Requested Loan Amount: {formatCurrency(reqAmount)}</Typography>
                      </Box>
                      <Slider value={reqAmount} min={50000} max={2000000} step={25000} onChange={(_, val) => setReqAmount(val as number)} />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                      <TextField label="Tenure (Months)" value={tenure} onChange={(e) => setTenure(Number(e.target.value))} fullWidth />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                      <TextField label="Existing Monthly EMIs (₹)" value={emis} onChange={(e) => setEmis(Number(e.target.value))} fullWidth />
                    </Grid>
                    <Grid item xs={12}>
                      <TextField label="Purpose of Loan" value={purpose} onChange={(e) => setPurpose(e.target.value)} fullWidth />
                    </Grid>
                    <Grid item xs={12}>
                      <Button type="submit" variant="contained" color="primary" size="large">
                        Submit Application
                      </Button>
                    </Grid>
                  </Grid>
                </form>
              </GlassCard>
            )}

            {/* Document Upload Tab */}
            {activeTab === 3 && (
              <GlassCard sx={{ p: 4 }}>
                <Typography variant="h4" color="primary" gutterBottom sx={{ fontWeight: 700 }}>
                  Upload Application Documents
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
                  Maximum file upload size is 5MB. Supported formats: PDF, JPG, PNG.
                </Typography>

                <Box sx={{ border: '2px dashed rgba(11,34,64,0.15)', borderRadius: 4, p: 5, textAlign: 'center', mb: 4, backgroundColor: 'rgba(11,34,64,0.02)' }}>
                  <CloudUpload sx={{ fontSize: 48, color: 'text.secondary', mb: 2 }} />
                  <Typography variant="body1" sx={{ fontWeight: 600, mb: 1 }}>Drag & drop your files here</Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>or select a file from your device</Typography>
                  
                  <input
                    accept=".pdf,.png,.jpg,.jpeg"
                    style={{ display: 'none' }}
                    id="raised-button-file"
                    type="file"
                    onChange={handleFileUpload}
                    disabled={isUploading}
                  />
                  <label htmlFor="raised-button-file">
                    <Button variant="contained" component="span" disabled={isUploading}>
                      {isUploading ? 'Uploading...' : 'Choose File'}
                    </Button>
                  </label>
                  {isUploading && <LinearProgress sx={{ mt: 2, borderRadius: 2 }} />}
                </Box>

                <Typography variant="h5" sx={{ fontWeight: 700, mb: 2 }}>Uploaded Documents Registry</Typography>
                <List sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
                  {uploadedDocs.map((doc) => (
                    <ListItem key={doc.id} sx={{ backgroundColor: 'rgba(255,255,255,0.7)', borderRadius: 3, border: '1px solid rgba(0,0,0,0.06)' }}>
                      <ListItemIcon>
                        <Description color="primary" />
                      </ListItemIcon>
                      <ListItemText
                        primary={<Typography variant="body2" sx={{ fontWeight: 700 }}>{doc.name} (v{doc.version})</Typography>}
                        secondary={<Typography variant="caption" color="text.secondary">Hash: {doc.hash} | Size: {doc.size}</Typography>}
                      />
                      <Chip label={doc.status} color="success" size="small" sx={{ fontWeight: 700 }} />
                    </ListItem>
                  ))}
                </List>
              </GlassCard>
            )}

            {/* EMI Schedule */}
            {activeTab === 4 && (
              <GlassCard sx={{ p: 4 }}>
                <Typography variant="h4" color="primary" gutterBottom sx={{ fontWeight: 700 }}>
                  Loan Amortization Amortization Schedule
                </Typography>
                <TableContainer component={Paper} elevation={0} sx={{ mt: 2 }}>
                  <Table>
                    <TableHead sx={{ backgroundColor: 'rgba(11,34,64,0.03)' }}>
                      <TableRow>
                        <TableCell sx={{ fontWeight: 700 }}>Month</TableCell>
                        <TableCell sx={{ fontWeight: 700 }}>Principal Paid</TableCell>
                        <TableCell sx={{ fontWeight: 700 }}>Interest Paid</TableCell>
                        <TableCell sx={{ fontWeight: 700 }}>Remaining Balance</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      <TableRow>
                        <TableCell>Month 1</TableCell>
                        <TableCell>₹10,500</TableCell>
                        <TableCell>₹4,200</TableCell>
                        <TableCell>₹1,39,500</TableCell>
                      </TableRow>
                      <TableRow>
                        <TableCell>Month 2</TableCell>
                        <TableCell>₹10,650</TableCell>
                        <TableCell>₹4,050</TableCell>
                        <TableCell>₹1,28,850</TableCell>
                      </TableRow>
                      <TableRow>
                        <TableCell>Month 3</TableCell>
                        <TableCell>₹10,800</TableCell>
                        <TableCell>₹3,900</TableCell>
                        <TableCell>₹1,18,050</TableCell>
                      </TableRow>
                    </TableBody>
                  </Table>
                </TableContainer>
              </GlassCard>
            )}

            {/* Notifications Tab */}
            {activeTab === 5 && (
              <GlassCard sx={{ p: 4 }}>
                <Typography variant="h4" color="primary" gutterBottom sx={{ fontWeight: 700 }}>
                  System Notification Logs
                </Typography>
                <List sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                  <ListItem sx={{ backgroundColor: 'rgba(16,185,129,0.04)', borderRadius: 3, borderLeft: '4px solid #10B981' }}>
                    <ListItemText
                      primary={<Typography variant="body2" sx={{ fontWeight: 700 }}>Loan Approved successfully</Typography>}
                      secondary="Congratulations, application PL-2026-00000001 has been approved for ₹1,40,000 at 11.5% interest."
                    />
                  </ListItem>
                  <ListItem sx={{ backgroundColor: 'rgba(59,130,246,0.04)', borderRadius: 3, borderLeft: '4px solid #3B82F6' }}>
                    <ListItemText
                      primary={<Typography variant="body2" sx={{ fontWeight: 700 }}>Verification Pending</Typography>}
                      secondary="Maker document checker review initiated on application PL-2026-00000002."
                    />
                  </ListItem>
                </List>
              </GlassCard>
            )}
          </Grid>
        </Grid>
      )}

      {/* LOAN_OFFICER (MAKER) PORTAL */}
      {role === 'LOAN_OFFICER' && (
        <Box>
          <GlassCard sx={{ p: 4 }}>
            <Typography variant="h4" color="primary" gutterBottom sx={{ fontWeight: 700 }}>
              Verification Queue (Maker Checking)
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
              Check user submitted identity files and details validation checks.
            </Typography>

            <TableContainer component={Paper} elevation={0}>
              <Table>
                <TableHead sx={{ backgroundColor: 'rgba(11,34,64,0.03)' }}>
                  <TableRow>
                    <TableCell sx={{ fontWeight: 700 }}>Application No.</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Customer Name</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Monthly Income</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Amount</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Action Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {loans.filter(l => l.status === 'SUBMITTED').map((row) => (
                    <TableRow key={row.id}>
                      <TableCell sx={{ fontWeight: 600 }}>{row.number}</TableCell>
                      <TableCell>{row.customerName}</TableCell>
                      <TableCell>{formatCurrency(row.income)}</TableCell>
                      <TableCell>{formatCurrency(row.amount)}</TableCell>
                      <TableCell>
                        <Chip label={row.status} color="info" size="small" sx={{ fontWeight: 700 }} />
                      </TableCell>
                      <TableCell>
                        <Box sx={{ display: 'flex', gap: 1 }}>
                          <Button variant="contained" color="success" size="small" onClick={() => handleVerify(row.id)}>
                            Verify Check
                          </Button>
                          <Button
                            variant="outlined"
                            size="small"
                            onClick={() => {
                              setSelectedLoan(row);
                              setDetailsOpen(true);
                            }}
                          >
                            Files Check
                          </Button>
                        </Box>
                      </TableCell>
                    </TableRow>
                  ))}
                  {loans.filter(l => l.status === 'SUBMITTED').length === 0 && (
                    <TableRow>
                      <TableCell colSpan={6} align="center" sx={{ py: 6, color: 'text.secondary' }}>
                        No pending loan verification documents in queue!
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </GlassCard>
        </Box>
      )}

      {/* CREDIT_MANAGER (CHECKER) PORTAL */}
      {role === 'CREDIT_MANAGER' && (
        <Box>
          <GlassCard sx={{ p: 4 }}>
            <Typography variant="h4" color="primary" gutterBottom sx={{ fontWeight: 700 }}>
              Approval Worklist (Checker Approvals)
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
              Approve limit requests, perform interest override settings, and authorize/reject applications.
            </Typography>

            <TableContainer component={Paper} elevation={0}>
              <Table>
                <TableHead sx={{ backgroundColor: 'rgba(11,34,64,0.03)' }}>
                  <TableRow>
                    <TableCell sx={{ fontWeight: 700 }}>Application No.</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Customer Name</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Income</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Requested Limit</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {loans.filter(l => l.status === 'VERIFIED').map((row) => (
                    <TableRow key={row.id}>
                      <TableCell sx={{ fontWeight: 600 }}>{row.number}</TableCell>
                      <TableCell>{row.customerName}</TableCell>
                      <TableCell>{formatCurrency(row.income)}</TableCell>
                      <TableCell>{formatCurrency(row.amount)}</TableCell>
                      <TableCell>
                        <Chip label={row.status} color="warning" size="small" sx={{ fontWeight: 700 }} />
                      </TableCell>
                      <TableCell>
                        <Button variant="contained" color="primary" size="small" onClick={() => handleOpenApproval(row)}>
                          Review & Authorize
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                  {loans.filter(l => l.status === 'VERIFIED').length === 0 && (
                    <TableRow>
                      <TableCell colSpan={6} align="center" sx={{ py: 6, color: 'text.secondary' }}>
                        No pending verified verification review files.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </GlassCard>
        </Box>
      )}

      {/* FINANCE_OFFICER PORTAL */}
      {role === 'FINANCE_OFFICER' && (
        <Box>
          <GlassCard sx={{ p: 4 }}>
            <Typography variant="h4" color="primary" gutterBottom sx={{ fontWeight: 700 }}>
              Treasury Sanction & Disbursements
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
              Execute final bank disbursements and release checks.
            </Typography>

            <TableContainer component={Paper} elevation={0}>
              <Table>
                <TableHead sx={{ backgroundColor: 'rgba(11,34,64,0.03)' }}>
                  <TableRow>
                    <TableCell sx={{ fontWeight: 700 }}>Application No.</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Customer Name</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Approved Limit</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Rate</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {loans.filter(l => l.status === 'APPROVED' || l.status === 'SANCTIONED').map((row) => (
                    <TableRow key={row.id}>
                      <TableCell sx={{ fontWeight: 600 }}>{row.number}</TableCell>
                      <TableCell>{row.customerName}</TableCell>
                      <TableCell>{formatCurrency(row.approvedAmount || row.amount)}</TableCell>
                      <TableCell>{row.interestRate}%</TableCell>
                      <TableCell>
                        <Chip label={row.status} color={getStatusChipColor(row.status)} size="small" sx={{ fontWeight: 700 }} />
                      </TableCell>
                      <TableCell>
                        <Box sx={{ display: 'flex', gap: 1 }}>
                          {row.status === 'APPROVED' && (
                            <Button variant="contained" color="secondary" size="small" sx={{ color: '#0B2240' }} onClick={() => handleSanction(row.id)}>
                              Generate Sanction Letter
                            </Button>
                          )}
                          {row.status === 'SANCTIONED' && (
                            <Button variant="contained" color="success" size="small" onClick={() => handleDisburse(row.id)}>
                              Release Funds (Disburse)
                            </Button>
                          )}
                        </Box>
                      </TableCell>
                    </TableRow>
                  ))}
                  {loans.filter(l => l.status === 'APPROVED' || l.status === 'SANCTIONED').length === 0 && (
                    <TableRow>
                      <TableCell colSpan={6} align="center" sx={{ py: 6, color: 'text.secondary' }}>
                        No pending approved loans waiting sanction or fund release.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </GlassCard>
        </Box>
      )}

      {/* ADMIN PORTAL */}
      {role === 'ADMIN' && (
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
          <Grid container spacing={3}>
            <Grid item xs={12} sm={4}>
              <GlassCard sx={{ p: 3, textAlign: 'center' }}>
                <Typography variant="body2" color="text.secondary">Database Status</Typography>
                <Typography variant="h3" color="success.main" sx={{ fontWeight: 800, mt: 1 }}>ACTIVE</Typography>
              </GlassCard>
            </Grid>
            <Grid item xs={12} sm={4}>
              <GlassCard sx={{ p: 3, textAlign: 'center' }}>
                <Typography variant="body2" color="text.secondary">Total Registered Users</Typography>
                <Typography variant="h3" color="primary" sx={{ fontWeight: 800, mt: 1 }}>242</Typography>
              </GlassCard>
            </Grid>
            <Grid item xs={12} sm={4}>
              <GlassCard sx={{ p: 3, textAlign: 'center' }}>
                <Typography variant="body2" color="text.secondary">System Log Integrity</Typography>
                <Typography variant="h3" color="secondary.dark" sx={{ fontWeight: 800, mt: 1 }}>VERIFIED</Typography>
              </GlassCard>
            </Grid>
          </Grid>

          <GlassCard sx={{ p: 4 }}>
            <Typography variant="h4" color="primary" gutterBottom sx={{ fontWeight: 700 }}>
              System Audit Trails Log
            </Typography>
            <TableContainer component={Paper} elevation={0} sx={{ mt: 2 }}>
              <Table>
                <TableHead sx={{ backgroundColor: 'rgba(11,34,64,0.03)' }}>
                  <TableRow>
                    <TableCell sx={{ fontWeight: 700 }}>Event Log</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Actor</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Role Context</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>IP Address</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Timestamp</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {auditLogs.map((log) => (
                    <TableRow key={log.id}>
                      <TableCell sx={{ fontWeight: 600 }}>{log.action}</TableCell>
                      <TableCell>{log.actor}</TableCell>
                      <TableCell>{log.role}</TableCell>
                      <TableCell>{log.ip}</TableCell>
                      <TableCell>{log.time}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </GlassCard>
        </Box>
      )}

      {/* DETAIL MODAL / TIMELINE VIEWER */}
      <Dialog open={detailsOpen} onClose={() => setDetailsOpen(false)} maxWidth="md" fullWidth>
        {selectedLoan && (
          <>
            <DialogTitle sx={{ backgroundColor: '#0B2240', color: '#FFFFFF', py: 2 }}>
              Application Detail Tracker - {selectedLoan.number}
            </DialogTitle>
            <DialogContent sx={{ mt: 2 }}>
              <Box sx={{ mb: 3 }}>
                <Typography variant="subtitle2" color="text.secondary">Current Application Stage:</Typography>
                <Box sx={{ mt: 2 }}>
                  <LoanTimeline currentStatus={selectedLoan.status} />
                </Box>
              </Box>

              <Divider sx={{ my: 3 }} />

              <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">Customer Name</Typography>
                  <Typography variant="body1" sx={{ fontWeight: 700, mt: 0.5 }}>{selectedLoan.customerName}</Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">Requested Amount</Typography>
                  <Typography variant="body1" sx={{ fontWeight: 700, mt: 0.5 }}>{formatCurrency(selectedLoan.amount)}</Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">Interest Rate</Typography>
                  <Typography variant="body1" sx={{ fontWeight: 700, mt: 0.5 }}>{selectedLoan.interestRate}%</Typography>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="text.secondary">Tenure (Months)</Typography>
                  <Typography variant="body1" sx={{ fontWeight: 700, mt: 0.5 }}>{selectedLoan.tenure} Months</Typography>
                </Grid>
                <Grid item xs={12}>
                  <Typography variant="body2" color="text.secondary">Purpose of Loan</Typography>
                  <Typography variant="body1" sx={{ mt: 0.5 }}>{selectedLoan.purpose}</Typography>
                </Grid>

                {role === 'CREDIT_MANAGER' && selectedLoan.status === 'VERIFIED' && (
                  <Grid item xs={12} sx={{ borderTop: '1px solid rgba(0,0,0,0.08)', pt: 3 }}>
                    <Typography variant="h5" color="primary" sx={{ fontWeight: 700, mb: 2 }}>Checker Controls</Typography>
                    <Grid container spacing={3}>
                      <Grid item xs={12} sm={6}>
                        <TextField
                          label="Approved Limit (₹)"
                          type="number"
                          value={approvedAmtInput}
                          onChange={(e) => setApprovedAmtInput(Number(e.target.value))}
                          fullWidth
                        />
                      </Grid>
                      <Grid item xs={12} sm={6}>
                        <TextField
                          label="Interest Override (%)"
                          type="number"
                          inputProps={{ step: 0.1 }}
                          value={rateInput}
                          onChange={(e) => setRateInput(Number(e.target.value))}
                          fullWidth
                        />
                      </Grid>
                      <Grid item xs={12}>
                        <TextField
                          label="Remarks (Mandatory for rejection)"
                          value={remarksInput}
                          onChange={(e) => setRemarksInput(e.target.value)}
                          fullWidth
                          multiline
                          rows={2}
                        />
                      </Grid>
                    </Grid>
                  </Grid>
                )}
              </Grid>
            </DialogContent>
            <DialogActions sx={{ p: 3 }}>
              <Button onClick={() => setDetailsOpen(false)} variant="outlined">Close</Button>
              {role === 'CREDIT_MANAGER' && selectedLoan.status === 'VERIFIED' && (
                <>
                  <Button onClick={handleReject} color="error" variant="contained" startIcon={<Cancel />}>
                    Reject Application
                  </Button>
                  <Button onClick={handleApprove} color="success" variant="contained" startIcon={<ThumbUpAlt />}>
                    Authorize & Approve
                  </Button>
                </>
              )}
            </DialogActions>
          </>
        )}
      </Dialog>

      {/* Global Snackbar Toast Notices */}
      {toast && (
        <Snackbar open={toast.open} autoHideDuration={4000} onClose={() => setToast(null)}>
          <Alert onClose={() => setToast(null)} severity={toast.severity} sx={{ width: '100%', borderRadius: 3 }}>
            {toast.message}
          </Alert>
        </Snackbar>
      )}
    </Container>
  );
};

export default DashboardPage;
