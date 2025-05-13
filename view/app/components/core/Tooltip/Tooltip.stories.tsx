import type { Meta, StoryObj } from '@storybook/react';

import { Portal } from '@ark-ui/react';
import { Button } from '../Button/Button';
import { Tooltip } from './Tooltip';

const meta: Meta<typeof Tooltip.Root> = {
  component: Tooltip.Root,
  render: () => (
    <Tooltip.Root>
      <Tooltip.Trigger asChild>
        <Button>ホバーでツールチップを開く</Button>
      </Tooltip.Trigger>
      <Portal>
        <Tooltip.Positioner>
          <Tooltip.Content>ツールチップです</Tooltip.Content>
        </Tooltip.Positioner>
      </Portal>
    </Tooltip.Root>
  ),
};

export default meta;
type Story = StoryObj<typeof Tooltip.Root>;

export const Default: Story = {};
