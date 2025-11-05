// dashboard.js
// - builds bar chart (Chart.js)
// - builds sliding calendar
// - builds attendance tables
// - all sizing fixed so chart does NOT stretch vertically

/* ---------------------- Mock Data Loader ---------------------- */
function loadMockData() {
  const today = new Date();
  const yyyy = today.getFullYear();
  const mm = String(today.getMonth() + 1).padStart(2, '0');

  return [
    { date: `${yyyy}-${mm}-02`, mood: 'positive', emoji: '😀', name:'Alice', role:'Staff' },
    { date: `${yyyy}-${mm}-04`, mood: 'neutral',  emoji: '😐', name:'Bob', role:'Dev' },
    { date: `${yyyy}-${mm}-08`, mood: 'negative', emoji: '😢', name:'Charlie', role:'HR' },
    { date: `${yyyy}-${mm}-11`, mood: 'positive', emoji: '🤩', name:'Dana', role:'PM' },
    { date: `${yyyy}-${mm}-15`, mood: 'positive', emoji: '😀', name:'Eve', role:'Staff' },
  ];
}

/* ---------------------- Chart ---------------------- */
function renderBarChart(canvasEl, history) {
  const counts = { positive: 0, neutral: 0, negative: 0 };
  history.forEach(h => counts[h.mood]++);

  const data = {
    labels: ['Negative', 'Neutral', 'Positive'],
    datasets: [{
      label: 'Mood Count',
      data: [counts.negative, counts.neutral, counts.positive],
      backgroundColor: ['#FF6B6B', '#9AA0B3', '#5CC96B'],
      borderRadius: 10
    }]
  };

  if (canvasEl._chart) canvasEl._chart.destroy();

  canvasEl._chart = new Chart(canvasEl, {
    type: 'bar',
    data,
    options: {
      responsive: true,
      maintainAspectRatio: false,   // ✅ STOP chart from stretching insanely
      scales: {
        y: { beginAtZero: true, ticks: { stepSize: 1, color: "#444" } },
        x: { grid: { display: false }, ticks: { color: "#444" } }
      },
      plugins: { legend: { display: false } }
    }
  });
}

/* ---------------------- Calendar ---------------------- */
function buildCalendar(containerEl, year, month, history) {
  containerEl.innerHTML = '';

  const first = new Date(year, month-1, 1);
  const weekdayOfFirst = first.getDay();
  const daysInMonth = new Date(year, month, 0).getDate();

  const weekNames = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];
  const headerRow = document.createElement('div');
  headerRow.style.display = 'grid';
  headerRow.style.gridTemplateColumns = 'repeat(7,1fr)';
  headerRow.style.gap = '6px';
  headerRow.style.marginBottom = '8px';

  weekNames.forEach(w => {
    const hh = document.createElement('div');
    hh.style.textAlign = 'center';
    hh.style.fontSize = '12px';
    hh.style.opacity = '.7';
    hh.textContent = w;
    headerRow.appendChild(hh);
  });
  containerEl.appendChild(headerRow);

  const grid = document.createElement('div');
  grid.style.display = 'grid';
  grid.style.gridTemplateColumns = 'repeat(7,1fr)';
  grid.style.gap = '10px';

  for (let i = 0; i < weekdayOfFirst; i++){
    const blank = document.createElement('div');
    blank.className = 'calendar-day';
    blank.style.background = 'transparent';
    grid.appendChild(blank);
  }

  for (let d = 1; d <= daysInMonth; d++){
    const cell = document.createElement('div');
    cell.className = 'calendar-day';
    cell.textContent = d;

    const yyyy_mm_dd = `${year}-${String(month).padStart(2,'0')}-${String(d).padStart(2,'0')}`;
    const matched = history.find(h => h.date === yyyy_mm_dd);

    if (matched) {
      cell.classList.add('hasMood');
      cell.textContent = matched.emoji;
      cell.title = `${matched.name || 'User'} — ${matched.mood}`;
      cell.dataset.date = yyyy_mm_dd;
      cell.dataset.mood = matched.mood;
    }
    grid.appendChild(cell);
  }

  containerEl.appendChild(grid);
}

/* ---------------------- Attendance Table ---------------------- */
function renderAttendanceTable(wrapperEl, history) {
  const table = document.createElement('table');
  table.innerHTML = `
    <thead>
      <tr style="text-align:left;color:#666;">
        <th style="padding:10px 8px">ID</th>
        <th style="padding:10px 8px">Name</th>
        <th style="padding:10px 8px">Role</th>
        <th style="padding:10px 8px">Date</th>
        <th style="padding:10px 8px">Mood</th>
      </tr>
    </thead>
    <tbody></tbody>
  `;

  const tbody = table.querySelector('tbody');
  history.forEach((h, idx) => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td style="padding:10px 8px;border-top:1px solid #eee">${idx+1}</td>
      <td style="padding:10px 8px;border-top:1px solid #eee">${h.name}</td>
      <td style="padding:10px 8px;border-top:1px solid #eee">${h.role}</td>
      <td style="padding:10px 8px;border-top:1px solid #eee">${h.date}</td>
      <td style="padding:10px 8px;border-top:1px solid #eee">${h.emoji} ${h.mood}</td>
    `;
    tbody.appendChild(tr);
  });

  wrapperEl.innerHTML = '';
  wrapperEl.appendChild(table);
}

/* ---------------------- Init ---------------------- */
document.addEventListener('DOMContentLoaded', () => {
  const history = loadMockData();

  const chartCanvas = document.getElementById('moodChart');
  renderBarChart(chartCanvas, history);

  const calContainer = document.getElementById('calendarContainer');
  let current = new Date();
  function refreshCalendar() {
    buildCalendar(calContainer, current.getFullYear(), current.getMonth()+1, history);
  }
  refreshCalendar();

  document.getElementById('calPrev').addEventListener('click', () => {
    current.setMonth(current.getMonth()-1);
    refreshCalendar();
  });
  document.getElementById('calNext').addEventListener('click', () => {
    current.setMonth(current.getMonth()+1);
    refreshCalendar();
  });

  renderAttendanceTable(document.getElementById('attendanceTableWrapper'), history);
  renderAttendanceTable(document.getElementById('attendanceDetail'), history);
});
