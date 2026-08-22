import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    primary: { main: '#0d1b2a', contrastText: '#fffaf0' },
    secondary: { main: '#e07a5f' },
    background: { default: '#f6f3ed', paper: '#fffdf8' },
    text: { primary: '#17202a', secondary: '#64707d' },
  },
  typography: {
    fontFamily: '"DM Sans", "Segoe UI", sans-serif',
    h1: { fontFamily: 'Georgia, serif', fontWeight: 700 },
    h2: { fontFamily: 'Georgia, serif', fontWeight: 700 },
    h3: { fontFamily: 'Georgia, serif', fontWeight: 700 },
  },
  shape: { borderRadius: 8 },
  components: {
    MuiButton: { defaultProps: { disableElevation: true } },
    MuiPaper: { styleOverrides: { root: { backgroundImage: 'none' } } },
  },
});
