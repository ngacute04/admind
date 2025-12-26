#!/bin/bash
# =============================================
# Git Dev → Main + Tag (Đơn giản)
# Chạy từng lệnh trong terminal
# =============================================

# --- 1️⃣ Chuyển sang develop và cập nhật ---
git checkout develop
git pull origin develop        # Lấy update mới nhất từ remote

# --- 2️⃣ Commit các thay đổi trên develop ---
git add .
git commit -m "feat: mô tả thay đổi trên develop"    # Thay nội dung cho phù hợp
git push origin develop        # Đẩy develop lên remote

# --- 3️⃣ Chuyển sang main ---
git checkout main
git pull origin main           # Lấy update main mới nhất

# --- 4️⃣ Merge develop vào main ---
git merge develop
git push origin main           # Push main lên GitHub

# --- 5️⃣ Tag version (nếu muốn) ---
git tag -a v1.0.0 -m "Release v1.0.0"   # Thay version phù hợp
git push origin --tags

# =============================================
# ✅ Hoàn tất: develop → main + tag
# =============================================
