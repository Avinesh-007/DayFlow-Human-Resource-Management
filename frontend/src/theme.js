import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    primary: { main: '#10212b', contrastText: '#fffaf0' },
    secondary: { main: '#ef765f' },
    success: { main: '#2f8f83' },
    background: { default: '#f4f1eb', paper: '#fffdf8' },
    text: { primary: '#10212b', secondary: '#61717a' },
  },
  typography: {
    fontFamily: '"Plus Jakarta Sans", "Segoe UI", sans-serif',
    h1: { fontFamily: '"Fraunces", Georgia, serif', fontWeight: 700, letterSpacing: '-.035em' },
    h2: { fontFamily: '"Fraunces", Georgia, serif', fontWeight: 700, letterSpacing: '-.035em' },
    h3: { fontFamily: '"Fraunces", Georgia, serif', fontWeight: 700, letterSpacing: '-.035em' },
  },
  shape: { borderRadius: 8 },
  components: {
    MuiButton: { defaultProps: { disableElevation: true }, styleOverrides: { root: { borderRadius: 10, textTransform: 'none', fontWeight: 700, transition: 'transform .2s, box-shadow .2s', '&:hover': { transform: 'translateY(-1px)' } } } },
    MuiPaper: { styleOverrides: { root: { backgroundImage: 'none', borderRadius: 14, borderColor: '#e2ddd4' } } },
    MuiTextField: { defaultProps: { variant: 'outlined' } },
  },
});
