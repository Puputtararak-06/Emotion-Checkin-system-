function createChart(id) {
  return new Chart(document.getElementById(id), {
    type: 'line',
    data: {
      labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'],
      datasets: [{
        data: [5, 2, 3, 2, 6],
        borderColor: "#333",
        borderWidth: 1,
        pointRadius: 3
      }]
    },
    options: { plugins: { legend: { display: false } }, scales: { x: { display: false }, y: { display: false }} }
  });
}

createChart("chart1");
createChart("chart2");
createChart("chart3");
