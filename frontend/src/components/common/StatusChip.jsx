import { Chip } from '@mui/material';

const colors = { PRESENT: 'success', APPROVED: 'success', PENDING: 'warning', ABSENT: 'error', REJECTED: 'error', HALF_DAY: 'info', LEAVE: 'default' };
export default function StatusChip({ status }) {
  if (!status) return <Chip label="Not recorded" size="small" variant="outlined" />;
  return <Chip label={status.replace('_', ' ')} size="small" color={colors[status] || 'default'} sx={{ fontWeight: 700 }} />;
}
