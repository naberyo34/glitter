import { createGlitterApiClient } from 'api/client';
import type { Bindings } from 'env';
import { Hono } from 'hono';
import { setCookie } from 'hono/cookie';
import { z } from 'zod';

export const login = new Hono<{
  Bindings: Bindings;
}>();

const LoginSchema = z.object({
  userId: z.string().min(1),
  password: z.string().min(1),
});

login.post('/login', async (c) => {
  const raw = await c.req.parseBody();
  const parsed = LoginSchema.safeParse({
    userId: raw.userId,
    password: raw.password,
  });

  if (!parsed.success) {
    return c.json('入力形式が不正です', {
      status: 400,
    });
  }

  const glitterApiClient = createGlitterApiClient(c.env.API_URL);
  const { data } = await glitterApiClient.POST('/auth/login', {
    body: {
      id: parsed.data.userId,
      password: parsed.data.password,
    },
  });

  if (data === undefined) {
    return c.json('ログインに失敗しました', {
      status: 401,
    });
  }
  const { token } = data;
  setCookie(c, 'authToken', token, {
    httpOnly: true,
    sameSite: 'lax',
    secure: c.env.ENV === 'production',
  });

  return c.redirect('/me');
});
