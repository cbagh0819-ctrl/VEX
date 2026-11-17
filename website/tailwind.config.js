/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        cyber: {
          dark: '#080A0F',
          darker: '#040608',
          card: '#0E131F',
          border: '#1F293D',
          accent: '#2563EB',
          cyan: '#06B6D4',
          emerald: '#10B981',
          gold: '#F59E0B',
          bronze: '#CD7F32',
          danger: '#EF4444',
          purple: '#8B5CF6'
        }
      },
      fontFamily: {
        mono: ['"JetBrains Mono"', 'Menlo', 'monospace'],
        sans: ['"Inter"', 'system-ui', '-apple-system', 'sans-serif'],
        display: ['"Space Grotesk"', 'sans-serif']
      },
      boxShadow: {
        'solid-blue': '4px 4px 0px 0px #2563EB',
        'solid-gold': '4px 4px 0px 0px #F59E0B',
        'solid-emerald': '4px 4px 0px 0px #10B981',
        'solid-card': '6px 6px 0px 0px #1E293B',
        'solid-white': '4px 4px 0px 0px #FFFFFF',
        'glow-blue': '0 0 30px rgba(37, 99, 235, 0.35)',
        'glow-gold': '0 0 30px rgba(245, 158, 11, 0.35)',
      },
      animation: {
        'float-slow': 'float 6s ease-in-out infinite',
        'pulse-glow': 'pulseGlow 3s ease-in-out infinite',
        'marquee': 'marquee 25s linear infinite',
      },
      keyframes: {
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-12px)' },
        },
        pulseGlow: {
          '0%, 100%': { opacity: 0.4 },
          '50%': { opacity: 0.8 },
        },
        marquee: {
          '0%': { transform: 'translateX(0%)' },
          '100%': { transform: 'translateX(-50%)' }
        }
      }
    },
  },
  plugins: [],
}
