import { Box, Card, CardContent, Typography } from '@mui/material';

function HomePage() {
  return (
    <Box maxWidth="md" mx="auto">
      <Card>
        <CardContent sx={{ p: 4 }}>
          <Typography variant="h4" gutterBottom>
            Welcome
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Personal Loan Application System — Sprint 0 setup complete.
            Business features will be added in upcoming sprints.
          </Typography>
        </CardContent>
      </Card>
    </Box>
  );
}

export default HomePage;
