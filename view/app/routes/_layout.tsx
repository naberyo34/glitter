import { Portal } from '@ark-ui/react';
import type { components } from 'api/schema';
import { Button } from 'app/components/core/Button/Button';
import { Dialog } from 'app/components/core/Dialog/Dialog';
import { Field } from 'app/components/core/Field/Field';
import { Icon } from 'app/components/core/Icon/Icon';
import { IconButton } from 'app/components/core/IconButton/IconButton';
import { Logo } from 'app/components/core/Logo/Logo';
import { Tooltip } from 'app/components/core/Tooltip/Tooltip';
import { useId } from 'react';
import { Outlet, useRouteLoaderData } from 'react-router';
import { css } from 'styled-system/css';

export default function RootLayout() {
  const routeLoaderData = useRouteLoaderData<{
    me: components['schemas']['UserDto'];
  }>('root');
  const postTrigger = useId();
  const loginTrigger = useId();

  return (
    <div
      className={css({
        display: 'grid',
        gridTemplateColumns: 'minmax(343px, 768px)',
        gridTemplateRows: '1fr',
        alignItems: 'flex-start',
        justifyContent: 'center',
        height: '[100dvh]',
        padding: 'x2',
      })}
    >
      <main
        className={css({
          height: '100%',
          backgroundColor: 'surface',
          borderRadius: 'x1',
          overflowY: 'scroll',
        })}
      >
        <Outlet />
      </main>
      <footer
        className={css({
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          paddingTop: 'x1',
        })}
      >
        <nav
          className={css({
            display: 'flex',
            gap: 'x1',
            padding: 'x1',
            backgroundColor: 'surface',
            borderRadius: 'x1',
          })}
        >
          <Dialog.Root id={postTrigger}>
            <Tooltip.Root id={postTrigger}>
              <Tooltip.Trigger asChild>
                <Dialog.Trigger asChild>
                  <IconButton
                    type="button"
                    aria-label="投稿"
                    disabled={!routeLoaderData?.me}
                  >
                    <Icon.Edit />
                  </IconButton>
                </Dialog.Trigger>
              </Tooltip.Trigger>
              <Portal>
                <Tooltip.Positioner>
                  <Tooltip.Content>投稿</Tooltip.Content>
                </Tooltip.Positioner>
              </Portal>
            </Tooltip.Root>
            <Portal>
              <Dialog.Backdrop />
              <Dialog.Positioner>
                <Dialog.Content>
                  <form
                    method="post"
                    action="/post"
                    className={css({
                      display: 'flex',
                      flexDirection: 'column',
                      alignItems: 'center',
                      gap: 'x2',
                      width: '[343px]',
                      padding: 'x2',
                    })}
                  >
                    <Field.Root>
                      <Field.Textarea placeholder="つぶやきましょう" rows={4} name="content" />
                    </Field.Root>
                    <Button type="submit" color="accent">
                      <Icon.Edit />
                      投稿
                    </Button>
                  </form>
                </Dialog.Content>
              </Dialog.Positioner>
            </Portal>
          </Dialog.Root>

          {!routeLoaderData?.me ? (
            <Dialog.Root id={loginTrigger}>
              <Tooltip.Root id={loginTrigger}>
                <Tooltip.Trigger asChild>
                  <Dialog.Trigger>
                    <IconButton as="div" aria-label="ログイン">
                      <Icon.Login />
                    </IconButton>
                  </Dialog.Trigger>
                </Tooltip.Trigger>
                <Portal>
                  <Tooltip.Positioner>
                    <Tooltip.Content>ログイン</Tooltip.Content>
                  </Tooltip.Positioner>
                </Portal>
              </Tooltip.Root>
              <Portal>
                <Dialog.Backdrop />
                <Dialog.Positioner>
                  <Dialog.Content>
                    <form
                      method="post"
                      action="/auth/login"
                      className={css({
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        gap: 'x2',
                        width: '[343px]',
                        padding: 'x2',
                      })}
                    >
                      <Field.Root>
                        <Field.InputWithIcon
                          type="text"
                          name="userId"
                          Icon={Icon.UserId}
                          placeholder="ユーザー名"
                        />
                      </Field.Root>
                      <Field.Root>
                        <Field.InputWithIcon
                          type="password"
                          name="password"
                          Icon={Icon.Password}
                          placeholder="パスワード"
                        />
                      </Field.Root>
                      <Button type="submit" color="accent">
                        <Icon.Login />
                        ログイン
                      </Button>
                      <p
                        className={css({
                          fontSize: 's',
                          textLayerColor: 'textLowEm',
                        })}
                      >
                        ※現在、新規登録は行えません。
                      </p>
                    </form>
                  </Dialog.Content>
                </Dialog.Positioner>
              </Portal>
            </Dialog.Root>
          ) : (
            <Tooltip.Root>
              <Tooltip.Trigger asChild>
                <IconButton as="a" href="/auth/logout" aria-label="ログアウト">
                  <Icon.Logout />
                </IconButton>
              </Tooltip.Trigger>
              <Portal>
                <Tooltip.Positioner>
                  <Tooltip.Content>ログアウト</Tooltip.Content>
                </Tooltip.Positioner>
              </Portal>
            </Tooltip.Root>
          )}
        </nav>
        <div>
          <h1 className={css({ textLayerColor: 'textDisabled' })}>
            <Logo
              aria-label="Glitter"
              className={css({
                height: 'x4',
              })}
            />
          </h1>
        </div>
      </footer>
    </div>
  );
}
