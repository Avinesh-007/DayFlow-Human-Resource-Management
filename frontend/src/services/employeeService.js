import api from './api';
export const getMyProfile = async () => (await api.get('/api/employees/me')).data;
export const updateMyProfile = async (request) => (await api.put('/api/employees/me', request)).data;
