import { View, Text, StyleSheet } from 'react-native';

const COLORS: Record<string, { bg: string; text: string }> = {
  DRAFT:       { bg: '#F3F4F6', text: '#6B7280' },
  ACTIVE:      { bg: '#D1FAE5', text: '#065F46' },
  COMPLETED:   { bg: '#DBEAFE', text: '#1E40AF' },
  CANCELLED:   { bg: '#FEE2E2', text: '#991B1B' },
  PENDING:     { bg: '#FEF3C7', text: '#92400E' },
  CONFIRMED:   { bg: '#D1FAE5', text: '#065F46' },
  DECLINED:    { bg: '#FEE2E2', text: '#991B1B' },
  OPEN:        { bg: '#EDE9FE', text: '#5B21B6' },
  ASSIGNED:    { bg: '#DBEAFE', text: '#1E40AF' },
  IN_PROGRESS: { bg: '#FEF3C7', text: '#92400E' },
  FREE:        { bg: '#F3F4F6', text: '#6B7280' },
  PRO:         { bg: '#DBEAFE', text: '#1E40AF' },
  PREMIUM:     { bg: '#FEF3C7', text: '#92400E' },
};

export function StatusBadge({ status }: { status: string }) {
  const color = COLORS[status] ?? { bg: '#F3F4F6', text: '#6B7280' };
  return (
    <View style={[styles.badge, { backgroundColor: color.bg }]}>
      <Text style={[styles.text, { color: color.text }]}>
        {status.replace(/_/g, ' ')}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  badge: { paddingHorizontal: 8, paddingVertical: 3, borderRadius: 6, alignSelf: 'flex-start' },
  text:  { fontSize: 11, fontWeight: '700', textTransform: 'uppercase', letterSpacing: 0.5 },
});
