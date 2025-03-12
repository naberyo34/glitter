import type { ActionFunctionArgs } from 'react-router';

export async function loader() {}

export async function action({ request }: ActionFunctionArgs) {}

export default function Index() {
  return <div className="flex flex-col gap-4" />;
}
