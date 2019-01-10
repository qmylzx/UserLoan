//Mychart1
new Chart(document.getElementById("myChart1").getContext('2d'), {
    type: 'bar',
    data: {
        labels: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
        datasets: [{
            label: '',
            type: 'bar',
            data: [12, 15, 3, 5, 2, 3],
            backgroundColor: [
                'rgba(255, 99, 132, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)'
            ],
            borderColor: [
                'rgba(255,99,132,1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)'
            ],
            borderWidth: 1
        }]
    },
  options: {
    legend: { display: false },
    title: {
      display: true,
      text: ''
    }
  }
});


//Mychart3
new Chart(document.getElementById("myChart3"), {
    type: 'bubble',
    data: {
      datasets: [
        {
          label: ["China"],
          backgroundColor: "rgba(255,221,50,0.2)",
          borderColor: "rgba(255,221,50,1)",
          data: [{
            x: 69017,
            y: 5.245,
            r: 15
          }]
        }, {
          label: ["Denmark"],
          backgroundColor: "rgba(60,186,159,0.2)",
          borderColor: "rgba(60,186,159,1)",
          data: [{
            x: 258702,
            y: 7.526,
            r: 10
          }]
        }, {
          label: ["Germany"],
          backgroundColor: "rgba(0,0,0,0.2)",
          borderColor: "#000",
          data: [{
            x: 759083,
            y: 6.994,
            r: 7
          }]
        }, {
          label: ["Japan"],
          backgroundColor: "rgba(193,46,12,0.2)",
          borderColor: "rgba(193,46,12,1)",
          data: [{
            x: 891877,
            y: 5.921,
            r: 20
          }]
        }
      ]
    },
    options: {
      legend: { display: false },
      title: {
        display: true,
        text: ''
       } 
    }
});

//Mychart4
new Chart(document.getElementById("myChart4").getContext('2d'), {
  type: 'doughnut',
  data: {
    labels: ["M", "T", "W", "T", "F", "S", "S"],
    datasets: [{
      backgroundColor: [
        "#2ecc71",
        "#3498db",
        "#95a5a6",
        "#9b59b6",
        "#f1c40f",
        "#e74c3c",
        "#34495e"
      ],
      data: [12, 19, 3, 17, 28, 24, 7]
    }]
  },
  options: {
      legend: { display: false },
      title: {
        display: true,
        text: ''
       } 
    }
});

// Polar chart
new Chart(document.getElementById("polar-chart").getContext('2d'), {
  type: 'polarArea',
  data: {
    labels: ["M", "T", "W", "T", "F", "S", "S"],
    datasets: [{
      backgroundColor: [
        "#2ecc71",
        "#3498db",
        "#95a5a6",
        "#9b59b6",
        "#f1c40f",
        "#e74c3c",
        "#34495e"
      ],
      data: [12, 19, 3, 17, 28, 24, 7]
    }]
  },
  options: {
    legend: { display: false },
    title: {
      display: true,
      text: ''
     } 
  }
});