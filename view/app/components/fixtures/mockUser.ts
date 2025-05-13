import type { components } from 'api/schema';

export const mockUser: components['schemas']['UserDto'] = {
  userId: 'test_user',
  username: 'テストユーザー',
  domain: 'example.com',
  actorUrl: 'https://example.com/users/test_user',
  profile: 'テスト用のアカウントです。',
  icon: 'https://momochitama-glitter.s3.ap-northeast-1.amazonaws.com/test_user/249767a9-972b-401f-bdba-13a8e136f3ef.png',
};
