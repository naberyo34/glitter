import type { Meta, StoryObj } from '@storybook/react';

import { Icon } from '../Icon/Icon';
import { Field } from './Field';

const meta: Meta<typeof Field.Root> = {
  component: Field.Root,
};

export default meta;
type Story = StoryObj<typeof Field.Root>;

export const Input: Story = {
  render: () => (
    <Field.Root>
      <Field.Input placeholder="プレースホルダー" />
    </Field.Root>
  ),
};

export const InputWithIcon: Story = {
  render: () => (
    <Field.Root>
      <Field.InputWithIcon Icon={Icon.Email} placeholder="メールアドレス" />
    </Field.Root>
  ),
};

export const Textarea: Story = {
  render: () => (
    <Field.Root>
      <Field.Textarea placeholder="プレースホルダー" rows={4} />
    </Field.Root>
  ),
};
