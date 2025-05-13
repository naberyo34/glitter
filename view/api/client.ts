import createClient from 'openapi-fetch';
import type { paths } from './schema';

export function createGlitterApiClient(apiUrl: string) {
  return createClient<paths>({
    baseUrl: apiUrl,
  });
}
