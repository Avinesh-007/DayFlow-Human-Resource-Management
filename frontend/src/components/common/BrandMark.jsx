import { Box, Stack, Typography } from '@mui/material';

export default function BrandMark({ compact = false, light = false }) {
  const ink = light ? '#fffaf0' : '#10212b';
  return <Stack direction="row" spacing={1.25} alignItems="center">
    <Box sx={{ width: 34, height: 34, display: 'grid', placeItems: 'center', borderRadius: '11px 11px 11px 4px', bgcolor: '#ef765f', transform: 'rotate(-8deg)', boxShadow: '0 7px 16px rgba(239,118,95,.25)' }}>
      <Box sx={{ width: 15, height: 15, border: '3px solid #fffaf0', borderTopColor: 'transparent', borderRadius: '50%', transform: 'rotate(35deg)' }} />
    </Box>
    {!compact && <Typography sx={{ color: ink, fontSize: '1.25rem', fontWeight: 800, letterSpacing: '-.04em', lineHeight: 1 }}>dayflow<span style={{ color: '#ef765f' }}>.</span></Typography>}
  </Stack>;
}
