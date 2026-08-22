import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { AppBar, Avatar, Box, Button, Drawer, IconButton, List, ListItemButton, ListItemIcon, ListItemText, Toolbar, Typography } from '@mui/material';
import { DashboardOutlined, Logout, Menu, PeopleOutline, EventAvailableOutlined, PaymentsOutlined, PersonOutline } from '@mui/icons-material';
import { useState } from 'react';
import { useAuth } from '../../context/AuthContext';

const employeeLinks = [
  { label: 'Dashboard', path: '/dashboard', icon: <DashboardOutlined /> },
  { label: 'Profile', path: '/profile', icon: <PersonOutline /> },
  { label: 'Attendance', path: '/attendance', icon: <EventAvailableOutlined /> },
  { label: 'Leave', path: '/leave', icon: <EventAvailableOutlined /> },
  { label: 'Payroll', path: '/payroll', icon: <PaymentsOutlined /> },
];
const adminLinks = [
  { label: 'Dashboard', path: '/dashboard', icon: <DashboardOutlined /> },
  { label: 'Employees', path: '/employees', icon: <PeopleOutline /> },
  { label: 'Attendance', path: '/attendance', icon: <EventAvailableOutlined /> },
  { label: 'Leave approvals', path: '/leave-approvals', icon: <EventAvailableOutlined /> },
  { label: 'Payroll', path: '/payroll', icon: <PaymentsOutlined /> },
];

export default function AppLayout() {
  const { user, signOut } = useAuth();
  const navigate = useNavigate();
  const [mobileOpen, setMobileOpen] = useState(false);
  const links = user.role === 'ADMIN' ? adminLinks : employeeLinks;
  const logout = () => { signOut(); navigate('/login'); };
  const drawer = (
    <Box sx={{ width: 250, pt: 2 }}>
      <Box sx={{ px: 3, pb: 3 }}>
        <Typography variant="h5" sx={{ fontWeight: 800, letterSpacing: '-0.04em' }}>dayflow<span style={{ color: '#e07a5f' }}>.</span></Typography>
        <Typography variant="caption" color="text.secondary">Every workday, perfectly aligned.</Typography>
      </Box>
      <List sx={{ px: 1 }}>
        {links.map((link) => <ListItemButton key={link.path} component={NavLink} to={link.path} onClick={() => setMobileOpen(false)} sx={{ borderRadius: 2, mb: .5, '&.active': { bgcolor: '#e7e1d6', color: 'primary.main', fontWeight: 700 } }}>
          <ListItemIcon sx={{ minWidth: 40 }}>{link.icon}</ListItemIcon><ListItemText primary={link.label} />
        </ListItemButton>)}
      </List>
    </Box>
  );
  return <Box sx={{ display: 'flex', minHeight: '100vh' }}>
    <AppBar position="fixed" color="inherit" elevation={0} sx={{ borderBottom: '1px solid #e4ded4', bgcolor: 'rgba(255,253,248,.92)', backdropFilter: 'blur(12px)' }}>
      <Toolbar sx={{ ml: { md: '250px' } }}>
        <IconButton onClick={() => setMobileOpen(true)} sx={{ display: { md: 'none' }, mr: 1 }}><Menu /></IconButton>
        <Box sx={{ flexGrow: 1 }} />
        <Avatar sx={{ width: 32, height: 32, bgcolor: 'secondary.main', mr: 1 }}>{(user.email || 'D')[0].toUpperCase()}</Avatar>
        <Box sx={{ display: { xs: 'none', sm: 'block' }, mr: 2 }}><Typography variant="body2" fontWeight={700}>{user.email}</Typography><Typography variant="caption" color="text.secondary">{user.role}</Typography></Box>
        <Button color="inherit" startIcon={<Logout />} onClick={logout}>Logout</Button>
      </Toolbar>
    </AppBar>
    <Drawer variant="permanent" sx={{ display: { xs: 'none', md: 'block' }, '& .MuiDrawer-paper': { width: 250, boxSizing: 'border-box', borderRight: '1px solid #e4ded4', bgcolor: '#fffdf8' } }}>{drawer}</Drawer>
    <Drawer open={mobileOpen} onClose={() => setMobileOpen(false)} sx={{ display: { md: 'none' } }}>{drawer}</Drawer>
    <Box component="main" sx={{ flexGrow: 1, ml: { md: '250px' }, pt: 10, px: { xs: 2, sm: 4, lg: 7 }, pb: 6 }}><Outlet /></Box>
  </Box>;
}
