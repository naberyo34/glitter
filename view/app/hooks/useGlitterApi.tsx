import { createGlitterApiClient } from 'api/client';
import type { paths } from 'api/schema';
import type { Client } from 'openapi-fetch';
import createClient, { type OpenapiQueryClient } from 'openapi-react-query';
import { createContext, useContext } from 'react';

const GlitterApiContext = createContext<OpenapiQueryClient<paths> | null>(null);

type GlitterApiProviderProps = {
  apiUrl: string;
  children: React.ReactNode;
};

export function GlitterApiProvider(props: GlitterApiProviderProps) {
  const client = createGlitterApiClient(props.apiUrl);
  const $client = createClient(client);

  return (
    <GlitterApiContext.Provider value={$client}>
      {props.children}
    </GlitterApiContext.Provider>
  );
}

export function useGlitterApi() {
  const context = useContext(GlitterApiContext);

  if (!context) {
    throw new Error('glitterApiClient の初期化が必要です。');
  }

  return context;
}
