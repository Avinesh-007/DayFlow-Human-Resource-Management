import api from './api';

export const login = async (credentials) => (await api.post('/api/auth/login', credentials)).data;
export const register = async (request) => (await api.post('/api/auth/register', request)).data;
export const verifyEmail = async (token) => (await api.get('/api/auth/verify-email', { params: { token } })).data;
