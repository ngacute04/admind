/* =========================================================
   1. PARTICLES.JS CONFIG - Nhẹ & đẹp đỏ chủ đạo
========================================================= */
particlesJS("particles-js", {
  "particles": {
    "number": { "value": 130 },           // nhiều hạt
    "color": { "value": "#ff3232" },      // đỏ rực
    "shape": { "type": "circle" },
    "opacity": { "value": 0.3 },         // sáng hơn
    "size": { "value": 1.5 },               // to hơn
    "line_linked": {
      "enable": true,
      "distance": 110,                    // nối xa hơn
      "color": "#ff3232",                 // đỏ rực
      "opacity": 0.3,                     // đường sáng
      "width": 1
    },
    "move": { "enable": true, "speed": 0.7 } // di chuyển nhanh, mượt
  },
  "interactivity": {
    "events": {
      "onhover": { "enable": true, "mode": "grab" },
      "onclick": { "enable": true, "mode": "push" }
    }
  }
});



/* =========================================================
   2. BIẾN TOÀN CỤC
========================================================= */
let isAgeValid = false;
let isContactAvailable = true; // Theo dõi trạng thái trùng contact

const elements = {
    form: document.querySelector('form'),
    contact: document.getElementById('contactInput'),
    pass: document.getElementById('password'),
    confirm: document.getElementById('confirm_password'),
    day: document.getElementById('dob_day'),
    month: document.getElementById('dob_month'),
    year: document.getElementById('dob_year'),
    submitBtn: document.getElementById('submitBtn')
};

const errors = {
    contact: document.getElementById('contact-error'),
    pass: document.getElementById('password-error'),
    confirm: document.getElementById('confirm-error'),
    dob: document.getElementById('dob-error')
};

const radioGenders = document.querySelectorAll('input[name="gender"]');

/* CSRF Token (Spring Security) */
const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

/* =========================================================
   3. UI HELPERS
========================================================= */
function togglePass(id, icon) {
    const input = document.getElementById(id);
    if (!input) return;
    input.type = input.type === 'password' ? 'text' : 'password';
    icon.classList.toggle('fa-eye');
    icon.classList.toggle('fa-eye-slash');
}

function toggleInputError(element, errorEl, message, isValid) {
    if (isValid || !element.value.trim()) {
        errorEl.classList.add('hidden');
        element.style.borderColor = "";
    } else {
        errorEl.textContent = message;
        errorEl.classList.remove('hidden');
        element.style.borderColor = "#b91c1c";
    }
}

function updateSubmitButton(isEnabled) {
    if (isEnabled) {
        elements.submitBtn.disabled = false;
        elements.submitBtn.className = "w-full py-4 rounded-2xl bg-red-700 text-white font-bold uppercase text-sm tracking-[0.2em] shadow-lg hover:bg-red-600 active:scale-95 transition-all cursor-pointer";
    } else {
        elements.submitBtn.disabled = true;
        elements.submitBtn.className = "w-full py-4 rounded-2xl bg-gray-600 text-white font-bold uppercase text-sm tracking-[0.2em] opacity-50 cursor-not-allowed";
    }
}

/* =========================================================
   4. MODAL THÔNG BÁO
========================================================= */
function openStatusModal(message, isSuccess = false) {
    const modal = document.getElementById('statusModal');
    const msg = document.getElementById('statusMsg');
    const btn = document.getElementById('modalActionBtn');
    const icon = document.getElementById('modalIcon');
    const loginUrl = /*[[@{/login}]]*/ "/login";

    if (!modal || !msg) return;

    msg.textContent = message;
    modal.style.display = 'flex';

    if (isSuccess) {
        icon.className = "fas fa-check-circle text-red-600 text-3xl";
        btn.textContent = "Đăng nhập ngay";
        btn.onclick = () => window.location.href = loginUrl;
        setTimeout(() => window.location.href = loginUrl, 5000);
    } else {
        icon.className = "fas fa-exclamation-triangle text-red-600 text-3xl";
        btn.textContent = "Tôi đã hiểu";
        btn.onclick = () => modal.style.display = 'none';
        setTimeout(() => modal.style.display = 'none', 6000);
    }
}

