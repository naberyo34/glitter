import {
  useLoaderData,
  type LoaderFunctionArgs,
  type unstable_RouterContextProvider,
} from 'react-router';
import { userContext } from '~/middlewares/userContext';

export async function loader({
  context,
}: LoaderFunctionArgs<unstable_RouterContextProvider>) {
  const user = context.get(userContext);

  return { user };
}

export default function Index() {
  const { user } = useLoaderData<typeof loader>();
  return (
    <div className="flex flex-col gap-4">{user ? user.username : 'Guest'}</div>
  );
}
