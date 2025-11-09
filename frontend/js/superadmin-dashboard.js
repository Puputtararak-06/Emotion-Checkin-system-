/* Mood Chart */
new Chart(document.getElementById('moodChart'), {
  type: 'bar',
  data: {
    labels: ['Happy', 'Neutral', 'Unhappy'],
    datasets: [{
      data: [50, 30, 20],
      backgroundColor: ['#FFE369', '#A6B4FF', '#90F5C4'],
      borderRadius: 12
    }]
  },
  options: {
    responsive: true,
    maintainAspectRatio: false,
    layout: { padding: { bottom: 25 }},
    scales: {
      x: { grid: { display: false }},
      y: { beginAtZero: true }
    }
  }
});

/* Calendar */
const monthLabel = document.getElementById("monthLabel");
const calendarGrid = document.getElementById("calendarGrid");
let date = new Date();

function loadCalendar() {
  calendarGrid.innerHTML = "";
  const year = date.getFullYear();
  const month = date.getMonth();
  monthLabel.textContent = date.toLocaleString('en-US', { month: 'long', year: 'numeric' });

  const days = new Date(year, month + 1, 0).getDate();
  const moods = ["happy", "neutral", "unhappy"];

  for (let d = 1; d <= days; d++) {
    const div = document.createElement("div");
    div.className = moods[Math.floor(Math.random() * moods.length)];
    div.textContent = d;
    calendarGrid.appendChild(div);
  }
}

document.getElementById("prevMonth").onclick = () => { date.setMonth(date.getMonth() - 1); loadCalendar(); };
document.getElementById("nextMonth").onclick = () => { date.setMonth(date.getMonth() + 1); loadCalendar(); };
loadCalendar();
