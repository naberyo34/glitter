import type { ComponentType, SVGProps } from 'react';

import IconImage_edit from './edit.svg?react';
import IconImage_email from './email.svg?react';
import IconImage_fail from './fail.svg?react';
import IconImage_info from './info.svg?react';
import IconImage_login from './login.svg?react';
import IconImage_logout from './logout.svg?react';
import IconImage_password from './password.svg?react';
import IconImage_search from './search.svg?react';
import IconImage_success from './success.svg?react';
import IconImage_userId from './userId.svg?react';
import IconImage_xmark from './xmark.svg?react';
import IconImage_loading from './loading.svg?react';

function createIconComponent(
  Image: React.FunctionComponent<SVGProps<SVGSVGElement>>,
  ariaLabel: string,
): ComponentType<SVGProps<SVGSVGElement>> {
  return function IconComponent(props: SVGProps<SVGSVGElement>) {
    return <Image aria-label={ariaLabel} {...props} />;
  };
}

const Xmark = createIconComponent(IconImage_xmark, '閉じる');
const Search = createIconComponent(IconImage_search, '検索');
const UserId = createIconComponent(IconImage_userId, 'ユーザーID');
const Email = createIconComponent(IconImage_email, 'メールアドレス');
const Password = createIconComponent(IconImage_password, 'パスワード');
const Login = createIconComponent(IconImage_login, 'ログイン');
const Logout = createIconComponent(IconImage_logout, 'ログアウト');
const Info = createIconComponent(IconImage_info, 'インフォ');
const Success = createIconComponent(IconImage_success, '成功');
const Fail = createIconComponent(IconImage_fail, '失敗');
const Edit = createIconComponent(IconImage_edit, '編集');
const Loading = createIconComponent(IconImage_loading, 'ロード中');

export const Icon = {
  Xmark,
  Search,
  UserId,
  Email,
  Password,
  Login,
  Logout,
  Info,
  Success,
  Fail,
  Edit,
  Loading,
};
