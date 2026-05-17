import { View, Text, ScrollView, StyleSheet, TouchableOpacity, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { router } from 'expo-router';
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '../../hooks/useAuth';
import { contractorService } from '../../services/contractorService';
import { bookingService } from '../../services/bookingService';
import { RatingStars } from '../../components/common/RatingStars';
import { StatusBadge } from '../../components/common/StatusBadge';
import { KerjaProLogo } from '../../components/common/KerjaProLogo';

export default function SubDashboard() {
  const { fullName, logout } = useAuth();

  const { data: profile, isLoading: loadingProfile } = useQuery({
    queryKey: ['my-profile'],
    queryFn:  contractorService.getMyProfile,
    retry: false,
  });

  const { data: bookings, isLoading: loadingBookings } = useQuery({
    queryKey: ['bookings-incoming', profile?.id],
    queryFn:  () => bookingService.getIncoming(profile!.id, { size: 5 }),
    enabled:  !!profile?.id,
  });

  const pendingCount   = bookings?.content?.filter((b: any) => b.status === 'PENDING').length ?? 0;
  const confirmedCount = bookings?.content?.filter((b: any) => b.status === 'CONFIRMED').length ?? 0;

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Header */}
        <View style={styles.header}>
          <KerjaProLogo size={36} variant="full" theme="light" />
          <TouchableOpacity onPress={logout} style={styles.logoutBtn}>
            <Text style={styles.logoutText}>Logout</Text>
          </TouchableOpacity>
        </View>
        <View style={styles.greetingBox}>
          <Text style={styles.greeting}>Welcome back,</Text>
          <Text style={styles.name}>{fullName ?? 'Subcontractor'} 🔧</Text>
        </View>

        {/* Profile card */}
        {loadingProfile
          ? <ActivityIndicator color="#F97316" style={{ marginTop: 20 }} />
          : profile
            ? (
              <TouchableOpacity style={styles.profileCard} onPress={() => router.push('/subcontractor/profile')}>
                <View style={styles.profileTop}>
                  <View style={styles.avatar}>
                    <Text style={styles.avatarText}>{profile.businessName[0]}</Text>
                  </View>
                  <View style={styles.profileInfo}>
                    <View style={styles.profileNameRow}>
                      <Text style={styles.profileName}>{profile.businessName}</Text>
                      {profile.verified && <Text style={styles.verifiedBadge}>✓ Verified</Text>}
                    </View>
                    <Text style={styles.profileLocation}>{profile.city}, {profile.state}</Text>
                    <RatingStars rating={profile.averageRating} />
                  </View>
                  <StatusBadge status={profile.subscriptionTier} />
                </View>
                <View style={styles.profileStats}>
                  <MiniStat label="Jobs Done" value={profile.totalCompletedJobs} />
                  <MiniStat label="Reviews"   value={profile.totalReviews} />
                  <MiniStat label="Rating"    value={parseFloat(profile.averageRating ?? '0').toFixed(1)} />
                </View>
              </TouchableOpacity>
            )
            : (
              <TouchableOpacity style={styles.setupCard} onPress={() => router.push('/auth/onboarding/subcontractor-setup')}>
                <Text style={styles.setupIcon}>👤</Text>
                <Text style={styles.setupText}>Complete your profile to start getting booked</Text>
                <Text style={styles.setupCta}>Set up profile →</Text>
              </TouchableOpacity>
            )
        }

        {/* Booking stats */}
        <View style={styles.stats}>
          <StatCard label="Pending"   value={pendingCount}   color="#F59E0B" icon="⏳" />
          <StatCard label="Confirmed" value={confirmedCount} color="#10B981" icon="✅" />
        </View>

        {/* Upcoming bookings */}
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Upcoming Bookings</Text>
          <TouchableOpacity onPress={() => router.push('/subcontractor/bookings')}>
            <Text style={styles.seeAll}>See all</Text>
          </TouchableOpacity>
        </View>

        {loadingBookings
          ? <ActivityIndicator color="#F97316" />
          : bookings?.content?.slice(0,3).map((b: any) => (
            <View key={b.id} style={styles.bookingRow}>
              <View style={styles.bookingDate}>
                <Text style={styles.bookingDay}>{new Date(b.appointmentAt).getDate()}</Text>
                <Text style={styles.bookingMon}>
                  {new Date(b.appointmentAt).toLocaleString('en', { month: 'short' })}
                </Text>
              </View>
              <View style={styles.bookingInfo}>
                <Text style={styles.bookingTime}>
                  {new Date(b.appointmentAt).toLocaleTimeString('en-MY', { hour: '2-digit', minute: '2-digit' })}
                </Text>
                <Text style={styles.bookingLoc} numberOfLines={1}>{b.location ?? 'No location'}</Text>
              </View>
              <StatusBadge status={b.status} />
            </View>
          ))
        }
      </ScrollView>
    </SafeAreaView>
  );
}

