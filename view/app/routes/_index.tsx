import { glitterApiClient } from 'api/client';
import { useLoaderData } from 'react-router';
import { Button } from '~/components/ui/button';
import { Input } from '~/components/ui/input';

export async function loader() {
  const { data } = await glitterApiClient.GET('/task/all');
  return { tasks: data ?? [] };
}

export default function Index() {
  const { tasks } = useLoaderData<typeof loader>();
  return (
    <div>
      <h1>TODO</h1>
      <div className="flex gap-2">
        <Input type="text" />
        <Button type="button">追加</Button>
      </div>
      <ul>
        {tasks.map((task) => (
          <li key={task.id}>
            {task.value} {task.isDone ? '✔️' : '❌'}
          </li>
        ))}
      </ul>
    </div>
  );
}
