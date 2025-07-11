import en from 'element-plus/dist/locale/en.mjs'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { defineStore } from 'pinia'
import { useStorage } from '@vueuse/core'
import { computed, reactive, ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebarStatus = useStorage('sidebarStatus', '1')
  const sidebar = reactive({
    opened: sidebarStatus.value ? !!+sidebarStatus.value : true,
    withoutAnimation: false,
    hide: false,
  })
  const device = ref<string>('desktop')
  const size = useStorage<'large' | 'default' | 'small'>('size', 'default')

  // 语言
  const language = useStorage('language', 'zh_CN')
  const languageObj: any = {
    en_US: en,
    zh_CN: zhCn,
  }
  const locale = computed(() => {
    return languageObj[language.value]
  })

  const toggleSideBar = (withoutAnimation: boolean) => {
    if (sidebar.hide) {
      return false
    }

    sidebar.opened = !sidebar.opened
    sidebar.withoutAnimation = withoutAnimation
    if (sidebar.opened) {
      sidebarStatus.value = '1'
    } else {
      sidebarStatus.value = '0'
    }
  }

  const closeSideBar = ({ withoutAnimation }: any): void => {
    sidebarStatus.value = '0'
    sidebar.opened = false
    sidebar.withoutAnimation = withoutAnimation
  }
  const toggleDevice = (d: string): void => {
    device.value = d
  }
  const setSize = (s: 'large' | 'default' | 'small'): void => {
    size.value = s
  }
  const toggleSideBarHide = (status: boolean): void => {
    sidebar.hide = status
  }

  const changeLanguage = (val: string): void => {
    language.value = val
  }

  return {
    device,
    sidebar,
    language,
    locale,
    size,
    changeLanguage,
    toggleSideBar,
    closeSideBar,
    toggleDevice,
    setSize,
    toggleSideBarHide,
  }
})
