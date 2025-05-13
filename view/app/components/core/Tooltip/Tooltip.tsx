import { Tooltip as ArkTooltip } from '@ark-ui/react';
import { styled } from 'styled-system/jsx';

export const Tooltip = {
  Root: ArkTooltip.Root,
  Trigger: ArkTooltip.Trigger,
  Positioner: ArkTooltip.Positioner,
  Content: styled(ArkTooltip.Content, {
    base: {
      fontSize: 'xs',
      backgroundColor: 'surface',
      border: 's',
      borderColor: 'border',
      borderRadius: 'x1',
      paddingInline: 'x1',
      paddingBlock: 'x0_5',
      zIndex: 'tooltip',
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
