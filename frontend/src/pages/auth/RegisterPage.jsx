import { useState } from 'react';
import { Alert, Box, Button, CircularProgress, Link, MenuItem, Stack, TextField, Typography } from '@mui/material';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { register } from '../../services/authService';
import { getApiMessage } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function RegisterPage() {
  const navigate = useNavigate(); const { signIn } = useAuth(); const [loading, setLoading] = useState(false); const [error, setError] = useState('');
  const [form, setForm] = useState({ employeeId: '', email: '', password: '', role: 'EMPLOYEE' });
  const update = (name) => (event) => setForm({ ...form, [name]: event.target.value });
  const submit = async (event) => { event.preventDefault(); setError(''); if (!form.employeeId || !form.email || !form.password) return setError('Complete every field to continue.'); setLoading(true); try { const response = await register(form); signIn(response); toast.success('Your Dayflow account is ready.'); navigate('/dashboard'); } catch (requestError) { setError(getApiMessage(requestError, 'Unable to create your account.')); } finally { setLoading(false); } };
  return <Box sx={{ minHeight: '100vh', display: 'grid', placeItems: 'center', bgcolor: 'background.default', p: 2 }}><Box sx={{ width: '100%', maxWidth: 480 }}><Typography variant="h4" sx={{ mb: 1 }}>Create your account<span style={{ color: '#e07a5f' }}>.</span></Typography><Typography color="text.secondary" sx={{ mb: 4 }}>Start with the details your organization recognizes.</Typography><Box component="form" onSubmit={submit}><Stack spacing={2}><TextField label="Employee ID" value={form.employeeId} onChange={update('employeeId')} /><TextField label="Work email" type="email" value={form.email} onChange={update('email')} /><TextField label="Password" type="password" helperText="8+ chars, uppercase, lowercase, number, and special character" value={form.password} onChange={update('password')} /><TextField select label="Role" value={form.role} onChange={update('role')}><MenuItem value="EMPLOYEE">Employee</MenuItem><MenuItem value="ADMIN">Admin</MenuItem></TextField>{error && <Alert severity="error">{error}</Alert>}<Button type="submit" variant="contained" size="large" disabled={loading}>{loading ? <CircularProgress size={24} color="inherit" /> : 'Create account'}</Button><Typography textAlign="center" color="text.secondary">Already registered? <Link component={RouterLink} to="/login">Sign in</Link></Typography></Stack></Box></Box></Box>;
}
