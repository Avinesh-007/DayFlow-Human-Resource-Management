import { Alert, Button, Stack } from '@mui/material';

export default function ErrorMessage({ message, onRetry }) {
  return <Stack spacing={2}><Alert severity="error">{message}</Alert>{onRetry && <Button onClick={onRetry} variant="outlined" sx={{ alignSelf: 'start' }}>Try again</Button>}</Stack>;
}
