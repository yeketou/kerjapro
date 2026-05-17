import { View, Text, ScrollView, StyleSheet, Alert, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { bookingService } from '../../services/bookingService';
import { contractorService } from '../../services/contractorService';
import { BookingCard } from '../../components/booking/BookingCard';

export default function SubBookings() {
  const qc = useQueryClient();

  const { data: profile } = useQuery({
    queryKey: ['my-profile'],
    queryFn:  contractorService.getMyProfile,
    retry: false,
  });

  const { data, isLoading } = useQuery({
    queryKey: ['bookings-incoming', profile?.id],
    queryFn:  () => bookingService.getIncoming(profile!.id, { size: 50 }),
    enabled:  !!profile?.id,
  });

  const confirm = useMutation({
    mutationFn: (id: string) => bookingService.confirm(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['bookings-incoming'] }),
  });

  const decline = useMutation({
    mutationFn: ({ id, reason }: { id: string; reason?: string }) =>
      bookingService.decline(id, reason),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['bookings-incoming'] }),
  });

  const handleDecline = (id: string) => {
    Alert.alert('Decline Booking', 'Are you sure you want to decline?', [
      { text: 'No', style: 'cancel' },
      { text: 'Decline', style: 'destructive',
        onPress: () => decline.mutate({ id, reason: 'Declined by subcontractor' }) },
    ]);
  };

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.title}>Incoming Bookings</Text>
      {!profile
        ? (
          <View style={styles.empty}>
            <Text style={styles.emptyIcon}>👤</Text>
            <Text style={styles.emptyText}>Set up your profile first</Text>
          </View>
        )
        : isLoading
          ? <ActivityIndicator color="#F97316" style={{ marginTop: 40 }} />
          : (
            <ScrollView contentContainerStyle={styles.list}>
              {data?.content?.length === 0 && (
                <View style={styles.empty}>
                  <Text style={styles.emptyIcon}>📭</Text>
                  <Text style={styles.emptyText}>No bookings yet</Text>
                  <Text style={styles.emptyHint}>Keep your profile updated to get discovered</Text>
                </View>
              )}
              {data?.content?.map((b: any) => (
                <BookingCard
                  key={b.id}
                  booking={b}
                  role="SUBCONTRACTOR"
                  onConfirm={() => confirm.mutate(b.id)}
                  onDecline={() => handleDecline(b.id)}
                />
              ))}
            </ScrollView>
          )
      }
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F9FAFB' },
  title:     { fontSize: 22, fontWeight: '800', color: '#1C1C1E', padding: 20, paddingBottom: 12 },
  list:      { paddingHorizontal: 16, paddingBottom: 24 },
  empty:     { alignItems: 'center', paddingTop: 80 },
  emptyIcon: { fontSize: 48, marginBottom: 12 },
  emptyText: { fontSize: 16, fontWeight: '700', color: '#374151' },
  emptyHint: { fontSize: 13, color: '#9CA3AF', marginTop: 4, textAlign: 'center' },
});
