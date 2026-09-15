/**
 * 网络请求封装
 * - 自动从 storage 取 token 放入 header
 * - 401 时自动静默登录并重试原请求
 */
const { BASE_URL } = require('./config')

/**
 * 静默登录（wx.login → 后端换 token）
 * 返回 Promise
 */
function silentLogin() {
  return new Promise((resolve, reject) => {
    wx.login({
      success(loginRes) {
        wx.request({
          url: BASE_URL + '/wx/login',
          method: 'POST',
          header: { 'Content-Type': 'application/json' },
          data: { code: loginRes.code },
          success(res) {
            if (res.data && res.data.code === 200 && res.data.data) {
              const token = res.data.data.token
              wx.setStorageSync('token', token)
              if (res.data.data.userId) {
                wx.setStorageSync('userId', res.data.data.userId)
              }
              resolve(token)
            } else {
              reject(new Error((res.data && res.data.message) || '登录失败'))
            }
          },
          fail(err) {
            reject(err)
          }
        })
      },
      fail(err) {
        reject(err)
      }
    })
  })
}

/**
 * 通用请求方法
 * @param {string} url   - 接口路径（如 /cart/list）
 * @param {string} method - HTTP 方法
 * @param {object} data   - 请求数据
 * @param {boolean} _retry - 内部标记，是否为重试请求
 */
function request(url, method, data, _retry) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token') || ''
    wx.request({
      url: BASE_URL + url,
      method: method,
      data: data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? ('Bearer ' + token) : ''
      },
      success(res) {
        if (res.data && res.data.code === 200) {
          resolve(res.data)
        } else if (res.data && res.data.code === 401 && !_retry) {
          // token 失效，自动重新登录后重试
          silentLogin().then(() => {
            request(url, method, data, true).then(resolve).catch(reject)
          }).catch(() => {
            reject(new Error('登录失效，请重新打开小程序'))
          })
        } else {
          const msg = (res.data && res.data.message) || '请求失败'
          wx.showToast({ title: msg, icon: 'none' })
          reject(new Error(msg))
        }
      },
      fail(err) {
        wx.showToast({ title: '网络异常', icon: 'none' })
        reject(err)
      }
    })
  })
}

// 快捷方法
function get(url, data) { return request(url, 'GET', data) }
function post(url, data) { return request(url, 'POST', data || {}) }
function put(url, data) { return request(url, 'PUT', data || {}) }
function del(url, data) { return request(url, 'DELETE', data) }

module.exports = {
  get, post, put, del,
  silentLogin,
  request
}
