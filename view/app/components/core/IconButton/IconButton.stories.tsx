import type { Meta, StoryObj } from '@storybook/react';

import { Icon } from '../Icon/Icon';
import { IconButton } from './IconButton';

const meta: Meta<typeof IconButton> = {
  component: IconButton,
  args: {
    children: <Icon.Search />,
  },
};

export default meta;
type Story = StoryObj<typeof IconButton>;

export const ButtonElement: Story = {};

export const LinkElement: Story = {
  args: {
    as: 'link',
  },
};

export const DivElement: Story = {
  args: {
    as: 'div',
  },
};

export const Disabled: Story = {
  args: {
    disabled: true,
  },
};
