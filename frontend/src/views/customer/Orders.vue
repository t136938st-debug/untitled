<template>
  <div class="orders-page">
    <!-- 顶部 -->
    <div class="header">
      <el-button text @click="$router.push('/menu')">
        <el-icon><ArrowLeft /></el-icon> 返回点餐
      </el-button>
      <h2>我的订单</h2>
      <span></span>
    </div>

    <!-- 订单列表 -->
    <div class="orders-content">
      <div v-if="orderList.length > 0">
        <div v-for="order in orderList" :key="order.id" class="order-card">
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <el-tag :type="statusTagType(order.status)" size="small">
              {{ statusText(order.status) }}
            </el-tag>
          </div>

          <div class="order-info">
            <div class="info-row">
              <span>下单时间：{{ order.createTime }}</span>
              <span>总金额：<b class="amount">¥{{ order.totalAmount }}</b></span>
            </div>
            <div class="info-row" v-if="order.remark">
              <span>备注：{{ order.remark }}</span>
            </div>
          </div>

          <!-- 订单明细 -->
          <div v-if="order._details" class="order-details">
            <div v-for="detail in order._details" :key="detail.id" class="detail-item">
              <span>{{ detail.dishName }}</span>
              <span>x{{ detail.quantity }}</span>
              <span>¥{{ (detail.price * detail.quantity).toFixed(2) }}</span>
            </div>
          </div>

          <div class="order-actions">
            <el-button
              v-if="order.status === 0"
              type="primary"
              size="small"
              @click="handlePay(order)"
            >
              去支付
            </el-button>
            <el-button
              v-if="order.status === 0"
              size="small"
              @click="handleCancel(order)"
            >
              取消订单
            </el-button>
            <el-button
              v-if="order.status === 1"
              type="success"
              size="small"
              @click="handleComplete(order)"
            >
              确认完成
            </el-button>
            <el-button size="small" text type="info" @click="toggleDetails(order)">
              {{ order._showDetails ? '收起明细' : '查看明细' }}
            </el-button>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无订单">
        <el-button type="primary" @click="$router.push('/menu')">去点餐</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderPage, getOrderDetail, updateOrderStatus } from '../../api'

const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
const orderList = ref([])

// 状态文本
const statusText = (status) => {
  const map = { 0: '待付款', 1: '已支付', 2: '已完成', 3: '已取消' }
  return map[status] || '未知'
}

// 状态标签类型
const statusTagType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return map[status] || 'info'
}

// 加载订单列表
const loadOrders = async () => {
  try {
    const res = await getOrderPage({ userId: userInfo.id, pageNum: 1, pageSize: 50 })
    orderList.value = (res.data?.records || []).map(o => ({ ...o, _showDetails: false, _details: [] }))
  } catch (e) {}
}

// 展开/收起明细
const toggleDetails = async (order) => {
  if (!order._showDetails && order._details.length === 0) {
    try {
      const res = await getOrderDetail(order.id)
      order._details = res.data?.details || []
    } catch (e) {}
  }
  order._showDetails = !order._showDetails
}

// 支付
const handlePay = async (order) => {
  try {
    await ElMessageBox.confirm('确认支付该订单？', '支付确认')
    await updateOrderStatus(order.id, 1)
    ElMessage.success('支付成功')
    loadOrders()
  } catch (e) {}
}

// 取消
const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' })
    await updateOrderStatus(order.id, 3)
    ElMessage.success('已取消')
    loadOrders()
  } catch (e) {}
}

// 完成
const handleComplete = async (order) => {
  try {
    await updateOrderStatus(order.id, 2)
    ElMessage.success('订单已完成')
    loadOrders()
  } catch (e) {}
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header h2 {
  font-size: 18px;
  color: #333;
}

.orders-content {
  max-width: 600px;
  margin: 20px auto;
  padding: 0 16px;
}

.order-card {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.order-no {
  font-size: 13px;
  color: #666;
}

.order-info .info-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #666;
  margin-bottom: 6px;
}

.amount {
  color: #e6522c;
  font-size: 16px;
}

.order-details {
  background: #fafafa;
  border-radius: 6px;
  padding: 10px;
  margin-top: 10px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #666;
  padding: 4px 0;
}

.order-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  justify-content: flex-end;
}
</style>
