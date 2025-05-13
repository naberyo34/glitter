import { Avatar as ArkAvatar, type AvatarRootBaseProps } from '@ark-ui/react';
import { cva } from 'styled-system/css';
import type { components } from 'api/schema';

type AvatarProps = {
  user: components['schemas']['UserDto'];
  size?: 's' | 'm';
} & AvatarRootBaseProps;

/**
 * アバター
 * @param
 * @returns
 */
export function Avatar({ user, size, ...props }: AvatarProps) {
  return (
    <ArkAvatar.Root className={avatarStyles({ size: size })} {...props}>
      <ArkAvatar.Image
        src={user.icon}
        alt={
          user.username
            ? `${user.username}さんのアバター画像`
            : 'ユーザーのアバター画像'
        }
      />
      {/* <ArkAvatar.Fallback className={avatarStyles({ size: size })} /> */}
    </ArkAvatar.Root>
  );
}

const avatarStyles = cva({
  base: {
    backgroundColor: 'background',
    borderRadius: 'full',
    overflow: 'hidden',
  },
  variants: {
    size: {
      s: {
        width: 'x3',
        height: 'x3',
      },
      m: {
        width: 'x5',
        height: 'x5',
      },
    },
  },
  defaultVariants: {
    size: 'm',
  },
});
