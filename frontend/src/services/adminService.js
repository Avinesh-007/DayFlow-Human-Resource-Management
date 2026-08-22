import api from './api';

export const getEmployees = (params = {}) => api.get('/api/employees', { params });
export const updateEmployee = (id, request) => api.put(`/api/employees/${id}`, request);
export const getAttendance = (params = {}) => api.get('/api/attendance', { params });
export const getLeaveRequests = (params = {}) => api.get('/api/admin/leaves', { params });
export const reviewLeave = (id, status, comment = '') => api.put(`/api/admin/leaves/${id}/${status.toLowerCase()}`, { comment });
export const getPayrollRecords = (params = {}) => api.get('/api/admin/payroll', { params });
export const createPayroll = (request) => api.post('/api/admin/payroll', request);
export const updatePayroll = (id, request) => api.put(`/api/admin/payroll/${id}`, request);