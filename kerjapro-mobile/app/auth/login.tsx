import {
  View, Text, TouchableOpacity, Image,
  StyleSheet, ActivityIndicator, Alert,
} from 'react-native';
import { useState, useEffect } from 'react';
import { SafeAreaView } from 'react-native-safe-area-context';
import { StatusBar } from 'expo-status-bar';
import { useAuth } from '../../hooks/useAuth';

export default function LoginScreen() {
  const { login, restoreSession, isAuthenticated } = useAuth();
  const [loading, setLoading] = useState(false);
  const [restoring, setRestoring] = useState(true);

  // Attempt to restore existing session on mount
  useEffect(() => {
    (async () => {
      try {
        await restoreSession();
      } finally {
        setRestoring(false);
      }
    })();
  }, []);

  if (restoring) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#F97316" />
      </View>
    );
  }

  const handleLogin = async () => {
    setLoading(true);
    try {
      const success = await login();
      if (!success) {
        Alert.alert('Login Failed', 'Please try again.');
      }
    } catch (e) {
      Alert.alert('Error', 'Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar style="dark" />

      {/* Logo & Tagline */}
      <View style={styles.hero}>
        <View style={styles.logoBox}>
          <Text style={styles.logoText}>KP</Text>
        </View>
        <Text style={styles.appName}>KerjaPro</Text>
        <Text style={styles.tagline}>
          Malaysia's Construction{'\n'}Subcontractor Marketplace
        </Text>
      </View>

      {/* Features */}
      <View style={styles.features}>
        {[
          { icon: '🔍', text: 'Find verified subcontractors by trade & brand' },
          { icon: '🏆', text: 'Trusted ratings from real projects' },
          { icon: '📅', text: 'Book site appointments instantly' },
        ].map((item, i) => (
          <View key={i} style={styles.featureRow}>
            <Text style={styles.featureIcon}>{item.icon}</Text>
            <Text style={styles.featureText}>{item.text}</Text>
          </View>
        ))}
      </View>

      {/* CTA */}
      <View style={styles.actions}>
        <TouchableOpacity
          style={[styles.loginButton, loading && styles.loginButtonDisabled]}
          onPress={handleLogin}
          disabled={loading}
          activeOpacity={0.85}
        >
          {loading ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={styles.loginButtonText}>Sign In / Register</Text>
          )}
        </TouchableOpacity>

        <Text style={styles.disclaimer}>
          By continuing, you agree to KerjaPro's{'\n'}
          Terms of Service and Privacy Policy
        </Text>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  loadingContainer: {
    flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#fff',
  },
  container: {
    flex: 1, backgroundColor: '#FFFFFF', paddingHorizontal: 28,
  },
  hero: {
    flex: 1, justifyContent: 'center', alignItems: 'center', paddingTop: 40,
  },
  logoBox: {
    width: 80, height: 80, borderRadius: 20,
    backgroundColor: '#F97316',
    justifyContent: 'center', alignItems: 'center',
    marginBottom: 16,
    shadowColor: '#F97316', shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.3, shadowRadius: 16, elevation: 8,
  },
  logoText: {
    fontSize: 32, fontWeight: '800', color: '#fff',
  },
  appName: {
    fontSize: 32, fontWeight: '800', color: '#1C1C1E', letterSpacing: -0.5,
  },
  tagline: {
    fontSize: 16, color: '#6B7280', textAlign: 'center',
    marginTop: 8, lineHeight: 24,
  },
  features: {
    paddingVertical: 32, gap: 16,
  },
  featureRow: {
    flexDirection: 'row', alignItems: 'center', gap: 12,
    backgroundColor: '#FFF7ED', borderRadius: 12, padding: 14,
  },
  featureIcon: { fontSize: 20 },
  featureText: { fontSize: 14, color: '#374151', flex: 1, fontWeight: '500' },
  actions: {
    paddingBottom: 20, gap: 16,
  },
  loginButton: {
    backgroundColor: '#F97316', borderRadius: 14,
    paddingVertical: 16, alignItems: 'center',
    shadowColor: '#F97316', shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.25, shadowRadius: 8, elevation: 4,
  },
  loginButtonDisabled: { opacity: 0.7 },
  loginButtonText: {
    color: '#fff', fontSize: 17, fontWeight: '700', letterSpacing: 0.2,
  },
  disclaimer: {
    fontSize: 12, color: '#9CA3AF', textAlign: 'center', lineHeight: 18,
  },
});
