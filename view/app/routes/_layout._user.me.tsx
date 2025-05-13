import { createGlitterApiClient } from 'api/client';
import type { Route } from '../+types/root';
import { createCookie, useLoaderData } from 'react-router';
import { Post } from 'app/components/module/Post/Post';
import { UserDetail } from 'app/features/UserDetail/UserDetail';
import cookie from 'cookie';

export async function loader({ request, context }: Route.LoaderArgs) {
  const cookieHeader = request.headers.get('Cookie');
  if (!cookieHeader) {
    return new Response(null, { status: 401 });
  }

  const cookies = cookie.parse(cookieHeader);
  const authToken = cookies.authToken;
  if (!authToken) {
    return new Response(null, { status: 401 });
  }

  const glitterApiClient = createGlitterApiClient(
    context.cloudflare.env.API_URL,
  );
  const { data: posts } = await glitterApiClient.GET('/user/me/post', {
    headers: {
      Authorization: `Bearer ${authToken}`,
    },
  });
  if (posts === undefined) {
    return new Response(null, { status: 500 });
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
