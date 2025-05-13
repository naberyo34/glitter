import type { Meta, StoryObj } from '@storybook/react';

import { Portal } from '@ark-ui/react';
import { Button } from '../Button/Button';
import { HoverCard } from './HoverCard';

const meta: Meta<typeof HoverCard.Root> = {
  component: HoverCard.Root,
  render: () => (
    <HoverCard.Root>
      <HoverCard.Trigger asChild>
        <Button>ホバーでホバーカードを開く</Button>
      </HoverCard.Trigger>
      <Portal>
        <HoverCard.Positioner>
          <HoverCard.Content>
            コンテンツはデフォルトのスタイルを持ちません。
          </HoverCard.Content>
        </HoverCard.Positioner>
      </Portal>
    </HoverCard.Root>
  ),
};

export default meta;
type Story = StoryObj<typeof HoverCard.Root>;

export const Default: Story = {};
