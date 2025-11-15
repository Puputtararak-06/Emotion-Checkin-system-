// js/createaccount.js

// รอให้ DOM โหลดก่อน
document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("createForm");
  const pdpaCheckbox = document.getElementById("pdpaCheck");

  form.addEventListener("submit", (e) => {
    // ตอนนี้ยังไม่มี backend เลยกัน submit จริงไว้ก่อน
    e.preventDefault();

    // เช็ค PDPA เผื่อ user hack เอา required ออก
    if (!pdpaCheckbox.checked) {
      alert("Please agree to the Personal Data Protection Policy (PDPA).");
      return;
    }

    // ตรงนี้สมมติว่าสร้างบัญชีสำเร็จแล้ว (prototype / frontend only)
    alert("Account created successfully!");

    // Redirect ไปหน้า Login
    window.location.href = "employee_login.html";
  });
});
