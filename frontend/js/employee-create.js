const form = document.getElementById("employeeCreateForm");

if (form) {
  form.addEventListener("submit", (e) => {
    e.preventDefault();

    const pdpa = document.getElementById("pdpa");
    if (!pdpa.checked) {
      alert("Please accept the PDPA policy before creating an account.");
      return;
    }

    // เก็บค่าจากฟอร์ม (ไว้ยิงไป backend ภายหลัง)
    const data = {
      firstName: form.firstName.value.trim(),
      lastName: form.lastName.value.trim(),
      email: form.email.value.trim(),
      password: form.password.value, // เดี๋ยวไป hash ที่ backend
    };

    console.log("Create account data:", data);

    // TODO: เรียก fetch ไป backend จริง
    alert("Account created (mock). Redirecting to login page...");
    window.location.href = "employee-login.html";
  });
}
