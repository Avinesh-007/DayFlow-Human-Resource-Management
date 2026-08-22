import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { AppBar, Avatar, Box, Button, Drawer, IconButton, List, ListItemButton, ListItemIcon, ListItemText, Toolbar, Typography } from '@mui/material';
import { DashboardOutlined, Logout, Menu, PeopleOutline, EventAvailableOutlined, PaymentsOutlined, PersonOutline, WorkOutline } from '@mui/icons-material';
import { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import BrandMark from '../common/BrandMark';

const employeeLinks = [
  { label: 'Dashboard', path: '/dashboard', icon: <DashboardOutlined /> },
  { label: 'Profile', path: '/profile', icon: <PersonOutline /> },
  { label: 'Attendance', path: '/attendance', icon: <EventAvailableOutlined /> },
  { label: 'Leave', path: '/leave', icon: <WorkOutline /> },
  { label: 'Payroll', path: '/payroll', icon: <PaymentsOutlined /> },
];
const adminLinks = [
  { label: 'Dashboard', path: '/dashboard', icon: <DashboardOutlined /> },
  { label: 'Employees', path: '/employees', icon: <PeopleOutline /> },
  { label: 'Attendance', path: '/admin-attendance', icon: <EventAvailableOutlined /> },
  { label: 'Leave approvals', path: '/leave-approvals', icon: <EventAvailableOutlined /> },
  { label: 'Payroll', path: '/admin-payroll', icon: <PaymentsOutlined /> },
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
        <BrandMark />
        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 1 }}>Every workday, perfectly aligned.</Typography>
      </Box>
      <List sx={{ px: 1 }}>
        {links.map((link) => <ListItemButton key={link.path} component={NavLink} to={link.path} onClick={() => setMobileOpen(false)} sx={{ borderRadius: 2.5, mb: .5, py: 1.15, '& .MuiListItemIcon-root': { color: 'text.secondary' }, '&.active': { bgcolor: '#e8f0ed', color: 'success.main', fontWeight: 700, '& .MuiListItemIcon-root': { color: 'secondary.main' } }, '&:hover': { bgcolor: '#f2e8df' } }}>
          <ListItemIcon sx={{ minWidth: 40 }}>{link.icon}</ListItemIcon><ListItemText primary={link.label} />
        </ListItemButton>)}
      </List>
    </Box>
  );
  return <Box sx={{ display: 'flex', minHeight: '100vh' }}>
    <AppBar position="fixed" color="inherit" elevation={0} sx={{ borderBottom: '1px solid #e2ddd4', bgcolor: 'rgba(255,253,248,.88)', backdropFilter: 'blur(18px)' }}>
      <Toolbar sx={{ ml: { md: '250px' } }}>
        <IconButton onClick={() => setMobileOpen(true)} sx={{ display: { md: 'none' }, mr: 1 }}><Menu /></IconButton>
        <Box sx={{ flexGrow: 1 }} />
        <Box sx={{ display: { xs: 'none', md: 'block' }, mr: 2, textAlign: 'right' }}><Typography variant="caption" color="text.secondary">Signed in as</Typography><Typography variant="body2" fontWeight={700}>{user.role === 'ADMIN' ? 'Workspace admin' : 'Team member'}</Typography></Box>
        <Avatar sx={{ width: 36, height: 36, bgcolor: 'secondary.main', mr: 1, fontWeight: 800 }}>{(user.email || 'D')[0].toUpperCase()}</Avatar>
        <Box sx={{ display: { xs: 'none', sm: 'block' }, mr: 2 }}><Typography variant="body2" fontWeight={700}>{user.email}</Typography><Typography variant="caption" color="text.secondary">{user.role}</Typography></Box>
        <Button color="inherit" startIcon={<Logout />} onClick={logout} sx={{ color: 'text.secondary' }}>Logout</Button>
      </Toolbar>
    </AppBar>
    <Drawer variant="permanent" sx={{ display: { xs: 'none', md: 'block' }, '& .MuiDrawer-paper': { width: 250, boxSizing: 'border-box', borderRight: '1px solid', borderColor: 'divider', bgcolor: 'background.paper' } }}>{drawer}</Drawer>
    <Drawer open={mobileOpen} onClose={() => setMobileOpen(false)} sx={{ display: { md: 'none' } }}>{drawer}</Drawer>
    <Box component="main" sx={{ flexGrow: 1, ml: { md: '250px' }, pt: 10, px: { xs: 2, sm: 4, lg: 7 }, pb: 6 }}><Outlet /></Box>
  </Box>;
}
