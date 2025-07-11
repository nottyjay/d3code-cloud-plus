import pluginVue from 'eslint-plugin-vue'
import globals from 'globals'
import prettier from 'eslint-plugin-prettier'
import pluginPrettier from 'eslint-plugin-prettier'
import { defineConfigWithVueTs, vueTsConfigs } from '@vue/eslint-config-typescript'
import skipFormatting from '@vue/eslint-config-prettier/skip-formatting'
import { readFileSync } from 'fs'
import pluginImport from 'eslint-plugin-import'

const autoImportGlobals = JSON.parse(readFileSync('./.eslintrc-auto-import.json', 'utf8'))

export default defineConfigWithVueTs(
  {
    name: 'app/files-to-lint',
    files: ['**/*.{js,cjs,ts,mts,tsx,vue}'],
    plugins: {
      import: pluginImport,
      prettier: pluginPrettier,
    },
    settings: {
      'import/resolver': {
        alias: {
          map: [['@', './src']],
          extensions: ['.js', '.jsx', '.ts', '.tsx', '.vue'],
        },
      },
    },
  },

  {
    name: 'app/files-to-ignore',
    ignores: ['**/dist/**', '**/dist-ssr/**', '**/coverage/**'],
  },
  {
    languageOptions: {
      globals: globals.browser,
    },
  },
  pluginVue.configs['flat/essential'],
  vueTsConfigs.recommended,
  skipFormatting,
  {
    languageOptions: {
      globals: {
        ...globals.browser,
        ...autoImportGlobals.globals,
        process: true,
      },
    },
    plugins: { prettier },
    rules: {
      // 集成Prettier格式化规则，确保代码风格统一
      'prettier/prettier': 'error',
      // 强制使用2空格缩进
      'indent': ['error', 2, { 'SwitchCase': 1 }],
      // 禁用quotes规则，由prettier处理引号格式
      'quotes': 'off',
      // 禁止使用分号，保持代码简洁
      'semi': ['error', 'never'],
      // 强制使用===和!==，避免类型转换错误
      'eqeqeq': ['error', 'always'],
      'import/extensions': [
        2,
        'ignorePackages',
        {
          js: 'never',
          jsx: 'never',
          ts: 'never',
          tsx: 'never',
          vue: 'always',
          json: 'always',
        },
      ],
      '@typescript-eslint/no-empty-function': 'off',
      '@typescript-eslint/no-explicit-any': 'off',
      '@typescript-eslint/no-unused-vars': 'off',
      '@typescript-eslint/no-this-alias': 'off',
      // 允许使用空Object类型 {}
      '@typescript-eslint/no-empty-object-type': 'off',
      '@typescript-eslint/no-unused-expressions': 'off',
      // vue
      'vue/script-setup-uses-vars': 'error',
      'vue/multi-word-component-names': 'off',
      'vue/singleline-html-element-content-newline': 'off',
      'vue/max-attributes-per-line': 'off',
      'vue/valid-define-props': 'off',
      'vue/no-v-model-argument': 'off',
      'vue/custom-event-name-casing': ['error', 'camelCase'],
      'vue/no-v-text': 'warn',
      'vue/padding-line-between-blocks': 'warn',
      'vue/require-direct-export': 'warn',
      'prefer-rest-params': 'off',
      // 生产环境禁止使用debugger，开发环境允许
      'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
      'no-param-reassign': 'off',
      'prefer-regex-literals': 'off',
      'import/no-extraneous-dependencies': 'off',
      'no-console': 'off',
      'no-unused-vars': [
        'error',
        {
          'vars': 'all',
          'args': 'none',
          'ignoreRestSiblings': false,
          'caughtErrors': 'none'
        },
      ],
    },
  },
  // 为 .d.ts 文件禁用 unused-vars 检查
  {
    files: ['**/*.d.ts', '**/enums/*.ts'],
    rules: {
      'no-unused-vars': 'off',
      '@typescript-eslint/no-unused-vars': 'off',
    },
  }
)
