import {
  Bell,
  ChevronsUpDown,
  Home,
  LogOut,
  Settings,
  UserCircle,
} from 'lucide-react';
import {
  Link,
  type LoaderFunctionArgs,
  Outlet,
  type unstable_RouterContextProvider,
  useLoaderData,
} from 'react-router';
import { joinURL } from 'ufo';
import { Avatar, AvatarFallback, AvatarImage } from '~/components/ui/avatar';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '~/components/ui/dropdown-menu';
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
import Glitter from 'public/glitter.svg?react';

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
        <SidebarHeader>
          <Link to="/" className="p-4">
            <Glitter className="fill-current transition-opacity opacity-10 hover:opacity-100" />
          </Link>
        </SidebarHeader>
        <SidebarContent>
          <SidebarGroup>
            <SidebarMenuButton asChild>
              <Link to="/">
                <Home />
                ホーム
              </Link>
            </SidebarMenuButton>
            <SidebarMenuButton asChild>
              <Link to="/">
                <Bell />
                通知
              </Link>
            </SidebarMenuButton>
            <SidebarMenuButton asChild>
              <Link to="/">
                <Settings />
                設定
              </Link>
            </SidebarMenuButton>
          </SidebarGroup>
        </SidebarContent>
        <SidebarFooter>
          {user && (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <SidebarMenuButton className="flex justify-between h-12">
                  <div className="flex gap-2">
                    <Avatar>
                      {user.icon && (
                        <AvatarImage
                          src={joinURL(appUrl.storage, user.icon)}
                        />
                      )}
                    </Avatar>
                    <div>
                      <p className="text-xs font-bold">{user.username}</p>
                      <p className="text-xs text-muted-foreground">
                        @{user.id}
                      </p>
                    </div>
                  </div>
                  <ChevronsUpDown />
                </SidebarMenuButton>
              </DropdownMenuTrigger>
              <DropdownMenuContent>
                <DropdownMenuItem asChild>
                  <Link to={joinURL('/', user.id)}>
                    <UserCircle />
                    プロフィール
                  </Link>
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem asChild>
                  <Link to="/logout">
                    <LogOut />
                    ログアウト
                  </Link>
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          )}
        </SidebarFooter>
      </Sidebar>
      <main>
        <Outlet context={{ user, appUrl }} />
      </main>
    </SidebarProvider>
  );
}
