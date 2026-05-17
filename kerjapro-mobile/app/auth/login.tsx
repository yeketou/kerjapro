import {
  View, Text, TouchableOpacity,
  StyleSheet, ActivityIndicator, Alert,
} from 'react-native';
import { useState, useEffect } from 'react';
import { SafeAreaView } from 'react-native-safe-area-context';
import { StatusBar } from 'expo-status-bar';
import { useAuth } from '../../hooks/useAuth';
import { KerjaProLogo } from '../../components/common/KerjaProLogo';

export default function LoginScreen() {
  const { login, restoreSession } = useAuth();
  const [loading,   setLoading]   = useState(false);
  const [restoring, setRestoring] = useState(true);

  useEffect(() => {
    (async () => {
      try { await restoreSession(); }
      finally { setRestoring(false); }
    })();
  }, []);

  if (restoring) {
    return (
      <View style={styles.splash}>
        <KerjaProLogo size={72} variant="icon" theme="dark" />
        <ActivityIndicator color="#F97316" style={{ marginTop: 24 }} />
      </View>
    );
  }

  const handleLogin = async () => {
    setLoading(true);
    try {
      const ok = await login();
      if (!ok) Alert.alert('Login Failed', 'Please try again.');
    } catch {
      Alert.alert('Error', 'Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar style="dark" />

      {/* Hero */}
      <View style={styles.hero}>
        <KerjaProLogo size={96} variant="icon" theme="dark" />
        <View style={styles.wordmarkRow}>
          <Text style={styles.wordKerja}>Kerja</Text>
          <Text style={styles.wordPro}>Pro</Text>
        </View>
        <Text style={styles.tagline}>
          Malaysia's Construction{'\n'}Subcontractor Marketplace
        </Text>
      </View>

      {/* Feature cards */}
      <View style={styles.features}>
        {[
          { icon: '🔍', title: 'Find by Trade & Brand',    body: 'Search certified subs by specialisation and brand expertise' },
          { icon: '⭐', title: 'Verified Ratings',          body: 'Trusted reviews from real completed projects' },
          { icon: '📅', title: 'Book Appointments',         body: 'Schedule site visits and meetings in seconds' },
        ].map((f, i) => (
          <View key={i} style={styles.featureCard}>
            <Text style={styles.featureIcon}>{f.icon}</Text>
            <View style={styles.featureText}>
              <Text style={styles.featureTitle}>{f.title}</Text>
              <Text style={styles.featureBody}>{f.body}</Text>
            </View>
          </View>
        ))}
      </View>

      {/* CTA */}
      <View style={styles.cta}>
        <TouchableOpacity
          style={[styles.loginBtn, loading && styles.loginBtnDisabled]}
          onPress={handleLogin}
          disabled={loading}
          activeOpacity={0.85}
        >
          {loading
            ? <ActivityIndicator color="#fff" />
            : <Text style={styles.loginBtnText}>Get Started</Text>
          }
        </TouchableOpacity>
        <Text style={styles.disclaimer}>
          By continuing you agree to KerjaPro's Terms & Privacy Policy
        </Text>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  splash: {
    flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#fff',
  },
  container: {
    flex: 1, backgroundColor: '#FFFFFF', paddingHorizontal: 24,
  },
  hero: {
    flex: 1, justifyContent: 'center', alignItems: 'center', paddingTop: 20,
  },
  wordmarkRow: {
    flexDirection: 'row', alignItems: 'baseline', marginTop: 16,
  },
  wordKerja: {
    fontSize: 34, fontWeight: '800', color: '#1C1C1E', letterSpacing: -1,
  },
  wordPro: {
    fontSize: 34, fontWeight: '900', color: '#F97316', letterSpacing: -1,
  },
  tagline: {
    fontSize: 15, color: '#9CA3AF', textAlign: 'center',
    marginTop: 8, lineHeight: 24,
  },
  features: {
    gap: 10, paddingBottom: 24,
  },
  featureCard: {
    flexDirection: 'row', alignItems: 'center', gap: 14,
    backgroundColor: '#F9FAFB', borderRadius: 14, padding: 14,
    borderWidth: 1, borderColor: '#F3F4F6',
  },
  featureIcon:  { fontSize: 24 },
  featureText:  { flex: 1 },
  featureTitle: { fontSize: 14, fontWeight: '700', color: '#1C1C1E' },
  featureBody:  { fontSize: 12, color: '#9CA3AF', marginTop: 2, lineHeight: 18 },
  cta: {
    paddingBottom: 24, gap: 12,
  },
  loginBtn: {
    backgroundColor: '#F97316', borderRadius: 14,
    paddingVertical: 16, alignItems: 'center',
    shadowColor: '#F97316', shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3, shadowRadius: 8, elevation: 5,
  },
  loginBtnDisabled: { opacity: 0.7 },
  loginBtnText: {
    color: '#fff', fontSize: 17, fontWeight: '800', letterSpacing: 0.2,
  },
  disclaimer: {
    fontSize: 11, color: '#D1D5DB', textAlign: 'center', lineHeight: 17,
  },
});
