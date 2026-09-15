import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  (error) => {
    ElMessage.error('网络异常，请稍后重试')
    return Promise.reject(error)
  }
)

// ========== 用户相关接口 ==========

/** 顾客登录 */
export function customerLogin(data) {
  return request.post('/user/login', data)
}

/** 管理员登录 */
export function adminLogin(data) {
  return request.post('/user/admin/login', data)
}

// ========== 分类相关接口 ==========

/** 查询分类列表 */
export function getCategoryList() {
  return request.get('/category/list')
}

/** 新增分类 */
export function addCategory(data) {
  return request.post('/category', data)
}

/** 修改分类 */
export function updateCategory(data) {
  return request.put('/category', data)
}

/** 删除分类 */
export function deleteCategory(id) {
  return request.delete(`/category/${id}`)
}

// ========== 菜品相关接口 ==========

/** 分页查询菜品 */
export function getDishPage(params) {
  return request.get('/dish/page', { params })
}

/** 根据分类查询菜品 */
export function getDishListByCategory(categoryId) {
  return request.get('/dish/list', { params: { categoryId } })
}

/** 新增菜品 */
export function addDish(data) {
  return request.post('/dish', data)
}

/** 修改菜品 */
export function updateDish(data) {
  return request.put('/dish', data)
}

/** 删除菜品 */
export function deleteDish(id) {
  return request.delete(`/dish/${id}`)
}

/** 菜品上下架 */
export function updateDishStatus(id, status) {
  return request.post(`/dish/status/${id}`, null, { params: { status } })
}

// ========== 购物车相关接口 ==========

/** 查询购物车 */
export function getCartList(userId) {
  return request.get('/shoppingCart/list', { params: { userId } })
}

/** 加入购物车 */
export function addToCart(data) {
  return request.post('/shoppingCart', data)
}

/** 修改购物车数量 */
export function updateCartQuantity(data) {
  return request.put('/shoppingCart', data)
}

/** 清空购物车 */
export function clearCart(userId) {
  return request.delete('/shoppingCart/clean', { params: { userId } })
}

// ========== 订单相关接口 ==========

/** 创建订单 */
export function createOrder(data) {
  return request.post('/order', data)
}

/** 查询订单列表 */
export function getOrderPage(params) {
  return request.get('/order/page', { params })
}

/** 查询订单详情 */
export function getOrderDetail(id) {
  return request.get(`/order/${id}`)
}

/** 修改订单状态 */
export function updateOrderStatus(orderId, status) {
  return request.put('/order/status', null, { params: { orderId, status } })
}

// ========== 餐桌相关接口 ==========

/** 查询餐桌列表 */
export function getTableList() {
  return request.get('/diningTable/list')
}

/** 查询空闲餐桌 */
export function getFreeTableList() {
  return request.get('/diningTable/free')
}

export default request
