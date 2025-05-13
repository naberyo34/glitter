import { Field as ArkField, type FieldInputProps } from '@ark-ui/react';
import type { ComponentType, SVGProps } from 'react';
import { css } from 'styled-system/css';
import { styled } from 'styled-system/jsx';

/**
 * フィールド
 * input としても textarea としても利用できます
 * @see https://ark-ui.com/react/docs/components/field
 */
export const Field = {
  Root: styled(ArkField.Root, {
    base: {
      width: '100%',
    },
  }),
  Label: ArkField.Label,
  Input: styled(ArkField.Input, {
    base: {
      width: '100%',
      padding: 'x1',
      backgroundColor: 'surface',
      borderRadius: 'x1',
      // focus 時の色変更を自然にするため border ではなく outline
      outline: '[1px solid {colors.border}]',
      transitionProperty: 'outline',
      transitionDuration: 'fast',
      _focus: {
        outlineColor: 'accent',
      },
      _placeholder: {
        color: 'textLowEm',
      },
    },
  }),
  InputWithIcon: InputWithIcon,
  HelperText: ArkField.HelperText,
  ErrorText: ArkField.ErrorText,
  Textarea: styled(ArkField.Textarea, {
    base: {
      resize: 'none',
      width: '100%',
      padding: 'x1',
      backgroundColor: 'surface',
      borderRadius: 'x1',
      // focus 時の色変更を自然にするため border ではなく outline
      outline: '[1px solid {colors.border}]',
      transitionProperty: 'outline',
      transitionDuration: 'fast',
      _focus: {
        outlineColor: 'accent',
      },
      _placeholder: {
        color: 'textLowEm',
      },
    },
  }),
  Select: ArkField.Select,
};

export type InputWithIconProps = {
  Icon: ComponentType<SVGProps<SVGSVGElement>>;
} & FieldInputProps;

function InputWithIcon({ Icon, ...props }: InputWithIconProps) {
  return (
    <div
      className={css({
        position: 'relative',
      })}
    >
      <span
        className={css({
          position: 'absolute',
          left: 'x1',
          top: '[10px]',
          textLayerColor: 'textLowEm',
        })}
      >
        <Icon />
      </span>
      <Field.Input
        className={css({
          width: '100%',
          padding: 'x1',
          paddingLeft: 'x4',
          backgroundColor: 'surface',
          borderRadius: 'x1',
          // focus 時の色変更を自然にするため border ではなく outline
          outline: '[1px solid {colors.border}]',
          transitionProperty: 'outline',
          transitionDuration: 'fast',
          _focus: {
            outlineColor: 'accent',
          },
          _placeholder: {
            color: 'textLowEm',
          },
        })}
        {...props}
      />
    </div>
  );
}
