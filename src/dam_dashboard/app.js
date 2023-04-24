const timestampArray = new Array();
const levelsArray = new Array();
let chart = null;

$(document).ready(function(){
    getJsonData();
    setInterval(getJsonData, 5000);
});

function getJsonData(){
    if(chart != null){
        chart.destroy();
    }
    $.getJSON("/data", function(result){
        $.each(result, function(i, field){

          switch(i){
            case "state":
                updateView(field);
                break;

            case "dam_opening":
                $("strong#dam_opening").text(field + "%");
                break;

            case "levels":
                generateDataset(field);
                createChart();
                break;  
          }
        });
      });
}


function generateDataset(field){
    $.each(field, function(timestamp, level){
        parsedTimestamp = parseTimestamp(timestamp);

        if(timestampArray.length >= 20){
            timestampArray.unshift(parsedTimestamp);
            levelsArray.unshift(level);

            timestampArray.pop();
            levelsArray.pop();
        }
        else {
            timestampArray.unshift(parsedTimestamp);
            levelsArray.unshift(level);
        }
      });
}

function parseTimestamp(timestamp){
   return new Date(Date.parse(timestamp)).toLocaleTimeString();
   //return secondsTimestamp.getHours() + ":" + secondsTimestamp.getMinutes() + ":" + secondsTimestamp.getSeconds();
}

function createChart(){
    var ctx = document.getElementById('levels_chart').getContext('2d');
    chart = new Chart(ctx, {

    type: 'line',

    data: {
        labels: timestampArray,
        datasets: [{
            label: 'Dam Water Level',
            fill: false,
            borderColor: 'rgb(0, 128, 255)',
            data: levelsArray
        }]
    },

    options: {}
    });
}

function updateView(state){
    switch(state){
        case "MANUAL":
            $("strong#state").text("ALARM");
            $("strong#state").css("color", "red");

            $("strong#manual_mode").text("Enabled");
            $("strong#manual_mode").css("color", "#29A847");
            $("p#manual_mode").show();
            $("p#dam_opening").show();
            $("canvas").show();
            break;
        
        case "NORMAL":
            $("strong#state").text(state);
            $("strong#state").css("color", "#29A847");
            $("p#manual_mode").hide();
            $("p#dam_opening").hide();
            $("canvas").hide();
            break;
        
        case "PREALARM":
            $("strong#state").text(state);
            $("strong#state").css("color", "#C9D148");
            $("p#dam_opening").hide();
            $("p#manual_mode").hide();
            $("canvas").show();
            break;
        
        case "ALARM":
            $("strong#state").text(state);
            $("strong#state").css("color", "red");

            $("strong#manual_mode").text("Disabled");
            $("strong#manual_mode").css("color", "red");
            $("p#manual_mode").show();
            $("p#dam_opening").show();
            $("canvas").show();
            break;
    }     
}