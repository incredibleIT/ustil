/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}"
  ],
  theme: {
    extend: {
      // 自定义颜色系统
      colors: {
        // 主色调 - 技术蓝
        primary: {
          50: '#eff6ff',
          100: '#dbeafe',
          200: '#bfdbfe',
          300: '#93c5fd',
          400: '#60a5fa',
          500: '#3b82f6',  // 主色
          600: '#2563eb',
          700: '#1d4ed8',
          800: '#1e40af',
          900: '#1e3a8a',
        },
        // 角色颜色
        role: {
          probation: '#9ca3af',  // 预备成员 - 灰色
          member: '#3b82f6',     // 正式成员 - 蓝色
          admin: '#f59e0b',      // 负责人 - 金色
        },
        // 内容状态颜色
        status: {
          draft: '#9ca3af',      // 草稿 - 灰色
          pending: '#f59e0b',    // 待审核 - 橙色
          published: '#10b981',  // 已发布 - 绿色
          rejected: '#ef4444',   // 已拒绝 - 红色
        },
        // 中性色
        neutral: {
          50: '#f9fafb',
          100: '#f3f4f6',
          200: '#e5e7eb',
          300: '#d1d5db',
          400: '#9ca3af',
          500: '#6b7280',
          600: '#4b5563',
          700: '#374151',
          800: '#1f2937',
          900: '#111827',
        },
      },
      // 自定义间距（基于 4px 基数）
      spacing: {
        '18': '4.5rem',    // 72px
        '88': '22rem',     // 352px
        '128': '32rem',    // 512px
      },
      // 自定义圆角
      borderRadius: {
        '4xl': '2rem',     // 32px
        '5xl': '2.5rem',   // 40px
      },
      // 自定义阴影系统
      boxShadow: {
        'card': '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
        'card-hover': '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
        'nav': '0 1px 3px 0 rgba(0, 0, 0, 0.1)',
        'nav-scrolled': '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
      },
      // 自定义动画
      animation: {
        'fade-in': 'fadeIn 300ms ease-out',
        'slide-up': 'slideUp 300ms ease-out',
        'scale-in': 'scaleIn 200ms ease-out',
      },
      // 自定义关键帧
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { opacity: '0', transform: 'translateY(20px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        scaleIn: {
          '0%': { opacity: '0', transform: 'scale(0.95)' },
          '100%': { opacity: '1', transform: 'scale(1)' },
        },
      },
      // 最大宽度
      maxWidth: {
        'content': '896px',  // max-w-4xl，内容区域最大宽度
      },
    },
  },
  plugins: []
}
