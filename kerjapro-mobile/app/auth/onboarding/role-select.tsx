import {
  View, Text, TouchableOpacity, StyleSheet, Alert,
} from 'react-native';
import { router } from 'expo-router';
import { SafeAreaView } from 'react-native-safe-area-context';
import apiClient from '../../../services/apiClient';
import { useAuth } from '../../../hooks/useAuth';
import { KerjaProLogo } from '../../../components/common/KerjaProLogo';

const ROLES = [
  {
    id: 'MAIN_CONTRACTOR',
    title: 'Main Contractor',
    subtitle: 'I manage construction projects and hire subcontractors',
    icon: '🏗️',
    features: ['Post work packages', 'Search verified subs', 'Book appointments', 'Manage your team'],
  },
  {
    id: 'SUBCONTRACTOR',
    title: 'Subcontractor',
    subtitle: 'I provide specialised trade services to contractors',
    icon: '🔧',
    features: ['Showcase your skills', 'Display brand certifications', 'Get booked directly', 'Build your reputation'],
  },
];

export default function RoleSelectScreen() {
  const { userId } = useAuth();

  const handleRoleSelect = async (role: string) => {
    try {
      // Register user with selected role
      await apiClient.post('/api/contractors/onboarding/role', { role, userId });

      if (role === 'SUBCONTRACTOR') {
        router.push('/auth/onboarding/subcontractor-setup');
      } else {
        router.push('/auth/onboarding/main-contractor-setup');
      }
    } catch {
      Alert.alert('Error', 'Could not save your role. Please try again.');
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <KerjaProLogo size={52} variant="icon" theme="dark" />
        <Text style={styles.title}>Welcome to KerjaPro</Text>
        <Text style={styles.subtitle}>How will you be using KerjaPro?</Text>
      </View>

      <View style={styles.cards}>
        {ROLES.map((role) => (
          <TouchableOpacity
            key={role.id}
            style={styles.card}
            onPress={() => handleRoleSelect(role.id)}
            activeOpacity={0.85}
          >
            <Text style={styles.cardIcon}>{role.icon}</Text>
            <Text style={styles.cardTitle}>{role.title}</Text>
            <Text style={styles.cardSubtitle}>{role.subtitle}</Text>

            <View style={styles.featureList}>
              {role.features.map((f, i) => (
                <View key={i} style={styles.featureRow}>
                  <Text style={styles.featureDot}>✓</Text>
                  <Text style={styles.featureText}>{f}</Text>
                </View>
              ))}
            </View>

            <View style={styles.selectBtn}>
              <Text style={styles.selectBtnText}>Select →</Text>
            </View>
          </TouchableOpacity>
        ))}
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F9FAFB', paddingHorizontal: 20 },
  header: { paddingTop: 32, paddingBottom: 24, alignItems: 'center', gap: 10 },
  title: { fontSize: 26, fontWeight: '800', color: '#1C1C1E' },
  subtitle: { fontSize: 15, color: '#6B7280', marginTop: 6 },
  cards: { gap: 16 },
  card: {
    backgroundColor: '#fff', borderRadius: 20, padding: 24,
    shadowColor: '#000', shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.08, shadowRadius: 12, elevation: 3,
  },
  cardIcon: { fontSize: 36, marginBottom: 12 },
  cardTitle: { fontSize: 20, fontWeight: '700', color: '#1C1C1E' },
  cardSubtitle: { fontSize: 14, color: '#6B7280', marginTop: 4, marginBottom: 16, lineHeight: 20 },
  featureList: { gap: 8, marginBottom: 20 },
  featureRow: { flexDirection: 'row', gap: 8, alignItems: 'center' },
  featureDot: { color: '#F97316', fontWeight: '700', fontSize: 14 },
  featureText: { fontSize: 14, color: '#374151' },
  selectBtn: {
    backgroundColor: '#F97316', borderRadius: 10,
    paddingVertical: 12, alignItems: 'center',
  },
  selectBtnText: { color: '#fff', fontWeight: '700', fontSize: 15 },
});
