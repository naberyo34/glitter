import type { Meta, StoryObj } from '@storybook/react';

import { Icon } from '../Icon/Icon';
import { Button } from './Button';

const meta: Meta<typeof Button> = {
  component: Button,
  args: {
    children: 'ボタン',
  },
};

export default meta;
type Story = StoryObj<typeof Button>;

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

export const WithIcon: Story = {
  args: {
    children: (
      <>
        <Icon.Search />
        検索
      </>
    ),
  },
};

export const Accent: Story = {
  args: {
    color: 'accent',
  },
};

export const Transparent: Story = {
  args: {
    color: 'transparent',
  },
};

export const Disabled: Story = {
  args: {
    disabled: true,
  },
};
