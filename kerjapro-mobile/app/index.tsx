import { Redirect } from 'expo-router';
import { useAuthStore } from '../store/authStore';

export default function Index() {
  const { isAuthenticated, role } = useAuthStore();

  if (!isAuthenticated) {
    return <Redirect href="/auth/login" />;
  }

  if (role === 'MAIN_CONTRACTOR') {
    return <Redirect href="/main-contractor/dashboard" />;
  }

  if (role === 'SUBCONTRACTOR') {
    return <Redirect href="/subcontractor/dashboard" />;
  }

  return <Redirect href="/auth/login" />;
}
