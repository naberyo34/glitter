import type { SVGProps } from 'react';
import LogoImage from './logo.svg?react';

/**
 * sara のロゴ
 * 必要に応じてサイズを指定して使ってください
 */
export function Logo(props: SVGProps<SVGSVGElement>) {
  return <LogoImage role="img" aria-label="sara" {...props} />;
}
