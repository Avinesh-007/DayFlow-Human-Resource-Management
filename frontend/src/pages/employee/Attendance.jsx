import { useEffect, useState } from 'react';
import { Box, Button, Paper, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography } from '@mui/material';
import { toast } from 'react-toastify';
import { checkIn, checkOut, getMyAttendance } from '../../services/attendanceService';
import { getApiMessage } from '../../services/api';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import EmptyState from '../../components/common/EmptyState';
import StatusChip from '../../components/common/StatusChip';
import PageControls from '../../components/common/PageControls';

export default function Attendance() {
  const [page, setPage] = useState(null);
  const [monthlyPage, setMonthlyPage] = useState(null);
  const [filters, setFilters] = useState({ startDate: '', endDate: '' });
  const [loading, setLoading] = useState(true);
  const [action, setAction] = useState('');
  const [error, setError] = useState('');

  const load = async (next = filters, pageNumber = 0) => {
    setLoading(true); setError('');
    try {
      const monthStart = new Date();
      monthStart.setDate(1);
      const [response, monthlyResponse] = await Promise.all([
        getMyAttendance({ ...next, page: pageNumber, size: 10 }),
        getMyAttendance({ startDate: monthStart.toISOString().slice(0, 10), page: 0, size: 31 }),
      ]);
      setPage(response.data); setMonthlyPage(monthlyResponse.data);
    } catch (requestError) {
      setError(getApiMessage(requestError, 'Unable to load attendance.'));
    } finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const attendanceToday = page?.content?.find((record) => record.date === new Date().toISOString().slice(0, 10));
  const monthlyRecords = monthlyPage?.content || [];
  const monthlyPresent = monthlyRecords.filter((record) => record.status === 'PRESENT').length;
  const monthlyAbsent = monthlyRecords.filter((record) => record.status === 'ABSENT').length;
  const monthlyLeave = monthlyRecords.filter((record) => record.status === 'LEAVE').length;

  const act = async (type) => {
    setAction(type);
    try {
      const response = type === 'in' ? await checkIn() : await checkOut();
      toast.success(response.message || `Checked ${type === 'in' ? 'in' : 'out'} successfully.`);
      await load();
    } catch (requestError) {
      toast.error(getApiMessage(requestError, 'Attendance action failed.'));
    } finally { setAction(''); }
  };

  if (loading) return <LoadingSpinner label="Loading attendance..." />;
  if (error) return <ErrorMessage message={error} onRetry={() => load()} />;

  return <Stack spacing={3}>
    <Box><Typography variant="h2" sx={{ fontSize: { xs: '2.5rem', md: '4rem' } }}>Attendance</Typography><Typography color="text.secondary">Record today and review your attendance history.</Typography></Box>
    <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(3, 1fr)' }, gap: 2 }}><SummaryCard label="Present this month" value={monthlyPresent} /><SummaryCard label="Absent this month" value={monthlyAbsent} /><SummaryCard label="Leave this month" value={monthlyLeave} /></Box>
    <Paper sx={{ p: 3, border: '1px solid #e4ded4' }}><Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems={{ sm: 'center' }} justifyContent="space-between"><Box><Typography variant="h6">Today</Typography><Typography color="text.secondary">{attendanceToday ? `Started ${formatDateTime(attendanceToday.checkIn)}` : 'No attendance recorded yet.'}</Typography></Box><Stack direction="row" spacing={1}><Button variant="contained" onClick={() => act('in')} disabled={Boolean(action) || Boolean(attendanceToday)}>{action === 'in' ? 'Checking in...' : 'Check in'}</Button><Button variant="outlined" onClick={() => act('out')} disabled={Boolean(action) || !attendanceToday?.checkIn || Boolean(attendanceToday?.checkOut)}>{action === 'out' ? 'Checking out...' : 'Check out'}</Button></Stack></Stack></Paper>
    <Paper sx={{ p: 2, border: '1px solid #e4ded4' }}><Stack component="form" onSubmit={(event) => { event.preventDefault(); load(filters); }} direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ p: 1 }}><TextField type="date" label="From" InputLabelProps={{ shrink: true }} value={filters.startDate} onChange={(event) => setFilters({ ...filters, startDate: event.target.value })} /><TextField type="date" label="To" InputLabelProps={{ shrink: true }} value={filters.endDate} onChange={(event) => setFilters({ ...filters, endDate: event.target.value })} /><Button type="submit" variant="outlined">Filter</Button></Stack><TableContainer><Table sx={{ minWidth: 620 }}><TableHead><TableRow>{['Date', 'Check in', 'Check out', 'Hours', 'Status'].map((heading) => <TableCell key={heading}>{heading}</TableCell>)}</TableRow></TableHead><TableBody>{page?.content?.length ? page.content.map((record) => <TableRow key={record.id}><TableCell>{record.date}</TableCell><TableCell>{formatDateTime(record.checkIn)}</TableCell><TableCell>{formatDateTime(record.checkOut)}</TableCell><TableCell>{record.workingHours ?? '—'}</TableCell><TableCell><StatusChip status={record.status} /></TableCell></TableRow>) : <TableRow><TableCell colSpan={5}><EmptyState>No attendance records found.</EmptyState></TableCell></TableRow>}</TableBody></Table></TableContainer><PageControls page={page} onChange={(pageNumber) => load(filters, pageNumber)} /></Paper>
  </Stack>;
}

const formatDateTime = (value) => value ? new Date(value).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' }) : '—';
function SummaryCard({ label, value }) { return <Paper sx={{ p: 3, border: '1px solid #e4ded4' }}><Typography variant="overline" color="text.secondary">{label}</Typography><Typography variant="h3" sx={{ mt: 1 }}>{value}</Typography></Paper>; }
