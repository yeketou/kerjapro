import { View, Text, StyleSheet } from 'react-native';

interface Props {
  rating: number | string | null | undefined;
  size?: number;
  showNumber?: boolean;
}

export function RatingStars({ rating, size = 14, showNumber = true }: Props) {
  const value = rating ? parseFloat(String(rating)) : 0;
  const stars  = Math.round(value);

  return (
    <View style={styles.row}>
      {[1,2,3,4,5].map(i => (
        <Text key={i} style={[styles.star, { fontSize: size, color: i <= stars ? '#F59E0B' : '#D1D5DB' }]}>
          ★
        </Text>
      ))}
      {showNumber && value > 0 && (
        <Text style={[styles.number, { fontSize: size - 2 }]}>{value.toFixed(1)}</Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  row:    { flexDirection: 'row', alignItems: 'center', gap: 1 },
  star:   { lineHeight: 20 },
  number: { color: '#6B7280', marginLeft: 4, fontWeight: '600' },
});
