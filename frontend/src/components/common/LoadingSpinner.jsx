import { Box, CircularProgress, Typography } from '@mui/material';

export default function LoadingSpinner({ label = 'Loading...' }) {
  return <Box sx={{ minHeight: 240, display: 'grid', placeItems: 'center', gap: 1 }}><Box sx={{ textAlign: 'center' }}><CircularProgress size={30} /><Typography color="text.secondary" sx={{ mt: 2 }}>{label}</Typography></Box></Box>;
}
