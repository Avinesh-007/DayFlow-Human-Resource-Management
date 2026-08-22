import api from './api';
export const getMyPayroll = async (params = {}) => (await api.get('/api/payroll/me', { params })).data;
