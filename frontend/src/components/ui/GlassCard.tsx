import React from 'react';
import { Card, CardProps } from '@mui/material';
import { styled } from '@mui/material/styles';

const StyledGlassCard = styled(Card)(() => ({
  background: 'rgba(255, 255, 255, 0.85)',
  backdropFilter: 'blur(12px)',
  WebkitBackdropFilter: 'blur(12px)',
  border: '1px solid rgba(255, 255, 255, 0.4)',
  boxShadow: '0 8px 32px 0 rgba(15, 23, 42, 0.04)',
  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
  '&:hover': {
    transform: 'translateY(-4px)',
    boxShadow: '0 16px 40px 0 rgba(15, 23, 42, 0.08)',
    border: '1px solid rgba(197, 168, 128, 0.3)', // Subtle premium Gold border on hover
  },
}));

export interface GlassCardProps extends CardProps {
  children?: React.ReactNode;
}

const GlassCard: React.FC<GlassCardProps> = ({ children, ...props }) => {
  return <StyledGlassCard {...props}>{children}</StyledGlassCard>;
};

export default GlassCard;
