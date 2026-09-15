/**
 * 菜品详情页：选择数量、加入购物车
 */
const { get, post } = require('../../utils/request')
const { API } = require('../../utils/config')

Page({
  data: {
    dish: null,
    quantity: 1
  },

  onLoad(options) {
    if (options.id) {
      this.loadDish(options.id)
    }
  },

  /** 加载菜品详情 */
  loadDish(id) {
    get(API.DISH_DETAIL, { id }).then(res => {
      this.setData({ dish: res.data })
    })
  },

  /** 数量减 */
  onMinus() {
    if (this.data.quantity > 1) {
      this.setData({ quantity: this.data.quantity - 1 })
    }
  },

  /** 数量加 */
  onPlus() {
    this.setData({ quantity: this.data.quantity + 1 })
  },

  /** 加入购物车 */
  addToCart() {
    const { dish, quantity } = this.data
    if (!dish) return
    post(API.CART_ADD, { dishId: dish.id, quantity }).then(() => {
      wx.showToast({ title: '已加入购物车', icon: 'success' })
      setTimeout(() => {
        wx.navigateBack()
      }, 800)
    }).catch(() => {})
  }
})
