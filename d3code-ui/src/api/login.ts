import request from '@/utils/request'
import { AxiosPromise } from 'axios'
import { LoginData, LoginResult, TenantInfo, VerifyCodeResult } from './types'
import { UserInfo } from '@/api/system/user/types'

// pc端固定客户端授权id
const clientId = import.meta.env.VITE_APP_CLIENT_ID

/**
 * @param data {LoginData}
 * @returns
 */
export function login(data: LoginData): AxiosPromise<LoginResult> {
  const params = {
    ...data,
    clientId: data.clientId || clientId,
    grantType: data.grantType || 'password',
  }
  return request({
    url: '/auth/login',
    headers: {
      isToken: false,
      isEncrypt: true,
      repeatSubmit: false,
    },
    method: 'post',
    data: params,
  })
}

// 注册方法
export function register(data: any) {
  const params = {
    ...data,
    clientId: clientId,
    grantType: 'password',
  }
  return request({
    url: '/auth/register',
    headers: {
      isToken: false,
      isEncrypt: true,
      repeatSubmit: false,
    },
    method: 'post',
    data: params,
  })
}

/**
 * 注销
 */
export function logout() {
  if (import.meta.env.VITE_APP_SSE === 'true') {
    request({
      url: '/resource/sse/close',
      method: 'get',
    })
  }
  return request({
    url: '/auth/logout',
    method: 'post',
  })
}

/**
 * 获取验证码
 */
export function getCodeImg(): AxiosPromise<VerifyCodeResult> {
  return request({
    url: '/auth/code',
    headers: {
      isToken: false,
    },
    method: 'get',
    timeout: 20000,
  })
}

// 获取用户详细信息
export function getInfo(): AxiosPromise<UserInfo> {
  return request({
    url: '/system/user/getInfo',
    method: 'get',
  })
}

// 获取租户列表
export function getTenantList(isToken: boolean): AxiosPromise<TenantInfo> {
  return request({
    url: '/auth/tenant/list',
    headers: {
      isToken: isToken,
    },
    method: 'get',
  })
}
