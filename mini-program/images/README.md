# 图片资源说明

tabBar 当前为纯文字模式（无图标），如需添加图标：

1. 准备 6 个 PNG 图标（建议 81x81px）：
   - `menu.png` / `menu-active.png` — 点餐 tab
   - `cart.png` / `cart-active.png` — 购物车 tab
   - `order.png` / `order-active.png` — 订单 tab

2. 放入此目录后，在 `app.json` 的 tabBar.list 中为每项添加：
   ```json
   "iconPath": "images/menu.png",
   "selectedIconPath": "images/menu-active.png"
   ```

3. 菜品占位图：放入 `placeholder.png` 即可自动回退使用
