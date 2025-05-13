import { Dialog as ArkDialog } from '@ark-ui/react';
import type { ReactNode } from 'react';
import { styled } from 'styled-system/jsx';

/**
 * ダイアログ
 * @see https://ark-ui.com/react/docs/components/dialog
 */
export const Dialog = {
  Root: ArkDialog.Root,
  Trigger: ArkDialog.Trigger,
  Backdrop: styled(ArkDialog.Backdrop, {
    base: {
      zIndex: 'overlay',
      position: 'fixed',
      inset: '0',
      backgroundColor: 'backdrop',
      '&[data-state="open"]': {
        animation: 'fadeIn',
        animationDuration: 'fast',
      },
      '&[data-state="closed"]': {
        animation: 'fadeOut',
        animationDuration: 'fast',
      },
    },
  }),
  Positioner: styled(ArkDialog.Positioner, {
    base: {
      zIndex: 'modal',
      position: 'fixed',
      inset: '0',
      display: 'grid',
      placeItems: 'center',
    },
  }),
  Content: styled(ArkDialog.Content, {
    base: {
      backgroundColor: 'surface',
      borderRadius: 'x1',
      '&[data-state="open"]': {
        animation: 'popIn',
        animationDuration: 'fast',
      },
      '&[data-state="closed"]': {
        animation: 'popOut',
        animationDuration: 'fast',
      },
    },
  }),
  Description: ArkDialog.Description,
};

export type DialogHeaderProps = {
  children: ReactNode;
};
