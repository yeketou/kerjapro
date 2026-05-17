import { Tabs } from 'expo-router';
import { Text } from 'react-native';

export default function MCLayout() {
  return (
    <Tabs screenOptions={{
      tabBarActiveTintColor: '#F97316',
      tabBarInactiveTintColor: '#9CA3AF',
      tabBarStyle: { borderTopColor: '#F3F4F6' },
      headerShown: false,
    }}>
      <Tabs.Screen name="dashboard"
        options={{ title: 'Dashboard', tabBarIcon: ({ color }) => <Text style={{ fontSize: 20, color }}>🏗️</Text> }} />
      <Tabs.Screen name="search"
        options={{ title: 'Find Subs', tabBarIcon: ({ color }) => <Text style={{ fontSize: 20, color }}>🔍</Text> }} />
      <Tabs.Screen name="bookings"
        options={{ title: 'Bookings', tabBarIcon: ({ color }) => <Text style={{ fontSize: 20, color }}>📅</Text> }} />
      <Tabs.Screen name="projects/index"
        options={{ title: 'Projects', tabBarIcon: ({ color }) => <Text style={{ fontSize: 20, color }}>📋</Text> }} />
    </Tabs>
  );
}
