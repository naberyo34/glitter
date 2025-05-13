import { HoverCard as ArkHoverCard } from '@ark-ui/react';
import { styled } from 'styled-system/jsx';

export const HoverCard = {
  Root: ArkHoverCard.Root,
  Trigger: ArkHoverCard.Trigger,
  Positioner: ArkHoverCard.Positioner,
  Content: styled(ArkHoverCard.Content, {
    base: {
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
};
