import { Toast, Toaster, createToaster, toastAnatomy } from '@ark-ui/react';
import { type ReactNode, createContext, useContext } from 'react';
import { css, sva } from 'styled-system/css';

const toaster = createToaster({
  placement: 'bottom-end',
  gap: 8,
});
const ToastContext = createContext(toaster);
export const useToast = () => useContext(ToastContext);

export type ToastProviderProps = {
  children: ReactNode;
};

/**
 * トーストを表示するためのコンテキストプロバイダー
 */
export const ToastProvider = (props: ToastProviderProps) => {
  return (
    <ToastContext.Provider value={toaster}>
      {props.children}
      <Toaster toaster={toaster}>
        {(toast) => (
          <Toast.Root key={toast.id} className={toastStyles.root}>
            <Toast.Title className={toastStyles.title}>
              {toast.title}
            </Toast.Title>
            <Toast.Description className={toastStyles.description}>
              {toast.description}
            </Toast.Description>
          </Toast.Root>
        )}
      </Toaster>
    </ToastContext.Provider>
  );
};

/**
 * トーストの挙動を実現するためにはスタイル側も正しく設定しておく必要がある
 * park-ui のスタイルを参考にしている
 * @see https://github.com/cschroeter/park-ui/blob/main/packages/panda/src/theme/recipes/toast.ts
 */
const toastStyles = sva({
  className: 'toast',
  slots: toastAnatomy.keys(),
  base: {
    root: {
      width: '[240px]',
      padding: 'x2',
      backgroundColor: 'surface',
      border: 's',
      borderColor: 'border',
      borderRadius: 'x1',
      height: 'var(--height)',
      opacity: 'var(--opacity)',
      overflowWrap: 'anywhere',
      position: 'relative',
      scale: 'var(--scale)',
      translate: '[var(--x) var(--y) 0]',
      willChange: 'translate, opacity, scale',
      zIndex: 'var(--z-index)',
      transitionDuration: 'fast',
      transitionProperty: 'translate, scale, opacity, height',
      transitionTimingFunction: 'default',
      '&[data-type="success"]': {
        color: 'textSuccess',
        backgroundColor: 'surfaceSuccess',
        borderColor: 'borderSuccess',
      },
      '&[data-type="error"]': {
        color: 'textFail',
        backgroundColor: 'surfaceFail',
        borderColor: 'borderFail',
      },
    },
    title: {
      fontWeight: 'bold',
    },
    description: {},
  },
})();
