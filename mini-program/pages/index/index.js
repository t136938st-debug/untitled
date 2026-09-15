/**
 * 首页：左侧分类导航 + 右侧菜品列表 + 悬浮购物车入口
 */
const { get, post } = require('../../utils/request')
const { API } = require('../../utils/config')

Page({
  data: {
    categories: [],
    dishes: [],
    currentCategoryId: 0,  // 0 = 全部
    cartCount: 0,          // 购物车商品总数
    tableId: null
  },

  onLoad(options) {
    // 解析扫码携带的 tableId
    let tableId = options.tableId || null
    // 扫码场景：options.q 包含编码后的 URL
    if (!tableId && options.q) {
      const q = decodeURIComponent(options.q)
      const match = q.match(/tableId=(\d+)/)
      if (match) tableId = match[1]
    }
    // 小程序码 scene 场景（限制 32 字符，直接传 tableId 值）
    if (!tableId && options.scene) {
      tableId = decodeURIComponent(options.scene)
    }
    if (tableId) {
      this.setData({ tableId: Number(tableId) })
      wx.setStorageSync('tableId', Number(tableId))
    }

    this.loadCategories()
    this.loadDishes()
    this.loadCartCount()
  },

  onShow() {
    // 每次显示时刷新购物车数量（从其他页面返回时更新）
    this.loadCartCount()
  },

  /** 加载分类列表 */
  loadCategories() {
    get(API.CATEGORY_LIST).then(res => {
      this.setData({ categories: res.data || [] })
    })
  },

  /** 加载菜品列表 */
  loadDishes() {
    const params = {}
    if (this.data.currentCategoryId > 0) {
      params.categoryId = this.data.currentCategoryId
    } else {
      params.categoryId = 0
    }
    get(API.DISH_LIST, params).then(res => {
      this.setData({ dishes: res.data || [] })
    })
  },

  /** 加载购物车数量 */
  loadCartCount() {
    get(API.CART_LIST).then(res => {
      if (res.data) {
        this.setData({ cartCount: res.data.totalCount || 0 })
      }
    }).catch(() => {})
  },

  /** 切换分类 */
  onCategoryTap(e) {
    const id = e.currentTarget.dataset.id
    this.setData({ currentCategoryId: id })
    this.loadDishes()
  },

  /** 点击菜品 → 跳转详情 */
  onDishTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/dish-detail/dish-detail?id=' + id })
  },

  /** 快速加入购物车 */
  onAddCart(e) {
    const dish = e.currentTarget.dataset.dish
    post(API.CART_ADD, { dishId: dish.id, quantity: 1 }).then(() => {
      wx.showToast({ title: '已加入购物车', icon: 'success' })
      this.loadCartCount()
    }).catch(() => {})
  },

  /** 跳转购物车 */
  goCart() {
    wx.switchTab({ url: '/pages/cart/cart' })
  }
})