/* =========================================================
   5. REAL-TIME VALIDATION CHI TIẾT
========================================================= */
function validateAllFields() {
    let allValid = true;

    // 1. Các trường bắt buộc không trống
    const requiredInputs = elements.form.querySelectorAll('input[required]');
    requiredInputs.forEach(input => {
        if (!input.value.trim()) allValid = false;
    });

    // 2. Ngày sinh hợp lệ + đủ 16 tuổi
    const d = parseInt(elements.day.value);
    const m = parseInt(elements.month.value);
    const y = parseInt(elements.year.value);

    const monthOk = !isNaN(m) && m >= 1 && m <= 12;
    const dayOk = !isNaN(d) && d >= 1 && d <= 31;
    const yearOk = !isNaN(y) && y >= 1900 && y <= new Date().getFullYear();

    let dateValid = false;
    if (monthOk && dayOk && yearOk) {
        const dateObj = new Date(y, m - 1, d);
        dateValid = dateObj.getFullYear() === y && dateObj.getMonth() === (m - 1) && dateObj.getDate() === d;
    }

    if (!monthOk || !dayOk || !yearOk || !dateValid) {
        allValid = false;
        isAgeValid = false;
        toggleInputError(elements.day, errors.dob, "Ngày sinh không hợp lệ", false);
    } else {
        // Tính tuổi
        const today = new Date();
        let age = today.getFullYear() - y;
        if (today.getMonth() < (m - 1) || (today.getMonth() === (m - 1) && today.getDate() < d)) age--;
        isAgeValid = age >= 16;

        if (!isAgeValid) {
            allValid = false;
            toggleInputError(elements.day, errors.dob, "Bạn phải từ 16 tuổi trở lên", false);
        } else {
            errors.dob.classList.add('hidden');
        }
    }

    // 3. Giới tính
    const genderSelected = Array.from(radioGenders).some(r => r.checked);
    if (!genderSelected) allValid = false;

    // 4. Mật khẩu
    const passOk = elements.pass.value.length >= 6 && /[a-zA-Z]/.test(elements.pass.value) && /[0-9]/.test(elements.pass.value);
    toggleInputError(elements.pass, errors.pass, "Mật khẩu cần chữ + số, ≥ 6 ký tự", passOk);
    if (!passOk) allValid = false;

    // 5. Xác nhận mật khẩu
    const confirmOk = elements.confirm.value === elements.pass.value && elements.confirm.value !== "";
    toggleInputError(elements.confirm, errors.confirm, "Mật khẩu xác nhận không khớp", confirmOk);
    if (!confirmOk) allValid = false;

    // 6. Trạng thái contact (từ AJAX)
    if (!isContactAvailable) allValid = false;

    // Cập nhật nút submit
    updateSubmitButton(allValid);
}

// Gắn lắng nghe toàn form
elements.form.addEventListener('input', validateAllFields);
radioGenders.forEach(radio => radio.addEventListener('change', validateAllFields));
document.addEventListener('DOMContentLoaded', validateAllFields);

/* =========================================================
   6. AUTO FOCUS CHO Ô NGÀY SINH
========================================================= */
document.querySelectorAll('.dob-field').forEach((input, index, inputs) => {
    input.addEventListener('input', function () {
        if (this.value.length > this.maxLength) {
            this.value = this.value.slice(0, this.maxLength);
        }
        if (this.value.length === parseInt(this.maxLength)) {
            const next = inputs[index + 1];
            if (next) next.focus();
        }
    });

    input.addEventListener('keydown', function (e) {
        if (e.key === 'Backspace' && this.value.length === 0) {
            const prev = inputs[index - 1];
            if (prev) prev.focus();
        }
    });
});

/* =========================================================
   7. KIỂM TRA TRÙNG EMAIL/SĐT (AJAX)
========================================================= */
elements.contact.addEventListener('blur', async () => {
    const value = elements.contact.value.trim();
    if (value.length < 6) {
        isContactAvailable = true;
        return;
    }

    // Validate định dạng trước khi gọi API
    const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
    const isPhone = /^0[1-9][0-9]{8}$/.test(value);
    if (!isEmail && !isPhone) {
        toggleInputError(elements.contact, errors.contact, "Email hoặc SĐT không hợp lệ", false);
        isContactAvailable = false;
        validateAllFields();
        return;
    }

    try {
        const response = await fetch(`/api/auth/check-contact?contact=${encodeURIComponent(value)}`, {
            method: 'GET',
            headers: csrfHeader ? { [csrfHeader]: csrfToken } : {}
        });

        if (response.ok) {
            const exists = await response.json();
            isContactAvailable = !exists;
            toggleInputError(elements.contact, errors.contact, "Email hoặc SĐT này đã được sử dụng", !exists);
        }
    } catch (err) {
        console.error("Lỗi kiểm tra trùng:", err);
        // Không khóa form nếu lỗi mạng
    } finally {
        validateAllFields();
    }
});

/* =========================================================
   8. FORM SUBMIT CUỐI CÙNG
========================================================= */
function validateForm() {
    validateAllFields(); // Cập nhật lần cuối

    if (elements.submitBtn.disabled) {
        return false;
    }

    // Loading state
    elements.submitBtn.innerHTML = '<i class="fas fa-circle-notch fa-spin mr-2"></i> Đang xử lý...';
    elements.submitBtn.disabled = true;

    return true;
}

/* =========================================================
   9. TỐI ƯU TOUCH CHO LINK ĐĂNG NHẬP
========================================================= */
document.addEventListener('DOMContentLoaded', () => {
    const loginLink = document.querySelector('a[th\\:href="@{/login}"], a[href*="/login"]');
    if (loginLink) {
        loginLink.addEventListener('touchstart', () => loginLink.style.opacity = '0.6', { passive: true });
        loginLink.addEventListener('touchend', () => loginLink.style.opacity = '1');
    }
});


