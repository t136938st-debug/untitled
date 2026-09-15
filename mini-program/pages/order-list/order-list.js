/**
 * 我的订单页：按状态筛选 + 订单列表
 */
const { get } = require('../../utils/request')
const { API, ORDER_STATUS } = require('../../utils/config')

Page({
  data: {
    tabs: [
      { label: '全部', value: -1 },
      { label: '待付款', value: 0 },
      { label: '已支付', value: 1 },
      { label: '已完成', value: 2 },
      { label: '已取消', value: 3 }
    ],
    currentTab: -1,
    orders: [],
    ORDER_STATUS: ORDER_STATUS
  },

  onShow() {
    this.loadOrders()
  },

  /** 切换标签 */
  onTabTap(e) {
    const value = e.currentTarget.dataset.value
    this.setData({ currentTab: value })
    this.loadOrders()
  },

  /** 加载订单列表 */
  loadOrders() {
    const params = {}
    if (this.data.currentTab >= 0) {
      params.status = this.data.currentTab
    } else {
      params.status = -1
    }
    get(API.ORDER_LIST, params).then(res => {
      const orders = res.data || []
      // 给每个订单加上状态文字和颜色
      orders.forEach(o => {
        const s = ORDER_STATUS[o.status] || { text: '未知', color: '#999' }
        o.statusText = s.text
        o.statusColor = s.color
      })
      this.setData({ orders })
    }).catch(() => {})
  },

  /** 跳转订单详情 */
  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + id })
  }
})
