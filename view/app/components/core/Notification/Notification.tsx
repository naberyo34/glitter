import type { ReactNode } from 'react';
import { css, cva } from 'styled-system/css';
import { Icon } from '../Icon/Icon';

export type NotificationProps = {
  type?: 'info' | 'success' | 'fail';
  children: ReactNode;
};

/**
 * 汎用的な通知メッセージ表示
 */
export function Notification(props: NotificationProps) {
  return (
    <div className={notificationStyles({ type: props.type })}>
      {props.type === 'info' && (
        <Icon.Info
          className={css({
            width: 'x3',
            height: 'auto',
          })}
        />
      )}
      {props.type === 'success' && (
        <Icon.Success
          className={css({
            width: 'x3',
            height: 'auto',
          })}
        />
      )}
      {props.type === 'fail' && (
        <Icon.Fail
          className={css({
            width: 'x3',
            height: 'auto',
          })}
        />
      )}
      <p>{props.children}</p>
    </div>
  );
}

const notificationStyles = cva({
  base: {
    display: 'flex',
    alignItems: 'center',
    gap: 'x1',
    width: '100%',
    padding: 'x2',
    borderRadius: 'x1',
    border: 's',
  },
  variants: {
    type: {
      info: {
        backgroundColor: 'surface',
        borderColor: 'border',
      },
      success: {
        textLayerColor: 'textSuccess',
        backgroundColor: 'surfaceSuccess',
        borderColor: 'borderSuccess',
      },
      fail: {
        textLayerColor: 'textFail',
        backgroundColor: 'surfaceFail',
        borderColor: 'borderFail',
      },
    },
  },
  defaultVariants: {
    type: 'info',
  },
});
