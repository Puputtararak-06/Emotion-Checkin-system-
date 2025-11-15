// js/employee-auth.js

// ================== LOGIN HANDLER ==================
const loginForm = document.getElementById('loginForm');
const loginError = document.getElementById('loginError');

if (loginForm) {
  loginForm.addEventListener('submit', function (e) {
    e.preventDefault();

    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value.trim();

    loginError.style.display = 'none';
    loginError.textContent = '';

    if (!email || !password) {
      loginError.textContent = 'Please enter both email and password.';
      loginError.style.display = 'block';
      return;
    }

    // 🔐 TODO: ตรงนี้ในระบบจริงให้เรียก API ตรวจสอบกับฐานข้อมูล
    // ตอนนี้ mock ง่าย ๆ ด้วย localStorage เพื่อทดสอบ flow
    const stored = localStorage.getItem('employee-' + email);

    if (!stored) {
      loginError.textContent = 'Account not found. Please check your email or create a new account.';
      loginError.style.display = 'block';
      return;
    }

    const user = JSON.parse(stored);
    if (user.password !== password) {
      loginError.textContent = 'Incorrect password. Please try again.';
      loginError.style.display = 'block';
      return;
    }

    // เก็บข้อมูล user ว่าล็อกอินแล้ว (mock)
    localStorage.setItem('currentEmployee', JSON.stringify(user));

    // ล็อกอินสำเร็จ -> ไปหน้า check-in
    window.location.href = 'checkin.html';
  });
}

// ================== REGISTER HANDLER ==================
const registerForm = document.getElementById('registerForm');
const registerError = document.getElementById('registerError');
const registerSuccess = document.getElementById('registerSuccess');

if (registerForm) {
  registerForm.addEventListener('submit', function (e) {
    e.preventDefault();

    registerError.style.display = 'none';
    registerError.textContent = '';
    registerSuccess.style.display = 'none';
    registerSuccess.textContent = '';

    const name = document.getElementById('regName').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const department = document.getElementById('regDepartment').value.trim();
    const position = document.getElementById('regPosition').value.trim();
    const password = document.getElementById('regPassword').value;
    const confirmPassword = document.getElementById('regConfirmPassword').value;

    if (!name || !email || !department || !position || !password || !confirmPassword) {
      registerError.textContent = 'Please fill in all required fields.';
      registerError.style.display = 'block';
      return;
    }

    if (password.length < 8) {
      registerError.textContent = 'Password must be at least 8 characters long.';
      registerError.style.display = 'block';
      return;
    }

    if (password !== confirmPassword) {
      registerError.textContent = 'Passwords do not match. Please re-enter.';
      registerError.style.display = 'block';
      return;
    }

    // 🔐 TODO: ในระบบจริงให้ส่งข้อมูลไป backend เพื่อบันทึกในฐานข้อมูล
    // ตอนนี้ลองเก็บ mock ด้วย localStorage ก่อน
    const existing = localStorage.getItem('employee-' + email);
    if (existing) {
      registerError.textContent = 'This email is already registered.';
      registerError.style.display = 'block';
      return;
    }

    const newUser = {
      name,
      email,
      department,
      position,
      password,   // ในระบบจริงต้องเก็บแบบ hash ใน backend
      role: 'Employee'
    };

    localStorage.setItem('employee-' + email, JSON.stringify(newUser));

    registerSuccess.textContent = 'Account created successfully. You can now log in.';
    registerSuccess.style.display = 'block';

    // เคลียร์ฟอร์มเบา ๆ
    registerForm.reset();

    // จะ redirect ไป login เลยก็ได้ ถ้าต้องการ:
    // setTimeout(() => { window.location.href = 'login.html'; }, 1200);
  });
}
