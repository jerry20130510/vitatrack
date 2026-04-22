# Vitatrack（電商結帳與金流系統）

## 專案簡介
Vitatrack 是一個模擬電商結帳流程的後端系統，實作從購物車、建立訂單、串接第三方金流到付款結果回傳的完整交易流程。
本專案重點在於：
- 建立可維護的訂單狀態流程（State Management）
- 處理金流非同步回傳（Callback）
- 確保前後端狀態一致性
---

## 系統架構
- Backend：Java / Spring / Spring MVC  
- ORM：Hibernate  
- Database：MySQL  
- Payment：ECPay（綠界金流）  
- Architecture：MVC + Service + DAO 分層設計  
---

## 核心流程（Checkout Flow）
1. 使用者進入結帳頁面  
2. 建立訂單（狀態：PENDING）  
3. 導向第三方金流（ECPay）  
4. 金流進行付款  
5. ECPay 呼叫後端 Callback（Server-to-Server）  
6. 系統驗證資料並更新訂單狀態（SUCCESS / FAILED）  
7. 前端透過 API 查詢最終狀態  
---

## 核心設計
### 訂單狀態機（Order State）
- `PENDING`：訂單已建立，尚未付款  
- `SUCCESS`：付款成功  
- `FAILED`：付款失敗  
👉 避免狀態覆寫與重複更新問題  
---

### 非同步處理（Callback 機制）
- 採用 ECPay Server-to-Server callback  
- 後端驗證 CheckMacValue 確保資料完整性  
- 避免前端偽造付款結果  
---

### 前後端狀態同步
提供 API：GET /api/checkout/result?orderId={orderId}
前端採 polling 機制取得最新付款狀態  
->解決付款延遲導致的畫面不一致問題  
---

## API 範例
### 查詢訂單狀態
```json
{
  "orderId": "12345",
  "paymentStatus": "SUCCESS",
  "totalAmount": 1000,
  "paymentTime": "2026-02-01 12:00:00",
  "paymentMethod": "CreditCard"
}
```

## 安全性設計
金流資料驗證（CheckMacValue）
訂單狀態不可逆（避免重複更新）
敏感資訊（DB / 金流金鑰）不存放於程式碼中
使用環境變數或外部設定檔管理
---

## 如何啟動專案（Quick Start）
### 設定資料庫
建立 MySQL 資料庫（例如：vitatrack）
---

### 設定環境變數
請建立以下環境變數：
DB_URL=jdbc:mysql://localhost:3306/your_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

ECPAY_MERCHANT_ID=your_id
ECPAY_HASH_KEY=your_key
ECPAY_HASH_IV=your_iv
ECPAY_RETURN_URL=your_callback_url
->請勿將真實帳密提交到 GitHub
---

### 啟動專案
使用 IntelliJ / Eclipse 或 Tomcat 部署啟動
---

### 專案亮點
完整實作電商交易流程（Order → Payment → Callback）
處理金流非同步回傳與資料驗證
設計訂單狀態機確保資料一致性
建立 RESTful API 支援前後端分離
具備基礎資安觀念（敏感資訊外部化）
---

## 注意事項
本專案為學習用途
請勿使用真實金流金鑰或資料庫帳密
---

## Author
Jerry
