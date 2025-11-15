// js/employee-auth.js

// ================== LOGIN HANDLER ==================
const loginForm = document.getElementById('loginForm');
const loginError = document.getElementById('loginError');

if (loginForm) {
  loginForm.addEventListener('submit', function (e) {
    e.preventDefault();

    const identifier = document.getElementById('loginIdentifier').value.trim();
    const password = document.getElementById('loginPassword').value.trim();

    loginError.style.display = 'none';
    loginError.textContent = '';

    if (!identifier || !password) {
      loginError.textContent = 'Please enter both email/username and password.';
      loginError.style.display = 'block';
      return;
    }

    let user = null;

    // 1) ลองมองว่า identifier เป็น email ก่อน
    const storedByEmail = localStorage.getItem('employee-' + identifier);
    if (storedByEmail) {
      user = JSON.parse(storedByEmail);
    } else {
      // 2) ถ้าไม่เจอ ลองค้นจาก username
      for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i);
        if (key.startsWith('employee-')) {
          const candidate = JSON.parse(localStorage.getItem(key));
          if (candidate.username === identifier) {
            user = candidate;
            break;
          }
        }
      }
    }

    if (!user) {
      loginError.textContent = 'Account not found. Please check your email/username or create a new account.';
      loginError.style.display = 'block';
      return;
    }

    if (user.password !== password) {
      loginError.textContent = 'Incorrect password. Please try again.';
      loginError.style.display = 'block';
      return;
    }

    // mock: เก็บว่า employee คนนี้ล็อกอินแล้ว
    localStorage.setItem('currentEmployee', JSON.stringify(user));

    // ไปหน้า check-in
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

    const firstName = document.getElementById('regFirstName').value.trim();
    const lastName = document.getElementById('regLastName').value.trim();
    const username = document.getElementById('regUsername').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value;
    const confirmPassword = document.getElementById('regConfirmPassword').value;
    const pdpaChecked = document.getElementById('pdpaCheck').checked;

    if (!firstName || !lastName || !username || !email || !password || !confirmPassword) {
      registerError.textContent = 'Please fill in all required fields.';
      registerError.style.display = 'block';
      return;
    }

    if (!pdpaChecked) {
      registerError.textContent = 'You must agree to the PDPA policy before creating an account.';
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

    // เช็ค email ซ้ำ
    const existingEmail = localStorage.getItem('employee-' + email);
    if (existingEmail) {
      registerError.textContent = 'This email is already registered.';
      registerError.style.display = 'block';
      return;
    }

    // เช็ค username ซ้ำ
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (key.startsWith('employee-')) {
        const usr = JSON.parse(localStorage.getItem(key));
        if (usr.username === username) {
          registerError.textContent = 'This username is already taken.';
          registerError.style.display = 'block';
          return;
        }
      }
    }

    const newUser = {
      firstName,
      lastName,
      username,
      email,
      password,   // ในระบบจริงควร hash ใน backend
      role: 'Employee'
    };

    localStorage.setItem('employee-' + email, JSON.stringify(newUser));

    registerSuccess.textContent = 'Account created successfully. You can now log in.';
    registerSuccess.style.display = 'block';

    // เคลียร์ฟอร์ม
    registerForm.reset();

    // redirect ไปหน้า login แบบชิล ๆ
    setTimeout(() => {
      window.location.href = 'employee_login.html';
    }, 1200);
  });
}
