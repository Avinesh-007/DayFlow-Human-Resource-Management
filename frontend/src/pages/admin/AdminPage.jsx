import { useEffect, useState } from 'react';
import {
  Alert,
  Button,
  Chip,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { toast } from 'react-toastify';
import { getApiMessage } from '../../services/api';
import {
  getAttendance,
  getEmployees,
  getLeaveRequests,
  getPayrollRecords,
  createPayroll,
  reviewLeave,
  updateEmployee,
  updatePayroll,
} from '../../services/adminService';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const config = {
  employees: { title: 'Employees', subtitle: 'View and manage the people in Dayflow.', load: getEmployees },
  attendance: { title: 'Attendance', subtitle: 'Review attendance across the organization.', load: getAttendance },
  leave: { title: 'Leave approvals', subtitle: 'Review pending and completed leave requests.', load: getLeaveRequests },
  payroll: { title: 'Payroll', subtitle: 'Review payroll records for every employee.', load: getPayrollRecords },
};

function display(value) {
  return value == null || value === '' ? '—' : value;
}

function rowsFor(section, data) {
  if (section === 'employees') return data.map((item) => [item.employeeId, `${item.firstName || ''} ${item.lastName || ''}`.trim(), item.email, item.department, item.designation, item.employmentType, item.active ? 'Active' : 'Inactive', item.id]);
  if (section === 'attendance') return data.map((item) => [item.employeeId, item.employeeName, item.date, item.status, item.checkInTime || item.checkIn, item.checkOutTime || item.checkOut]);
  if (section === 'leave') return data.map((item) => [item.employeeId, item.employeeName, item.leaveType, `${item.startDate} to ${item.endDate}`, item.numberOfDays, item.status, item.id]);
  return data.map((item) => [item.employeeId, item.employeeName, item.effectiveFrom, item.grossSalary, item.netSalary, item.active ? 'Active' : 'Inactive', item.id]);
}

export default function AdminPage({ section }) {
  const current = config[section];
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [busyId, setBusyId] = useState(null);
  const [comments, setComments] = useState({});
  const [edits, setEdits] = useState({});
  const [editingPayrollId, setEditingPayrollId] = useState(null);
  const [editingEmployeeId, setEditingEmployeeId] = useState(null);
  const [employeeEdits, setEmployeeEdits] = useState({});

  const load = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await current.load({ page: 0, size: 50 });
      setItems(response.data?.data?.content || response.data?.content || []);
    } catch (requestError) {
      setError(getApiMessage(requestError, `Unable to load ${current.title.toLowerCase()}.`));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [section]);

  const actOnLeave = async (id, status) => {
    setBusyId(id);
    try {
      await reviewLeave(id, status, comments[id]);
      await load();
    } catch (requestError) {
      setError(getApiMessage(requestError, 'Unable to review this leave request.'));
    } finally {
      setBusyId(null);
    }
  };

  const savePayroll = async (item) => {
    const values = edits[item.id] || item;
    const basicSalary = Number(values.basicSalary);
    const allowances = Number(values.allowances);
    const deductions = Number(values.deductions);
    if (!Number.isFinite(basicSalary) || basicSalary <= 0 || !Number.isFinite(allowances) || allowances < 0 || !Number.isFinite(deductions) || deductions < 0) {
      setError('Enter a valid basic salary, allowance, and deduction amount.');
      return;
    }
    setBusyId(item.id);
    setError('');
    try {
      await updatePayroll(item.id, { employeeId: item.employeeId, basicSalary, allowances, deductions, effectiveFrom: values.effectiveFrom, effectiveTo: values.effectiveTo || null });
      await load();
      setEditingPayrollId(null);
      toast.success(`Salary updated for ${item.employeeId}.`);
    } catch (requestError) {
      setError(getApiMessage(requestError, 'Unable to update this salary structure.'));
    } finally {
      setBusyId(null);
    }
  };
  const saveEmployee = async (item) => {
    const values = employeeEdits[item.id] || item;
    setBusyId(item.id);
    setError('');
    try {
      await updateEmployee(item.id, { firstName: values.firstName, lastName: values.lastName, email: values.email, dateOfJoining: values.dateOfJoining, department: values.department, designation: values.designation, employmentType: values.employmentType });
      await load();
      setEditingEmployeeId(null);
      toast.success(`Employee details updated for ${item.employeeId}.`);
    } catch (requestError) {
      setError(getApiMessage(requestError, 'Unable to update employee details.'));
    } finally {
      setBusyId(null);
    }
  };

  const headings = section === 'employees'
    ? ['Employee ID', 'Name', 'Email', 'Department', 'Designation', 'Employment type', 'Status', 'Update details']
    : section === 'attendance'
      ? ['Employee ID', 'Name', 'Date', 'Status', 'Check in', 'Check out']
      : section === 'leave'
        ? ['Employee ID', 'Name', 'Type', 'Dates', 'Days', 'Status', '']
        : ['Employee ID', 'Name', 'Effective from', 'Gross salary', 'Net salary', 'Status', 'Update salary'];
  const rows = rowsFor(section, items);

  return (
    <Stack spacing={4}>
      <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" spacing={2}>
        <BoxTitle title={current.title} subtitle={current.subtitle} />
        <Button component={RouterLink} to="/dashboard" variant="outlined">Admin dashboard</Button>
      </Stack>
      {error && <Alert severity="error" onClose={() => setError('')}>{error}</Alert>}
      {section === 'payroll' && <PayrollCreateForm onCreated={load} />}
      {loading ? <LoadingSpinner label={`Loading ${current.title.toLowerCase()}...`} /> : (
        <Paper sx={{ overflow: 'auto', border: '1px solid #e4ded4' }}>
          <Table sx={{ minWidth: 760 }}>
            <TableHead><TableRow>{headings.map((heading) => <TableCell key={heading}>{heading}</TableCell>)}</TableRow></TableHead>
            <TableBody>
              {rows.length ? rows.map((row, index) => (
                <TableRow key={row[0] || index}>
                  {row.map((value, cellIndex) => (
                    <TableCell key={cellIndex}>
                      {section === 'employees' && cellIndex === 7 ? (
                        editingEmployeeId === items[index].id ? (
                          <Stack spacing={1} sx={{ minWidth: 320 }}>
                            {['department', 'designation', 'employmentType'].map((field) => (
                              <TextField key={field} size="small" label={field === 'employmentType' ? 'Employment type' : field[0].toUpperCase() + field.slice(1)} value={(employeeEdits[items[index].id] || items[index])[field] || ''} onChange={(event) => setEmployeeEdits({ ...employeeEdits, [items[index].id]: { ...items[index], ...(employeeEdits[items[index].id] || {}), [field]: event.target.value } })} />
                            ))}
                            <Stack direction="row" spacing={1}><Button size="small" variant="contained" disabled={busyId === value} onClick={() => saveEmployee(items[index])}>Save details</Button><Button size="small" onClick={() => setEditingEmployeeId(null)}>Cancel</Button></Stack>
                          </Stack>
                        ) : <Button size="small" onClick={() => setEditingEmployeeId(items[index].id)}>Update details</Button>
                      ) : section === 'leave' && cellIndex === 6 ? (
                        row[5] === 'PENDING' ? (
                          <Stack direction="row" spacing={1} alignItems="center">
                            <TextField size="small" placeholder="Comment" value={comments[value] || ''} onChange={(event) => setComments({ ...comments, [value]: event.target.value })} />
                            <Button size="small" color="success" disabled={busyId === value} onClick={() => actOnLeave(value, 'APPROVE')}>Approve</Button>
                            <Button size="small" color="error" disabled={busyId === value} onClick={() => actOnLeave(value, 'REJECT')}>Reject</Button>
                          </Stack>
                        ) : null
                      ) : section === 'leave' && cellIndex === 5 ? (
                        <Chip size="small" label={display(value)} color={value === 'PENDING' ? 'warning' : value === 'APPROVED' ? 'success' : 'default'} />
                      ) : section === 'payroll' && cellIndex === 6 ? (
                        editingPayrollId === items[index].id ? (
                          <Stack spacing={1} sx={{ minWidth: 390 }}>
                            <Stack direction="row" spacing={1}>
                              {['basicSalary', 'allowances', 'deductions'].map((field) => (
                                <TextField key={field} size="small" label={field === 'basicSalary' ? 'Basic' : field === 'allowances' ? 'Allowances' : 'Deductions'} type="number" value={(edits[items[index].id] || items[index])[field]} onChange={(event) => setEdits({ ...edits, [items[index].id]: { ...items[index], ...(edits[items[index].id] || {}), [field]: event.target.value } })} />
                              ))}
                            </Stack>
                            <Stack direction="row" spacing={1}>
                              <Button size="small" variant="contained" disabled={busyId === value} onClick={() => savePayroll(items[index])}>Save salary</Button>
                              <Button size="small" onClick={() => setEditingPayrollId(null)}>Cancel</Button>
                            </Stack>
                          </Stack>
                        ) : <Button size="small" onClick={() => setEditingPayrollId(items[index].id)}>Update salary</Button>
                      ) : display(value)}
                    </TableCell>
                  ))}
                </TableRow>
              )) : <TableRow><TableCell colSpan={headings.length}><Typography color="text.secondary" sx={{ py: 4, textAlign: 'center' }}>No records found.</Typography></TableCell></TableRow>}
            </TableBody>
          </Table>
        </Paper>
      )}
    </Stack>
  );
}

