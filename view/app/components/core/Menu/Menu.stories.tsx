import type { Meta, StoryObj } from '@storybook/react';

import { Button } from '../Button/Button';
import { Menu } from './Menu';

const meta: Meta<typeof Menu.Root> = {
  component: Menu.Root,
  render: (args) => (
    <Menu.Root {...args}>
      <Menu.Trigger asChild>
        <Button>メニューを開く</Button>
      </Menu.Trigger>
      <Menu.Positioner>
        <Menu.Content>
          <Menu.Item value="1">アイテム1</Menu.Item>
          <Menu.Item value="2">アイテム2</Menu.Item>
          <Menu.Item value="3">アイテム3</Menu.Item>
        </Menu.Content>
      </Menu.Positioner>
    </Menu.Root>
  ),
};

export default meta;
type Story = StoryObj<typeof Menu.Root>;

export const Default: Story = {};

export const WithButton: Story = {
  render: (args) => (
    <Menu.Root {...args}>
      <Menu.Trigger asChild>
        <Button>メニューを開く</Button>
      </Menu.Trigger>
      <Menu.Positioner>
        <Menu.Content>
          <Menu.Item value="1">
            <Button color="transparent">アイテム1</Button>
          </Menu.Item>
          <Menu.Item value="2">
            <Button color="transparent">アイテム2</Button>
          </Menu.Item>
          <Menu.Item value="3">
            <Button color="transparent">アイテム3</Button>
          </Menu.Item>
        </Menu.Content>
      </Menu.Positioner>
    </Menu.Root>
  ),
};
