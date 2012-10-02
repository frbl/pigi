google.load('visualization', '1', {packages: ['corechart']});
google.setOnLoadCallback(drawChart);

var chartOptions = {
	title: 'Average complexity',
	min: 0,
	max: 50,
	animation:{
		duration: 500,
		easing: 'inandout',
		}
	};
var chart;

function drawChart() {
  chartData = new google.visualization.DataTable();
	chartData.addColumn('number', 'Revision'); // Implicit domain label col.
	chartData.addColumn('number', 'Complexity'); // Implicit series 1 data col.
	
  chart = new google.visualization.LineChart(document.getElementById('chart_div'));
  chart.draw(chartData, chartOptions);
}

function addComplexity(revision, complexity) {
	
   //chartData.setValue(1, 0, chartData.getValue(0, 0) + dir * 25);
   //chartData.setValue(1, 1, chartData.getValue(0, 1) + dir * 20);
	chartData.addRow(
	  [revision,complexity]
		);
   chart.draw(chartData, chartOptions);
}


