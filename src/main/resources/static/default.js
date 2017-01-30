var prefix = window.location.href;
var datakey = "";
var stats = "";

var RunQuery = function() {
	var endpoint = document.getElementById("endpoint").value;
	var limit = document.getElementById("limit").value;
	var query = document.getElementById("queryfield").value;
	$.ajax({
		type : 'POST',
		url : prefix + '/rdfcf/query',
		data : {
			"query" : query,
			"limit" : limit,
			"endpoint" : endpoint
		},
		dataType : 'json',
		async : true,
		success : function(result) {
			datakey = result.datakey;
			if (datakey !== "") {
				document.getElementById("delete").style.display = "block";
				document.getElementById("label_datakey").innerHTML = result.datakey;
				document.getElementById("filter").style.display = "block";
				document.getElementById("download").style.display = "none";
				// btn_filter is switched in RDFAnalyze
				AnalyzeRDF();
			} else {
				alert("Query Error");
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert(jqXHR.status + ' ' + jqXHR.responseText);
		}
	});
}

var AnalyzeRDF = function(property) {
	$.ajax({
		type : 'GET',
		url : prefix + '/rdfcf/analyze/' + datakey,
		dataType : 'json',
		async : true,
		success : function(result) {
			stats = result
			out = "<table style=\"witdh: 99%;\">";
			out +="<tr><th class=\"st_1\">Property</th><th class=\"st_1\">Datatype</th><th class=\"st_2\">Count</th></tr>";
			for (var i = 0; i < result.properties; i ++ ) {
				
				out += "<tr><td>"+result[i].property+"</td><td>";
				
				for (var ii = 0; ii < result[i].numberofdatatypes; ii++) {
					out += "<input type=\"checkbox\" class=\"datatypes\" name=\""+result[i].property+"\"";
					out += "value=\""+result[i].datatypes[ii].name+"\"/>"+result[i].datatypes[ii].name+"<br>";
				}
				out += "</td><td>";
				for (var ii = 0; ii < result[i].numberofdatatypes; ii++) {
					out += result[i].datatypes[ii].value+"<br>";
				}
				out += "</td></tr>";
			}
			out += "</table>";
			document.getElementById("filter").style.display = "block";
			document.getElementById("statistics").innerHTML = out;

		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert(jqXHR.status + ' ' + jqXHR.responseText);
		}
	});
}

var FilterAttr = function() {
	var result = [];
	var boxes = $('.datatypes:checked');
	var out = "";
	var tmp_p = "";
	var tmp = "";
	if(boxes.length > 0) {
		tmp_p = boxes[0].getAttribute("name");
		tmp = boxes[0].getAttribute("name");
	}
	for(i = 0; i < boxes.length; i++) {
		//TODO how to split p from d
		if(boxes[i].getAttribute("name") != tmp_p) {
			result.push(tmp);
			tmp_p = boxes[i].getAttribute("name");
			tmp = tmp_p+";"+boxes[i].value;
		} else {

			tmp += ";" + boxes[i].value;
		}
	}
	result.push(tmp);

	return result;
}

var RunFilter = function() {

	var remove_duplicates = false;
	if(document.getElementById("remove_duplicates").checked) {
		remove_duplicates = true;
	};
	var consistent = false;
	if(document.getElementById("consistent").checked) {
		consistent = true;
	};
	var rdfunit_params = "skip"
	if(document.getElementById("isRDFUnitSelected").checked) {
		rdfunit_params = document.getElementById("rdfunit_schema").value;
		rdfunit_params += document.getElementById("rdfunit_args").value;
	};
	
	var filter = FilterAttr();
	
	var po_ar = FilterAttr();
	
	//data: "{'data1':'" + value1+ "', 'data2':'" + value2+ "', 'data3':'" + value3+ "'}",
//	
//	for(i = 0; i < po_ar.length; i++) {
//		if(i == po_ar.length-1)
//			filter += "'filter':'"+po_ar[i]+"'";
//		else {
//			filter += "'filter':'"+po_ar[i]+"', "
//		}
//	}
	
	//document.getElementById("test").innerHTML = "---"+remove_duplicates+"---"+consistent+"---"+rdfunit_params;
	$.ajax({
		type : "POST",
		url : prefix + "/rdfcf/filter/" + datakey,
		data : {
			filter,
			remove_duplicates,
			"consistent" : consistent,
			"rdfunit_params" : rdfunit_params
		},
		dataType : 'json',
		async : true,
		success : function(result) {
			if (result.message == "filtered") {
				document.getElementById("download").style.display = "block";
				document.getElementById("test").innerHtml += "done ";
			} else {
				alert("Filter Error");
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('Error'+jqXHR.status + ' 222' + jqXHR.responseText);
		}
	});
}

var DownloadRDF = function() {
	var formatlist = document.getElementById("menue_format");
	var format = formatlist.options[formatlist.selectedIndex].value;
	window.open(prefix + "/rdfcf/show/" + datakey + "?format=" + format,
			"_blank");
}

var DeleteRDF = function() {
	$.ajax({
		type : "DELETE",
		url : prefix + "rdfcf/delete/" + datakey,
		async : true,
		success : function(result) {
			if (result.message == "failed") {
				alert("Could not delete dataset. \n Dataset not found.");
			} else {
				alert("deleted");
				location.reload();
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert(jqXHR.status + ' 222' + jqXHR.responseText);
		}
	});
}



var Foobar = function() {
	var checkboxValues = $('.datatypes:checked').map(function() {
	    return this.value;
	}).get();
//	var datatypes = "";
//	$('.datatypes:checked').each(function() {
//		   
//		});
	
	document.getElementById("test").innerHTML = window.location.href  ;
}



