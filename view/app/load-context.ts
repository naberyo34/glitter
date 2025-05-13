import type { PlatformProxy } from 'wrangler';

// Cloudflare Workers の context から取得される型定義を Remix に追加している
// @see https://remix.run/docs/en/main/guides/vite#augmenting-load-context

type Cloudflare = Omit<PlatformProxy<Env>, 'dispose'>;

declare module 'react-router' {
  interface AppLoadContext {
    cloudflare: Cloudflare;
  }
}
