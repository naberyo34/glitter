import type { Meta, StoryObj } from '@storybook/react';

import { createToaster } from '@ark-ui/react';
import { useCallback } from 'react';
import { Button } from '../Button/Button';
import { ToastProvider, useToast } from './Toast';

const meta: Meta<typeof ToastProvider> = {
  component: ToastProvider,
  render: () => {
    const toast = useToast();
    const onClick = useCallback(() => {
      toast.create({
        title: 'タイトル',
        description: '本文',
        type: 'info',
      });
    }, [toast]);

    return <Button onClick={onClick}>トーストを表示</Button>;
  },
};

export default meta;
type Story = StoryObj<typeof ToastProvider>;

export const Info: Story = {};

export const SuccessMessage: Story = {
  render: () => {
    const toast = useToast();
    const onClick = useCallback(() => {
      toast.create({
        title: 'タイトル',
        description: '本文',
        type: 'success',
      });
    }, [toast]);

    return <Button onClick={onClick}>成功トーストを表示</Button>;
  },
};

export const ErrorMessage: Story = {
  render: () => {
    const toast = useToast();
    const onClick = useCallback(() => {
      toast.create({
        title: 'タイトル',
        description: '本文',
        type: 'error',
      });
    }, [toast]);

    return <Button onClick={onClick}>失敗トーストを表示</Button>;
  },
};
