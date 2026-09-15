<template>
  <div class="order-manage">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="待付款" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadOrders">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="orderList" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="id" label="订单ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="tableId" label="餐桌ID" width="80" />
        <el-table-column label="总金额" width="120">
          <template #default="{ row }">
            <span style="color: #e6522c; font-weight: bold">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column prop="payTime" label="支付时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="showDetail(row)">查看明细</el-button>
            <el-button
              v-if="row.status === 0"
              size="small"
              type="success"
              @click="handleStatusChange(row, 1)"
            >
              确认支付
            </el-button>
            <el-button
              v-if="row.status === 1"
              size="small"
              type="primary"
              @click="handleStatusChange(row, 2)"
            >
              完成
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @change="loadOrders"
        />
      </div>
    </el-card>

    <!-- 订单明细弹窗 -->
    <el-dialog v-model="detailVisible" title="订单明细" width="500px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(currentOrder.status)" size="small">
            {{ statusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
      </el-descriptions>

      <el-table :data="orderDetails" style="margin-top: 16px" size="small">
        <el-table-column prop="dishName" label="菜品" />
        <el-table-column prop="price" label="单价">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="小计" width="100">
          <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderPage, getOrderDetail, updateOrderStatus } from '../../api'

const loading = ref(false)
const orderList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  status: null
})

// 明细弹窗
const detailVisible = ref(false)
const currentOrder = ref({})
const orderDetails = ref([])

const statusText = (status) => {
  const map = { 0: '待付款', 1: '已支付', 2: '已完成', 3: '已取消' }
  return map[status] || '未知'
}

const statusTagType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return map[status] || 'info'
}

// 加载订单
const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getOrderPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: searchForm.status
    })
    orderList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.status = null
  pageNum.value = 1
  loadOrders()
}

// 查看明细
const showDetail = async (row) => {
  currentOrder.value = row
  try {
    const res = await getOrderDetail(row.id)
    orderDetails.value = res.data?.details || []
    detailVisible.value = true
  } catch (e) {}
}

// 修改状态
const handleStatusChange = async (row, status) => {
  try {
    await updateOrderStatus(row.id, status)
    ElMessage.success('操作成功')
    loadOrders()
  } catch (e) {}
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.search-form {
  margin-bottom: 16px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
