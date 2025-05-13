import { defineConfig } from 'vite';
import tsconfigPaths from 'vite-tsconfig-paths';
import svgr from 'vite-plugin-svgr';
import addViewBox from 'svgo-add-viewbox';

export default defineConfig({
  plugins: [
    tsconfigPaths(),
    svgr({
      // ?react をつけたとき svgr を利用
      include: '**/*.svg?react',
      svgrOptions: {
        plugins: ['@svgr/plugin-svgo', '@svgr/plugin-jsx'],
        svgoConfig: {
          plugins: [
            {
              name: 'preset-default',
              params: {
                overrides: {
                  removeViewBox: false,
                },
              },
            },
            // memo: Aseprite が出力する svg は viewBox 属性を入れてくれない
            // アセットの拡大、縮小を行うときに不便なため plugin で追加している
            addViewBox,
          ],
        },
      },
    }),
  ],
  build: {
    cssMinify: true,
    ssr: false,
  },
});
