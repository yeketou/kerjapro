import { View, Text, ScrollView, StyleSheet, TouchableOpacity, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { router } from 'expo-router';
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '../../hooks/useAuth';
import { projectService } from '../../services/projectService';
import { bookingService } from '../../services/bookingService';
import { StatusBadge } from '../../components/common/StatusBadge';
import { KerjaProLogo } from '../../components/common/KerjaProLogo';

export default function MCDashboard() {
  const { fullName, logout } = useAuth();

  const { data: projects, isLoading: loadingProjects } = useQuery({
    queryKey: ['projects'],
    queryFn: () => projectService.getProjects({ size: 5 }),
  });

  const { data: bookings, isLoading: loadingBookings } = useQuery({
    queryKey: ['bookings-outgoing'],
    queryFn: () => bookingService.getOutgoing({ size: 5 }),
  });

  const activeProjects  = projects?.content?.filter((p: any) => p.status === 'ACTIVE').length ?? 0;
  const pendingBookings = bookings?.content?.filter((b: any) => b.status === 'PENDING').length ?? 0;

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
          <Text style={styles.greeting}>Good day,</Text>
          <Text style={styles.name}>{fullName ?? 'Contractor'} 👋</Text>
        </View>

        {/* Stats */}
        <View style={styles.stats}>
          <StatCard label="Active Projects" value={activeProjects} color="#10B981" icon="🏗️" />
          <StatCard label="Pending Bookings" value={pendingBookings} color="#F97316" icon="📅" />
        </View>

        {/* Quick Actions */}
        <Text style={styles.sectionTitle}>Quick Actions</Text>
        <View style={styles.actions}>
          <ActionCard icon="🔍" label="Find Subcontractors" onPress={() => router.push('/main-contractor/search')} />
          <ActionCard icon="📋" label="New Project"         onPress={() => router.push('/main-contractor/projects/index')} />
        </View>

        {/* Recent Projects */}
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Recent Projects</Text>
          <TouchableOpacity onPress={() => router.push('/main-contractor/projects/index')}>
            <Text style={styles.seeAll}>See all</Text>
          </TouchableOpacity>
        </View>

        {loadingProjects
          ? <ActivityIndicator color="#F97316" style={{ marginTop: 20 }} />
          : projects?.content?.slice(0, 3).map((p: any) => (
            <TouchableOpacity
              key={p.id}
              style={styles.projectRow}
              onPress={() => router.push(`/main-contractor/projects/${p.id}`)}
              activeOpacity={0.85}
            >
              <View style={styles.projectInfo}>
                <Text style={styles.projectTitle} numberOfLines={1}>{p.title}</Text>
                <Text style={styles.projectLocation}>{p.location ?? 'No location'}</Text>
              </View>
              <View style={styles.projectRight}>
                <StatusBadge status={p.status} />
                <Text style={styles.packageCount}>
                  {p.workPackages?.length ?? 0} packages
                </Text>
              </View>
            </TouchableOpacity>
          ))
        }

        {/* Recent Bookings */}
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Recent Bookings</Text>
          <TouchableOpacity onPress={() => router.push('/main-contractor/bookings')}>
            <Text style={styles.seeAll}>See all</Text>
          </TouchableOpacity>
        </View>

        {loadingBookings
          ? <ActivityIndicator color="#F97316" style={{ marginTop: 12 }} />
          : bookings?.content?.slice(0, 3).map((b: any) => (
            <View key={b.id} style={styles.bookingRow}>
              <Text style={styles.bookingDate}>
                {new Date(b.appointmentAt).toLocaleDateString('en-MY', { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' })}
              </Text>
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

function ActionCard({ icon, label, onPress }: any) {
  return (
    <TouchableOpacity style={styles.actionCard} onPress={onPress} activeOpacity={0.85}>
      <Text style={styles.actionIcon}>{icon}</Text>
      <Text style={styles.actionLabel}>{label}</Text>
    </TouchableOpacity>
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
  stats:          { flexDirection: 'row', gap: 12, padding: 20, paddingTop: 12 },
  statCard:       { flex: 1, backgroundColor: '#fff', borderRadius: 14, padding: 16, borderLeftWidth: 4, shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.06, shadowRadius: 4, elevation: 2 },
  statIcon:       { fontSize: 22, marginBottom: 4 },
  statValue:      { fontSize: 28, fontWeight: '800' },
  statLabel:      { fontSize: 12, color: '#9CA3AF', marginTop: 2 },
  actions:        { flexDirection: 'row', gap: 12, paddingHorizontal: 20, marginBottom: 8 },
  actionCard:     { flex: 1, backgroundColor: '#fff', borderRadius: 14, padding: 16, alignItems: 'center', shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.06, shadowRadius: 4, elevation: 2 },
  actionIcon:     { fontSize: 28, marginBottom: 6 },
  actionLabel:    { fontSize: 13, fontWeight: '600', color: '#374151', textAlign: 'center' },
  sectionHeader:  { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingHorizontal: 20, marginTop: 12, marginBottom: 8 },
  sectionTitle:   { fontSize: 16, fontWeight: '700', color: '#1C1C1E', paddingHorizontal: 20, marginTop: 12, marginBottom: 8 },
  seeAll:         { fontSize: 13, color: '#F97316', fontWeight: '600' },
  projectRow:     { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#fff', marginHorizontal: 20, marginBottom: 8, borderRadius: 12, padding: 14 },
  projectInfo:    { flex: 1 },
  projectTitle:   { fontSize: 14, fontWeight: '600', color: '#1C1C1E' },
  projectLocation: { fontSize: 12, color: '#9CA3AF', marginTop: 2 },
  projectRight:   { alignItems: 'flex-end', gap: 4 },
  packageCount:   { fontSize: 11, color: '#9CA3AF' },
  bookingRow:     { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#fff', marginHorizontal: 20, marginBottom: 8, borderRadius: 12, padding: 14 },
  bookingDate:    { fontSize: 13, color: '#374151', fontWeight: '500' },
});
