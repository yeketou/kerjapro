import {
  View, Text, TextInput, TouchableOpacity,
  StyleSheet, ScrollView, Alert, ActivityIndicator,
} from 'react-native';
import { useState } from 'react';
import { router } from 'expo-router';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import apiClient from '../../../services/apiClient';

const MALAYSIA_STATES = [
  'Kuala Lumpur', 'Selangor', 'Johor', 'Penang', 'Sabah',
  'Sarawak', 'Perak', 'Kedah', 'Kelantan', 'Terengganu',
  'Pahang', 'Negeri Sembilan', 'Melaka', 'Perlis', 'Putrajaya', 'Labuan',
];

const schema = z.object({
  businessName: z.string().min(2, 'Business name is required'),
  displayName:  z.string().optional(),
  phone:        z.string().min(9, 'Valid phone number required'),
  city:         z.string().min(2, 'City is required'),
  state:        z.string().min(2, 'State is required'),
  bio:          z.string().max(500).optional(),
  cidbGrade:    z.string().optional(),
});

type FormData = z.infer<typeof schema>;

export default function SubcontractorSetupScreen() {
  const [loading, setLoading] = useState(false);

  const { control, handleSubmit, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
  });

  const onSubmit = async (data: FormData) => {
    setLoading(true);
    try {
      await apiClient.post('/api/contractors/profiles', data);
      router.replace('/subcontractor/dashboard');
    } catch {
      Alert.alert('Error', 'Could not create your profile. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView showsVerticalScrollIndicator={false} keyboardShouldPersistTaps="handled">
        <View style={styles.header}>
          <Text style={styles.step}>Step 1 of 1</Text>
          <Text style={styles.title}>Set Up Your Profile</Text>
          <Text style={styles.subtitle}>
            This is what main contractors will see when searching for subcontractors
          </Text>
        </View>

        <View style={styles.form}>
          <Field label="Business Name *" error={errors.businessName?.message}>
            <Controller control={control} name="businessName" render={({ field }) => (
              <TextInput style={styles.input} placeholder="e.g. Ahmad Plumbing Services"
                onChangeText={field.onChange} value={field.value} />
            )} />
          </Field>

          <Field label="Display Name" error={errors.displayName?.message}>
            <Controller control={control} name="displayName" render={({ field }) => (
              <TextInput style={styles.input} placeholder="e.g. Ahmad Razif"
                onChangeText={field.onChange} value={field.value} />
            )} />
          </Field>

          <Field label="Phone Number *" error={errors.phone?.message}>
            <Controller control={control} name="phone" render={({ field }) => (
              <TextInput style={styles.input} placeholder="+60 12-345 6789"
                keyboardType="phone-pad" onChangeText={field.onChange} value={field.value} />
            )} />
          </Field>

          <View style={styles.row}>
            <View style={{ flex: 1 }}>
              <Field label="City *" error={errors.city?.message}>
                <Controller control={control} name="city" render={({ field }) => (
                  <TextInput style={styles.input} placeholder="e.g. Petaling Jaya"
                    onChangeText={field.onChange} value={field.value} />
                )} />
              </Field>
            </View>
            <View style={{ flex: 1 }}>
              <Field label="State *" error={errors.state?.message}>
                <Controller control={control} name="state" render={({ field }) => (
                  <TextInput style={styles.input} placeholder="e.g. Selangor"
                    onChangeText={field.onChange} value={field.value} />
                )} />
              </Field>
            </View>
          </View>

          <Field label="CIDB Grade" error={errors.cidbGrade?.message}>
            <Controller control={control} name="cidbGrade" render={({ field }) => (
              <TextInput style={styles.input} placeholder="e.g. G1, G5, G7"
                onChangeText={field.onChange} value={field.value} />
            )} />
          </Field>

          <Field label="Short Bio" error={errors.bio?.message}>
            <Controller control={control} name="bio" render={({ field }) => (
              <TextInput style={[styles.input, styles.textArea]}
                placeholder="Briefly describe your experience and expertise..."
                multiline numberOfLines={4} textAlignVertical="top"
                onChangeText={field.onChange} value={field.value} />
            )} />
          </Field>

          <Text style={styles.hint}>
            💡 You'll be able to add trade specialisations and brand certifications after setup
          </Text>

          <TouchableOpacity
            style={[styles.submitBtn, loading && styles.submitBtnDisabled]}
            onPress={handleSubmit(onSubmit)}
            disabled={loading}
            activeOpacity={0.85}
          >
            {loading
              ? <ActivityIndicator color="#fff" />
              : <Text style={styles.submitBtnText}>Create My Profile →</Text>
            }
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

function Field({ label, error, children }: { label: string; error?: string; children: React.ReactNode }) {
  return (
    <View style={styles.field}>
      <Text style={styles.label}>{label}</Text>
      {children}
      {error && <Text style={styles.error}>{error}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F9FAFB' },
  header: { padding: 24, paddingBottom: 8 },
  step: { fontSize: 12, color: '#F97316', fontWeight: '700', textTransform: 'uppercase', letterSpacing: 1 },
  title: { fontSize: 24, fontWeight: '800', color: '#1C1C1E', marginTop: 4 },
  subtitle: { fontSize: 14, color: '#6B7280', marginTop: 6, lineHeight: 20 },
  form: { padding: 24, gap: 4 },
  field: { marginBottom: 16 },
  label: { fontSize: 13, fontWeight: '600', color: '#374151', marginBottom: 6 },
  input: {
    backgroundColor: '#fff', borderWidth: 1, borderColor: '#E5E7EB',
    borderRadius: 10, padding: 12, fontSize: 15, color: '#1C1C1E',
  },
  textArea: { height: 100 },
  error: { fontSize: 12, color: '#EF4444', marginTop: 4 },
  row: { flexDirection: 'row', gap: 12 },
  hint: { fontSize: 13, color: '#6B7280', backgroundColor: '#FFF7ED', padding: 12, borderRadius: 10, lineHeight: 20 },
  submitBtn: {
    backgroundColor: '#F97316', borderRadius: 14,
    paddingVertical: 16, alignItems: 'center', marginTop: 8,
  },
  submitBtnDisabled: { opacity: 0.7 },
  submitBtnText: { color: '#fff', fontSize: 16, fontWeight: '700' },
});
