import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/customer/login'
  },
  // ========== 顾客端 ==========
  {
    path: '/customer/login',
    name: 'CustomerLogin',
    component: () => import('../views/customer/CustomerLogin.vue')
  },
  {
    path: '/menu',
    name: 'Menu',
    component: () => import('../views/customer/Menu.vue')
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('../views/customer/Cart.vue')
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('../views/customer/Orders.vue')
  },
  // ========== 管理员端 ==========
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('../views/admin/AdminLogin.vue')
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('../views/admin/AdminLayout.vue'),
    redirect: '/admin/dish',
    children: [
      {
        path: 'dish',
        name: 'DishManage',
        component: () => import('../views/admin/DishManage.vue')
      },
      {
        path: 'category',
        name: 'CategoryManage',
        component: () => import('../views/admin/CategoryManage.vue')
      },
      {
        path: 'order',
        name: 'OrderManage',
        component: () => import('../views/admin/OrderManage.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