function BoxTitle({ title, subtitle }) {
  return <Stack spacing={0.5}><Typography variant="h2" sx={{ fontSize: { xs: '2.5rem', md: '4rem' } }}>{title}</Typography><Typography color="text.secondary">{subtitle}</Typography></Stack>;
}

function PayrollCreateForm({ onCreated }) {
  const [form, setForm] = useState({ employeeId: '', basicSalary: '', allowances: '0', deductions: '0', effectiveFrom: new Date().toISOString().slice(0, 10) });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const update = (field) => (event) => setForm({ ...form, [field]: event.target.value });
  const submit = async (event) => {
    event.preventDefault();
    if (!form.employeeId || Number(form.basicSalary) <= 0 || Number(form.allowances) < 0 || Number(form.deductions) < 0 || !form.effectiveFrom) return setError('Enter an employee ID, valid salary amounts, and an effective date.');
    setSaving(true); setError('');
    try { await createPayroll({ ...form, basicSalary: Number(form.basicSalary), allowances: Number(form.allowances), deductions: Number(form.deductions) }); setForm({ ...form, employeeId: '', basicSalary: '', allowances: '0', deductions: '0' }); toast.success('Payroll record created.'); await onCreated(); } catch (requestError) { setError(getApiMessage(requestError, 'Unable to create payroll record.')); } finally { setSaving(false); }
  };
  return <Paper component="form" onSubmit={submit} sx={{ p: 3, border: '1px solid #e4ded4' }}><Typography variant="h6" sx={{ mb: 2 }}>Assign salary</Typography><Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} flexWrap="wrap"><TextField size="small" label="Employee ID" value={form.employeeId} onChange={update('employeeId')} required /><TextField size="small" label="Basic salary" type="number" value={form.basicSalary} onChange={update('basicSalary')} required /><TextField size="small" label="Allowances" type="number" value={form.allowances} onChange={update('allowances')} /><TextField size="small" label="Deductions" type="number" value={form.deductions} onChange={update('deductions')} /><TextField size="small" label="Effective from" type="date" value={form.effectiveFrom} onChange={update('effectiveFrom')} InputLabelProps={{ shrink: true }} required /><Button type="submit" variant="contained" disabled={saving}>{saving ? 'Saving...' : 'Assign salary'}</Button></Stack>{error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}</Paper>;
}
