<template>
  <div class="cart-page">
    <!-- 顶部 -->
    <div class="header">
      <el-button text @click="$router.push('/menu')">
        <el-icon><ArrowLeft /></el-icon> 返回点餐
      </el-button>
      <h2>我的购物车</h2>
      <el-button type="danger" plain size="small" @click="handleClear" :disabled="cartList.length === 0">
        清空购物车
      </el-button>
    </div>

    <!-- 购物车列表 -->
    <div class="cart-content">
      <div v-if="cartList.length > 0">
        <div v-for="item in cartList" :key="item.id" class="cart-item">
          <el-image :src="item.dishImage || defaultImg" fit="cover" class="item-img" />
          <div class="item-info">
            <div class="item-name">{{ item.dishName }}</div>
            <div class="item-price">¥{{ item.price }}</div>
          </div>
          <div class="item-actions">
            <el-input-number
              v-model="item.quantity"
              :min="0"
              :max="99"
              size="small"
              @change="(val) => handleQuantityChange(item, val)"
            />
          </div>
        </div>

        <!-- 合计 & 下单 -->
        <div class="cart-footer">
          <div class="total">
            合计：<span class="total-price">¥{{ totalPrice }}</span>
          </div>
          <el-button type="primary" size="large" round @click="handleSubmitOrder" :loading="submitting">
            提交订单
          </el-button>
        </div>
      </div>

      <el-empty v-else description="购物车是空的，快去点餐吧~">
        <el-button type="primary" @click="$router.push('/menu')">去点餐</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCartList, updateCartQuantity, clearCart, createOrder } from '../../api'

const router = useRouter()
const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
const defaultImg = 'https://cube.elemecdn.com/3/2/5/325d6c39e5bba63e0c76881973e3e1jpeg.jpeg'

const cartList = ref([])
const submitting = ref(false)

// 总价
const totalPrice = computed(() => {
  return cartList.value
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
    .toFixed(2)
})

// 加载购物车
const loadCart = async () => {
  try {
    const res = await getCartList(userInfo.id)
    cartList.value = res.data || []
  } catch (e) {}
}

// 修改数量
const handleQuantityChange = async (item, val) => {
  if (val <= 0) {
    // 数量为0，删除该项
    try {
      await updateCartQuantity({
        userId: userInfo.id,
        dishId: item.dishId,
        quantity: 0
      })
      cartList.value = cartList.value.filter(c => c.id !== item.id)
      ElMessage.success('已移除')
    } catch (e) {}
  } else {
    try {
      await updateCartQuantity({
        userId: userInfo.id,
        dishId: item.dishId,
        quantity: val
      })
    } catch (e) {}
  }
}

// 清空购物车
const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定清空购物车吗？', '提示', { type: 'warning' })
    await clearCart(userInfo.id)
    cartList.value = []
    ElMessage.success('已清空')
  } catch (e) {}
}

// 提交订单
const handleSubmitOrder = async () => {
  if (cartList.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  submitting.value = true
  try {
    // 使用购物车中第一个商品的餐桌信息（实际应从选座流程获取）
    await createOrder({
      userId: userInfo.id,
      tableId: 1, // 简化处理，默认桌号
      remark: ''
    })
    ElMessage.success('下单成功！')
    router.push('/orders')
  } catch (e) {
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart-page {
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

.cart-content {
  max-width: 600px;
  margin: 20px auto;
  padding: 0 16px;
}

.cart-item {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.item-img {
  width: 70px;
  height: 70px;
  border-radius: 8px;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  margin: 0 16px;
}

.item-name {
  font-size: 15px;
  font-weight: bold;
  color: #333;
}

.item-price {
  font-size: 16px;
  color: #e6522c;
  font-weight: bold;
  margin-top: 8px;
}

.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-radius: 10px;
  padding: 16px 20px;
  margin-top: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.total {
  font-size: 16px;
  color: #333;
}

.total-price {
  font-size: 22px;
  font-weight: bold;
  color: #e6522c;
}
</style>
