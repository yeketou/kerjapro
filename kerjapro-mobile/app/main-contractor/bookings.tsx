import { View, Text, ScrollView, StyleSheet, Alert, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { router } from 'expo-router';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { bookingService } from '../../services/bookingService';
import { BookingCard } from '../../components/booking/BookingCard';

export default function MCBookings() {
  const qc = useQueryClient();

  const { data, isLoading } = useQuery({
    queryKey: ['bookings-outgoing'],
    queryFn:  () => bookingService.getOutgoing({ size: 50 }),
  });

  const complete = useMutation({
    mutationFn: (id: string) => bookingService.complete(id),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['bookings-outgoing'] }),
  });

  const cancel = useMutation({
    mutationFn: ({ id, reason }: { id: string; reason?: string }) =>
      bookingService.cancel(id, reason),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['bookings-outgoing'] }),
  });

  const handleCancel = (id: string) => {
    Alert.alert('Cancel Booking', 'Are you sure you want to cancel?', [
      { text: 'No', style: 'cancel' },
      { text: 'Cancel Booking', style: 'destructive',
        onPress: () => cancel.mutate({ id, reason: 'Cancelled by contractor' }) },
    ]);
  };

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.title}>My Bookings</Text>
      {isLoading
        ? <ActivityIndicator color="#F97316" style={{ marginTop: 40 }} />
        : (
          <ScrollView contentContainerStyle={styles.list}>
            {data?.content?.length === 0 && (
              <View style={styles.empty}>
                <Text style={styles.emptyIcon}>📅</Text>
                <Text style={styles.emptyText}>No bookings yet</Text>
                <Text style={styles.emptyHint}>Find a subcontractor and book an appointment</Text>
              </View>
            )}
            {data?.content?.map((b: any) => (
              <BookingCard
                key={b.id}
                booking={b}
                role="MAIN_CONTRACTOR"
                onComplete={() => complete.mutate(b.id)}
                onCancel={() => handleCancel(b.id)}
                onReview={() => router.push({ pathname: '/main-contractor/review', params: { bookingId: b.id, subProfileId: b.subProfileId } })}
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
