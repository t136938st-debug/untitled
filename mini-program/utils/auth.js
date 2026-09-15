/**
 * 认证工具模块
 * 管理 token 和用户信息
 */

/**
 * 获取当前用户ID
 */
function getUserId() {
  const userInfo = wx.getStorageSync('userInfo')
  return userInfo ? userInfo.userId : null
}

/**
 * 获取当前用户信息
 */
function getUserInfo() {
  return wx.getStorageSync('userInfo') || {}
}

/**
 * 判断是否已登录
 */
function isLoggedIn() {
  return !!wx.getStorageSync('token')
}

/**
 * 获取桌号（扫码进入时存储）
 */
function getTableId() {
  return wx.getStorageSync('tableId') || getApp().globalData.tableId
}

/**
 * 设置桌号
 */
function setTableId(tableId) {
  wx.setStorageSync('tableId', tableId)
  getApp().globalData.tableId = tableId
}

module.exports = { getUserId, getUserInfo, isLoggedIn, getTableId, setTableId }
