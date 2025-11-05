// 15 emotions catalog (same as check-in)
const CATALOG = {
  Negative: ["😢","😠","😫","😰","😒"],
  Neutral: ["😌","😴","🤷","🎯","🤔"],
  Positive: ["😀","🧘","🤩","🏆","💪"]
};

// Load from localStorage
let mood = localStorage.getItem("latestMood");
let emoji = localStorage.getItem("latestEmoji");

// Default mock chart count
let counts = {Negative:1, Neutral:1, Positive:1};

// Count occurrences if used across days later
if(mood) counts[mood]++;

// Draw Bar Chart
const ctx = document.getElementById('moodChart').getContext('2d');
new Chart(ctx, {
  type: 'bar',
  data: {
    labels: ["Negative", "Neutral", "Positive"],
    datasets: [{
      label: "Mood Count",
      data: [counts.Negative, counts.Neutral, counts.Positive]
    }]
  }
});

// Calendar
const calendar = document.getElementById("calendar");
const daysInMonth = 30;
for (let i = 1; i <= daysInMonth; i++) {
  const div = document.createElement("div");
  div.className = "calendar-day";
  div.textContent = i;

  // Mark today
  const today = new Date().getDate();
  if (i === today && emoji) {
    div.classList.add("hasMood");
    div.textContent = emoji;
  }

  calendar.appendChild(div);
}
