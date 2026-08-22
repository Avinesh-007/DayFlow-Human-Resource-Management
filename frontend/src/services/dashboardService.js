import api from './api';

export const getEmployeeDashboard = async () => (await api.get('/api/dashboard/employee')).data;
