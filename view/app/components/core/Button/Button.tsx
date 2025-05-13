import type { ButtonHTMLAttributes, HTMLAttributes } from 'react';
import { Link, type LinkProps } from 'react-router';
import { cva } from 'styled-system/css';

export type ButtonProps = (
  | ({
      as?: 'button';
    } & ButtonHTMLAttributes<HTMLButtonElement>)
  | ({
      as: 'link';
    } & LinkProps)
  | ({
      as: 'div';
    } & HTMLAttributes<HTMLDivElement>)
) & { color?: 'default' | 'accent' | 'transparent' };

/**
 * ボタン
 * ボタンの見た目でリンクを作成することもできます。
 */
export function Button(props: ButtonProps) {
  // リンクとして振る舞う
  if (props.as === 'link') {
    // memo: as は除去
    const { as, color, ...linkProps } = props;
    return <Link className={buttonStyles({ color: color })} {...linkProps} />;
  }

  // div として振る舞う 見た目だけ欲しい場合に使う
  if (props.as === 'div') {
    // memo: as は除去
    const { as, color, ...divProps } = props;
    return <div className={buttonStyles({ color: color })} {...divProps} />;
  }

  const { as, color, type = 'button', ...buttonProps } = props;
  return (
    <button
      className={buttonStyles({ color: color })}
      type={type}
      {...buttonProps}
    />
  );
}

const buttonStyles = cva({
  base: {
    display: 'inline-flex',
    alignItems: 'center',
    gap: 'x0_5',
    paddingInline: 'x2',
    paddingBlock: 'x1',
    borderRadius: 'full',
    transitionProperty: 'background-color',
    transitionDuration: 'fast',
    cursor: 'pointer',
    '& svg': {
      width: 'em',
      height: 'auto',
    },
    _disabled: {
      textLayerColor: 'textDisabled',
      backgroundColor: 'buttonDisabled',
      cursor: 'default',
    },
  },
  variants: {
    color: {
      default: {
        textLayerColor: 'textInverted',
        backgroundColor: 'button',
        '&:not(:disabled):hover': {
          backgroundColor: 'buttonHover',
        },
      },
      accent: {
        textLayerColor: 'text',
        backgroundGradient: 'accent',
        '&:not(:disabled):hover': {
          backgroundGradient: 'accentHover',
        },
      },
      transparent: {
        textLayerColor: 'text',
        backgroundColor: 'buttonTransparent',
        '&:not(:disabled):hover': {
          backgroundColor: 'buttonTransparentHover',
        },
      },
    },
  },
  defaultVariants: {
    color: 'default',
  },
});
