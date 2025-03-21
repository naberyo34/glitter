import { glitterApiClient } from 'api/client';
import {
  type ActionFunctionArgs,
  Form,
  type LoaderFunctionArgs,
  redirect,
  useActionData,
} from 'react-router';
import { Button } from '~/components/ui/button';
import { Input } from '~/components/ui/input';
import { authCookie } from '~/middlewares/auth.server';
import { userContext } from '~/middlewares/userContext';

export async function loader({ context }: LoaderFunctionArgs) {
  const user = context.get(userContext);

  if (user) {
    return redirect('/');
  }
}

export async function action({ request }: ActionFunctionArgs) {
  try {
    const formData = await request.formData();
    const id = formData.get('id') as string;
    const password = formData.get('password') as string;

    const { data, error } = await glitterApiClient.POST('/auth/token', {
      body: {
        id: id,
        password: password,
      },
    });

    if (data) {
      return redirect('/', {
        headers: {
          'Set-Cookie': await authCookie.serialize(data),
        },
      });
    }

    return error;
  } catch (e) {
    throw new Response(null, { status: 500 });
  }
}

export default function Login() {
  return (
    <div>
      <h1>Login</h1>
      <Form method="post">
        <Input type="text" name="id" />
        <Input type="password" name="password" />
        <Button type="submit">Login</Button>
      </Form>
    </div>
  );
}
