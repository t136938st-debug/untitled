<template>
  <div class="menu-page">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-left">
        <h2>美味餐厅</h2>
        <span class="table-info" v-if="selectedTable">桌号：{{ selectedTable.tableNumber }}</span>
      </div>
      <div class="header-right">
        <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
          <el-button type="warning" round @click="$router.push('/cart')">
            <el-icon><ShoppingCart /></el-icon>
            购物车
          </el-button>
        </el-badge>
        <el-button round @click="$router.push('/orders')">
          <el-icon><List /></el-icon>
          我的订单
        </el-button>
        <el-button type="info" plain round @click="handleLogout">退出</el-button>
      </div>
    </div>

    <!-- 选择餐桌 -->
    <div class="table-select" v-if="!selectedTable">
      <el-card shadow="hover">
        <template #header>
          <span>请选择餐桌</span>
        </template>
        <div class="table-grid">
          <div
            v-for="table in freeTables"
            :key="table.id"
            class="table-item"
            :class="{ occupied: table.status === 1 }"
            @click="selectTable(table)"
          >
            <el-icon :size="32"><Grid /></el-icon>
            <span>{{ table.tableNumber }}</span>
            <small>{{ table.seats }}人桌</small>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 菜品区域 -->
    <div class="menu-content" v-if="selectedTable">
      <!-- 左侧分类 -->
      <div class="category-sidebar">
        <div
          v-for="cat in categories"
          :key="cat.id"
          class="category-item"
          :class="{ active: activeCategoryId === cat.id }"
          @click="selectCategory(cat.id)"
        >
          {{ cat.name }}
        </div>
      </div>

      <!-- 右侧菜品列表 -->
      <div class="dish-list">
        <div v-for="dish in currentDishes" :key="dish.id" class="dish-card">
          <el-image :src="dish.image || 'https://cube.elemecdn.com/3/2/5/325d6c39e5bba63e0c76881973e3e1jpeg.jpeg'" fit="cover" class="dish-img" />
          <div class="dish-info">
            <div class="dish-name">{{ dish.name }}</div>
            <div class="dish-desc">{{ dish.description || '暂无描述' }}</div>
            <div class="dish-bottom">
              <span class="dish-price">¥{{ dish.price }}</span>
              <el-button type="primary" size="small" round @click="handleAddCart(dish)">
                <el-icon><Plus /></el-icon>
                加入购物车
              </el-button>
            </div>
          </div>
        </div>
        <el-empty v-if="currentDishes.length === 0" description="该分类暂无菜品" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCategoryList, getDishListByCategory, addToCart, getFreeTableList, getCartList } from '../../api'

const router = useRouter()

// 用户信息
const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

// 餐桌数据
const freeTables = ref([])
const selectedTable = ref(null)

// 分类数据
const categories = ref([])
const activeCategoryId = ref(null)

// 菜品数据
const dishMap = ref({}) // { categoryId: [dishes] }

// 购物车数量
const cartCount = ref(0)

// 获取空闲餐桌
const loadFreeTables = async () => {
  try {
    const res = await getFreeTableList()
    freeTables.value = res.data || []
  } catch (e) {}
}

// 选择餐桌
const selectTable = (table) => {
  selectedTable.value = table
  loadCategories()
}

// 加载分类
const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
    if (categories.value.length > 0) {
      selectCategory(categories.value[0].id)
    }
  } catch (e) {}
}

// 选择分类，加载菜品
const selectCategory = async (categoryId) => {
  activeCategoryId.value = categoryId
  if (!dishMap.value[categoryId]) {
    try {
      const res = await getDishListByCategory(categoryId)
      dishMap.value[categoryId] = res.data || []
    } catch (e) {
      dishMap.value[categoryId] = []
    }
  }
}

// 当前分类的菜品
const currentDishes = computed(() => {
  return dishMap.value[activeCategoryId.value] || []
})

// 加入购物车
const handleAddCart = async (dish) => {
  try {
    await addToCart({
      userId: userInfo.id,
      dishId: dish.id
    })
    ElMessage.success(`已添加「${dish.name}」`)
    // 刷新购物车数量
    loadCartCount()
  } catch (e) {}
}

// 加载购物车数量
const loadCartCount = async () => {
  try {
    const res = await getCartList(userInfo.id)
    const items = res.data || []
    cartCount.value = items.reduce((sum, item) => sum + item.quantity, 0)
  } catch (e) {}
}

// 退出登录
const handleLogout = () => {
  sessionStorage.removeItem('userInfo')
  router.push('/customer/login')
}

onMounted(() => {
  loadFreeTables()
  loadCartCount()
})
</script>

<style scoped>
.menu-page {
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
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left h2 {
  font-size: 20px;
  color: #e6522c;
}

.table-info {
  font-size: 13px;
  color: #999;
  margin-left: 12px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.table-select {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;
}

.table-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 16px;
}

.table-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 8px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.table-item:hover {
  border-color: #e6522c;
  background: #fff5f0;
}

.table-item span {
  margin-top: 8px;
  font-weight: bold;
  font-size: 16px;
}

.table-item small {
  color: #999;
  margin-top: 4px;
}

.table-item.occupied {
  opacity: 0.4;
  cursor: not-allowed;
}

/* 菜品区域 */
.menu-content {
  display: flex;
  height: calc(100vh - 60px);
}

.category-sidebar {
  width: 100px;
  background: #fff;
  overflow-y: auto;
  flex-shrink: 0;
}

.category-item {
  padding: 16px 10px;
  text-align: center;
  font-size: 14px;
  cursor: pointer;
  border-left: 3px solid transparent;
  transition: all 0.2s;
}

.category-item:hover {
  background: #f5f5f5;
}

.category-item.active {
  background: #f5f5f5;
  color: #e6522c;
  font-weight: bold;
  border-left-color: #e6522c;
}

.dish-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.dish-card {
  display: flex;
  background: #fff;
  border-radius: 10px;
  margin-bottom: 12px;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.dish-img {
  width: 120px;
  height: 120px;
  flex-shrink: 0;
}

.dish-info {
  flex: 1;
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.dish-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.dish-desc {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dish-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}

.dish-price {
  font-size: 18px;
  font-weight: bold;
  color: #e6522c;
}
</style>