function StatCard({ label, value, color, icon }: any) {
  return (
    <View style={[styles.statCard, { borderLeftColor: color }]}>
      <Text style={styles.statIcon}>{icon}</Text>
      <Text style={[styles.statValue, { color }]}>{value}</Text>
      <Text style={styles.statLabel}>{label}</Text>
    </View>
  );
}

function MiniStat({ label, value }: any) {
  return (
    <View style={styles.miniStat}>
      <Text style={styles.miniValue}>{value}</Text>
      <Text style={styles.miniLabel}>{label}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container:      { flex: 1, backgroundColor: '#F9FAFB' },
  header:         { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingHorizontal: 20, paddingTop: 16, paddingBottom: 4 },
  greetingBox:    { paddingHorizontal: 20, paddingBottom: 8 },
  greeting:       { fontSize: 13, color: '#9CA3AF' },
  name:           { fontSize: 20, fontWeight: '800', color: '#1C1C1E' },
  logoutBtn:      { padding: 8 },
  logoutText:     { fontSize: 13, color: '#9CA3AF' },
  profileCard:    { backgroundColor: '#fff', marginHorizontal: 16, borderRadius: 16, padding: 16, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.06, shadowRadius: 8, elevation: 2 },
  profileTop:     { flexDirection: 'row', alignItems: 'flex-start', gap: 12, marginBottom: 16 },
  avatar:         { width: 48, height: 48, borderRadius: 24, backgroundColor: '#F97316', justifyContent: 'center', alignItems: 'center' },
  avatarText:     { color: '#fff', fontSize: 20, fontWeight: '700' },
  profileInfo:    { flex: 1 },
  profileNameRow: { flexDirection: 'row', alignItems: 'center', gap: 6 },
  profileName:    { fontSize: 15, fontWeight: '700', color: '#1C1C1E' },
  verifiedBadge:  { fontSize: 11, color: '#10B981', fontWeight: '700' },
  profileLocation: { fontSize: 12, color: '#9CA3AF', marginBottom: 3 },
  profileStats:   { flexDirection: 'row', borderTopWidth: 1, borderTopColor: '#F3F4F6', paddingTop: 12 },
  miniStat:       { flex: 1, alignItems: 'center' },
  miniValue:      { fontSize: 18, fontWeight: '800', color: '#1C1C1E' },
  miniLabel:      { fontSize: 11, color: '#9CA3AF', marginTop: 2 },
  setupCard:      { backgroundColor: '#FFF7ED', marginHorizontal: 16, borderRadius: 16, padding: 20, alignItems: 'center', borderWidth: 1, borderColor: '#FED7AA', borderStyle: 'dashed' },
  setupIcon:      { fontSize: 36, marginBottom: 8 },
  setupText:      { fontSize: 14, color: '#92400E', textAlign: 'center', marginBottom: 12 },
  setupCta:       { fontSize: 14, color: '#F97316', fontWeight: '700' },
  stats:          { flexDirection: 'row', gap: 12, padding: 16 },
  statCard:       { flex: 1, backgroundColor: '#fff', borderRadius: 14, padding: 16, borderLeftWidth: 4, shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.06, shadowRadius: 4, elevation: 2 },
  statIcon:       { fontSize: 22, marginBottom: 4 },
  statValue:      { fontSize: 28, fontWeight: '800' },
  statLabel:      { fontSize: 12, color: '#9CA3AF', marginTop: 2 },
  sectionHeader:  { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingHorizontal: 20, marginTop: 4, marginBottom: 8 },
  sectionTitle:   { fontSize: 16, fontWeight: '700', color: '#1C1C1E' },
  seeAll:         { fontSize: 13, color: '#F97316', fontWeight: '600' },
  bookingRow:     { flexDirection: 'row', alignItems: 'center', gap: 12, backgroundColor: '#fff', marginHorizontal: 16, marginBottom: 8, borderRadius: 12, padding: 14 },
  bookingDate:    { backgroundColor: '#FFF7ED', borderRadius: 10, padding: 8, alignItems: 'center', minWidth: 40 },
  bookingDay:     { fontSize: 18, fontWeight: '800', color: '#F97316' },
  bookingMon:     { fontSize: 10, color: '#F97316', fontWeight: '600' },
  bookingInfo:    { flex: 1 },
  bookingTime:    { fontSize: 14, fontWeight: '600', color: '#1C1C1E' },
  bookingLoc:     { fontSize: 12, color: '#9CA3AF', marginTop: 2 },
});
