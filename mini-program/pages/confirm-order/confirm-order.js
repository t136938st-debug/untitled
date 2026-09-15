// pages/confirm-order/confirm-order.js - 确认下单页（含模拟支付）
const { get, post } = require('../../utils/request')
const auth = require('../../utils/auth')

Page({
  data: {
    cartList: [],
    totalPrice: '0.00',
    tableId: null,
    remark: '',
    orderId: null,
    submitting: false,
    paying: false
  },

  onLoad() {
    this.setData({ tableId: auth.getTableId() })
    this.loadCart()
  },

  /** 加载购物车数据用于确认 */
  loadCart() {
    const userId = auth.getUserId()
    get('/shoppingCart/list', { userId }).then(res => {
      const cartList = res.data || []
      const totalPrice = cartList.reduce((sum, item) => {
        return sum + item.price * item.quantity
      }, 0).toFixed(2)
      this.setData({ cartList, totalPrice })
    })
  },

  /** 备注输入 */
  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  /** 提交订单 */
  submitOrder() {
    if (this.data.cartList.length === 0) {
      wx.showToast({ title: '购物车为空', icon: 'none' })
      return
    }
    if (!this.data.tableId) {
      wx.showToast({ title: '请先选择餐桌', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    post('/order', {
      userId: auth.getUserId(),
      tableId: this.data.tableId,
      remark: this.data.remark
    }).then(res => {
      const orderId = res.data
      this.setData({ orderId, submitting: false })
      wx.showModal({
        title: '下单成功',
        content: `订单已创建，是否立即模拟支付？`,
        confirmText: '模拟支付',
        cancelText: '稍后支付',
        success: (modalRes) => {
          if (modalRes.confirm) {
            this.simPay(orderId)
          } else {
            wx.redirectTo({ url: '/pages/orders/orders' })
          }
        }
      })
    }).catch(() => {
      this.setData({ submitting: false })
    })
  },

  /** 模拟支付 */
  simPay(orderId) {
    this.setData({ paying: true })
    wx.showLoading({ title: '支付中...' })

    post('/order/simPay', { orderId }).then(() => {
      wx.hideLoading()
      this.setData({ paying: false })
      wx.showToast({ title: '支付成功！', icon: 'success' })
      setTimeout(() => {
        wx.redirectTo({ url: `/pages/order-detail/order-detail?id=${orderId}` })
      }, 1500)
    }).catch(() => {
      wx.hideLoading()
      this.setData({ paying: false })
    })
  }
})
