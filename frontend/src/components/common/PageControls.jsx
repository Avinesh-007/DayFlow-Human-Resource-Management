import { Box, Button, Typography } from '@mui/material';

export default function PageControls({ page, onChange }) {
  if (!page || page.totalPages <= 1) return null;
  return <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 2, px: 1, pt: 2 }}>
    <Typography variant="body2" color="text.secondary">Page {page.number + 1} of {page.totalPages}</Typography>
    <Box sx={{ display: 'flex', gap: 1 }}>
      <Button size="small" variant="outlined" disabled={page.first} onClick={() => onChange(page.number - 1)}>Previous</Button>
      <Button size="small" variant="outlined" disabled={page.last} onClick={() => onChange(page.number + 1)}>Next</Button>
    </Box>
  </Box>;
}
