import { createGlitterApiClient } from '~/api/client';
import { useLoaderData, type LoaderFunctionArgs } from 'react-router';

export async function loader({ params, context }: LoaderFunctionArgs) {
  if (!params.id) {
    throw new Response(null, { status: 404 });
  }

  const glitterApiClient = createGlitterApiClient(context.cloudflare.env.API_URL);

  const { data: user, error: userError } = await glitterApiClient.GET(
    '/user/{id}',
    {
      params: { path: { id: params.id } },
    },
  );
  const { data: posts } = await glitterApiClient.GET('/user/{id}/post', {
    params: { path: { id: params.id } },
  });

  if (userError) {
    throw new Response(null, {
      status: userError.status,
      statusText: userError.title,
    });
  }

  return {
    user: {
      ...user,
      posts,
    },
  };
}

export default function User() {
  const { user } = useLoaderData<typeof loader>();

  return (
    <section>
      <h1>{user.username}</h1>
      <ul>
        {user.posts?.map((post) => (
          <li key={post.id}>{post.content}</li>
        ))}
      </ul>
    </section>
  );
}
