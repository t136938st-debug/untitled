// pages/order-detail/order-detail.js - 订单详情页
const { get, post } = require('../../utils/request')

Page({
  data: {
    order: null,
    details: [],
    statusMap: { 0: '待付款', 1: '已支付', 2: '已完成', 3: '已取消' },
    statusColor: { 0: '#faad14', 1: '#1890ff', 2: '#52c41a', 3: '#999' }
  },

  onLoad(options) {
    if (options.id) {
      this.loadOrderDetail(options.id)
    }
  },

  /** 加载订单详情 */
  loadOrderDetail(id) {
    get(`/order/${id}`).then(res => {
      this.setData({
        order: res.data.order,
        details: res.data.details || []
      })
    })
  },

  /** 模拟支付 */
  simPay() {
    const order = this.data.order
    wx.showLoading({ title: '支付中...' })
    post('/order/simPay', { orderId: order.id }).then(() => {
      wx.hideLoading()
      wx.showToast({ title: '支付成功', icon: 'success' })
      this.loadOrderDetail(order.id)
    }).catch(() => {
      wx.hideLoading()
    })
  }
})
