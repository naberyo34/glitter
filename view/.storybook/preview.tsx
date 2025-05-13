import type { Preview } from '@storybook/react';
import { createRoutesStub } from 'react-router';
import { ToastProvider } from 'app/components/core/Toast/Toast';
import '../app/index.css';

const preview: Preview = {
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
  },
  decorators: [
    (Story) => {
      const RoutesStub = createRoutesStub([
        {
          path: '*',
          action: () => ({ redirect: '/' }),
          loader: () => ({ redirect: '/' }),
          Component: () => {
            return (
              <ToastProvider>
                <Story />
              </ToastProvider>
            );
          },
        },
      ]);
      return <RoutesStub />;
    },
  ],
};

export default preview;
