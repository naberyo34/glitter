import { Bell, Home, Settings, Sparkles } from 'lucide-react';
import { Link, Outlet } from 'react-router';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarHeader,
  SidebarMenuButton,
  SidebarProvider,
} from '~/components/ui/sidebar';

export default function RootLayout() {
  return (
    <SidebarProvider>
      <Sidebar>
        <SidebarHeader>
          <Link to="/">
            <Sparkles />
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
          {/* {user && (
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
          )} */}
        </SidebarFooter>
      </Sidebar>
      <main>
        <Outlet />
      </main>
    </SidebarProvider>
  );
}
