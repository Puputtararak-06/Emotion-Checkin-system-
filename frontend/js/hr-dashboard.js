/* Mood Bar Chart */
const ctx = document.getElementById('moodChart').getContext('2d');

new Chart(ctx, {
  type: 'bar',
  data: {
    labels: ['Happy', 'Unhappy', 'Anxiety'],
    datasets: [{
      data: [12, 6, 8],
      backgroundColor: ['#FFE369', '#A6B4FF', '#90F5C4'],
      borderRadius: 12
    }]
  },
  options: {
    plugins: { legend: { display: false } },
    scales: {
      y: { beginAtZero: true, ticks: { stepSize: 2 } }
    },
    maintainAspectRatio: false
  }
});


/* Calendar Display */
const monthLabel = document.getElementById("monthLabel");
const calendarGrid = document.getElementById("calendarGrid");

let date = new Date();
function loadCalendar() {
  calendarGrid.innerHTML = "";
  const year = date.getFullYear();
  const month = date.getMonth();
  monthLabel.textContent = date.toLocaleDateString('en-US', {month:'long', year:'numeric'});

  const days = new Date(year, month+1, 0).getDate();
  const moodMap = ["happy","sad","anxiety"];

  for(let i=1;i<=days;i++){
    const d = document.createElement("div");
    d.className = `calendar-day ${moodMap[Math.floor(Math.random()*3)]}`;
    d.textContent = i;
    calendarGrid.appendChild(d);
  }
}
document.getElementById("prevMonth").onclick = ()=>{ date.setMonth(date.getMonth()-1); loadCalendar(); }
document.getElementById("nextMonth").onclick = ()=>{ date.setMonth(date.getMonth()+1); loadCalendar(); }
loadCalendar();

/* Attendance Sample */
document.getElementById("attendanceBody").innerHTML = `
<tr><td>1</td><td>Sarah</td><td>HR</td><td>2025-01-02</td><td>Present</td></tr>
`;
