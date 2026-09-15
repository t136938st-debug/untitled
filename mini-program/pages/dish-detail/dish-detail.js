// pages/dish-detail/dish-detail.js - 菜品详情页
const { get, post } = require('../../utils/request')
const auth = require('../../utils/auth')

Page({
  data: {
    dish: null
  },

  onLoad(options) {
    if (options.id) {
      this.loadDish(options.id)
    }
  },

  /** 加载菜品详情 */
  loadDish(id) {
    get(`/dish/${id}`).then(res => {
      this.setData({ dish: res.data })
    })
  },

  /** 加入购物车 */
  addToCart() {
    const dish = this.data.dish
    const userId = auth.getUserId()
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    post('/shoppingCart', { userId, dishId: dish.id }).then(() => {
      wx.showToast({ title: '已加入购物车', icon: 'success' })
    })
  }
})
