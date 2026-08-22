import api from './api';
export const getMyAttendance = async (params = {}) => (await api.get('/api/attendance/me', { params })).data;
export const checkIn = async () => (await api.post('/api/attendance/check-in')).data;
export const checkOut = async () => (await api.post('/api/attendance/check-out')).data;
