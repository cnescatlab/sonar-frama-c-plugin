//var Highcharts = require('https://code.highcharts.com/highcharts.src.js');

window.registerExtension('framac/static_dashboard_framac', function (options) {
	
  // let's create a flag telling if the page is still displayed
  var isDisplayed = true;

  console.log(options);

  // then do a Web API call to the /api/issues/search to get the number of issues
  // we pass `resolved: false` to request only unresolved issues
  // and `componentKeys: options.component.key` to request issues of the given project
  window.SonarRequest.getJSON('/api/issues/search', {
    resolved: false,
    componentKeys: options.component.key
  }).then(function (response) {

    // once the request is done, and the page is still displayed (not closed already)
    if (isDisplayed) {

      console.log(options.component.key);

      // let's create an `h2` tag and place the text inside
      var header = document.createElement('h2');
//      header.textContent = 'The project ' + options.component.key + ' has ' + response.total + ' issues';
      header.textContent = 'Frama-C dashboard';
      
      var script = document.createElement('script');
      script.src = "https://code.highcharts.com/highcharts.src.js";
      
      var script2 = document.createElement('script');
      script2.src = "https://code.highcharts.com/modules/bullet.js";
      
      var graphDiv1 = document.createElement('div');
      graphDiv1.setAttribute("id", "graph1");
      
      var graphDiv2 = document.createElement('div');
      graphDiv2.setAttribute("id", "graph2");
      
      var graphDiv3 = document.createElement('div');
      graphDiv3.setAttribute("id", "graph3");
      
      var graphDiv4 = document.createElement('div');
      graphDiv4.setAttribute("id", "graph4");
      
      var scriptBuildgraph = document.createElement('script');
      scriptBuildgraph.textContent = 'var Highcharts = require();buildgraph(Highcharts);';

      // append just created element to the container
      header.appendChild(script);
      header.appendChild(script2);
      header.appendChild(graphDiv1);
      header.appendChild(graphDiv2);
      header.appendChild(graphDiv3);
      header.appendChild(graphDiv4);
      header.appendChild(scriptBuildgraph);
      
      options.el.appendChild(header); 
    }
  });

  // return a function, which is called when the page is being closed
  return function () {

    // we unset the `isDisplayed` flag to ignore to Web API calls finished after the page is closed
    isDisplayed = false;
  };
});

function buildgraph(libHighcharts){
	libHighcharts.setOptions({
	    chart: {
	        inverted: true,
	        marginLeft: 135,
	        type: 'bullet'
	    },
	    title: {
	        text: null
	    },
	    legend: {
	        enabled: false
	    },
	    yAxis: {
	        gridLineWidth: 0
	    },
	    plotOptions: {
	        series: {
	            pointPadding: 0.25,
	            borderWidth: 0,
	            color: '#000',
	            targetOptions: {
	                width: '200%'
	            }
	        }
	    },
	    credits: {
	        enabled: false
	    },
	    exporting: {
	        enabled: false
	    }
	});

	libHighcharts.chart('graph1', {
	    chart: {
	        marginTop: 40
	    },
	    title: {
	        text: '2017 YTD'
	    },
	    xAxis: {
	        categories: ['<span class="hc-cat-title">Revenue</span><br/>U.S. $ (1,000s)']
	    },
	    yAxis: {
	        plotBands: [{
	            from: 0,
	            to: 150,
	            color: '#666'
	        }, {
	            from: 150,
	            to: 225,
	            color: '#999'
	        }, {
	            from: 225,
	            to: 9e9,
	            color: '#bbb'
	        }],
	        title: null
	    },
	    series: [{
	        data: [{
	            y: 275,
	            target: 250
	        }]
	    }],
	    tooltip: {
	        pointFormat: '<b>{point.y}</b> (with target at {point.target})'
	    }
	});

	libHighcharts.chart('graph2', {
	    xAxis: {
	        categories: ['<span class="hc-cat-title">Profit</span><br/>%']
	    },
	    yAxis: {
	        plotBands: [{
	            from: 0,
	            to: 20,
	            color: '#666'
	        }, {
	            from: 20,
	            to: 25,
	            color: '#999'
	        }, {
	            from: 25,
	            to: 100,
	            color: '#bbb'
	        }],
	        labels: {
	            format: '{value}%'
	        },
	        title: null
	    },
	    series: [{
	        data: [{
	            y: 22,
	            target: 27
	        }]
	    }],
	    tooltip: {
	        pointFormat: '<b>{point.y}</b> (with target at {point.target})'
	    }
	});


	libHighcharts.chart('graph3', {
	    xAxis: {
	        categories: ['<span class="hc-cat-title">New Customers</span><br/>Count']
	    },
	    yAxis: {
	        plotBands: [{
	            from: 0,
	            to: 1400,
	            color: '#666'
	        }, {
	            from: 1400,
	            to: 2000,
	            color: '#999'
	        }, {
	            from: 2000,
	            to: 9e9,
	            color: '#bbb'
	        }],
	        labels: {
	            format: '{value}'
	        },
	        title: null
	    },
	    series: [{
	        data: [{
	            y: 1650,
	            target: 2100
	        }]
	    }],
	    tooltip: {
	        pointFormat: '<b>{point.y}</b> (with target at {point.target})'
	    },
	    credits: {
	        enabled: true
	    }
	});
	
};
