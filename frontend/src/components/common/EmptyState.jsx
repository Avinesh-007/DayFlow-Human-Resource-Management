import { Box, Typography } from '@mui/material';

export default function EmptyState({ children }) {
  return <Box sx={{ py: 5, textAlign: 'center', color: 'text.secondary' }}><Typography>{children}</Typography></Box>;
}
