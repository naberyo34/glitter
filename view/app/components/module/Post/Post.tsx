import { Portal } from '@ark-ui/react';
import { format } from '@formkit/tempo';
import type { components } from 'api/schema';
import { Avatar } from 'app/components/core/Avatar/Avatar';
import { HoverCard } from 'app/components/core/HoverCard/HoverCard';
import type { ReactNode } from 'react';
import { css } from 'styled-system/css';

export type PostProps = {
  post: components['schemas']['PostWithAuthor'];
  userDetail: ReactNode;
};

/**
 * 投稿
 * @param props
 * @returns
 */
export function Post(props: PostProps) {
  return (
    <li
      key={props.post.uuid}
      className={css({
        display: 'flex',
        gap: 'x2',
        alignItems: 'flex-start',
        padding: 'x2',
        borderBottom: 's',
        borderColor: 'border',
        transitionProperty: 'background-color',
        transitionDuration: 'fast',
        _hover: {
          backgroundColor: 'buttonTransparentHover',
        },
      })}
    >
      <HoverCard.Root lazyMount>
        <HoverCard.Trigger className={css({ cursor: 'pointer' })}>
          <Avatar user={props.post.user} />
        </HoverCard.Trigger>
        <Portal>
          <HoverCard.Positioner>
            <HoverCard.Content className={css({ padding: 'x2' })}>
              {props.userDetail}
            </HoverCard.Content>
          </HoverCard.Positioner>
        </Portal>
      </HoverCard.Root>
      <div className={css({ flex: '1' })}>
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
        <p className={css({ paddingBlock: 'x2' })}>{props.post.content}</p>
        <time
          className={css({
            display: 'block',
            textAlign: 'right',
            fontSize: 'xs',
          })}
        >
          {format(props.post.createdAt, 'YYYY/MM/DD HH:mm:ss')}
        </time>
      </div>
    </li>
  );
}
