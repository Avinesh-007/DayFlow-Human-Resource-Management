import { createContext, useContext, useEffect, useMemo, useState } from 'react';

const AuthContext = createContext(null);
const TOKEN_KEY = 'dayflow_token';
const USER_KEY = 'dayflow_user';

const readUser = () => {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY)) || null;
  } catch {
    return null;
  }
};

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readUser);

  useEffect(() => {
    const handleUnauthorized = () => setUser(null);
    window.addEventListener('dayflow:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('dayflow:unauthorized', handleUnauthorized);
  }, []);

  const signIn = (authResponse) => {
    const account = authResponse.data || authResponse;
    const normalized = { ...account, role: account.role?.replace('ROLE_', '') };
    localStorage.setItem(TOKEN_KEY, normalized.token);
    localStorage.setItem(USER_KEY, JSON.stringify(normalized));
    setUser(normalized);
  };

  const signOut = () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    setUser(null);
  };

  const value = useMemo(() => ({ user, isAuthenticated: Boolean(user?.token), signIn, signOut }), [user]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
