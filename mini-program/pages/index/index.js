// pages/index/index.js - 首页：分类 + 菜品列表
const { get } = require('../../utils/request')
const auth = require('../../utils/auth')

Page({
  data: {
    categories: [],       // 分类列表
    activeCategoryId: 0,  // 当前选中分类ID
    dishes: [],           // 当前分类的菜品列表
    tableId: null,        // 桌号
    cartCount: 0          // 购物车数量
  },

  onLoad(options) {
    // 扫码进入时，解析二维码中的 tableId
    if (options.tableId) {
      auth.setTableId(options.tableId)
    }
    // 扫码场景（scanType）
    if (options.q) {
      const scene = decodeURIComponent(options.q)
      // 假设二维码内容格式：tableId=1
      const match = scene.match(/tableId=(\d+)/)
      if (match) {
        auth.setTableId(match[1])
      }
    }
    this.setData({ tableId: auth.getTableId() })
  },

  onShow() {
    this.loadCategories()
    this.loadCartCount()
  },

  /** 加载分类列表 */
  loadCategories() {
    get('/category/list').then(res => {
      const categories = res.data || []
      this.setData({ categories })
      if (categories.length > 0 && !this.data.activeCategoryId) {
        this.selectCategory(categories[0].id)
      }
    })
  },

  /** 选中分类，加载对应菜品 */
  selectCategory(e) {
    const categoryId = e.currentTarget.dataset.id
    this.setData({ activeCategoryId: categoryId })
    get('/dish/list', { categoryId }).then(res => {
      this.setData({ dishes: res.data || [] })
    })
  },

  /** 跳转到菜品详情 */
  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/dish-detail/dish-detail?id=${id}` })
  },

  /** 加入购物车 */
  addToCart(e) {
    const dishId = e.currentTarget.dataset.id
    const userId = auth.getUserId()
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    const { post } = require('../../utils/request')
    post('/shoppingCart', { userId, dishId }).then(() => {
      wx.showToast({ title: '已添加', icon: 'success' })
      this.loadCartCount()
    })
  },

  /** 加载购物车数量 */
  loadCartCount() {
    const userId = auth.getUserId()
    if (!userId) return
    get('/shoppingCart/list', { userId }).then(res => {
      const items = res.data || []
      const cartCount = items.reduce((sum, item) => sum + item.quantity, 0)
      this.setData({ cartCount })
    })
  },

  /** 下拉刷新 */
  onPullDownRefresh() {
    this.loadCategories()
    wx.stopPullDownRefresh()
  }
})
