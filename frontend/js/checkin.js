// ================= EMOJI DATA =================
const moodGroups = {
  negative: [
    { emoji: "😢", label: "Sad" },
    { emoji: "😡", label: "Angry" },
    { emoji: "😞", label: "Disappointed" },
    { emoji: "😖", label: "Stressed" },
    { emoji: "😫", label: "Exhausted" }
  ],
  neutral: [
    { emoji: "😐", label: "Neutral" },
    { emoji: "😶", label: "Blank" },
    { emoji: "😑", label: "Tired" },
    { emoji: "🫥", label: "Low Energy" },
    { emoji: "😕", label: "Unsure" }
  ],
  positive: [
    { emoji: "🙂", label: "Content" },
    { emoji: "😊", label: "Happy" },
    { emoji: "😄", label: "Excited" },
    { emoji: "🤩", label: "Inspired" },
    { emoji: "🥰", label: "Loved" }
  ]
};

// ================= ELEMENTS =================
const emojiContainer = document.getElementById("emojiContainer");
const tabs = document.querySelectorAll(".mood-tab");
const submitBtn = document.getElementById("submitBtn");
const noteInput = document.getElementById("noteInput");

let selectedGroup = "negative";   // top-level level: negative/neutral/positive
let selectedMood = null;          // sub-emotion object {emoji, label}

// ================= RENDER EMOJIS =================
function renderEmojis(group) {
  selectedGroup = group;
  selectedMood = null;
  emojiContainer.innerHTML = "";

  moodGroups[group].forEach((item) => {
    const div = document.createElement("div");
    div.className = "emotion-item";
    div.innerHTML = `
      <div style="font-size:38px;">${item.emoji}</div>
      <div style="font-size:13px;opacity:.85;">${item.label}</div>
    `;

    div.onclick = () => {
      document
        .querySelectorAll(".emotion-item")
        .forEach((el) => el.classList.remove("active"));
      div.classList.add("active");
      selectedMood = item; // เลือก sub-emotion ตัวเดียว
    };

    emojiContainer.appendChild(div);
  });
}

// โหลดหน้าครั้งแรก → default เป็น Negative
renderEmojis("negative");

// ================= TAB SWITCH =================
tabs.forEach((tab) => {
  tab.addEventListener("click", () => {
    tabs.forEach((t) => t.classList.remove("active"));
    tab.classList.add("active");
    renderEmojis(tab.dataset.group);
  });
});

// ============== MOCK NLP SENTIMENT =============
// ในของจริงตรงนี้จะเรียก API ไปหา NLP service แทน
function mockAnalyzeSentiment(level, comment) {
  let baseScore = 0;
  if (level === "negative") baseScore = -0.7;
  if (level === "neutral") baseScore = 0;
  if (level === "positive") baseScore = 0.7;

  const magnitude = Math.min(2, comment.length / 60); // แรงขึ้นตามความยาวคอมเมนต์
  return {
    score: baseScore,
    magnitude
  };
}

// ================= SUBMIT =================
submitBtn.addEventListener("click", () => {
  // ต้องเลือก sub-emotion ก่อน
  if (!selectedMood) {
    alert("Please select how you feel.");
    return;
  }

  const comment = noteInput.value.trim();
  const now = new Date();
  const dateKey = now.toISOString().split("T")[0]; // YYYY-MM-DD

  // ดึง current employee จาก localStorage (ตอน login เก็บไว้)
  const currentStr = localStorage.getItem("currentEmployee");
  let employeeId = "guest";

  if (currentStr) {
    try {
      const user = JSON.parse(currentStr);
      employeeId = user.username || user.email || "guest";
    } catch (e) {
      console.warn("currentEmployee parse error", e);
    }
  }

  // จำกัด 1 check-in ต่อวันต่อ employee
  const storageKey = `checkin-${employeeId}-${dateKey}`;
  const existing = localStorage.getItem(storageKey);
  if (existing) {
    alert("You already checked in today.");
    return;
  }

  // วิเคราะห์ sentiment (mock)
  const sentiment = mockAnalyzeSentiment(selectedGroup, comment);

  // สร้าง record ตาม FR-02
  const record = {
    id: Date.now(),           // mock id
    employeeId,               // ใช้ username/email เป็น id ชั่วคราว
    level: selectedGroup,     // Negative / Neutral / Positive
    subEmotion: selectedMood.label,
    emoji: selectedMood.emoji,
    comment: comment || null,
    sentimentScore: sentiment.score,
    sentimentMagnitude: sentiment.magnitude,
    timestamp: now.toISOString()
  };

  // เก็บ record ของวันนี้
  localStorage.setItem(storageKey, JSON.stringify(record));

  // (option) เก็บเป็น history array ด้วย เผื่อเอาไปใช้หน้า dashboard
  const historyKey = `checkin-history-${employeeId}`;
  const history = JSON.parse(localStorage.getItem(historyKey) || "[]");
  history.push(record);
  localStorage.setItem(historyKey, JSON.stringify(history));

  // แสดงข้อความยืนยัน + redirect ตาม FR
  alert("Emotion check-in submitted successfully.");
  window.location.href = "dashboard.html"; // หรือจะอยู่หน้าเดิมก็ได้
});
