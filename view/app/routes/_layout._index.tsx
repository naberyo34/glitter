import { format } from '@formkit/tempo';
import {
  type ActionFunctionArgs,
  Form,
  type LoaderFunctionArgs,
  type unstable_RouterContextProvider,
  useLoaderData,
  useOutletContext,
} from 'react-router';
import { joinURL } from 'ufo';
import { glitterApiClient } from '~/api/client.server';
import { Avatar, AvatarImage } from '~/components/ui/avatar';
import { Button } from '~/components/ui/button';
import { Input } from '~/components/ui/input';
import { authCookie } from '~/middlewares/auth.server';
import { userContext } from '~/middlewares/userContext.server';
import type { RootLayoutContext } from './_layout';

export async function loader({
  context,
}: LoaderFunctionArgs<unstable_RouterContextProvider>) {
  const user = context.get(userContext);

  if (!user) {
    return { posts: null };
  }

  const { data } = await glitterApiClient.GET('/user/{id}/post', {
    params: {
      path: {
        id: user.id,
      },
    },
  });

  return { posts: data };
}

export async function action({ request }: ActionFunctionArgs) {
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
  const { user, appUrl } = useOutletContext<RootLayoutContext>();
  const { posts } = useLoaderData<typeof loader>();
  return (
    <section>
      <ul className="flex flex-col gap-4">
        {posts?.map((post) => (
          <li key={post.id} className="flex gap-4">
            {post.user.icon && (
              <Avatar>
                {post.user.icon && (
                  <AvatarImage
                    src={joinURL(appUrl.storage, post.user.icon)}
                  />
                )}
              </Avatar>
            )}
            <div>
              <div className="flex gap-2">
                <p className="font-bold">{post.user.username}</p>
                <p className="text-muted-foreground">@{post.user.id}</p>
                <p className="text-muted-foreground">
                  {format(post.createdAt)}
                </p>
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
