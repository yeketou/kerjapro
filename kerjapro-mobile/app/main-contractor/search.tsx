import { View, Text, TextInput, ScrollView, StyleSheet, TouchableOpacity, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { contractorService } from '../../services/contractorService';
import { SubcontractorCard } from '../../components/contractor/SubcontractorCard';

const TRADES = ['PLUMBING','ELECTRICAL','TILING','CARPENTRY','BATHROOM','KITCHEN','PAINTING','HVAC','FLOORING','ROOFING'];
const STATES = ['Kuala Lumpur','Selangor','Johor','Penang','Perak','Sabah','Sarawak'];

export default function SearchScreen() {
  const [trade,     setTrade]     = useState('');
  const [brand,     setBrand]     = useState('');
  const [state,     setState]     = useState('');
  const [minRating, setMinRating] = useState('');
  const [showFilters, setShowFilters] = useState(false);

  const { data, isLoading, refetch } = useQuery({
    queryKey: ['search', trade, brand, state, minRating],
    queryFn: () => contractorService.search({
      tradeCategory: trade || undefined,
      brandName:     brand || undefined,
      state:         state || undefined,
      minRating:     minRating ? parseFloat(minRating) : undefined,
      size: 20,
    }),
  });

  return (
    <SafeAreaView style={styles.container}>
      {/* Search bar */}
      <View style={styles.searchBox}>
        <Text style={styles.title}>Find Subcontractors</Text>
        <TouchableOpacity
          style={[styles.filterBtn, showFilters && styles.filterBtnActive]}
          onPress={() => setShowFilters(v => !v)}
        >
          <Text style={[styles.filterBtnText, showFilters && { color: '#fff' }]}>
            ⚙️ Filters
          </Text>
        </TouchableOpacity>
      </View>

      {/* Filters */}
      {showFilters && (
        <View style={styles.filters}>
          <Text style={styles.filterLabel}>Trade Category</Text>
          <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.chipScroll}>
            {TRADES.map(t => (
              <TouchableOpacity
                key={t}
                style={[styles.chip, trade === t && styles.chipActive]}
                onPress={() => setTrade(trade === t ? '' : t)}
              >
                <Text style={[styles.chipText, trade === t && styles.chipTextActive]}>
                  {t.replace(/_/g, ' ')}
                </Text>
              </TouchableOpacity>
            ))}
          </ScrollView>

          <Text style={styles.filterLabel}>Brand</Text>
          <TextInput style={styles.input} placeholder="e.g. Grohe, American Standard"
            value={brand} onChangeText={setBrand} />

          <Text style={styles.filterLabel}>State</Text>
          <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.chipScroll}>
            {STATES.map(s => (
              <TouchableOpacity
                key={s}
                style={[styles.chip, state === s && styles.chipActive]}
                onPress={() => setState(state === s ? '' : s)}
              >
                <Text style={[styles.chipText, state === s && styles.chipTextActive]}>{s}</Text>
              </TouchableOpacity>
            ))}
          </ScrollView>

          <Text style={styles.filterLabel}>Minimum Rating</Text>
          <ScrollView horizontal showsHorizontalScrollIndicator={false} style={[styles.chipScroll, { marginBottom: 0 }]}>
            {['3.0','3.5','4.0','4.5'].map(r => (
              <TouchableOpacity
                key={r}
                style={[styles.chip, minRating === r && styles.chipActive]}
                onPress={() => setMinRating(minRating === r ? '' : r)}
              >
                <Text style={[styles.chipText, minRating === r && styles.chipTextActive]}>★ {r}+</Text>
              </TouchableOpacity>
            ))}
          </ScrollView>
        </View>
      )}

      {/* Results */}
      {isLoading
        ? <ActivityIndicator color="#F97316" style={{ marginTop: 40 }} />
        : (
          <ScrollView style={styles.results} showsVerticalScrollIndicator={false}>
            <Text style={styles.resultCount}>
              {data?.totalElements ?? 0} subcontractors found
            </Text>
            {data?.content?.map((profile: any) => (
              <SubcontractorCard key={profile.id} profile={profile} />
            ))}
            {data?.content?.length === 0 && (
              <View style={styles.empty}>
                <Text style={styles.emptyIcon}>🔍</Text>
                <Text style={styles.emptyText}>No subcontractors found</Text>
                <Text style={styles.emptyHint}>Try adjusting your filters</Text>
              </View>
            )}
          </ScrollView>
        )
      }
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container:       { flex: 1, backgroundColor: '#F9FAFB' },
  searchBox:       { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', padding: 20, paddingBottom: 12 },
  title:           { fontSize: 22, fontWeight: '800', color: '#1C1C1E' },
  filterBtn:       { backgroundColor: '#F3F4F6', paddingHorizontal: 14, paddingVertical: 8, borderRadius: 20 },
  filterBtnActive: { backgroundColor: '#F97316' },
  filterBtnText:   { fontSize: 13, fontWeight: '600', color: '#374151' },
  filters:         { backgroundColor: '#fff', marginHorizontal: 16, borderRadius: 16, padding: 16, marginBottom: 12, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.06, shadowRadius: 8, elevation: 2 },
  filterLabel:     { fontSize: 12, fontWeight: '600', color: '#9CA3AF', textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 8, marginTop: 8 },
  chipScroll:      { marginBottom: 4 },
  chip:            { backgroundColor: '#F3F4F6', paddingHorizontal: 12, paddingVertical: 7, borderRadius: 20, marginRight: 8 },
  chipActive:      { backgroundColor: '#F97316' },
  chipText:        { fontSize: 13, fontWeight: '600', color: '#374151' },
  chipTextActive:  { color: '#fff' },
  input:           { backgroundColor: '#F9FAFB', borderWidth: 1, borderColor: '#E5E7EB', borderRadius: 10, padding: 10, fontSize: 14, color: '#1C1C1E' },
  results:         { flex: 1, paddingHorizontal: 16 },
  resultCount:     { fontSize: 13, color: '#9CA3AF', marginBottom: 12 },
  empty:           { alignItems: 'center', paddingTop: 60 },
  emptyIcon:       { fontSize: 48, marginBottom: 12 },
  emptyText:       { fontSize: 16, fontWeight: '700', color: '#374151' },
  emptyHint:       { fontSize: 13, color: '#9CA3AF', marginTop: 4 },
});
