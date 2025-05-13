import { Menu as ArkMenu } from '@ark-ui/react';
import { styled } from 'styled-system/jsx';

export const Menu = {
  Root: ArkMenu.Root,
  Trigger: ArkMenu.Trigger,
  Positioner: ArkMenu.Positioner,
  Content: styled(ArkMenu.Content, {
    base: {
      display: 'flex',
      flexDirection: 'column',
      gap: 'x1',
      padding: 'x1',
      backgroundColor: 'surface',
      border: 's',
      borderColor: 'border',
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
  Item: styled(ArkMenu.Item, {
    base: {
      width: '100%',
      '& *': {
        width: '100%',
      },
    },
  }),
};
