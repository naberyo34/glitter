import type { AnchorHTMLAttributes, ButtonHTMLAttributes, HTMLAttributes } from 'react';
import { Link, type LinkProps } from 'react-router';
import { css } from 'styled-system/css';

export type IconButtonProps =
  | ({
      as?: 'button';
    } & ButtonHTMLAttributes<HTMLButtonElement>)
  | ({
      as: 'link';
    } & LinkProps)
  | ({
      as: 'a';
    } & AnchorHTMLAttributes<HTMLAnchorElement>)
  | ({
      as: 'div';
    } & HTMLAttributes<HTMLDivElement>);

/**
 * アイコンボタン
 * ボタンの見た目でリンクを作成することもできます。
 */
export function IconButton(props: IconButtonProps) {
  // リンクとして振る舞う
  if (props.as === 'link') {
    // memo: as は除去
    const { as, ...linkProps } = props;
    return <Link className={iconButtonStyles} {...linkProps} />;
  }

  // a タグとして振る舞う
  if (props.as === 'a') {
    // memo: as は除去
    const { as, ...anchorProps } = props;
    return <a className={iconButtonStyles} {...anchorProps} />;
  }

  // div として振る舞う 見た目だけ欲しい場合に使う
  if (props.as === 'div') {
    // memo: as は除去
    const { as, ...divProps } = props;
    return <div className={iconButtonStyles} {...divProps} />;
  }

  const { as, ...buttonProps } = props;
  return <button className={iconButtonStyles} {...buttonProps} />;
}

const iconButtonStyles = css({
  display: 'inline-flex',
  alignItems: 'center',
  justifyContent: 'center',
  width: 'x4',
  height: 'x4',
  padding: 'x0_5',
  textLayerColor: 'text',
  backgroundColor: 'buttonTransparent',
  borderRadius: 'full',
  transitionProperty: 'background-color',
  transitionDuration: 'fast',
  cursor: 'pointer',
  _disabled: {
    textLayerColor: 'textDisabled',
    cursor: 'default',
  },
  '&:not(:disabled):hover': {
    backgroundColor: 'buttonTransparentHover',
  },
});
