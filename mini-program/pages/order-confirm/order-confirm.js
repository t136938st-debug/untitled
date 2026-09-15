/**
 * 确认下单页：桌号 + 备注 + 提交订单 + 模拟支付
 */
const { get, post } = require('../../utils/request')
const { API } = require('../../utils/config')

Page({
  data: {
    tableId: null,
    remark: '',
    cartItems: [],
    totalAmount: '0.00',
    submitting: false
  },

  onLoad() {
    const tableId = wx.getStorageSync('tableId') || null
    this.setData({ tableId })
    this.loadCart()
  },

  /** 加载购物车数据展示 */
  loadCart() {
    get(API.CART_LIST).then(res => {
      const data = res.data || {}
      this.setData({
        cartItems: data.items || [],
        totalAmount: (data.totalAmount || 0).toFixed ? (data.totalAmount || 0).toFixed(2) : '0.00'
      })
    })
  },

  /** 输入备注 */
  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  /** 提交订单 */
  submitOrder() {
    if (this.data.submitting) return
    if (this.data.cartItems.length === 0) {
      wx.showToast({ title: '购物车为空', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    const params = { remark: this.data.remark }
    if (this.data.tableId) {
      params.tableId = this.data.tableId
    }

    post(API.ORDER_CREATE, params).then(res => {
      const orderData = res.data
      this.setData({ submitting: false })

      wx.showModal({
        title: '下单成功',
        content: '订单号：' + orderData.orderNo + '\n是否立即支付？',
        confirmText: '立即支付',
        cancelText: '稍后支付',
        success: (modalRes) => {
          if (modalRes.confirm) {
            this.simPay(orderData.id)
          } else {
            // 跳转订单列表
            wx.switchTab({ url: '/pages/order-list/order-list' })
          }
        }
      })
    }).catch(() => {
      this.setData({ submitting: false })
    })
  },

  /** 模拟支付 */
  simPay(orderId) {
    wx.showLoading({ title: '支付中...' })
    post(API.ORDER_SIM_PAY, { orderId }).then(() => {
      wx.hideLoading()
      wx.showToast({ title: '支付成功', icon: 'success' })
      setTimeout(() => {
        wx.navigateTo({
          url: '/pages/order-detail/order-detail?id=' + orderId
        })
      }, 800)
    }).catch(() => {
      wx.hideLoading()
    })
  }
})
