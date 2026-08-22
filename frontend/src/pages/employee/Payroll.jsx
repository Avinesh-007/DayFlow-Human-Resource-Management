import { useEffect, useState } from 'react';
import { Box, Paper, Stack, Typography } from '@mui/material';
import { getMyPayroll } from '../../services/payrollService';
import { getApiMessage } from '../../services/api';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import EmptyState from '../../components/common/EmptyState';
import PageControls from '../../components/common/PageControls';

const fields = [['basicSalary', 'Basic salary'], ['allowances', 'Allowances'], ['deductions', 'Deductions'], ['grossSalary', 'Gross salary'], ['netSalary', 'Net salary']];
export default function Payroll() {
  const [page, setPage] = useState(null); const [loading, setLoading] = useState(true); const [error, setError] = useState('');
  const load = async (pageNumber = 0) => { setLoading(true); setError(''); try { const response = await getMyPayroll({ page: pageNumber, size: 10 }); setPage(response.data); } catch (requestError) { setError(getApiMessage(requestError, 'Unable to load payroll information.')); } finally { setLoading(false); } };
  useEffect(() => { load(); }, []);
  if (loading) return <LoadingSpinner label="Loading payroll..." />; if (error) return <ErrorMessage message={error} onRetry={load} />;
  return <Stack spacing={3}><Box><Typography variant="h2" sx={{ fontSize: { xs: '2.5rem', md: '4rem' } }}>Payroll</Typography><Typography color="text.secondary">A read-only view of your active salary records.</Typography></Box>{page?.content?.length ? page.content.map((record) => <Paper key={record.id} sx={{ p: { xs: 2, sm: 4 }, border: '1px solid #e4ded4' }}><Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" sx={{ mb: 3 }}><Box><Typography variant="h5">Salary summary</Typography><Typography color="text.secondary">Effective from {record.effectiveFrom}{record.effectiveTo ? ` to ${record.effectiveTo}` : ''}</Typography></Box><Typography variant="body2" color="text.secondary">{record.active ? 'Active record' : 'Inactive record'}</Typography></Stack><Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)' }, gap: 2 }}>{fields.map(([key, label]) => <Box key={key} sx={{ p: 2, bgcolor: key === 'netSalary' ? '#e7e1d6' : '#f6f3ed', borderRadius: 1 }}><Typography variant="caption" color="text.secondary">{label}</Typography><Typography variant={key === 'netSalary' ? 'h5' : 'h6'}>{formatMoney(record[key])}</Typography></Box>)}</Box></Paper>) : <Paper sx={{ p: 2, border: '1px solid #e4ded4' }}><EmptyState>Payroll information is not available.</EmptyState></Paper>}<PageControls page={page} onChange={load} /></Stack>;
}
function formatMoney(value) { return value == null ? '—' : new Intl.NumberFormat(undefined, { style: 'currency', currency: 'INR', maximumFractionDigits: 2 }).format(Number(value)); }
