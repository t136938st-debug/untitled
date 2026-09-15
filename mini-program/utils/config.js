/**
 * 全局配置：后端地址 + 接口路径 + 状态字典
 * 对接后端时主要改这里的 BASE_URL
 */

// 后端地址（本地调试用 http://localhost:8080/api）
// 真机预览需改为公网 HTTPS 域名，并在小程序后台配置 request 合法域名
const BASE_URL = 'http://localhost:8080/api'

// 接口路径
const API = {
  // 登录
  WX_LOGIN: '/wx/login',

  // 分类
  CATEGORY_LIST: '/category/list',

  // 菜品
  DISH_LIST: '/dish/list',
  DISH_DETAIL: '/dish/detail',

  // 购物车
  CART_ADD: '/cart/add',
  CART_LIST: '/cart/list',
  CART_UPDATE: '/cart/update',
  CART_REMOVE: '/cart/remove',
  CART_CLEAR: '/cart/clear',

  // 订单
  ORDER_CREATE: '/order/create',
  ORDER_SIM_PAY: '/order/simPay',
  ORDER_CANCEL: '/order/cancel',
  ORDER_LIST: '/order/list',
  ORDER_DETAIL: '/order/detail'   // 使用时拼接 /order/detail/{id}
}

// 订单状态字典
const ORDER_STATUS = {
  0: { text: '待付款', color: '#ff9800' },
  1: { text: '已支付', color: '#4caf50' },
  2: { text: '已完成', color: '#2196f3' },
  3: { text: '已取消', color: '#999' }
}

module.exports = {
  BASE_URL,
  API,
  ORDER_STATUS
}
