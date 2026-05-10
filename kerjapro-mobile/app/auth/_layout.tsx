import { Stack } from 'expo-router';

export default function AuthLayout() {
  return (
    <Stack screenOptions={{ headerShown: false }}>
      <Stack.Screen name="login" />
      <Stack.Screen name="onboarding/role-select" />
      <Stack.Screen name="onboarding/subcontractor-setup" />
      <Stack.Screen name="onboarding/main-contractor-setup" />
    </Stack>
  );
}
