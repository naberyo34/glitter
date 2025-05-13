import type { Meta, StoryObj } from '@storybook/react';

import { mockUser } from 'app/components/fixtures/mockUser';
import { Avatar } from './Avatar';

const meta: Meta<typeof Avatar> = {
  component: Avatar,
  args: {
    user: mockUser,
  },
};

export default meta;
type Story = StoryObj<typeof Avatar>;

export const Default: Story = {};
