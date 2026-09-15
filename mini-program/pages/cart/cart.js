// pages/cart/cart.js - 购物车页面
const { get, put, del } = require('../../utils/request')
const auth = require('../../utils/auth')

Page({
  data: {
    cartList: [],
    totalPrice: '0.00'
  },

  onShow() {
    this.loadCart()
  },

  /** 加载购物车列表 */
  loadCart() {
    const userId = auth.getUserId()
    if (!userId) return
    get('/shoppingCart/list', { userId }).then(res => {
      const cartList = res.data || []
      const totalPrice = cartList.reduce((sum, item) => {
        return sum + item.price * item.quantity
      }, 0).toFixed(2)
      this.setData({ cartList, totalPrice })
    })
  },

  /** 修改数量 */
  changeQuantity(e) {
    const { index } = e.currentTarget.dataset
    const item = this.data.cartList[index]
    const newQuantity = e.detail

    if (newQuantity <= 0) {
      // 数量为0，删除
      this.removeItem(item)
      return
    }

    put('/shoppingCart', {
      userId: auth.getUserId(),
      dishId: item.dishId,
      quantity: newQuantity
    }).then(() => {
      item.quantity = newQuantity
      const cartList = this.data.cartList
      const totalPrice = cartList.reduce((sum, i) => sum + i.price * i.quantity, 0).toFixed(2)
      this.setData({ cartList, totalPrice })
    })
  },

  /** 删除购物车项 */
  removeItem(item) {
    put('/shoppingCart', {
      userId: auth.getUserId(),
      dishId: item.dishId,
      quantity: 0
    }).then(() => {
      wx.showToast({ title: '已移除', icon: 'success' })
      this.loadCart()
    })
  },

  /** 清空购物车 */
  clearCart() {
    wx.showModal({
      title: '提示',
      content: '确定清空购物车吗？',
      success: (res) => {
        if (res.confirm) {
          del('/shoppingCart/clean', { userId: auth.getUserId() }).then(() => {
            wx.showToast({ title: '已清空', icon: 'success' })
            this.setData({ cartList: [], totalPrice: '0.00' })
          })
        }
      }
    })
  },

  /** 去下单 */
  goConfirm() {
    if (this.data.cartList.length === 0) {
      wx.showToast({ title: '购物车为空', icon: 'none' })
      return
    }
    wx.navigateTo({ url: '/pages/confirm-order/confirm-order' })
  }
})
