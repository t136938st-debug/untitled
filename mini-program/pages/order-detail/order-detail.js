/**
 * 订单详情页：状态卡片 + 菜品明细 + 模拟支付 / 取消订单
 */
const { get, post } = require('../../utils/request')
const { API, ORDER_STATUS } = require('../../utils/config')

Page({
  data: {
    order: null,
    ORDER_STATUS: ORDER_STATUS
  },

  onLoad(options) {
    if (options.id) {
      this.orderId = options.id
      this.loadDetail()
    }
  },

  onShow() {
    if (this.orderId) {
      this.loadDetail()
    }
  },

  /** 加载订单详情 */
  loadDetail() {
    get(API.ORDER_DETAIL + '/' + this.orderId).then(res => {
      const order = res.data
      const s = ORDER_STATUS[order.status] || { text: '未知', color: '#999' }
      order.statusText = s.text
      order.statusColor = s.color
      this.setData({ order })
    }).catch(() => {})
  },

  /** 模拟支付 */
  simPay() {
    wx.showLoading({ title: '支付中...' })
    post(API.ORDER_SIM_PAY, { orderId: this.orderId }).then(() => {
      wx.hideLoading()
      wx.showToast({ title: '支付成功', icon: 'success' })
      this.loadDetail()
    }).catch(() => {
      wx.hideLoading()
    })
  },

  /** 取消订单 */
  cancelOrder() {
    wx.showModal({
      title: '提示',
      content: '确定取消此订单？',
      success: (res) => {
        if (res.confirm) {
          post(API.ORDER_CANCEL, { orderId: this.orderId }).then(() => {
            wx.showToast({ title: '已取消', icon: 'success' })
            this.loadDetail()
          }).catch(() => {})
        }
      }
    })
  },

  /** 返回 */
  goBack() {
    wx.navigateBack()
  }
})
