/**
 * 购物车页：加减数量 / 删除 / 清空 / 结算
 */
const { get, post } = require('../../utils/request')
const { API } = require('../../utils/config')

Page({
  data: {
    items: [],
    totalCount: 0,
    totalAmount: '0.00'
  },

  onShow() {
    this.loadCart()
  },

  /** 加载购物车 */
  loadCart() {
    get(API.CART_LIST).then(res => {
      const data = res.data || {}
      this.setData({
        items: data.items || [],
        totalCount: data.totalCount || 0,
        totalAmount: (data.totalAmount || 0).toFixed ? (data.totalAmount || 0).toFixed(2) : '0.00'
      })
    }).catch(() => {})
  },

  /** 增加数量 */
  onPlus(e) {
    const item = e.currentTarget.dataset.item
    post(API.CART_UPDATE, { cartId: item.id, quantity: item.quantity + 1 })
      .then(() => this.loadCart())
      .catch(() => {})
  },

  /** 减少数量 */
  onMinus(e) {
    const item = e.currentTarget.dataset.item
    if (item.quantity <= 1) {
      // 数量为 1 时删除
      this.onRemove(e)
      return
    }
    post(API.CART_UPDATE, { cartId: item.id, quantity: item.quantity - 1 })
      .then(() => this.loadCart())
      .catch(() => {})
  },

  /** 删除单条 */
  onRemove(e) {
    const item = e.currentTarget.dataset.item
    wx.showModal({
      title: '提示',
      content: '确定删除「' + item.dishName + '」？',
      success: (res) => {
        if (res.confirm) {
          post(API.CART_REMOVE, { cartId: item.id })
            .then(() => this.loadCart())
            .catch(() => {})
        }
      }
    })
  },

  /** 清空购物车 */
  onClear() {
    if (this.data.items.length === 0) return
    wx.showModal({
      title: '提示',
      content: '确定清空购物车？',
      success: (res) => {
        if (res.confirm) {
          post(API.CART_CLEAR)
            .then(() => this.loadCart())
            .catch(() => {})
        }
      }
    })
  },

  /** 去结算 */
  goConfirm() {
    if (this.data.items.length === 0) {
      wx.showToast({ title: '购物车为空', icon: 'none' })
      return
    }
    wx.navigateTo({ url: '/pages/order-confirm/order-confirm' })
  }
})
