# vitatrack
wellness foods E-commerce 
# E-commerce System

## 📌 專案簡介
本專案為電商後端系統，實作完整訂單與金流流程，包含會員管理、商品管理、購物車、結帳流程與第三方金流串接（ECPay）。

## 🧱 系統架構
- Backend: Java / Spring / Spring MVC
- Database: MySQL
- Architecture: RESTful API + 前後端分離 + 非同步 Callback

## 🔄 核心流程
Order → Payment → Callback → Status Update

## 🚀 技術重點（這才是重點）
- 處理第三方金流非同步 Callback（Server-to-Server）
- 設計訂單狀態機（PENDING / SUCCESS / FAILED）
- 解決付款延遲與前後端狀態不同步問題
- 設計 polling API 讓前端即時取得訂單狀態

## ⚠️ 解決的問題
- 金流回傳時間不可控 → 導致前端誤判
- Callback 無法直接更新前端 → 狀態不同步
- 資料一致性問題（訂單 vs 金流）

## 🛠 技術實作
- RESTful API 設計
- Hibernate ORM
- Transaction 控制（@Transactional）
- DTO 分層避免資料外洩

## 📎 GitHub Repo
https://github.com/jerry20130510/vitatrack
