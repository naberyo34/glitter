import { redirect } from 'react-router';
import { authCookie } from '~/middlewares/auth.server';

export async function loader() {
  return redirect('/', {
    headers: {
      'Set-Cookie': await authCookie.serialize(null, { expires: new Date(0) }),
    },
  });
}
