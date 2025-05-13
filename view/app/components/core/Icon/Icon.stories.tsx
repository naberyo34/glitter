import type { Meta, StoryObj } from '@storybook/react';

import { css } from 'styled-system/css';
import { Icon } from './Icon';

const meta: Meta<typeof Icon.Xmark> = {
  component: Icon.Xmark,
};

export default meta;
type Story = StoryObj<typeof Icon.Xmark>;

export const Catalogue: Story = {
  render: () => {
    const icons = [
      { name: 'xmark', Icon: Icon.Xmark },
      { name: 'search', Icon: Icon.Search },
      { name: 'userId', Icon: Icon.UserId },
      { name: 'email', Icon: Icon.Email },
      { name: 'password', Icon: Icon.Password },
      { name: 'login', Icon: Icon.Login },
      { name: 'logout', Icon: Icon.Logout },
      { name: 'info', Icon: Icon.Info },
      { name: 'success', Icon: Icon.Success },
      { name: 'fail', Icon: Icon.Fail },
      { name: 'edit', Icon: Icon.Edit },
      { name: 'loading', Icon: Icon.Loading },
    ];
    return (
      <table
        className={css({
          width: '100%',
          backgroundColor: 'surface',
          borderRadius: 'x1',
          '& tr:not(:last-child)': {
            borderBottom: 's',
            borderColor: 'border',
          },
          '& th, & td': {
            padding: 'x1',
            textAlign: 'left',
          },
        })}
      >
        <thead>
          <tr>
            <th>名前</th>
            <th>アイコン</th>
          </tr>
        </thead>
        <tbody>
          {icons.map(({ name, Icon }) => (
            <tr key={name}>
              <td>{name}</td>
              <td>
                <Icon />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  },
};
