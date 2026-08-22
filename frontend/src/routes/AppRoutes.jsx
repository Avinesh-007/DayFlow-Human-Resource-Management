import { Navigate, Outlet, Route, Routes } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/layout/AppLayout';
import LoginPage from '../pages/auth/LoginPage';
import RegisterPage from '../pages/auth/RegisterPage';
import DashboardPage from '../pages/dashboard/DashboardPage';
import Profile from '../pages/employee/Profile';
import Attendance from '../pages/employee/Attendance';
import Leave from '../pages/employee/Leave';
import Payroll from '../pages/employee/Payroll';
import AdminPage from '../pages/admin/AdminPage';

function RequireAuth({ role }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (role && user.role !== role) return <Navigate to="/dashboard" replace />;
  return <Outlet />;
}

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route element={<RequireAuth />}>
        <Route element={<AppLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route element={<RequireAuth role="ADMIN" />}>
            <Route path="/employees" element={<AdminPage section="employees" />} />
            <Route path="/leave-approvals" element={<AdminPage section="leave" />} />
            <Route path="/admin-attendance" element={<AdminPage section="attendance" />} />
            <Route path="/admin-payroll" element={<AdminPage section="payroll" />} />
          </Route>
          <Route path="/profile" element={<Profile />} />
          <Route path="/attendance" element={<Attendance />} />
          <Route path="/leave" element={<Leave />} />
          <Route path="/payroll" element={<Payroll />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
