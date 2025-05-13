import { defineConfig } from '@pandacss/dev';

const sizes = {
  0: { value: '0' },
  x0_5: { value: '4px' },
  x1: { value: '8px' },
  x1_5: { value: '12px' },
  x2: { value: '16px' },
  x3: { value: '24px' },
  x4: { value: '32px' },
  x5: { value: '40px' },
  x6: { value: '48px' },
  x7: { value: '56px' },
  x8: { value: '64px' },
  x9: { value: '72px' },
  x10: { value: '80px' },
};

/**
 * ライトモード / ダークモードのカラー一括定義ユーティリティ
 * 1つの色しか渡さなかった場合はライトモード / ダークモードで共通となる
 */
function color(light: string, dark?: string) {
  return {
    _osLight: light,
    _osDark: dark ?? light,
  };
}

/**
 * @see https://panda-css.com/docs/references/config
 */
export default defineConfig({
  presets: ['@pandacss/preset-base'],
  preflight: true,
  minify: true,
  include: ['./app/**/{**,.client,.server,.stories}/**/*.{js,jsx,ts,tsx}'],
  exclude: [],
  shorthands: false,
  globalCss: {
    'html,body': {
      color: 'text',
      fontFamily: 'default',
      fontSize: 'm',
      fontWeight: 'regular',
      lineHeight: 'm',
      backgroundColor: 'background',
    },
  },
  theme: {
    tokens: {
      // see: https://uicolors.app/browse/tailwind-colors
      colors: {
        brand: {
          '50': { value: '#ffffe7' },
          '100': { value: '#fdffc1' },
          '200': { value: '#fffe86' },
          '300': { value: '#fff641' },
          '400': { value: '#ffe80d' },
          '500': { value: '#ffd900' },
          '600': { value: '#d1a000' },
          '700': { value: '#a67202' },
          '800': { value: '#89590a' },
          '900': { value: '#74490f' },
          '950': { value: '#442604' },
        },
        rose: {
          '50': { value: '#fff1f2' },
          '100': { value: '#ffe4e6' },
          '200': { value: '#fecdd3' },
          '300': { value: '#fda4af' },
          '400': { value: '#fb7185' },
          '500': { value: '#f43f5e' },
          '600': { value: '#e11d48' },
          '700': { value: '#be123c' },
          '800': { value: '#9f1239' },
          '900': { value: '#881337' },
          '950': { value: '#4c0519' },
        },
        teal: {
          '50': { value: '#f0fdfa' },
          '100': { value: '#ccfbf1' },
          '200': { value: '#99f6e4' },
          '300': { value: '#5eead4' },
          '400': { value: '#2dd4bf' },
          '500': { value: '#14b8a6' },
          '600': { value: '#0d9488' },
          '700': { value: '#0f766e' },
          '800': { value: '#115e59' },
          '900': { value: '#134e4a' },
          '950': { value: '#042f2e' },
        },
        slate: {
          '50': { value: '#f8fafc' },
          '100': { value: '#f1f5f9' },
          '200': { value: '#e2e8f0' },
          '300': { value: '#cbd5e1' },
          '400': { value: '#94a3b8' },
          '500': { value: '#64748b' },
          '600': { value: '#475569' },
          '700': { value: '#334155' },
          '800': { value: '#1e293b' },
          '900': { value: '#0f172a' },
          '950': { value: '#020617' },
        },
      },
      sizes: {
        ...sizes,
        '100%': { value: '100%' },
        em: { value: '1em' },
      },
      spacing: {
        ...sizes,
      },
      fonts: {
        default: {
          value:
            'Hiragino Maru Gothic Pro,BIZ UDGothic,Roboto,HelveticaNeue,Arial,sans-serif',
        },
      },
      fontSizes: {
        '2xs': { value: '10px' },
        xs: { value: '12px' },
        s: { value: '14px' },
        m: { value: '16px' },
        l: { value: '18px' },
        xl: { value: '24px' },
        '2xl': { value: '32px' },
        '3xl': { value: '64px' },
      },
      fontWeights: {
        regular: { value: '400' },
        bold: { value: '700' },
      },
      lineHeights: {
        '1': { value: '1' },
        m: { value: '1.4' },
      },
      radii: {
        0: { value: '0' },
        x1: { value: '8px' },
        x2: { value: '16px' },
        full: { value: '9999px' },
      },
      borders: {
        s: { value: '1px solid' },
        m: { value: '2px solid' },
      },
      opacity: {
        '0': { value: '0' },
        '0_5': { value: '0.5' },
        '0_75': { value: '0.75' },
        '1': { value: '1' },
      },
      // see: https://www.chakra-ui.com/docs/theming/z-index から使いそうなものだけ
      zIndex: {
        hide: { value: '-1' },
        base: { value: '0' },
        docked: { value: '10' },
        dropdown: { value: '1000' },
        sticky: { value: '1100' },
        overlay: { value: '1300' },
        modal: { value: '1400' },
        popover: { value: '1500' },
        toast: { value: '1700' },
        tooltip: { value: '1800' },
      },
    },
    semanticTokens: {
      colors: {
        background: {
          value: color('{colors.slate.200}', '{colors.slate.900}'),
        },
        surface: {
          value: color('{colors.slate.50}', '{colors.slate.800}'),
        },
        surfaceSuccess: {
          value: color('{colors.teal.50}', '{colors.teal.950}'),
        },
        surfaceFail: {
          value: color('{colors.rose.50}', '{colors.rose.950}'),
        },
        text: {
          value: color('{colors.slate.900}', '{colors.slate.50}'),
        },
        textSuccess: {
          value: color('{colors.teal.700}', '{colors.teal.500}'),
        },
        textFail: {
          value: color('{colors.rose.600}', '{colors.rose.500}'),
        },
        textLowEm: {
          value: color('{colors.slate.400}', '{colors.slate.500}'),
        },
        textDisabled: {
          value: color('{colors.slate.300}', '{colors.slate.600}'),
        },
        textInverted: {
          value: color('{colors.slate.50}', '{colors.slate.900}'),
        },
        button: {
          value: color('{colors.slate.500}'),
        },
        buttonHover: {
          value: color('{colors.slate.400}'),
        },
        buttonDisabled: {
          value: color('{colors.slate.100}', '{colors.slate.700}'),
        },
        buttonTransparent: {
          value: color('inherit'),
        },
        buttonTransparentHover: {
          value: color('{colors.slate.200}', '{colors.slate.800}'),
        },
        border: {
          value: color('{colors.slate.300}', '{colors.slate.700}'),
        },
        borderSuccess: {
          value: color('{colors.teal.700}', '{colors.teal.500}'),
        },
        borderFail: {
          value: color('{colors.rose.600}', '{colors.rose.500}'),
        },
        accent: {
          value: color('{colors.brand.500}', '{colors.brand.600}'),
        },
        accentHover: {
          value: color('{colors.brand.400}', '{colors.brand.500}'),
        },
        accentSecondary: {
          value: color('{colors.brand.600}', '{colors.brand.700}'),
        },
        backdrop: {
          // alpha を含めたいので直接指定 (alpha 0.5)
          value: color('#00000080'),
        },
      },
      gradients: {
        accent: {
          value: color(
            'linear-gradient(90deg, {colors.brand.400} 0%, {colors.brand.500} 100%)',
            'linear-gradient(90deg, {colors.brand.500} 0%, {colors.brand.600} 100%)',
          ),
        },
        accentHover: {
          value: color(
            'linear-gradient(90deg, {colors.brand.300} 0%, {colors.brand.400} 100%)',
            'linear-gradient(90deg, {colors.brand.400} 0%, {colors.brand.500} 100%)',
          ),
        },
      },
      durations: {
        fast: { value: '0.2s' },
      },
      animations: {
        fadeIn: {
          value: 'fadeIn ease',
        },
        fadeOut: {
          value: 'fadeOut ease',
        },
        popIn: {
          value: 'popIn ease',
        },
        popOut: {
          value: 'popOut ease',
        },
      },
    },
    extend: {
      keyframes: {
        fadeIn: {
          from: { opacity: '0' },
          to: { opacity: '1' },
        },
        fadeOut: {
          from: { opacity: '1' },
          to: { opacity: '0' },
        },
        popIn: {
          from: { opacity: '0', transform: 'scale(0.75) translateY(24px)' },
          to: { opacity: '1', transform: 'scale(1) translateY(0)' },
        },
        popOut: {
          from: { opacity: '1', transform: 'scale(1) translateY(0)' },
          to: { opacity: '0', transform: 'scale(0.75) translateY(24px)' },
        },
      },
    },
  },
  utilities: {
    extend: {
      textLayerColor: {
        values: [
          'text',
          'textLowEm',
          'textInverted',
          'textDisabled',
          'textSuccess',
          'textFail',
        ],
        transform(
          value:
            | 'text'
            | 'textLowEm'
            | 'textInverted'
            | 'textDisabled'
            | 'textSuccess'
            | 'textFail',
          { token },
        ) {
          switch (value) {
            case 'text':
              return {
                color: token('colors.text'),
                '& svg path': {
                  fill: token('colors.text'),
                },
              };
            case 'textLowEm':
              return {
                color: token('colors.textLowEm'),
                '& svg path': {
                  fill: token('colors.textLowEm'),
                },
              };
            case 'textInverted':
              return {
                color: token('colors.textInverted'),
                '& svg path': {
                  fill: token('colors.textInverted'),
                },
              };
            case 'textDisabled':
              return {
                color: token('colors.textDisabled'),
                '& svg path': {
                  fill: token('colors.textDisabled'),
                },
              };
            case 'textSuccess':
              return {
                color: token('colors.textSuccess'),
                '& svg path': {
                  fill: token('colors.textSuccess'),
                },
              };
            case 'textFail':
              return {
                color: token('colors.textFail'),
                '& svg path': {
                  fill: token('colors.textFail'),
                },
              };
          }
        },
      },
    },
  },
  outdir: 'styled-system',
  strictTokens: true,
  strictPropertyValues: true,
  jsxFramework: 'react',
  jsxStyleProps: 'none',
  logLevel: 'error',
  validation: 'error',
});
