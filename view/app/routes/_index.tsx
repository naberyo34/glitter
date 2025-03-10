import { glitterApiClient } from 'api/client';
import {
  useFetcher,
  useLoaderData,
  type ActionFunctionArgs,
} from 'react-router';
import { Button } from '~/components/ui/button';
import { Input } from '~/components/ui/input';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '~/components/ui/tabs';

export async function loader() {
  const { data: inProgressTasks } =
    await glitterApiClient.GET('/task/in_progress');
  const { data: doneTasks } = await glitterApiClient.GET('/task/done');
  return {
    tasks: {
      inProgress: inProgressTasks ?? [],
      done: doneTasks ?? [],
    },
  };
}

export async function action({ request }: ActionFunctionArgs) {
  const formData = await request.formData();
  const { _action } = Object.fromEntries(formData);

  switch (_action) {
    case 'add': {
      const value = formData.get('value');
      const { error } = await glitterApiClient.POST('/task/add', {
        params: {
          query: {
            value: String(value),
          },
        },
      });

      return error;
    }
    case 'toggle': {
      const id = Number(formData.get('id'));
      const { error } = await glitterApiClient.POST('/task/{id}/toggle', {
        params: {
          path: {
            id: Number(id),
          },
        },
      });

      return error;
    }
  }
}

export default function Index() {
  const { tasks } = useLoaderData<typeof loader>();
  const fetcher = useFetcher<typeof loader>();

  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-xl font-bold">🍙 わくわくTODOリスト</h1>
      <fetcher.Form method="post" className="flex gap-2">
        <Input type="text" name="value" maxLength={20} />
        <Button type="submit" name="_action" value="add">
          追加
        </Button>
      </fetcher.Form>
      <Tabs defaultValue="inProgress">
        <TabsList className="w-full">
          <TabsTrigger value="inProgress">
            進行中 ({tasks.inProgress.length})
          </TabsTrigger>
          <TabsTrigger value="done">完了 ({tasks.done.length})</TabsTrigger>
        </TabsList>
        <TabsContent value="inProgress">
          <ul className="flex flex-col gap-2">
            {tasks.inProgress.map((task) => (
              <li key={task.id}>
                <fetcher.Form
                  method="post"
                  className="flex justify-between items-center"
                >
                  <input type="hidden" name="id" value={task.id} />
                  {task.value}
                  <Button type="submit" name="_action" value="toggle">
                    完了
                  </Button>
                </fetcher.Form>
              </li>
            ))}
          </ul>
        </TabsContent>
        <TabsContent value="done">
          <ul className="flex flex-col gap-2">
            {tasks.done.map((task) => (
              <li key={task.id}>
                <fetcher.Form
                  method="post"
                  className="flex justify-between items-center"
                >
                  <input type="hidden" name="id" value={task.id} />
                  {task.value}
                  <Button type="submit" name="_action" value="toggle">
                    進行中に戻す
                  </Button>
                </fetcher.Form>
              </li>
            ))}
          </ul>
        </TabsContent>
      </Tabs>
    </div>
  );
}
