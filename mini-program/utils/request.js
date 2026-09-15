/**
 * 网络请求封装
 * 自动携带 JWT token，统一处理错误
 */

const BASE_URL = 'http://localhost:8080'

/**
 * 封装 wx.request
 * @param {string} url    - 请求路径（不含域名）
 * @param {string} method - 请求方法
 * @param {object} data   - 请求参数
 * @returns Promise
 */
function request(url, method = 'GET', data = {}) {
  return new Promise((resolve, reject) => {
    // 从本地缓存获取 token
    const token = wx.getStorageSync('token')

    wx.request({
      url: BASE_URL + url,
      method: method,
      data: data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? ('Bearer ' + token) : ''
      },
      success(res) {
        if (res.statusCode === 200 && res.data) {
          const result = res.data
          if (result.code === 200) {
            resolve(result)
          } else if (result.code === 401) {
            // token 过期或无效，重新登录
            wx.removeStorageSync('token')
            getApp().silentLogin()
            reject(result)
          } else {
            wx.showToast({ title: result.message || '请求失败', icon: 'none' })
            reject(result)
          }
        } else {
          wx.showToast({ title: '网络异常', icon: 'none' })
          reject(res)
        }
      },
      fail(err) {
        wx.showToast({ title: '网络连接失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

// 快捷方法
const get = (url, data) => request(url, 'GET', data)
const post = (url, data) => request(url, 'POST', data)
const put = (url, data) => request(url, 'PUT', data)
const del = (url, data) => request(url, 'DELETE', data)

module.exports = { request, get, post, put, del, BASE_URL }
