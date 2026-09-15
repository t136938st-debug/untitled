// pages/orders/orders.js - 我的订单页面
const { get, put } = require('../../utils/request')
const auth = require('../../utils/auth')

Page({
  data: {
    orders: [],
    statusMap: { 0: '待付款', 1: '已支付', 2: '已完成', 3: '已取消' },
    statusColor: { 0: '#faad14', 1: '#1890ff', 2: '#52c41a', 3: '#999' }
  },

  onShow() {
    this.loadOrders()
  },

  /** 加载订单列表 */
  loadOrders() {
    const userId = auth.getUserId()
    get('/order/page', { userId, pageNum: 1, pageSize: 50 }).then(res => {
      const orders = res.data?.records || []
      this.setData({ orders })
    })
  },

  /** 查看订单详情 */
  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/order-detail/order-detail?id=${id}` })
  },

  /** 模拟支付 */
  simPay(e) {
    const { id } = e.currentTarget.dataset
    wx.showLoading({ title: '支付中...' })
    const { post } = require('../../utils/request')
    post('/order/simPay', { orderId: id }).then(() => {
      wx.hideLoading()
      wx.showToast({ title: '支付成功', icon: 'success' })
      this.loadOrders()
    }).catch(() => {
      wx.hideLoading()
    })
  },

  /** 取消订单 */
  cancelOrder(e) {
    const { id } = e.currentTarget.dataset
    wx.showModal({
      title: '提示',
      content: '确定取消该订单吗？',
      success: (res) => {
        if (res.confirm) {
          put('/order/status', { orderId: id, status: 3 }).then(() => {
            wx.showToast({ title: '已取消', icon: 'success' })
            this.loadOrders()
          })
        }
      }
    })
  }
})
