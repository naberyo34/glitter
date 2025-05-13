import { Hono } from 'hono';
import { login } from './auth/login';
import { logout } from './auth/logout';
import { parse } from 'cookie';
import { z } from 'zod';
import { createGlitterApiClient } from 'api/client';
import type { Bindings } from 'env';

const app = new Hono<{
  Bindings: Bindings;
}>();

/**
 * DevTools からの通知を無視する
 */
app.get('/.well-known/appspecific/*', (c) => {
  return c.body(null, 200);
});

const PostSchema = z.object({
  content: z.string().min(1),
});

/**
 * 投稿
 */
app.post('/post', async (c) => {
  const cookieHeader = c.req.header('Cookie');
  if (!cookieHeader) {
    return c.json('認証情報が見つかりません', {
      status: 401,
    });
  }

  const cookies = parse(cookieHeader);
  const token = cookies.authToken;
  if (!token) {
    return c.json('認証情報が見つかりません', {
      status: 401,
    });
  }

  console.log('token', token);

  const raw = await c.req.parseBody();
  const parsed = PostSchema.safeParse({
    content: raw.content,
  });
  if (!parsed.success) {
    return c.json('入力形式が不正です', {
      status: 400,
    });
  }

  const glitterApiClient = createGlitterApiClient(c.env.API_URL);
  await glitterApiClient.POST('/post', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: { content: parsed.data.content },
  });

  return c.redirect('/me');
});

app.route('/auth', login);
app.route('/auth', logout);

export default app;
