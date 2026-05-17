import { View, Text, ScrollView, StyleSheet, TouchableOpacity, Alert, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { contractorService } from '../../services/contractorService';
import { reviewService } from '../../services/reviewService';
import { RatingStars } from '../../components/common/RatingStars';
import { StatusBadge } from '../../components/common/StatusBadge';

export default function SubProfile() {
  const qc = useQueryClient();

  const { data: profile, isLoading } = useQuery({
    queryKey: ['my-profile'],
    queryFn:  contractorService.getMyProfile,
    retry: false,
  });

  const { data: summary } = useQuery({
    queryKey: ['rating-summary', profile?.id],
    queryFn:  () => reviewService.getRatingSummary(profile!.id),
    enabled:  !!profile?.id,
  });

  const removeTrade = useMutation({
    mutationFn: (id: string) => contractorService.removeTrade(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['my-profile'] }),
  });

  const removeCert = useMutation({
    mutationFn: (id: string) => contractorService.removeCertification(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['my-profile'] }),
  });

  if (isLoading) return <ActivityIndicator color="#F97316" style={{ flex: 1, marginTop: 100 }} />;
  if (!profile) return (
    <SafeAreaView style={styles.container}>
      <View style={styles.empty}>
        <Text style={styles.emptyIcon}>👤</Text>
        <Text style={styles.emptyText}>No profile found</Text>
      </View>
    </SafeAreaView>
  );

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Profile header */}
        <View style={styles.header}>
          <View style={styles.avatar}>
            <Text style={styles.avatarText}>{profile.businessName[0]}</Text>
          </View>
          <Text style={styles.businessName}>{profile.businessName}</Text>
          {profile.displayName && <Text style={styles.displayName}>{profile.displayName}</Text>}
          <Text style={styles.location}>{profile.city}, {profile.state}</Text>
          <View style={styles.row}>
            <RatingStars rating={profile.averageRating} size={16} />
            <Text style={styles.reviewCount}>({profile.totalReviews} reviews)</Text>
          </View>
          <View style={[styles.row, { marginTop: 8, gap: 8 }]}>
            <StatusBadge status={profile.subscriptionTier} />
            {profile.verified && <StatusBadge status="VERIFIED" />}
            {profile.cidbGrade && (
              <View style={styles.cidbBadge}><Text style={styles.cidbText}>CIDB {profile.cidbGrade}</Text></View>
            )}
          </View>
        </View>

        {/* Rating breakdown */}
        {summary && summary.totalReviews > 0 && (
          <Section title="Rating Breakdown">
            <RatingRow label="Workmanship"  value={summary.averageWorkmanship} />
            <RatingRow label="Punctuality"  value={summary.averagePunctuality} />
            <RatingRow label="Communication" value={summary.averageCommunication} />
            <RatingRow label="Brand Knowledge" value={summary.averageBrandKnowledge} />
          </Section>
        )}

        {/* Bio */}
        {profile.bio && (
          <Section title="About">
            <Text style={styles.bio}>{profile.bio}</Text>
          </Section>
        )}

        {/* Trade specializations */}
        <Section title="Trade Specialisations">
          {profile.tradeSpecializations?.length === 0
            ? <Text style={styles.emptySection}>No trades added yet</Text>
            : profile.tradeSpecializations?.map(t => (
              <View key={t.id} style={styles.tradeRow}>
                <View style={styles.tradeInfo}>
                  <Text style={styles.tradeName}>{t.tradeCategory.replace(/_/g, ' ')}</Text>
                  {t.yearsExperience && <Text style={styles.tradeYears}>{t.yearsExperience} years exp.</Text>}
                </View>
                <TouchableOpacity onPress={() => Alert.alert('Remove Trade', 'Remove this trade?', [
                  { text: 'Cancel', style: 'cancel' },
                  { text: 'Remove', style: 'destructive', onPress: () => removeTrade.mutate(t.id) },
                ])}>
                  <Text style={styles.removeBtn}>✕</Text>
                </TouchableOpacity>
              </View>
            ))
          }
        </Section>

        {/* Brand certifications */}
        <Section title="Brand Certifications">
          {profile.brandCertifications?.length === 0
            ? <Text style={styles.emptySection}>No certifications added yet</Text>
            : profile.brandCertifications?.map(c => (
              <View key={c.id} style={styles.certRow}>
                <View style={styles.certInfo}>
                  <View style={styles.row}>
                    <Text style={styles.brandName}>{c.brandName}</Text>
                    {c.verified && <Text style={styles.certVerified}>✓ Verified</Text>}
                  </View>
                  <Text style={styles.certName}>{c.certificationName}</Text>
                  {c.certificationNo && <Text style={styles.certNo}>#{c.certificationNo}</Text>}
                </View>
                <TouchableOpacity onPress={() => Alert.alert('Remove Certification', 'Remove this certification?', [
                  { text: 'Cancel', style: 'cancel' },
                  { text: 'Remove', style: 'destructive', onPress: () => removeCert.mutate(c.id) },
                ])}>
                  <Text style={styles.removeBtn}>✕</Text>
                </TouchableOpacity>
              </View>
            ))
          }
        </Section>

        {/* Portfolio */}
        <Section title={`Portfolio (${profile.portfolio?.length ?? 0})`}>
          {profile.portfolio?.length === 0
            ? <Text style={styles.emptySection}>No portfolio items yet</Text>
            : profile.portfolio?.map(p => (
              <View key={p.id} style={styles.portfolioItem}>
                <Text style={styles.portfolioTitle}>{p.projectTitle}</Text>
                {p.location && <Text style={styles.portfolioLoc}>{p.location}</Text>}
                {p.brandUsed && <Text style={styles.portfolioBrand}>Brand: {p.brandUsed}</Text>}
              </View>
            ))
          }
        </Section>
      </ScrollView>
    </SafeAreaView>
  );
}

