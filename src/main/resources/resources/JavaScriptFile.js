document.addEventListener('readystatechange', event => {
	  if (event.target.readyState === 'interactive') {
		  document.getElementById("loadingDiv").style = ""; 
	  }
	  else if (event.target.readyState === 'loading') {
		  document.getElementById("loadingDiv").style = ""; 
	  }
	  
	  else {
		  document.getElementById("loadingDiv").style = "display:none"; 
		  setResourcesForFamilyDD();
	  }
	});
	

function setResourcesForFamilyDD() {
	 var checkboxArr = document.getElementsByName("filtersMap");
		//alert("-->");
		//alert(checkboxArr[0].value);

	var familyResources ;
	var finalResponse = "";
	var resourceCandidatesForFamilyCheckbox = [];

	for (var i = 0; i < checkboxArr.length; i++) {
	      if(checkboxArr[i].checked == true) {
	    		var firstIndex = checkboxArr[i].value.indexOf("[")+1;
	    		var lastIndex = checkboxArr[i].value.indexOf("]");
	    		var familyResources = checkboxArr[i].value.substring(firstIndex, lastIndex);
	    		var resourcesTokens = familyResources.split(",");
	    		for (var j = 0; j < resourcesTokens.length; j++) 
					{
		    			resourceCandidatesForFamilyCheckbox.push(resourcesTokens[j]);
					}
	      }
	}
	
	if(resourceCandidatesForFamilyCheckbox.length==0)
		{
		document.getElementById("resourcesForFamily").innerHTML = finalResponse;
		return;
		}
	//alert("valid cases ->");

	finalResponse += "<b>Resource </b> <select name=\"resourceCandidatesForFamilyCheckbox\" id=\"resourceCandidatesForFamilyCheckbox\" multiple style=\"width: 400px;\"> ";

	finalResponse += " <option value=\""+""+"\"\">"+"---All---"+"</option>";
	
	for (var i = 0; i < resourceCandidatesForFamilyCheckbox.length; i++) {
		//alert(resourceCandidatesForFamilyCheckbox[i]);
		finalResponse+=" <option value=\""+resourceCandidatesForFamilyCheckbox[i]+"\"\">"+resourceCandidatesForFamilyCheckbox[i]+"</option>";
	}
	
	
	//for (var j = 0; j < resourcesTokens.length; j++) {
		//alert(resourcesTokens[j]);

	//	response+="<input type=\"checkbox\" name=\"filters\" value=\""+ resourcesTokens[j] +"\">" + "<label>"+resourcesTokens[j]+"</label>";
	//}
	
	finalResponse +=  "</select>";
	//alert("valid cases ->" + finalResponse);
	
	document.getElementById("resourcesForFamily").innerHTML = finalResponse;

	
}


//document.onreadystatechange = function() { 
   // document.getElementById("loading1").className  = ""; 

    //if (document.readyState !== "complete") { 
        //document.querySelector("body").style.visibility = "hidden"; 
        //document.querySelector("#loading1").style.visibility = "visible"; 
    //} else { 
    //    document.querySelector("#loading1").style.display = "none"; 
       // document.querySelector("body").style.visibility = "visible"; 
   // } 
//}


//https://www.technicalkeeda.com/spring-tutorials/spring-framework-jquery-ajax-request-and-json-response-example
//        data:'firstName=' + $("#firstName").val() + "&lastName;=" + $("#lastName").val() + "&email;=" + $("#email").val(),
function madeAjaxCall(){
    $.ajax({
     type: "post",
     url: "/add-estimate",
     cache: false,    
     
     data:'units=' + $("#units").val() + "&sku=" + $("#sku").val(),
     success: function(response){
    		document.getElementById("result").innerHTML = "Added successfully to estimate!!";
    	    document.getElementById("result").className  = ""; 
    	    document.getElementById("result").style.color = "red";
     },
     error: function(){      
      alert('Error while request..');
     }
    });
   }


function madeAjaxCall1(){
	//var e = document.getElementById("regionDD");
	//var value1 = e.options[e.selectedIndex].value;
	//var text = e.options[e.selectedIndex].text;

		var filterNames = [];
		var inputs = document.getElementsByName('filters');
		for (var i = 0; i < inputs.length; i++) {
			if (inputs[i].checked) {
				var val = inputs[i].value;
				filterNames.push(val);
				//alert(val);
			}
		}

		$.ajax({
			type : "post",
			url : "/filter-service",
			cache : false,
			data : 'filters=' + filterNames + "&regionId="+ $("#regionId").val(),
			success : function(response) {
				//$('#skuDD').load('/sku-list');
				document.getElementById('skuDD').contentWindow.location.reload(true);
				//document.getElementById("result").innerHTML = "Added successfully to estimate!!";
				//document.getElementById("result").className  = ""; 
				//document.getElementById("result").style.color = "red";
			},
			error : function() {
				alert('Error while request..');
			}
		});
	}

