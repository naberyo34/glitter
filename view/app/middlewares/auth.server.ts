import { glitterApiClient } from 'api/client';
import { createCookie, type unstable_MiddlewareFunction } from 'react-router';
import { userContext } from './userContext';

export const authCookie = createCookie("auth", {
  maxAge: 604_800, // 1 week
});

/**
 * ユーザー情報を取得する middleware
 */
export const authMiddleware: unstable_MiddlewareFunction = async ({
  request,
  context,
}) => {
  // クッキーがない場合、ゲスト扱いとする
  const cookieHeader = request.headers.get('Cookie');
  const cookie = (await authCookie.parse(cookieHeader)) || {};
  if (!cookie) {
    return;
  }

  const { data } = await glitterApiClient.GET('/user/me', {
    headers: {
      Authorization: `Bearer ${cookie.token}`,
    },
  });

  if (data) {
    context.set(userContext, data);
  }
};