function Section({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <View style={styles.section}>
      <Text style={styles.sectionTitle}>{title}</Text>
      {children}
    </View>
  );
}

function RatingRow({ label, value }: { label: string; value?: string }) {
  if (!value) return null;
  return (
    <View style={styles.ratingRow}>
      <Text style={styles.ratingLabel}>{label}</Text>
      <RatingStars rating={value} size={13} />
    </View>
  );
}

const styles = StyleSheet.create({
  container:    { flex: 1, backgroundColor: '#F9FAFB' },
  header:       { backgroundColor: '#fff', padding: 24, alignItems: 'center', marginBottom: 8 },
  avatar:       { width: 72, height: 72, borderRadius: 36, backgroundColor: '#F97316', justifyContent: 'center', alignItems: 'center', marginBottom: 12 },
  avatarText:   { color: '#fff', fontSize: 28, fontWeight: '700' },
  businessName: { fontSize: 20, fontWeight: '800', color: '#1C1C1E' },
  displayName:  { fontSize: 14, color: '#9CA3AF', marginTop: 2 },
  location:     { fontSize: 13, color: '#9CA3AF', marginTop: 4 },
  row:          { flexDirection: 'row', alignItems: 'center', gap: 6, marginTop: 4 },
  reviewCount:  { fontSize: 12, color: '#9CA3AF' },
  cidbBadge:    { backgroundColor: '#EFF6FF', paddingHorizontal: 8, paddingVertical: 3, borderRadius: 6 },
  cidbText:     { fontSize: 11, color: '#1D4ED8', fontWeight: '700' },
  section:      { backgroundColor: '#fff', marginHorizontal: 16, marginBottom: 12, borderRadius: 14, padding: 16 },
  sectionTitle: { fontSize: 14, fontWeight: '700', color: '#374151', marginBottom: 12 },
  bio:          { fontSize: 14, color: '#6B7280', lineHeight: 22 },
  emptySection: { fontSize: 13, color: '#9CA3AF' },
  tradeRow:     { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingVertical: 8, borderBottomWidth: 1, borderBottomColor: '#F3F4F6' },
  tradeInfo:    { flex: 1 },
  tradeName:    { fontSize: 14, fontWeight: '600', color: '#1C1C1E' },
  tradeYears:   { fontSize: 12, color: '#9CA3AF', marginTop: 2 },
  certRow:      { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start', paddingVertical: 8, borderBottomWidth: 1, borderBottomColor: '#F3F4F6' },
  certInfo:     { flex: 1 },
  brandName:    { fontSize: 14, fontWeight: '700', color: '#F97316' },
  certVerified: { fontSize: 11, color: '#10B981', fontWeight: '700' },
  certName:     { fontSize: 13, color: '#374151', marginTop: 2 },
  certNo:       { fontSize: 11, color: '#9CA3AF', marginTop: 1 },
  removeBtn:    { fontSize: 16, color: '#9CA3AF', padding: 4 },
  ratingRow:    { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingVertical: 6 },
  ratingLabel:  { fontSize: 13, color: '#374151' },
  portfolioItem: { paddingVertical: 8, borderBottomWidth: 1, borderBottomColor: '#F3F4F6' },
  portfolioTitle: { fontSize: 14, fontWeight: '600', color: '#1C1C1E' },
  portfolioLoc:  { fontSize: 12, color: '#9CA3AF', marginTop: 2 },
  portfolioBrand: { fontSize: 12, color: '#F97316', marginTop: 2, fontWeight: '600' },
  empty:         { flex: 1, alignItems: 'center', justifyContent: 'center' },
  emptyIcon:     { fontSize: 48, marginBottom: 12 },
  emptyText:     { fontSize: 16, color: '#374151', fontWeight: '700' },
});