function setValue55(source) {
	if(!localStorage.getItem('initData')){
	    $window.localStorage.setItem('initData', JSON.stringify($scope.initData));
	}

}
	function setValue3(source) {
		//alert("-->"+source)
		var e = document.getElementById("regionDD");
		var value1 = e.options[e.selectedIndex].value;
		var text = e.options[e.selectedIndex].text;
		document.getElementById("regionId").value = text;

	}
	

	function printPricingCurrentSku(source) {

		document.getElementById("result").innerHTML = "";

		if (source.includes('skuDD')) {
			document.getElementById("w-input-search1").value = "";
			var e = document.getElementById("skuDD");
			var sku = e.options[e.selectedIndex].value;
			var skuName = e.options[e.selectedIndex].text;
		} else {
			document.getElementById("skuDD").value = "";
			var sku = document.getElementById("serviceId1").value;
			var skuName = document.getElementById("serviceName").value;
		}

		var finalResponse = "";
		var finalResponseJava = "";
		var output = [];
		var outputArrJava = [];

		//alert("-->"+sku)
		var firstIndex = sku.indexOf("[startUsageAmount");
		var lastIndex = sku.indexOf("],aggregationInfo");
		var startUsageAmountToken = sku.substring(firstIndex, lastIndex);
		var input = startUsageAmountToken
				.split(",additionalProperties={}],additionalProperties={}");
		var tokenVal, tokenVal1;

		var firstIndex1 = sku.indexOf("serviceDisplayName");
		var lastIndex1 = sku.indexOf(",resourceFamily");
		var serviceName = sku.substring(firstIndex1, lastIndex1);
		firstIndex1 = serviceName.indexOf("=") + 1;
		lastIndex1 = serviceName.length;
		serviceName = serviceName.substring(firstIndex1, lastIndex1);

		firstIndex1 = sku.indexOf("usageUnitDescription=");
		lastIndex1 = sku.indexOf(",baseUnit=");
		var usageUnit = sku.substring(firstIndex1, lastIndex1);
		firstIndex1 = usageUnit.indexOf("=") + 1;
		lastIndex1 = usageUnit.length;
		usageUnit = usageUnit.substring(firstIndex1, lastIndex1);

		//baseUnitConversionFactor 
		//nanoDivisorFactor = 1000000000

		//unitPrice = nanos / (baseUnitConversionFactor * nanoDivisorFactor)

		firstIndex1 = sku.indexOf("baseUnitConversionFactor=");
		lastIndex1 = sku.indexOf(",displayQuantity");
		var baseUnitConversionFactor = sku.substring(firstIndex1, lastIndex1);
		firstIndex1 = baseUnitConversionFactor.indexOf("=") + 1;
		lastIndex1 = baseUnitConversionFactor.length;
		baseUnitConversionFactor = baseUnitConversionFactor.substring(
				firstIndex1, lastIndex1);

		var nanoDivisorFactor = 1000000000;

		firstIndex1 = sku.indexOf("displayQuantity=");
		lastIndex1 = sku.indexOf(",tieredRates");
		var displayQuantity = sku.substring(firstIndex1, lastIndex1);
		firstIndex1 = displayQuantity.indexOf("=") + 1;
		lastIndex1 = displayQuantity.length;
		displayQuantity = displayQuantity.substring(firstIndex1, lastIndex1);

		var priceTokens;
		var unitPrice;
		var nanos;
		var currencyCode;
		var startUsageAmount;
		var finalPrice;
		var currentToken;
		var outputStr = "";
		var outputStrJava = "";

		for (var i = 0; i < input.length; i++) {
			//alert(input[i]);
			if (input[i].includes('startUsageAmount')) {
				//alert('0->'+input[i]);
				firstIndex = input[i].indexOf("startUsageAmount");
				lastIndex = input[i].length;
				tokenVal = input[i].substring(firstIndex, lastIndex);
				//alert('1->'+tokenVal);
				firstIndex = tokenVal.indexOf("unitPrice");
				lastIndex = tokenVal.indexOf("currencyCode");
				tokenVal1 = tokenVal.substring(firstIndex, lastIndex);
				tokenVal = tokenVal.replace(tokenVal1, "");
				priceTokens = tokenVal.split(",");
				for (var j = 0; j < priceTokens.length; j++) {
					firstIndex1 = priceTokens[j].indexOf("=") + 1;
					lastIndex1 = priceTokens[j].length;
					if (priceTokens[j].includes('nanos')) {
						nanos = priceTokens[j].substring(firstIndex1,
								lastIndex1);
					}
					if (priceTokens[j].includes('currencyCode')) {
						currencyCode = priceTokens[j].substring(firstIndex1,
								lastIndex1);
						if ("USD" === currencyCode.trim()) {
							currencyCode = "$";
						}
					}
					if (priceTokens[j].includes('startUsageAmount')) {
						startUsageAmount = priceTokens[j].substring(
								firstIndex1, lastIndex1);
					}
				}

				unitPrice = nanos / nanoDivisorFactor;
				finalPrice = unitPrice * displayQuantity;
				finalPrice = finalPrice.toFixed(4);
				outputStrJava = "~startUsageAmount:" + startUsageAmount
						+ "~currencyCode:" + currencyCode + "~finalPrice:"
						+ finalPrice + "~displayQuantity:" + displayQuantity
						+ "~usageUnit:" + usageUnit + "</br>";
				//outputStr = ""+ "startUsageAmount: "+ startUsageAmount + " "  +currencyCode + "" + finalPrice + " per"+ " "+ displayQuantity + " " + usageUnit +"<br>";
				outputStr = "" + startUsageAmount + "~" + currencyCode + ""
						+ finalPrice + " per" + " " + displayQuantity + " "
						+ usageUnit + "<br>";

				//alert('outputStr->'+outputStr);
				output.push(outputStr);
				outputArrJava.push(outputStrJava);
			}
		}

		//finalResponse += "<br><br> <b> Service </b> <br>" + serviceName + "<br><br>";
		finalResponse += "<br><br>" + "<b> Rate Card </b> <br>";
		finalResponseJava += "~skuName:" + skuName + "</br>";
		finalResponseJava += "~serviceName:" + serviceName + "</br>";
		var pricingTable = "<table> <tr> <th>Start Usage Amount</th> <th>Cost</th>";

		var slab;
		var cost;
		var pricingTable1 = "";

		//alert('0->'+finalResponse);
		for (i = 0; i < output.length; i++) {
			//alert('output[i]->'+output[i]);
			firstIndex1 = output[i].indexOf("~");
			lastIndex1 = output[i].length;
			slab = output[i].substring(0, firstIndex1);
			cost = output[i].substring(firstIndex1 + 1, lastIndex1);
			pricingTable += "<tr><td>" + slab + "</td>" + "<td>" + cost
					+ "</td></tr>";
			pricingTable1 += output[i];
			//finalResponse += output[i];
		}

		pricingTable += "</table>";
		finalResponse += pricingTable;
		//finalResponse += pricingTable1;
		//finalResponse += sku;

		for (i = 0; i < outputArrJava.length; i++) {
			//alert('output[i]->'+output[i]);
			finalResponseJava += outputArrJava[i];
		}

		//alert('1->'+finalResponse);

		var pricingInfoForm = "<br><b> Add Units to Estimate </b> <br>"
				+ "<form action=\"/estimate\" id=\"usrform1\" method=\"POST\">"
				+ "<input type=\"text\" id=\"units\" size=\"10\">"
				+ "<input type=\"text\" style=\"display: none\" id=\"sku\" name=\"sku\" value=\""+ finalResponseJava +"\"\">"
				+
				//	"<input type=\"text\" style=\"display: none\" id=\"sku\" name=\"sku\" value="" \">"+

				"<input type=\"button\" value=\"Add\" onclick=\"madeAjaxCall();\" >"
				+ "</form>";

		finalResponse += pricingInfoForm;

		//finalResponse += "<br><br><br><br><br><br><br><br><br><br><br><br><b>"+sku;

		document.getElementById("pricingInfoId").innerHTML = finalResponse;
		//document.getElementById("serviceDetailsId").innerHTML = res55;
		
	}

	$(document).ready(function() {
		  
		$('#w-input-search').autocomplete({
			serviceUrl : '/getSuggestions',
			paramName : "searchTerm",
			delimiter : ",",
			onSelect : function(suggestion) {
				cityID = suggestion.data;
				//alert('1:'+cityID)
				serviceId = cityID;
				jQuery("#serviceId").val(cityID);
				return false;
			},
			transformResult : function(response) {
				return {
					suggestions : $.map($.parseJSON(response), function(item) {
						//document.getElementById('serviceId').value = item.serviceId;
						return {
							value : item.displayName,
							data : item.serviceId,
						};
					})
				};
			}
		});

	});

	$(document).ready(function() {
		$('#w-input-search1').autocomplete({
			serviceUrl : '/getSuggestions1',
			paramName : "searchTerm",
			delimiter : ",",
			onSelect : function(suggestion) {
				sku = suggestion.data.skuToString;
				serviceName = suggestion.value;
				//alert('2:'+cityID)
				//serviceId = sku;
				jQuery("#serviceId1").val(sku);
				jQuery("#serviceName").val(serviceName);

				return printPricingCurrentSku('usrform1');
			},
			transformResult : function(response) {
				return {
					suggestions : $.map($.parseJSON(response), function(item) {
						//document.getElementById('serviceId').value = item.serviceId;
						return {
							value : item.description,
							data : item,
						};
					})
				};
			}
		});

	});

	
	function setValue(source) {
		//document.getElementById("helloid").style.display = ""; 
		//document.getElementById("loaderid").className = "loader";
		var serviceName = document.getElementById('w-input-search').value;
		  document.getElementById("loadingDiv").style = ""; 

		// alert (serviceName);
		// document.getElementById("serviceInfoId").innerHTML = serviceName;

		//alert(source);
		//var e = document.getElementById("skuDD");
		//var value1 = e.options[e.selectedIndex].value;
		//var text = e.options[e.selectedIndex].text;
		//document.getElementById("pricingInfoId").innerHTML = finalResponse;
		//var hello = document.getElementById("w-input-search").name;
		//alert(hello);
		//displayName
		//		    			document.getElementById("serviceDetailsId").innerHTML = item.displayName;

	}