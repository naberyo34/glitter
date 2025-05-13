import { createGlitterApiClient } from 'api/client';
import { Post } from 'app/components/module/Post/Post';
import { UserDetail } from 'app/features/UserDetail/UserDetail';
import { useLoaderData } from 'react-router';
import type { Route } from './+types/_layout';

export async function loader({ params, context }: Route.LoaderArgs) {
  if (!params.id) {
    throw new Response(null, { status: 404 });
  }

  const glitterApiClient = createGlitterApiClient(
    context.cloudflare.env.API_URL,
  );
  const { data: posts } = await glitterApiClient.GET('/user/{id}/post', {
    params: { path: { id: params.id } },
  });

  if (posts === undefined) {
    throw new Response(null, { status: 404 });
  }

  return {
    posts,
  };
}

export default function User() {
  const { posts } = useLoaderData<typeof loader>();

  return (
    <ul>
      {posts?.map((post) => (
        <Post
          key={post.uuid}
          post={post}
          userDetail={<UserDetail post={post} />}
        />
      ))}
    </ul>
  );
}
