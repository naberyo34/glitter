import type { components } from 'api/schema';
import { Avatar } from 'app/components/core/Avatar/Avatar';
import { Icon } from 'app/components/core/Icon/Icon';
import { Tooltip } from 'app/components/core/Tooltip/Tooltip';
import { useGlitterApi } from 'app/hooks/useGlitterApi';
import { Suspense } from 'react';
import { css } from 'styled-system/css';

export type UserDetailProps = {
  post: components['schemas']['PostWithAuthor'];
};

export function UserDetail(props: UserDetailProps) {
  return (
    <>
      <div
        className={css({
          display: 'flex',
          gap: 'x2',
          paddingBottom: 'x2',
          borderBottom: 's',
          borderColor: 'border',
        })}
      >
        <Avatar user={props.post.user} />
        <div>
          <p className={css({ fontWeight: 'bold' })}>
            {props.post.user.username}
          </p>
          <p
            className={css({
              fontSize: 'xs',
              fontWeight: 'bold',
              textLayerColor: 'textLowEm',
            })}
          >
            {props.post.user.userId}
            <span className={css({ fontWeight: 'regular' })}>
              @{props.post.user.domain}
            </span>
          </p>
        </div>
      </div>
      <Suspense
        fallback={
          <div
            className={css({
              display: 'grid',
              placeItems: 'center',
              marginTop: 'x2',
              textLayerColor: 'textLowEm',
            })}
          >
            <Icon.Loading
              className={css({
                width: 'x2',
                height: 'auto',
              })}
            />
          </div>
        }
      >
        <UserDetailFollowers userId={props.post.user.userId} />
      </Suspense>
    </>
  );
}

type UserDetailFollowersProps = {
  userId: string;
};

export function UserDetailFollowers(props: UserDetailFollowersProps) {
  const glitterApi = useGlitterApi();
  const { data } = glitterApi.useSuspenseQuery('get', '/user/{id}/followers', {
    params: {
      path: {
        id: props.userId,
      },
    },
  });
  // application/json から取得できる型は絶対こっちなのでキャスト もっといい方法ありそう
  const followers = data as components['schemas']['UserDto'][];

  return (
    <div className={css({ marginTop: 'x2' })}>
      <p
        className={css({
          display: 'flex',
          justifyContent: 'space-between',
          fontWeight: 'bold',
          textLayerColor: 'textLowEm',
        })}
      >
        <span
          className={css({ display: 'flex', gap: 'x1', alignItems: 'center' })}
        >
          <Icon.UserId />
          フォロワー
        </span>
        <span>{followers.length}人</span>
      </p>
      <ul className={css({ display: 'flex', gap: 'x1', paddingBlock: 'x1' })}>
        {followers.map((follower) => (
          <li key={follower.userId}>
            <Tooltip.Root>
              <Tooltip.Trigger>
                <Avatar size="s" user={follower} />
              </Tooltip.Trigger>
              <Tooltip.Positioner>
                <Tooltip.Content>
                  {follower.userId}@{follower.domain}
                </Tooltip.Content>
              </Tooltip.Positioner>
            </Tooltip.Root>
          </li>
        ))}
      </ul>
    </div>
  );
}
