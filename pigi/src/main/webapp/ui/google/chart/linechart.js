google.load('visualization', '1', {packages: ['corechart']});
google.setOnLoadCallback(drawChart);

// Options for the chart, which displays the complexities over time.
var chartOptions = {
	curveType: "function", // Make edges soft
	title: 'Average complexity', // Title
	min: 0,
	max: 50,
	vAxis: {
		viewWindowMode: 'explicit', // Take care no - complexities are shown
		viewWindow: {
			min: 0,
			},
		minValue: 0,
		},
	animation:{ // Some animation
		duration: 500,
		easing: 'inandout',
		}
	};

// The chart is used to draw the entire chart
var chart;

/*
	This function is called when the page is loading. It will create the first 
	column in the datastore (the Revisions) which is an incrementing number.
	It also fills the chart variable with the actual data
 */
function drawChart() {

	// Datatabel to store chart data in
  chartData = new google.visualization.DataTable(); 
	
	// Add the first column, i.e., revision
	chartData.addColumn('number', 'Revision'); // Implicit domain label col.
		
	// Get the element from HTML where to put chart in
  chart = new google.visualization.LineChart(document.getElementById('chart_div'));

}

/*
	Function to get the column number from the repository name. The chartData uses
	numbers (indeces) to manage the different repository
 */
function getRepositoryIndex(repository) {
	
	// Value to return (-1 if no repository is found)
	var repositoryIndex = -1;
	
	// Check all columns and compare the name.
	for(var i = 1; i < chartData.getNumberOfColumns() ; i++) {

		if (chartData.getColumnLabel(i) == repository) {
			
			return i;
			
		}
		
	}
	
	return repositoryIndex;
	
}

/*
	Function to actually insert data into the chartdata object. This is done according
	to repository, revision and complexity.
 */
function addComplexity(repository, revision, complexity) {

	// When the row does not yet exist, create it.
	while(revision >= chartData.getNumberOfRows()) {

		chartData.addRows(1)

		chartData.setValue(chartData.getNumberOfRows()-1, 0, chartData.getNumberOfRows()-1);

	}

	// Find the columnindex of the repository in the chartData object
	var repositoryIndex = getRepositoryIndex(repository);
	
	// If it hasn't been found, add it.
	if(repositoryIndex == -1) {
		
		chartData.addColumn('number', repository);
		
		repositoryIndex = getRepositoryIndex(repository);

	}
	
	// Put the data in the chartData object
	chartData.setCell(revision, repositoryIndex, complexity);
   
}

/*
	Function to draw the actual graph.
 */
function drawMe() {
	
	chart.draw(chartData, chartOptions);
	
}

