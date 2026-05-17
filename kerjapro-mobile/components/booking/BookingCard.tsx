import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { Booking } from '../../types';
import { StatusBadge } from '../common/StatusBadge';

interface Props {
  booking: Booking;
  role: 'MAIN_CONTRACTOR' | 'SUBCONTRACTOR';
  onConfirm?:  () => void;
  onDecline?:  () => void;
  onComplete?: () => void;
  onCancel?:   () => void;
  onReview?:   () => void;
}

export function BookingCard({ booking, role, onConfirm, onDecline, onComplete, onCancel, onReview }: Props) {
  const date = new Date(booking.appointmentAt);
  const dateStr = date.toLocaleDateString('en-MY', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' });
  const timeStr = date.toLocaleTimeString('en-MY', { hour: '2-digit', minute: '2-digit' });

  return (
    <View style={styles.card}>
      <View style={styles.header}>
        <View style={styles.dateBox}>
          <Text style={styles.dateDay}>{date.getDate()}</Text>
          <Text style={styles.dateMon}>{date.toLocaleString('en', { month: 'short' })}</Text>
        </View>
        <View style={styles.info}>
          <Text style={styles.time}>{timeStr} · {booking.durationMinutes} min</Text>
          <Text style={styles.location} numberOfLines={1}>{booking.location ?? 'No location set'}</Text>
          {booking.notes && <Text style={styles.notes} numberOfLines={2}>{booking.notes}</Text>}
        </View>
        <StatusBadge status={booking.status} />
      </View>

      {/* Actions */}
      <View style={styles.actions}>
        {role === 'SUBCONTRACTOR' && booking.status === 'PENDING' && (
          <>
            <ActionBtn label="Confirm" color="#10B981" onPress={onConfirm} />
            <ActionBtn label="Decline" color="#EF4444" onPress={onDecline} />
          </>
        )}
        {role === 'MAIN_CONTRACTOR' && booking.status === 'CONFIRMED' && (
          <>
            <ActionBtn label="Mark Complete" color="#3B82F6" onPress={onComplete} />
            <ActionBtn label="Cancel"        color="#EF4444" onPress={onCancel} />
          </>
        )}
        {role === 'MAIN_CONTRACTOR' && booking.status === 'PENDING' && (
          <ActionBtn label="Cancel" color="#EF4444" onPress={onCancel} />
        )}
        {role === 'MAIN_CONTRACTOR' && booking.status === 'COMPLETED' && (
          <ActionBtn label="Leave Review" color="#F97316" onPress={onReview} />
        )}
      </View>
    </View>
  );
}

function ActionBtn({ label, color, onPress }: { label: string; color: string; onPress?: () => void }) {
  return (
    <TouchableOpacity style={[styles.btn, { backgroundColor: color }]} onPress={onPress} activeOpacity={0.85}>
      <Text style={styles.btnText}>{label}</Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#fff', borderRadius: 14, padding: 16, marginBottom: 12,
    shadowColor: '#000', shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.06, shadowRadius: 6, elevation: 2,
  },
  header:  { flexDirection: 'row', gap: 12, marginBottom: 12 },
  dateBox: { backgroundColor: '#FFF7ED', borderRadius: 10, padding: 8, alignItems: 'center', minWidth: 44 },
  dateDay: { fontSize: 20, fontWeight: '800', color: '#F97316' },
  dateMon: { fontSize: 11, color: '#F97316', fontWeight: '600' },
  info:    { flex: 1 },
  time:    { fontSize: 14, fontWeight: '700', color: '#1C1C1E' },
  location: { fontSize: 12, color: '#9CA3AF', marginTop: 2 },
  notes:   { fontSize: 12, color: '#6B7280', marginTop: 4 },
  actions: { flexDirection: 'row', gap: 8 },
  btn:     { flex: 1, paddingVertical: 9, borderRadius: 8, alignItems: 'center' },
  btnText: { color: '#fff', fontSize: 13, fontWeight: '700' },
});
