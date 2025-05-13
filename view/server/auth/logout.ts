import { Hono } from 'hono';
import { setCookie } from 'hono/cookie';

export const logout = new Hono();

logout.get('/logout', (c) => {
  setCookie(c, 'authToken', '', {
    maxAge: 0,
  });

  return c.redirect('/');
});
