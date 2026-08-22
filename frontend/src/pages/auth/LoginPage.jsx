import { useState } from 'react';
import { Alert, Box, Button, CircularProgress, Link, Paper, Stack, TextField, Typography } from '@mui/material';
import { Visibility, VisibilityOff } from '@mui/icons-material';
import { IconButton, InputAdornment } from '@mui/material';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { toast } from 'react-toastify';
import { login } from '../../services/authService';
import { getApiMessage } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { useState as usePasswordState } from 'react';

export default function LoginPage() {
  const navigate = useNavigate(); const { signIn } = useAuth();
  const [form, setForm] = useState({ email: '', password: '' }); const [error, setError] = useState(''); const [loading, setLoading] = useState(false); const [show, setShow] = usePasswordState(false);
  const submit = async (event) => { event.preventDefault(); setError(''); if (!form.email || !form.password) return setError('Email and password are required.'); setLoading(true); try { const response = await login(form); signIn(response); toast.success('Welcome back to Dayflow.'); navigate('/dashboard'); } catch (requestError) { setError(getApiMessage(requestError, 'Unable to sign in. Check your credentials.')); } finally { setLoading(false); } };
  return <AuthFrame title="Welcome back" subtitle="Keep the workday moving in the right direction."><Box component="form" onSubmit={submit}><Stack spacing={2.5}><TextField label="Work email" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} fullWidth autoComplete="email" /><TextField label="Password" type={show ? 'text' : 'password'} value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} fullWidth autoComplete="current-password" InputProps={{ endAdornment: <InputAdornment position="end"><IconButton onClick={() => setShow(!show)} edge="end">{show ? <VisibilityOff /> : <Visibility />}</IconButton></InputAdornment> }} />{error && <Alert severity="error">{error}</Alert>}<Button type="submit" variant="contained" size="large" disabled={loading}>{loading ? <CircularProgress size={24} color="inherit" /> : 'Sign in'}</Button><Typography textAlign="center" color="text.secondary">New to Dayflow? <Link component={RouterLink} to="/register">Create an account</Link></Typography></Stack></Box></AuthFrame>;
}

function AuthFrame({ title, subtitle, children }) { return <Box sx={{ minHeight: '100vh', display: 'grid', gridTemplateColumns: { md: '1.1fr 1fr' }, bgcolor: 'background.default' }}><Box sx={{ display: { xs: 'none', md: 'flex' }, flexDirection: 'column', justifyContent: 'space-between', p: 8, color: '#fffaf0', bgcolor: 'primary.main', background: 'linear-gradient(145deg, #0d1b2a 0%, #193b4d 72%, #e07a5f 220%)' }}><Typography variant="h4" fontWeight={800} letterSpacing="-.05em">dayflow<span style={{ color: '#e07a5f' }}>.</span></Typography><Typography variant="h2" sx={{ maxWidth: 520, fontSize: 'clamp(2.8rem, 5vw, 5rem)', lineHeight: 1.02 }}>Make room for better work.</Typography><Typography color="#b7c6ca">People, presence, and progress in one calm place.</Typography></Box><Box sx={{ display: 'grid', placeItems: 'center', p: { xs: 3, sm: 6 } }}><Paper elevation={0} sx={{ p: { xs: 3, sm: 5 }, width: '100%', maxWidth: 440, bgcolor: 'transparent' }}><Typography variant="h3" sx={{ mb: 1 }}>{title}</Typography><Typography color="text.secondary" sx={{ mb: 4 }}>{subtitle}</Typography>{children}</Paper></Box></Box>; }
