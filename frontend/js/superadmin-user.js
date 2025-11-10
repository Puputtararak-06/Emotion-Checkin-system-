let userData = [
  { name:"Sarah Kim", username:"sarah01", role:"User", department:"HR", date:"2025-01-02", time:"09:10" },
  { name:"Jason Wong", username:"jason22", role:"HR", department:"IT", date:"2025-01-03", time:"08:45" }
];

function renderTable(){
  let tbody = document.getElementById("userList");
  tbody.innerHTML = "";
  userData.forEach((u,i)=>{
    tbody.innerHTML += `
      <tr>
        <td>🙂</td>
        <td>${u.name}</td>
        <td>${u.username}</td>
        <td>${u.role}</td>
        <td>${u.department}</td>
        <td>${u.date} ${u.time}</td>
        <td>
          <span class="action-btn" onclick="openEdit(${i})">✏</span>
          <span class="action-btn" onclick="openConfirm(${i})">🗑</span>
        </td>
      </tr>
    `;
  });
}
renderTable();

document.getElementById("btnAddUser").onclick = ()=>{
  document.getElementById("popupTitle").textContent = "Add User";
  document.getElementById("saveUserBtn").onclick = saveUser;
  openPopup();
};

function saveUser(){
  userData.push({
    name: fullName.value,
    username: username.value,
    role: role.value,
    department: department.value,
    date:"2025-01-02",
    time:"09:00"
  });
  renderTable();
  closePopup();
}

function openEdit(i){
  document.getElementById("popupTitle").textContent = "Edit User";
  fullName.value = userData[i].name;
  username.value = userData[i].username;
  role.value = userData[i].role;
  department.value = userData[i].department;
  saveUserBtn.onclick = ()=>{ saveEdit(i); };
  openPopup();
}

function saveEdit(i){
  userData[i].name = fullName.value;
  userData[i].username = username.value;
  userData[i].role = role.value;
  userData[i].department = department.value;
  renderTable();
  closePopup();
}

function openConfirm(i){
  confirmPopup.classList.remove("hidden");
  btnYes.onclick = ()=>{ userData.splice(i,1); renderTable(); closeConfirm(); };
}

function openPopup(){ userPopup.classList.remove("hidden"); }
function closePopup(){ userPopup.classList.add("hidden"); }
function closeConfirm(){ confirmPopup.classList.add("hidden"); }
