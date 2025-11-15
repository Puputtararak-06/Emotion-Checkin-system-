// js/dashboard.js  (Employee Dashboard Only)

document.addEventListener('DOMContentLoaded', () => {
  const chartCanvas = document.getElementById('moodTrendChart');
  const historyWrapper = document.getElementById('historyTableWrapper');

  // 1) ดึงข้อมูล check-in ทั้งหมดจาก localStorage
  const checkins = loadCheckinsFromLocalStorage();

  // 2) ถ้าไม่มีข้อมูลเลย ให้แสดงข้อความ แล้วไม่ต้อง render chart
  if (!checkins.length) {
    if (historyWrapper) {
      historyWrapper.innerHTML = `
        <p style="font-size:14px; opacity:.8;">
          You haven't submitted any check-ins yet. Try checking in on the <a href="checkin.html">Check-in</a> page.
        </p>
      `;
    }
    if (chartCanvas) {
      chartCanvas.parentElement.innerHTML = `
        <p style="font-size:14px; opacity:.8;">
          No check-in data available to display the trend yet.
        </p>
      `;
    }
    return;
  }

  // 3) สร้าง line chart จากข้อมูล
  if (chartCanvas) {
    renderMoodTrendChart(chartCanvas, checkins);
  }

  // 4) สร้างตาราง history
  if (historyWrapper) {
    renderHistoryTable(historyWrapper, checkins);
  }
});

/**
 * ดึงข้อมูล check-in ทั้งหมดจาก localStorage
 * ฟอร์แมตที่หน้า checkin.js เซฟ:
 *   key: "checkin-YYYY-MM-DD"
 *   value: { date, mood, emoji, note }
 */
function loadCheckinsFromLocalStorage() {
  const items = [];

  for (let i = 0; i < localStorage.length; i++) {
    const key = localStorage.key(i);
    if (!key || !key.startsWith('checkin-')) continue;

    try {
      const raw = localStorage.getItem(key);
      if (!raw) continue;

      const data = JSON.parse(raw);

      // กัน null / undefined
      if (!data.date || !data.mood) continue;

      items.push({
        date: data.date,
        mood: data.mood,
        emoji: data.emoji || '',
        note: data.note || ''
      });
    } catch (err) {
      console.warn('Cannot parse checkin data for key', key, err);
    }
  }

  // sort ตามวันที่น้อยไปมาก สำหรับกราฟ
  items.sort((a, b) => a.date.localeCompare(b.date));
  return items;
}

/**
 * Map emotion label -> numeric score (ใช้ทำกราฟ)
 * Negative = 1, Neutral = 2, Positive = 3
 */
function mapMoodToScore(label) {
  const negative = ['Sad', 'Angry', 'Disappointed', 'Stressed', 'Exhausted'];
  const neutral  = ['Neutral', 'Blank', 'Tired', 'Low Energy', 'Unsure'];
  const positive = ['Content', 'Happy', 'Excited', 'Inspired', 'Loved'];

  if (negative.includes(label)) return 1;
  if (neutral.includes(label))  return 2;
  if (positive.includes(label)) return 3;

  // default กลาง ๆ
  return 2;
}

/**
 * วาดกราฟ Line Chart ด้วย Chart.js
 */
function renderMoodTrendChart(canvasEl, checkins) {
  const labels = checkins.map(c => c.date);
  const scores = checkins.map(c => mapMoodToScore(c.mood));

  const ctx = canvasEl.getContext('2d');

  // eslint-disable-next-line no-undef
  new Chart(ctx, {
    type: 'line',
    data: {
      labels,
      datasets: [{
        label: 'Emotion Level',
        data: scores,
        fill: false,
        borderColor: '#7aa2ff',
        backgroundColor: '#7aa2ff',
        pointRadius: 4,
        pointHoverRadius: 6,
        tension: 0.3
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        y: {
          min: 1,
          max: 3,
          ticks: {
            stepSize: 1,
            callback: (value) => {
              if (value === 1) return 'Negative';
              if (value === 2) return 'Neutral';
              if (value === 3) return 'Positive';
              return value;
            }
          },
          title: {
            display: true,
            text: 'Emotion Level'
          }
        },
        x: {
          title: {
            display: true,
            text: 'Date'
          }
        }
      },
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          callbacks: {
            label: (context) => {
              const idx = context.dataIndex;
              const item = checkins[idx];
              return `${item.mood}${item.note ? ' — ' + item.note : ''}`;
            }
          }
        }
      }
    }
  });
}

/**
 * แสดงตาราง history ของ check-in
 */
function renderHistoryTable(container, checkins) {
  // เรียงใหม่ให้ "ล่าสุดอยู่บนสุด" ในตาราง
  const sorted = [...checkins].sort((a, b) => b.date.localeCompare(a.date));

  let html = `
    <table>
      <thead>
        <tr>
          <th style="width:110px;">Date</th>
          <th style="width:70px;">Emoji</th>
          <th style="width:120px;">Mood</th>
          <th>Comment</th>
        </tr>
      </thead>
      <tbody>
  `;

  sorted.forEach(item => {
    html += `
      <tr>
        <td>${item.date}</td>
        <td style="font-size:20px;">${item.emoji || ''}</td>
        <td>${item.mood}</td>
        <td>${item.note || '-'}</td>
      </tr>
    `;
  });

  html += `
      </tbody>
    </table>
  `;

  container.innerHTML = html;
}
