import { useEffect } from 'react';
import { View, ActivityIndicator, StyleSheet } from 'react-native';
import * as WebBrowser from 'expo-web-browser';

/**
 * OAuth callback screen.
 * Expo Go redirects here after Keycloak login.
 * WebBrowser.maybeCompleteAuthSession() signals expo-auth-session
 * that the auth flow is complete and passes the token back.
 */
export default function AuthCallback() {
  useEffect(() => {
    WebBrowser.maybeCompleteAuthSession();
  }, []);

  return (
    <View style={styles.container}>
      <ActivityIndicator size="large" color="#F97316" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#fff' },
});
