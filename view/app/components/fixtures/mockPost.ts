import type { components } from 'api/schema';

export const mockPost: components['schemas']['PostWithAuthor'] = {
  uuid: '42fcdc69-7f08-40e1-9004-b65e7a6e5331',
  userId: 'test_user',
  domain: 'example.com',
  content: 'テスト投稿',
  createdAt: '2025-05-01T01:17:38.087+00:00',
  user: {
    userId: 'test_user',
    domain: 'example.com',
    actorUrl: 'https://example.com/user/test_user',
    username: 'テストユーザー',
    profile: 'テスト用のアカウントです。',
    icon: 'https://momochitama-glitter.s3.ap-northeast-1.amazonaws.com/test_user/249767a9-972b-401f-bdba-13a8e136f3ef.png',
  },
};
