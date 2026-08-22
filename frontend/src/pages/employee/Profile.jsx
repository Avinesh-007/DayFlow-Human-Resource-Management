import { useEffect, useState } from 'react';
import { Alert, Box, Button, Divider, Paper, Stack, TextField, Typography } from '@mui/material';
import { toast } from 'react-toastify';
import { getMyProfile, updateMyProfile } from '../../services/employeeService';
import { getApiMessage } from '../../services/api';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';

const editable = ['phone', 'address', 'profilePictureUrl', 'emergencyContactName', 'emergencyContactPhone'];
const contactFields = [['phone', 'Phone'], ['address', 'Address'], ['profilePictureUrl', 'Profile picture URL'], ['emergencyContactName', 'Emergency contact name'], ['emergencyContactPhone', 'Emergency contact phone']];

export default function Profile() {
  const [profile, setProfile] = useState(null);
  const [form, setForm] = useState({});
  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const load = async () => {
    setLoading(true); setError('');
    try { const response = await getMyProfile(); setProfile(response.data); setForm(response.data || {}); }
    catch (requestError) { setError(getApiMessage(requestError, 'Unable to load your profile.')); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const save = async (event) => {
    event.preventDefault();
    if (form.phone && !/^[+\d][\d\s()-]{6,}$/.test(form.phone)) return toast.error('Enter a valid phone number.');
    if (form.emergencyContactPhone && !/^[+\d][\d\s()-]{6,}$/.test(form.emergencyContactPhone)) return toast.error('Enter a valid emergency contact phone number.');
    setSaving(true);
    try { const response = await updateMyProfile(Object.fromEntries(editable.map((key) => [key, form[key] || null]))); setProfile(response.data); setForm(response.data || {}); setEditing(false); toast.success('Profile updated.'); }
    catch (requestError) { toast.error(getApiMessage(requestError, 'Unable to update your profile.')); }
    finally { setSaving(false); }
  };

  const cancel = () => { setForm(profile || {}); setEditing(false); };
  if (loading) return <LoadingSpinner label="Loading profile..." />;
  if (error) return <ErrorMessage message={error} onRetry={load} />;

  return <Stack spacing={3}>
    <Box><Typography variant="h2" sx={{ fontSize: { xs: '2.5rem', md: '4rem' } }}>Your profile</Typography><Typography color="text.secondary">Your identity and work details, kept current.</Typography></Box>
    <Paper sx={{ p: { xs: 2, sm: 4 }, border: '1px solid #e4ded4' }}>
      <Stack spacing={3}>
        <Box><Typography variant="h4">{profile?.firstName} {profile?.lastName}</Typography><Typography color="text.secondary">{profile?.designation || 'Employee'}</Typography>{profile?.profilePictureUrl && <Box component="img" src={profile.profilePictureUrl} alt="Profile" sx={{ mt: 2, width: 72, height: 72, borderRadius: '50%', objectFit: 'cover' }} />}</Box>
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)' }, gap: 2 }}>{[['Employee ID', profile?.employeeId], ['Email', profile?.email], ['Department', profile?.department], ['Designation', profile?.designation], ['Employment type', profile?.employmentType], ['Date joined', profile?.dateOfJoining], ['Status', profile?.active ? 'Active' : 'Inactive']].map(([label, value]) => <Box key={label}><Typography variant="caption" color="text.secondary">{label}</Typography><Typography>{value || 'Not provided'}</Typography></Box>)}</Box>
        <Divider />
        <Stack direction="row" justifyContent="space-between" alignItems="center"><Typography variant="h6">Contact details</Typography>{!editing && <Button variant="outlined" onClick={() => setEditing(true)}>Edit details</Button>}</Stack>
        {editing ? <Box component="form" onSubmit={save} sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)' }, gap: 2 }}>{contactFields.map(([key, label]) => <TextField key={key} label={label} value={form[key] || ''} onChange={(event) => setForm({ ...form, [key]: event.target.value })} multiline={key === 'address'} minRows={key === 'address' ? 2 : 1} />)}<Stack direction="row" spacing={1} sx={{ gridColumn: { sm: '1 / -1' } }}><Button type="submit" variant="contained" disabled={saving}>{saving ? 'Saving...' : 'Save changes'}</Button><Button type="button" onClick={cancel} disabled={saving}>Cancel</Button></Stack></Box> : <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)' }, gap: 2 }}>{contactFields.map(([key, label]) => <Box key={key}><Typography variant="caption" color="text.secondary">{label}</Typography><Typography sx={{ whiteSpace: 'pre-wrap' }}>{profile?.[key] || 'Not provided'}</Typography></Box>)}</Box>}
      </Stack>
    </Paper>
  </Stack>;
}
