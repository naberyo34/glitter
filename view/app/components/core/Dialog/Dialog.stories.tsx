import type { Meta, StoryObj } from '@storybook/react';

import { Portal } from '@ark-ui/react';
import { Button } from '../Button/Button';
import { Dialog } from './Dialog';

const meta: Meta<typeof Dialog.Root> = {
  component: Dialog.Root,
  render: () => (
    <Dialog.Root>
      <Dialog.Trigger asChild>
        <Button>ダイアログを開く</Button>
      </Dialog.Trigger>
      <Portal>
        <Dialog.Backdrop />
        <Dialog.Positioner>
          <Dialog.Content>
            <Dialog.Description>
              コンテンツはデフォルトのスタイルを持ちません。
            </Dialog.Description>
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>
    </Dialog.Root>
  ),
};

export default meta;
type Story = StoryObj<typeof Dialog.Root>;

export const Default: Story = {};
