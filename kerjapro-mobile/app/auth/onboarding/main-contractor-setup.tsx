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

const schema = z.object({
  companyName: z.string().min(2, 'Company name is required'),
  slug:        z.string()
    .min(3, 'Workspace ID must be at least 3 characters')
    .max(30, 'Workspace ID must be under 30 characters')
    .regex(/^[a-z0-9-]+$/, 'Only lowercase letters, numbers and hyphens'),
  phone:       z.string().min(9, 'Valid phone number required'),
  plan:        z.enum(['STARTER', 'PROFESSIONAL', 'ENTERPRISE']),
});

type FormData = z.infer<typeof schema>;

const PLANS = [
  {
    id: 'STARTER',
    name: 'Starter',
    price: 'RM 199/mo',
    features: ['Up to 5 active projects', '10 bookings/month', 'Basic search'],
  },
  {
    id: 'PROFESSIONAL',
    name: 'Professional',
    price: 'RM 599/mo',
    features: ['Unlimited projects', 'Unlimited bookings', 'Advanced filters', 'Private sub network'],
    popular: true,
  },
  {
    id: 'ENTERPRISE',
    name: 'Enterprise',
    price: 'RM 1,499/mo',
    features: ['Everything in Pro', 'Custom branding', 'API access', 'Dedicated support'],
  },
];

export default function MainContractorSetupScreen() {
  const [loading, setLoading] = useState(false);
  const [selectedPlan, setSelectedPlan] = useState<string>('PROFESSIONAL');

  const { control, handleSubmit, setValue, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { plan: 'PROFESSIONAL' },
  });

  const onSubmit = async (data: FormData) => {
    setLoading(true);
    try {
      await apiClient.post('/api/contractors/tenants', data);
      router.replace('/main-contractor/dashboard');
    } catch {
      Alert.alert('Error', 'Could not set up your workspace. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView showsVerticalScrollIndicator={false} keyboardShouldPersistTaps="handled">
        <View style={styles.header}>
          <Text style={styles.step}>Company Setup</Text>
          <Text style={styles.title}>Create Your Workspace</Text>
          <Text style={styles.subtitle}>Your team will use this workspace to manage projects and subcontractors</Text>
        </View>

        <View style={styles.form}>
          <Field label="Company Name *" error={errors.companyName?.message}>
            <Controller control={control} name="companyName" render={({ field }) => (
              <TextInput style={styles.input} placeholder="e.g. Bina Jaya Sdn Bhd"
                onChangeText={field.onChange} value={field.value} />
            )} />
          </Field>

          <Field label="Workspace ID *" error={errors.slug?.message}>
            <Controller control={control} name="slug" render={({ field }) => (
              <View>
                <View style={styles.slugRow}>
                  <Text style={styles.slugPrefix}>kerjapro.com/</Text>
                  <TextInput style={[styles.input, styles.slugInput]}
                    placeholder="bina-jaya" autoCapitalize="none" autoCorrect={false}
                    onChangeText={(v) => field.onChange(v.toLowerCase().replace(/\s/g, '-'))}
                    value={field.value} />
                </View>
                <Text style={styles.slugHint}>This is your unique workspace URL — cannot be changed later</Text>
              </View>
            )} />
          </Field>

          <Field label="Phone Number *" error={errors.phone?.message}>
            <Controller control={control} name="phone" render={({ field }) => (
              <TextInput style={styles.input} placeholder="+60 3-1234 5678"
                keyboardType="phone-pad" onChangeText={field.onChange} value={field.value} />
            )} />
          </Field>

          {/* Plan Selection */}
          <Text style={styles.label}>Choose Your Plan *</Text>
          <View style={styles.plans}>
            {PLANS.map((plan) => (
              <TouchableOpacity
                key={plan.id}
                style={[styles.planCard, selectedPlan === plan.id && styles.planCardActive]}
                onPress={() => { setSelectedPlan(plan.id); setValue('plan', plan.id as any); }}
                activeOpacity={0.85}
              >
                {plan.popular && (
                  <View style={styles.popularBadge}>
                    <Text style={styles.popularText}>Most Popular</Text>
                  </View>
                )}
                <Text style={[styles.planName, selectedPlan === plan.id && styles.planNameActive]}>
                  {plan.name}
                </Text>
                <Text style={[styles.planPrice, selectedPlan === plan.id && styles.planPriceActive]}>
                  {plan.price}
                </Text>
                <View style={styles.planFeatures}>
                  {plan.features.map((f, i) => (
                    <View key={i} style={styles.planFeatureRow}>
                      <Text style={[styles.planCheck, selectedPlan === plan.id && { color: '#fff' }]}>✓</Text>
                      <Text style={[styles.planFeatureText, selectedPlan === plan.id && { color: '#fff' }]}>{f}</Text>
                    </View>
                  ))}
                </View>
              </TouchableOpacity>
            ))}
          </View>

          <Text style={styles.trialNote}>🎉 All plans include a 14-day free trial</Text>

          <TouchableOpacity
            style={[styles.submitBtn, loading && styles.submitBtnDisabled]}
            onPress={handleSubmit(onSubmit)}
            disabled={loading}
            activeOpacity={0.85}
          >
            {loading
              ? <ActivityIndicator color="#fff" />
              : <Text style={styles.submitBtnText}>Launch My Workspace →</Text>
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
  slugRow: { flexDirection: 'row', alignItems: 'center' },
  slugPrefix: { fontSize: 14, color: '#9CA3AF', backgroundColor: '#F3F4F6', padding: 12, borderRadius: 10, borderWidth: 1, borderColor: '#E5E7EB', marginRight: 4 },
  slugInput: { flex: 1 },
  slugHint: { fontSize: 12, color: '#9CA3AF', marginTop: 4 },
  error: { fontSize: 12, color: '#EF4444', marginTop: 4 },
  plans: { gap: 12, marginBottom: 16 },
  planCard: {
    backgroundColor: '#fff', borderWidth: 2, borderColor: '#E5E7EB',
    borderRadius: 16, padding: 16,
  },
  planCardActive: { backgroundColor: '#F97316', borderColor: '#F97316' },
  popularBadge: {
    backgroundColor: '#FEF3C7', alignSelf: 'flex-start',
    paddingHorizontal: 8, paddingVertical: 3, borderRadius: 6, marginBottom: 8,
  },
  popularText: { fontSize: 11, fontWeight: '700', color: '#D97706' },
  planName: { fontSize: 16, fontWeight: '700', color: '#1C1C1E' },
  planNameActive: { color: '#fff' },
  planPrice: { fontSize: 14, color: '#6B7280', marginBottom: 10 },
  planPriceActive: { color: '#FED7AA' },
  planFeatures: { gap: 4 },
  planFeatureRow: { flexDirection: 'row', gap: 8, alignItems: 'center' },
  planCheck: { color: '#F97316', fontWeight: '700', fontSize: 13 },
  planFeatureText: { fontSize: 13, color: '#374151' },
  trialNote: {
    fontSize: 13, color: '#065F46', backgroundColor: '#D1FAE5',
    padding: 12, borderRadius: 10, textAlign: 'center',
  },
  submitBtn: {
    backgroundColor: '#F97316', borderRadius: 14,
    paddingVertical: 16, alignItems: 'center', marginTop: 8,
  },
  submitBtnDisabled: { opacity: 0.7 },
  submitBtnText: { color: '#fff', fontSize: 16, fontWeight: '700' },
});
