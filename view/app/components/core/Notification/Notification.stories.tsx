import type { Meta, StoryObj } from '@storybook/react';

import { Notification } from './Notification';

const meta: Meta<typeof Notification> = {
  component: Notification,
  args: {
    children: 'メッセージ',
    type: 'info',
  },
};

export default meta;
type Story = StoryObj<typeof Notification>;

export const Info: Story = {};

export const Success: Story = {
  args: {
    type: 'success',
  },
};

export const Fail: Story = {
  args: {
    type: 'fail',
  },
};
