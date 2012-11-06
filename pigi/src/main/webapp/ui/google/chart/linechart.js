google.load('visualization', '1', {packages: ['corechart']});
google.setOnLoadCallback(drawChart);

var chartOptions = {
	curveType: "function",
	title: 'Average complexity',
	min: 0,
	max: 50,
	vAxis: {
		viewWindowMode: 'explicit',
		viewWindow: {
			min: 0,
			},
		minValue: 0,
		},
	animation:{
		duration: 500,
		easing: 'inandout',
		}
	};
var chart;

function drawChart() {
  chartData = new google.visualization.DataTable();
	
	chartData.addColumn('number', 'Revision'); // Implicit domain label col.
		
  chart = new google.visualization.LineChart(document.getElementById('chart_div'));

}

function getRepositoryIndex(repository) {
	
	var repositoryIndex = -1;
	
	for(var i = 1; i < chartData.getNumberOfColumns() ; i++) {
		
		if (chartData.getColumnLabel(i) == repository) {
			
			repositoryIndex = i;
			
			i = chartData.getNumberOfColumns(); // Break
			
		}
		
	}
	
	return repositoryIndex;
	
}

function addComplexity(repository, revision, complexity) {

	while(revision >= chartData.getNumberOfRows()) {
		chartData.addRows(1)
		chartData.setValue(chartData.getNumberOfRows()-1, 0, chartData.getNumberOfRows()-1);
	}

	var repositoryIndex = getRepositoryIndex(repository);
		
	if(repositoryIndex == -1) {
		chartData.addColumn('number', repository);
		repositoryIndex = getRepositoryIndex(repository);

	}
	
	chartData.setCell(revision, repositoryIndex, complexity);
   
}

function drawMe() {
	chart.draw(chartData, chartOptions);
}

