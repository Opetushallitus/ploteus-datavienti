var interval = false;
var processOwner = false;
var processInProgress = false;


function start() {
	processOwner = true;
	$("#downloadButton").prop("disabled", true);
	$("#startButton").prop("disabled", true);
	$.ajax({
		type : "GET",
		url : "koulutus/",
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function(data) {
			console.log("data: " + data);
		}
	});
	if(interval == false){
		interval = setInterval("checkStatus()", 1000);
	}
}
function checkStatus() {
	$.ajax({
		type : "GET",
		url : "koulutus/status",
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function(data) {
			if (processOwner || data.status == 0.00 || data.status == 1.00) {
				if(processOwner){
					$("#outputTextarea").html(data.frontendOutput + "\n" + data.statusText);
					if(data.durationEstimate != 0.00){
						$("#outputTextarea").html($("#outputTextarea").html() + "\t\t" + "Arvioitu kesto: " + Math.ceil(data.durationEstimate) + " minuuttia");
					}
					document.getElementById("outputTextarea").scrollTop = document.getElementById("outputTextarea").scrollHeight;
					$("#progressbar").css("width",Math.round(data.status * 100) + "%");
					$("#progressbar").attr('aria-valuenow', Math.round(data.status * 100));
					$("#progressbar").html(Math.round(data.status * 100) + "%");
					if(data.statusText != null){
						//$("#infoDiv").html("<i>" + data.statusText + "</i>");
					}
					/*if (data.durationEstimate == 0.0) {
						$("#infoTimeDiv").html("<i>Arvioitu kesto: - </i>");
					} else {
						$("#infoTimeDiv").html("<i>Arvioitu kesto: " + Math.ceil(data.durationEstimate) + " minuuttia</i>");
					}*/
					if (data.status == 1.00) {
						/*clearInterval(interval);
						interval = false;*/
						$("#downloadButton").prop("disabled", false);
						processOwner = false;
						$("#outputTextarea").html(data.frontendOutput);
						$("#infoDiv").html("<i>" + data.statusText + "</i>");
					}else if(data.status == 0.00){
						processOwner = false;
						$("#infoDiv").html("<i>" + data.statusText + "</i>");
					}
				}
				else{
					if(processInProgress){
						$("#infoTimeDiv").html("<i>" + Math.round(data.status * 100) + "%" + "</i>");
						$("#infoDiv").html("<i>Prosessi valmis, päivittäkää sivu</i>");
					}
				}
			} else {
				processInProgress = true;
				$("#startButton").prop("disabled", true);
				$("#progressbar").css("width", Math.round(0) + "%");
				$("#progressbar").attr('aria-valuenow', Math.round(0 * 100));
				$("#progressbar").html(Math.round(data.status * 100) + "%");
				$("#infoDiv").html("<i>Prosessi kesken, odottakaa vuoroa</i>");
				$("#infoTimeDiv").html("<i>" + Math.round(data.status * 100) + "%" + "</i>");
			}
		}
	});
}