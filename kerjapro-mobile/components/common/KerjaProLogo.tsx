import { View, Text, StyleSheet } from 'react-native';
import Svg, {
  Rect, Circle, Path, Defs, LinearGradient, Stop, G,
} from 'react-native-svg';

interface Props {
  size?: number;
  /** 'icon' = square badge only | 'full' = badge + wordmark side by side | 'stacked' = badge + wordmark below */
  variant?: 'icon' | 'full' | 'stacked';
  /** 'dark' (default) = dark bg | 'light' = white bg, orange icon */
  theme?: 'dark' | 'light';
}

export function KerjaProLogo({ size = 80, variant = 'icon', theme = 'dark' }: Props) {
  if (variant === 'stacked') {
    return (
      <View style={styles.stackedContainer}>
        <KerjaProIcon size={size} theme={theme} />
        <View style={styles.wordmarkRow}>
          <Text style={[styles.wordKerja, { fontSize: size * 0.3 }]}>Kerja</Text>
          <Text style={[styles.wordPro,   { fontSize: size * 0.3 }]}>Pro</Text>
        </View>
      </View>
    );
  }

  if (variant === 'full') {
    return (
      <View style={styles.fullContainer}>
        <KerjaProIcon size={size} theme={theme} />
        <View style={[styles.wordmarkRow, { marginLeft: size * 0.15 }]}>
          <Text style={[styles.wordKerja, { fontSize: size * 0.38 }]}>Kerja</Text>
          <Text style={[styles.wordPro,   { fontSize: size * 0.38 }]}>Pro</Text>
        </View>
      </View>
    );
  }

  return <KerjaProIcon size={size} theme={theme} />;
}

function KerjaProIcon({ size, theme }: { size: number; theme: 'dark' | 'light' }) {
  const isDark = theme === 'dark';

  return (
    <Svg width={size} height={size} viewBox="0 0 100 100">
      <Defs>
        {/* Background gradient */}
        <LinearGradient id="bgGrad" x1="0" y1="0" x2="1" y2="1">
          <Stop offset="0" stopColor={isDark ? '#1E3A5F' : '#FFFFFF'} />
          <Stop offset="1" stopColor={isDark ? '#0C1F35' : '#F9FAFB'} />
        </LinearGradient>

        {/* Orange beam gradient */}
        <LinearGradient id="orangeGrad" x1="0" y1="0" x2="0" y2="1">
          <Stop offset="0" stopColor="#FB923C" />
          <Stop offset="1" stopColor="#EA580C" />
        </LinearGradient>

        {/* Orange glow gradient for badge border */}
        <LinearGradient id="borderGrad" x1="0" y1="0" x2="1" y2="1">
          <Stop offset="0" stopColor="#FB923C" />
          <Stop offset="1" stopColor="#F97316" />
        </LinearGradient>
      </Defs>

      {/* ── Background rounded square ──────────────── */}
      <Rect x="0" y="0" width="100" height="100" rx="22" fill="url(#bgGrad)" />

      {/* ── Orange construction beam accent ─────────── */}
      {/* Top beam — full width */}
      <Rect x="0" y="0" width="100" height="14" rx="0"
            fill="url(#orangeGrad)" opacity="0.18" />
      {/* Top-left corner join */}
      <Rect x="0" y="0" width="14" height="100" rx="0"
            fill="url(#orangeGrad)" opacity="0.1" />

      {/* ── Orange border ring ───────────────────────── */}
      <Rect x="3" y="3" width="94" height="94" rx="19"
            fill="none" stroke="url(#borderGrad)" strokeWidth="3" />

      {/* ── K — construction I-beam vertical bar ──────── */}
      {/* Top flange */}
      <Rect x="15" y="18" width="22" height="8"  rx="3"
            fill={isDark ? 'white' : '#1E3A5F'} />
      {/* Web */}
      <Rect x="20" y="26" width="12" height="48" rx="1"
            fill={isDark ? 'white' : '#1E3A5F'} />
      {/* Bottom flange */}
      <Rect x="15" y="74" width="22" height="8"  rx="3"
            fill={isDark ? 'white' : '#1E3A5F'} />

      {/* ── K — upper arm (rotated beam from pivot 32,50) */}
      <Rect x="32" y="45" width="48" height="10" rx="3"
            fill={isDark ? 'white' : '#1E3A5F'}
            transform="rotate(-38 32 50)" />

      {/* ── K — lower arm (rotated beam from same pivot) */}
      <Rect x="32" y="45" width="48" height="10" rx="3"
            fill={isDark ? 'white' : '#1E3A5F'}
            transform="rotate(38 32 50)" />

      {/* ── Orange junction badge (where K arms meet) ── */}
      {/* Outer circle */}
      <Circle cx="32" cy="50" r="10" fill="url(#orangeGrad)" />
      {/* Inner white ring */}
      <Circle cx="32" cy="50" r="6"  fill={isDark ? '#1E3A5F' : 'white'} />
      {/* Centre dot */}
      <Circle cx="32" cy="50" r="3"  fill="url(#orangeGrad)" />

      {/* ── Hard hat silhouette — top-right corner ──── */}
      {/* Hat dome */}
      <Path
        d="M68 34 Q68 24 78 24 Q88 24 88 34 L88 38 L68 38 Z"
        fill="url(#orangeGrad)"
      />
      {/* Hat brim */}
      <Rect x="64" y="37" width="28" height="5" rx="2.5"
            fill="url(#orangeGrad)" />
      {/* Hat band */}
      <Rect x="68" y="32" width="20" height="3" rx="1"
            fill={isDark ? 'rgba(255,255,255,0.3)' : 'rgba(30,58,95,0.3)'} />

      {/* ── "PRO" label — bottom right ─────────────── */}
      <Rect x="58" y="74" width="30" height="14" rx="4"
            fill="url(#orangeGrad)" />
      {/* P */}
      <Path d="M63 78 L63 86 M63 78 L67 78 Q70 78 70 81 Q70 84 67 84 L63 84"
            stroke="white" strokeWidth="2" strokeLinecap="round" fill="none" />
      {/* R */}
      <Path d="M72 78 L72 86 M72 78 L76 78 Q79 78 79 81 Q79 84 76 84 L72 84 L79 86"
            stroke="white" strokeWidth="2" strokeLinecap="round" fill="none" />
      {/* O */}
      <Path d="M82 81 Q82 78 85 78 Q88 78 88 81 Q88 84 85 84 Q82 84 82 81 Z"
            stroke="white" strokeWidth="2" fill="none" />
    </Svg>
  );
}

const styles = StyleSheet.create({
  stackedContainer: { alignItems: 'center', gap: 8 },
  fullContainer:    { flexDirection: 'row', alignItems: 'center' },
  wordmarkRow:      { flexDirection: 'row', alignItems: 'baseline' },
  wordKerja:        { fontWeight: '800', color: '#1C1C1E', letterSpacing: -0.5 },
  wordPro:          { fontWeight: '900', color: '#F97316', letterSpacing: -0.5 },
});
