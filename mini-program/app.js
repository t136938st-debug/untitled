// app.js - 小程序入口文件
App({
  onLaunch() {
    // 尝试从本地缓存恢复登录态
    const token = wx.getStorageSync('token')
    if (!token) {
      // 未登录，静默登录
      this.silentLogin()
    }
  },

  /**
   * 静默登录：调用 wx.login 获取 code，换取后端 token
   */
  silentLogin() {
    wx.login({
      success: (res) => {
        if (res.code) {
          wx.request({
            url: 'http://localhost:8080/wx/login',
            method: 'POST',
            data: { code: res.code },
            success: (resp) => {
              if (resp.data && resp.data.code === 200) {
                const data = resp.data.data
                wx.setStorageSync('token', data.token)
                wx.setStorageSync('userInfo', data)
                console.log('静默登录成功', data)
              } else {
                console.error('静默登录失败', resp.data)
              }
            }
          })
        }
      }
    })
  },

  globalData: {
    userInfo: null,
    tableId: null // 扫码进入时携带的桌号
  }
})
