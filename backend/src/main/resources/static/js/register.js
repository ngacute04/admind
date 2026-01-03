particlesJS("particles-js", {
    particles: {
        number: { value: 35 },
        color: { value: "#b91c1c" },
        opacity: { value: 0.15 },
        size: { value: 2 },
        line_linked: {
            enable: true,
            distance: 150,
            color: "#b91c1c",
            opacity: 0.1,
            width: 1
        },
        move: { enable: true, speed: 0.8 }
    }
});

/* =========================================================
   2. VARIABLES
========================================================= */
let isAgeValid = false;

const elements = {
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

function resetSubmitButton() {
    if (!elements.submitBtn) return;
    elements.submitBtn.innerHTML = 'Tạo tài khoản';
    elements.submitBtn.style.pointerEvents = 'auto';
}

function toggleInputError(element, errorElement, message, isValid) {
    if (isValid || element.value === "") {
        errorElement.classList.add('hidden');
        element.style.borderColor = "rgba(255,255,255,0.08)";
    } else {
        errorElement.innerText = message;
        errorElement.classList.remove('hidden');
        element.style.borderColor = "#b91c1c";
    }
}

/* =========================================================
   4. STATUS MODAL (GLOBAL)
========================================================= */
function openStatusModal(message, isSuccess = false) {
    const modal = document.getElementById('statusModal');
    const msg = document.getElementById('statusMsg');
    const btn = document.getElementById('modalActionBtn');
    const icon = document.getElementById('modalIcon');
    const loginUrl = /*[[@{/login}]]*/ "/login";

    if (!modal || !msg) return;

    msg.innerText = message;
    modal.style.display = 'flex';

    if (isSuccess) {
        icon.className = "fas fa-check-circle text-red-600 text-3xl";
        btn.innerText = "Đăng nhập ngay";
        btn.onclick = () => window.location.href = loginUrl;
        setTimeout(() => window.location.href = loginUrl, 5000);
    } else {
        icon.className = "fas fa-exclamation-triangle text-red-600 text-3xl";
        btn.innerText = "Tôi đã hiểu";
        btn.onclick = () => {
            modal.style.display = 'none';
            resetSubmitButton();
        };
        setTimeout(() => modal.style.display = 'none', 5000);
    }
}

/* =========================================================
   5. REAL-TIME VALIDATION
========================================================= */
elements.contact.addEventListener('input', () => {
    const v = elements.contact.value.trim();
    const ok = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) || /^0[35789][0-9]{8}$/.test(v);
    toggleInputError(elements.contact, errors.contact, "Email hoặc SĐT không hợp lệ", ok);
});

elements.pass.addEventListener('input', () => {
    const v = elements.pass.value;
    const ok = v.length >= 6 && /[a-zA-Z]/.test(v) && /[0-9]/.test(v);
    toggleInputError(elements.pass, errors.pass, "Mật khẩu cần chữ + số, ≥ 6 ký tự", ok);
});

elements.confirm.addEventListener('input', () => {
    toggleInputError(
        elements.confirm,
        errors.confirm,
        "Mật khẩu xác nhận không khớp",
        elements.confirm.value === elements.pass.value
    );
});

/* =========================================================
   6. AGE VALIDATION (ĐÃ TỐI ƯU)
========================================================= */
[elements.day, elements.month, elements.year].forEach(el => {
    el.addEventListener('input', () => {
        const d = +elements.day.value;
        const m = +elements.month.value - 1;
        const y = +elements.year.value;
        
        // Chỉ validate khi đã nhập đủ 3 trường
        if (!d || isNaN(m) || !y || y < 1000) return;

        const date = new Date(y, m, d);
        const isValidDate = date.getFullYear() === y && date.getMonth() === m && date.getDate() === d;

        if (!isValidDate) {
            toggleInputError(elements.day, errors.dob, "Ngày sinh không hợp lệ", false);
            isAgeValid = false;
            return;
        }

        const today = new Date();
        let age = today.getFullYear() - y;
        const monthDiff = today.getMonth() - m;
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < d)) {
            age--;
        }

        isAgeValid = age >= 16;
        toggleInputError(elements.day, errors.dob, "Bạn phải từ 16 tuổi trở lên", isAgeValid);
    });
});

/* =========================================================
   7. FORM SUBMIT
========================================================= */
function validateForm() {
    if (!document.querySelector('input[name="gender"]:checked')) {
        openStatusModal("Vui lòng chọn giới tính của bạn.", false);
        return false;
    }
    if (!isAgeValid) {
        openStatusModal("Bạn phải đủ 16 tuổi trở lên.", false);
        return false;
    }

    elements.submitBtn.innerHTML =
        '<i class="fas fa-circle-notch fa-spin mr-2"></i> Đang xử lý...';
    elements.submitBtn.style.pointerEvents = 'none';
    return true;
}
document.querySelectorAll('.dob-field').forEach((input, index, inputs) => {
    input.addEventListener('input', function() {
        // 1. Giới hạn số lượng ký tự nhập vào
        if (this.value.length > this.maxLength) {
            this.value = this.value.slice(0, this.maxLength);
        }

        // 2. Tự động chuyển sang ô tiếp theo khi nhập đủ
        if (this.value.length === this.maxLength) {
            const nextInput = inputs[index + 1];
            if (nextInput) {
                nextInput.focus();
            }
        }
    });

    // 3. (Tùy chọn) Nhấn Backspace khi ô trống thì quay lại ô trước
    input.addEventListener('keydown', function(e) {
        if (e.key === 'Backspace' && this.value.length === 0) {
            const prevInput = inputs[index - 1];
            if (prevInput) {
                prevInput.focus();
            }
        }
    });
});
/* =========================================================
   8. TỐI ƯU CHUYỂN TRANG (KHỬ DELAY MOBILE)
========================================================= */
document.addEventListener('DOMContentLoaded', () => {
    const loginLink = document.querySelector('a[href*="login"]');
    
    if (loginLink) {
        // Sử dụng event 'touchstart' để phản hồi ngay khi ngón tay vừa chạm vào
        loginLink.addEventListener('touchstart', function() {
            this.style.opacity = '0.5';
        }, {passive: true});

       
    }
});
