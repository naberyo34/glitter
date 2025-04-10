import {
  Link,
  type LoaderFunctionArgs,
  Outlet,
  type unstable_RouterContextProvider,
  useLoaderData,
} from 'react-router';
import { joinURL } from 'ufo';
import { Avatar, AvatarImage } from '~/components/ui/avatar';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarHeader,
  SidebarMenuButton,
  SidebarProvider,
} from '~/components/ui/sidebar';
import { appUrl } from '~/lib/appUrl.server';
import { type User, userContext } from '~/middlewares/userContext';

export type RootLayoutContext = {
  user: User | null;
  appUrl: typeof appUrl;
};

export async function loader({
  context,
}: LoaderFunctionArgs<unstable_RouterContextProvider>) {
  const user = context.get(userContext);
  return { user: user, appUrl: appUrl };
}

export default function RootLayout() {
  const { user, appUrl } = useLoaderData<typeof loader>();
  return (
    <SidebarProvider>
      <Sidebar>
        <SidebarHeader>Glitter</SidebarHeader>
        <SidebarContent>
          <SidebarGroup>
            <SidebarMenuButton asChild>
              <Link to="/login">ログイン</Link>
            </SidebarMenuButton>
            <SidebarMenuButton asChild>
              <Link to="/logout">ログアウト</Link>
            </SidebarMenuButton>
            <SidebarMenuButton>ポスト</SidebarMenuButton>
          </SidebarGroup>
        </SidebarContent>
        <SidebarFooter>
          {user?.icon && (
            <div className="flex items-center gap-4">
              <Avatar>
                <AvatarImage
                  src={joinURL(appUrl.storage, 'glitter', user.icon)}
                />
              </Avatar>
              <div>
                <p className="font-bold">{user.username}</p>
                <p className="text-muted-foreground">@{user.id}</p>
              </div>
            </div>
          )}
        </SidebarFooter>
      </Sidebar>
      <main className="p-4">
        <Outlet context={{ user, appUrl }} />
      </main>
    </SidebarProvider>
  );
}
