// ================= EMOJI DATA =================
const moodGroups = {
  negative: [
    {emoji:"😢", label:"Sad"},
    {emoji:"😡", label:"Angry"},
    {emoji:"😞", label:"Disappointed"},
    {emoji:"😖", label:"Stressed"},
    {emoji:"😫", label:"Exhausted"}
  ],
  neutral: [
    {emoji:"😐", label:"Neutral"},
    {emoji:"😶", label:"Blank"},
    {emoji:"😑", label:"Tired"},
    {emoji:"🫥", label:"Low Energy"},
    {emoji:"🤷", label:"Unsure"}
  ],
  positive: [
    {emoji:"🙂", label:"Content"},
    {emoji:"😊", label:"Happy"},
    {emoji:"😄", label:"Excited"},
    {emoji:"🤩", label:"Inspired"},
    {emoji:"🥰", label:"Loved"}
  ]
};

// ================= ELEMENTS =================
const emojiContainer = document.getElementById("emojiContainer");
const tabs = document.querySelectorAll(".mood-tab");
const submitBtn = document.getElementById("submitBtn");
const noteInput = document.getElementById("noteInput");

let selectedMood = null;

// ================= RENDER EMOJIS =================
function renderEmojis(group) {
  emojiContainer.innerHTML = "";
  moodGroups[group].forEach(item => {
    const div = document.createElement("div");
    div.className = "emotion-item";
    div.innerHTML = `<div style="font-size:38px;">${item.emoji}</div><div style="font-size:13px;opacity:.85;">${item.label}</div>`;
    div.onclick = () => {
      document.querySelectorAll(".emotion-item").forEach(el=>el.classList.remove("active"));
      div.classList.add("active");
      selectedMood = item;
    };
    emojiContainer.appendChild(div);
  });
}
renderEmojis("negative");

// ================= TAB SWITCH =================
tabs.forEach(tab => {
  tab.addEventListener("click", () => {
    tabs.forEach(t=>t.classList.remove("active"));
    tab.classList.add("active");
    renderEmojis(tab.dataset.group);
  });
});

// ================= SUBMIT =================
submitBtn.addEventListener("click", () => {
  if (!selectedMood) return alert("Please select how you feel.");
  
  const today = new Date().toISOString().split("T")[0];
  const existing = localStorage.getItem("checkin-"+today);
  if (existing) return alert("You already checked in today.");

  const note = noteInput.value.trim();

  const data = {
    date: today,
    mood: selectedMood.label,
    emoji: selectedMood.emoji,
    note: note
  };

  localStorage.setItem("checkin-"+today, JSON.stringify(data));
  window.location.href = "success.html";
});
