import { glitterApiClient } from 'api/client';
import {
  type ActionFunctionArgs,
  Form,
  type LoaderFunctionArgs,
  type unstable_RouterContextProvider,
  useLoaderData,
} from 'react-router';
import { Button } from '~/components/ui/button';
import { Input } from '~/components/ui/input';
import { authCookie } from '~/middlewares/auth.server';
import { userContext } from '~/middlewares/userContext';

export async function loader({
  context,
}: LoaderFunctionArgs<unstable_RouterContextProvider>) {
  const user = context.get(userContext);

  if (user) {
    const { data } = await glitterApiClient.GET('/user/{id}/post', {
      params: {
        path: {
          id: user.id,
        },
      },
    });

    return { user, posts: data };
  }

  return { user };
}

export async function action({
  request,
}: ActionFunctionArgs<unstable_RouterContextProvider>) {
  const cookieHeader = request.headers.get('Cookie');
  const cookie = (await authCookie.parse(cookieHeader)) || {};

  if (!cookie) {
    return;
  }

  const formData = await request.formData();
  const content = formData.get('content') as string;

  await glitterApiClient.POST('/post', {
    headers: {
      Authorization: `Bearer ${cookie.token}`,
    },
    body: {
      content: content,
    },
  });
}

export default function Index() {
  const { user, posts } = useLoaderData<typeof loader>();
  return (
    <section>
      <ul className="flex flex-col gap-4">
        {posts?.map((post) => (
          <li key={post.id} className="flex gap-2">
            <img src={post.user.icon} alt="" width="40" height="40" />
            <div>
              <div className="flex gap-2">
                <p>{post.user.username}</p>
                <p>@{post.user.id}</p>
                <p>{post.createdAt}</p>
              </div>
              <p>{post.content}</p>
            </div>
          </li>
        ))}
      </ul>
      {user && (
        <div>
          <Form method="post" className="flex gap-2">
            <Input type="text" name="content" placeholder="投稿内容" />
            <Button type="submit">投稿</Button>
          </Form>
        </div>
      )}
    </section>
  );
}
