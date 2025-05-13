import {
  Links,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
  isRouteErrorResponse,
  useLoaderData,
} from 'react-router';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import cookie from 'cookie';
import type { Route } from './+types/root';
import { GlitterApiProvider } from './hooks/useGlitterApi';
import styles from './index.css?url';
import { createGlitterApiClient } from 'api/client';

export const links: Route.LinksFunction = () => [
  { rel: 'stylesheet', href: styles },
];

export const meta: Route.MetaFunction = () => {
  return [
    {
      title: 'Glitter',
    },
    {
      property: 'og:title',
      content: 'Glitter',
    },
    {
      name: 'description',
      content: 'tama の おひとりさま ActivityPub クライアント',
    },
  ];
};

const queryClient = new QueryClient();

export async function loader({ request, context }: Route.LoaderArgs) {
  const cookieHeader = request.headers.get('Cookie');
  if (!cookieHeader) {
    return {
      context,
    };
  }

  const cookies = cookie.parse(cookieHeader);
  const authToken = cookies.authToken;
  if (!authToken) {
    return {
      context,
    };
  }

  const glitterApiClient = createGlitterApiClient(
    context.cloudflare.env.API_URL,
  );
  const { data: me } = await glitterApiClient.GET('/user/me', {
    headers: {
      Authorization: `Bearer ${authToken}`,
    },
  });

  return {
    me,
    context,
  };
}

export function Layout({ children }: { children: React.ReactNode }) {
  const { context } = useLoaderData<typeof loader>();
  return (
    <html lang="ja">
      <head>
        <meta charSet="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <Meta />
        <Links />
      </head>
      <body>
        <QueryClientProvider client={queryClient}>
          <GlitterApiProvider apiUrl={context.cloudflare.env.API_URL}>
            {children}
          </GlitterApiProvider>
        </QueryClientProvider>
        <ScrollRestoration />
        <Scripts />
      </body>
    </html>
  );
}

export default function App() {
  return <Outlet />;
}

export function ErrorBoundary({ error }: Route.ErrorBoundaryProps) {
  let message = 'Oops!';
  let details = 'An unexpected error occurred.';
  let stack: string | undefined;

  if (isRouteErrorResponse(error)) {
    message = error.status === 404 ? '404' : 'Error';
    details =
      error.status === 404
        ? 'The requested page could not be found.'
        : error.statusText || details;
  } else if (import.meta.env.DEV && error && error instanceof Error) {
    details = error.message;
    stack = error.stack;
  }

  return (
    <main className="pt-16 p-4 container mx-auto">
      <h1>{message}</h1>
      <p>{details}</p>
      {stack && (
        <pre className="w-full p-4 overflow-x-auto">
          <code>{stack}</code>
        </pre>
      )}
    </main>
  );
}
