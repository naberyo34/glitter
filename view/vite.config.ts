import adapter from '@hono/vite-dev-server/cloudflare';
import { reactRouter } from '@react-router/dev/vite';
import serverAdapter from 'hono-react-router-adapter/vite';
import addViewBox from 'svgo-add-viewbox';
import { defineConfig } from 'vite';
import svgr from 'vite-plugin-svgr';
import tsconfigPaths from 'vite-tsconfig-paths';

export default defineConfig({
  plugins: [
    reactRouter(),
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
    serverAdapter({
      adapter,
      entry: 'server/index.ts',
    }),
  ],
  ssr: {
    resolve: {
      conditions: ['workerd', 'worker', 'browser'],
      externalConditions: ['workerd', 'worker'],
    },
  },
});
