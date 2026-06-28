import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import { AppBar, Container, Toolbar, Typography } from '@mui/material';

function Header() {
  return (
    <AppBar position="static" color="primary">
      <Container maxWidth="xl">
        <Toolbar disableGutters>
          <AccountBalanceIcon sx={{ mr: 1.5 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Personal Loan Application System
          </Typography>
        </Toolbar>
      </Container>
    </AppBar>
  );
}

export default Header;
