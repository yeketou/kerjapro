import { View, Text, TouchableOpacity, StyleSheet, Image } from 'react-native';
import { router } from 'expo-router';
import { SubcontractorProfile } from '../../types';
import { RatingStars } from '../common/RatingStars';
import { StatusBadge } from '../common/StatusBadge';

interface Props {
  profile: SubcontractorProfile;
  onBook?: () => void;
}

export function SubcontractorCard({ profile, onBook }: Props) {
  return (
    <TouchableOpacity
      style={styles.card}
      onPress={() => router.push(`/main-contractor/search/${profile.id}`)}
      activeOpacity={0.85}
    >
      {/* Header */}
      <View style={styles.header}>
        <View style={styles.avatar}>
          {profile.profilePhotoUrl
            ? <Image source={{ uri: profile.profilePhotoUrl }} style={styles.avatarImg} />
            : <Text style={styles.avatarInitial}>
                {(profile.businessName ?? '?')[0].toUpperCase()}
              </Text>
          }
        </View>
        <View style={styles.headerInfo}>
          <View style={styles.nameRow}>
            <Text style={styles.name} numberOfLines={1}>{profile.businessName}</Text>
            {profile.verified && <Text style={styles.verified}>✓</Text>}
          </View>
          <Text style={styles.location}>{profile.city}, {profile.state}</Text>
          <RatingStars rating={profile.averageRating} />
        </View>
        <StatusBadge status={profile.subscriptionTier} />
      </View>

      {/* Trades */}
      {profile.tradeSpecializations?.length > 0 && (
        <View style={styles.tags}>
          {profile.tradeSpecializations.slice(0, 3).map(t => (
            <View key={t.id} style={styles.tag}>
              <Text style={styles.tagText}>{t.tradeCategory.replace(/_/g, ' ')}</Text>
            </View>
          ))}
        </View>
      )}

      {/* Brand certifications */}
      {profile.brandCertifications?.length > 0 && (
        <View style={styles.brands}>
          <Text style={styles.brandsLabel}>Certified: </Text>
          <Text style={styles.brandsText} numberOfLines={1}>
            {profile.brandCertifications.map(b => b.brandName).join(' · ')}
          </Text>
        </View>
      )}

      {/* Footer */}
      <View style={styles.footer}>
        <Text style={styles.jobs}>{profile.totalCompletedJobs} jobs completed</Text>
        {onBook && (
          <TouchableOpacity style={styles.bookBtn} onPress={onBook} activeOpacity={0.85}>
            <Text style={styles.bookBtnText}>Book</Text>
          </TouchableOpacity>
        )}
      </View>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#fff', borderRadius: 16, padding: 16, marginBottom: 12,
    shadowColor: '#000', shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.07, shadowRadius: 8, elevation: 2,
  },
  header:      { flexDirection: 'row', alignItems: 'flex-start', gap: 12, marginBottom: 12 },
  avatar:      { width: 48, height: 48, borderRadius: 24, backgroundColor: '#F97316', justifyContent: 'center', alignItems: 'center' },
  avatarImg:   { width: 48, height: 48, borderRadius: 24 },
  avatarInitial: { color: '#fff', fontSize: 20, fontWeight: '700' },
  headerInfo:  { flex: 1 },
  nameRow:     { flexDirection: 'row', alignItems: 'center', gap: 4 },
  name:        { fontSize: 15, fontWeight: '700', color: '#1C1C1E', flex: 1 },
  verified:    { color: '#10B981', fontWeight: '800', fontSize: 14 },
  location:    { fontSize: 12, color: '#9CA3AF', marginBottom: 3 },
  tags:        { flexDirection: 'row', flexWrap: 'wrap', gap: 6, marginBottom: 8 },
  tag:         { backgroundColor: '#F3F4F6', paddingHorizontal: 8, paddingVertical: 3, borderRadius: 6 },
  tagText:     { fontSize: 11, color: '#374151', fontWeight: '600' },
  brands:      { flexDirection: 'row', marginBottom: 10 },
  brandsLabel: { fontSize: 12, color: '#9CA3AF' },
  brandsText:  { fontSize: 12, color: '#F97316', fontWeight: '600', flex: 1 },
  footer:      { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  jobs:        { fontSize: 12, color: '#9CA3AF' },
  bookBtn:     { backgroundColor: '#F97316', paddingHorizontal: 16, paddingVertical: 8, borderRadius: 8 },
  bookBtnText: { color: '#fff', fontSize: 13, fontWeight: '700' },
});
