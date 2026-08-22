import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('dayflow_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('dayflow_token');
      localStorage.removeItem('dayflow_user');
      window.dispatchEvent(new Event('dayflow:unauthorized'));
    }
    return Promise.reject(error);
  },
);

export const getApiMessage = (error, fallback = 'Something went wrong. Please try again.') => {
  if (!error.response && (error.code === 'ERR_NETWORK' || error.message === 'Network Error')) {
    return 'Unable to connect to the server. Please start the backend on http://localhost:8080.';
  }
  if (error.response?.status === 403) return 'You do not have permission to perform this action.';
  if (error.response?.status === 404) return 'The requested resource was not found.';
  if (error.response?.status >= 500) return 'Something went wrong on the server. Please try again.';
  return error.response?.data?.message || error.message || fallback;
};

export default api;
