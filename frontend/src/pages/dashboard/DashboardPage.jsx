import { useEffect, useState } from 'react';
import { Box, Button, Chip, Paper, Stack, Typography } from '@mui/material';
import { ArrowForward, EventAvailableOutlined, PaymentsOutlined, PersonOutline, WorkOutline } from '@mui/icons-material';
import { Link as RouterLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getApiMessage } from '../../services/api';
import { getEmployeeDashboard } from '../../services/dashboardService';
import { getMyAttendance } from '../../services/attendanceService';
import { getMyPayroll } from '../../services/payrollService';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import EmptyState from '../../components/common/EmptyState';
import StatusChip from '../../components/common/StatusChip';

const actions = [
  ['Profile', 'Review your work details', '/profile', <PersonOutline />],
  ['Attendance', 'Record and review presence', '/attendance', <EventAvailableOutlined />],
  ['Leave', 'Request time away', '/leave', <WorkOutline />],
  ['Payroll', 'Review salary information', '/payroll', <PaymentsOutlined />],
];

export default function DashboardPage() {
  const { user } = useAuth();
  const [dashboard, setDashboard] = useState(null); const [attendance, setAttendance] = useState(null); const [payroll, setPayroll] = useState(null); const [loading, setLoading] = useState(true); const [error, setError] = useState('');
  const load = async () => { setLoading(true); setError(''); try { const monthStart = new Date(); monthStart.setDate(1); const [dashboardResponse, attendanceResponse, payrollResponse] = await Promise.all([getEmployeeDashboard(), getMyAttendance({ startDate: monthStart.toISOString().slice(0, 10), page: 0, size: 31 }), getMyPayroll({ page: 0, size: 1 })]); setDashboard(dashboardResponse.data); setAttendance(attendanceResponse.data); setPayroll(payrollResponse.data); } catch (requestError) { setError(getApiMessage(requestError, 'Unable to load your dashboard.')); } finally { setLoading(false); } };
  useEffect(() => { if (user.role !== 'ADMIN') load(); }, [user.role]);
  if (user.role === 'ADMIN') return <Stack spacing={5}><Typography variant="h2">Admin dashboard</Typography><Typography color="text.secondary">Admin features will be added in the next phase.</Typography></Stack>;
  if (loading) return <LoadingSpinner label="Loading dashboard..." />;
  if (error) return <ErrorMessage message={error} onRetry={load} />;
  const profile = dashboard?.profile; const todayAttendance = dashboard?.todayAttendance; const leaves = dashboard?.recentLeaveRequests || []; const records = attendance?.content || [];
  const presentDays = records.filter((record) => record.status === 'PRESENT').length; const absentDays = records.filter((record) => record.status === 'ABSENT').length; const leaveDays = records.filter((record) => record.status === 'LEAVE').length;
  const name = profile?.firstName || user.email?.split('@')[0] || 'there';
  return <Stack spacing={5}><Box><Chip label="EMPLOYEE WORKSPACE" size="small" sx={{ bgcolor: '#e7e1d6', mb: 2, fontWeight: 700, letterSpacing: '.08em' }} /><Typography variant="h2" sx={{ fontSize: { xs: '2.5rem', md: '4rem' }, mb: 1 }}>Welcome back, {name}.</Typography><Typography color="text.secondary" fontSize="1.1rem">{profile?.employeeId || user.employeeId || 'Your employee workspace'}{profile?.department ? ` · ${profile.department}` : ''}{profile?.designation ? ` · ${profile.designation}` : ''}</Typography></Box><Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)', lg: 'repeat(4, 1fr)' }, gap: 2 }}>{actions.map(([title, copy, path, icon]) => <Paper key={title} component={RouterLink} to={path} sx={{ p: 2.5, textDecoration: 'none', color: 'inherit', border: '1px solid #e4ded4', transition: 'transform .2s, box-shadow .2s', '&:hover': { transform: 'translateY(-4px)', boxShadow: '0 14px 30px rgba(13,27,42,.08)' } }}><Stack direction="row" justifyContent="space-between"><Box sx={{ p: 1, bgcolor: '#f1e9dd', color: 'secondary.main', borderRadius: 2 }}>{icon}</Box><ArrowForward sx={{ color: 'text.secondary' }} /></Stack><Typography variant="h6" sx={{ mt: 3, mb: .5 }}>{title}</Typography><Typography variant="body2" color="text.secondary">{copy}</Typography></Paper>)}</Box><Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: 'repeat(3, 1fr)' }, gap: 2 }}><SummaryCard label="Present this month" value={presentDays} /><SummaryCard label="Absent this month" value={absentDays} /><SummaryCard label="Leave days this month" value={leaveDays} /></Box><Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '1.1fr .9fr' }, gap: 2 }}><Paper sx={{ p: 3, border: '1px solid #e4ded4' }}><Typography variant="overline" color="text.secondary">Today&apos;s attendance</Typography><Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mt: 2 }}><Box><Typography variant="h4">{dashboard?.currentAttendanceStatus?.replace('_', ' ') || 'Not recorded'}</Typography><Typography color="text.secondary" sx={{ mt: 1 }}>{todayAttendance?.checkIn ? `Checked in at ${formatTime(todayAttendance.checkIn)}` : 'No check-in recorded today'}</Typography>{todayAttendance?.checkOut && <Typography color="text.secondary">Checked out at {formatTime(todayAttendance.checkOut)}</Typography>}</Box><StatusChip status={todayAttendance?.status || dashboard?.currentAttendanceStatus} /></Stack><Button component={RouterLink} to="/attendance" endIcon={<ArrowForward />} sx={{ mt: 3, px: 0 }}>Open attendance</Button></Paper><Paper sx={{ p: 3, border: '1px solid #e4ded4' }}><Typography variant="overline" color="text.secondary">Latest payroll</Typography>{payroll?.content?.length ? <><Typography variant="h4" sx={{ mt: 2 }}>{formatMoney(payroll.content[0].netSalary)}</Typography><Typography color="text.secondary">Net salary · effective {payroll.content[0].effectiveFrom}</Typography></> : <EmptyState>Payroll information is not available.</EmptyState>}<Button component={RouterLink} to="/payroll" endIcon={<ArrowForward />} sx={{ mt: 1, px: 0 }}>View payroll</Button></Paper></Box><Paper sx={{ p: 3, border: '1px solid #e4ded4' }}><Typography variant="overline" color="text.secondary">Leave overview</Typography><Typography variant="h3" sx={{ mt: 2 }}>{dashboard?.pendingLeaveCount || 0}</Typography><Typography color="text.secondary">pending request{dashboard?.pendingLeaveCount === 1 ? '' : 's'}</Typography>{leaves.length ? <Typography color="text.secondary" sx={{ mt: 2 }}>{leaves.length} recent request{leaves.length === 1 ? '' : 's'} available to review</Typography> : <EmptyState>No recent leave requests.</EmptyState>}<Button component={RouterLink} to="/leave" endIcon={<ArrowForward />} sx={{ mt: 1, px: 0 }}>View leave history</Button></Paper></Stack>;
}

function formatTime(value) { return new Date(value).toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' }); }
function formatMoney(value) { return value == null ? '—' : new Intl.NumberFormat(undefined, { style: 'currency', currency: 'INR', maximumFractionDigits: 2 }).format(Number(value)); }
function SummaryCard({ label, value }) { return <Paper sx={{ p: 3, border: '1px solid #e4ded4' }}><Typography variant="overline" color="text.secondary">{label}</Typography><Typography variant="h3" sx={{ mt: 1 }}>{value}</Typography></Paper>; }
