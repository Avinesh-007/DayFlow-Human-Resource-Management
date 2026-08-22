import api from './api';
export const getMyLeaves = async (params = {}) => (await api.get('/api/leaves/me', { params })).data;
export const applyLeave = async (request) => (await api.post('/api/leaves', request)).data;
