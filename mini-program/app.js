/**
 * 小程序入口
 * 启动时自动微信静默登录
 */
const { silentLogin } = require('./utils/request')

App({
  onLaunch() {
    // 检查是否已有 token，没有则静默登录
    const token = wx.getStorageSync('token')
    if (!token) {
      this.doSilentLogin()
    }
  },

  /**
   * 静默登录（失败不阻塞使用）
   */
  doSilentLogin() {
    silentLogin().then(() => {
      console.log('静默登录成功')
    }).catch(err => {
      console.warn('静默登录失败', err)
    })
  },

  globalData: {
    tableId: null
  }
})
