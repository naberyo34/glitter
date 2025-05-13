import type { Meta, StoryObj } from '@storybook/react';

import { mockPost } from 'app/components/fixtures/mockPost';
import { Post } from './Post';

const meta: Meta<typeof Post> = {
  component: Post,
  args: {
    post: mockPost
  },
};

export default meta;
type Story = StoryObj<typeof Post>;

export const Default: Story = {};
