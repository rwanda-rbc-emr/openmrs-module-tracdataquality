function displayForm1() {
	
		document.getElementById('myDiv').style.display = '';
		document.getElementById('2eme').style.display = 'none';
		document.getElementById('3eme').style.display = 'none';
}
function displayForm2() {
	document.getElementById('myDiv').style.display = 'none';
	document.getElementById('2eme').style.display = '';
	document.getElementById('3eme').style.display = 'none';
}
function displayForm3() {
	document.getElementById('myDiv').style.display = 'none';
	document.getElementById('2eme').style.display = 'none';
	document.getElementById('3eme').style.display = '';
}
function hideForm(){
	document.getElementById('mydiv').style.display = 'none';
	document.getElementById('mydiv1').style.display = 'none';
}

  function validateNumber() {
   if(document.Form.checkType.value=="choose"){
		var answer = alert ("You have to choose one criteria")
  	 if (answer)
  	 return false;
  	 else
  	 return false;
   }	
     if (isNaN(document.Form.createriaValue.value)) {
    	 var answer = alert ("You gave an invalid value")
    	 if (answer)
    	 return false;
    	 else
    	 return false;
           }
     else if(document.Form.createriaValue.value=="" || document.Form.createriaValue.value==null || document.Form.createriaValue.value<0){
    	 var answer = alert ("You are not allowed to give an empty value")
    	 if (answer)
    	 return false;
    	 else
    	 return false;
     }
  }
  function displayUnit(option){
	  if (option == "patientWithMoreKgs") {
			document.getElementById('unit').style.display = '';
			document.getElementById('unit').innerHTML = "kg";
		}else if(option=="patientsWithMoreHeight"){ 
			document.getElementById('unit').style.display = '';
			document.getElementById('unit').innerHTML = "cm";
		}else if(option=="patientsWithMoreTEMPERATURE"){
			document.getElementById('unit').style.display = '';
			document.getElementById('unit').innerHTML = "C";
		}else if(option=="patientsWithMoreBLOODOXYGENSATURATION"){
			document.getElementById('unit').style.display = '';
			document.getElementById('unit').innerHTML = "%";
		}else if(option=="patientsWithMoreDIASTOLICBLOODPRESSURE"){
			document.getElementById('unit').style.display = '';
			document.getElementById('unit').innerHTML = "mmHg";
		}else if(option=="patientsWithMoreHEADCIRCUMFERENCE"){
			document.getElementById('unit').style.display = '';
			document.getElementById('unit').innerHTML = "cm";
		}else if(option=="patientsWithMoreKARNOFSKYPERFORMANCESCORE"){
			document.getElementById('unit').style.display = '';
			document.getElementById('unit').innerHTML = "%";
		}else if(option=="patientsWithMorePULSE"){
			document.getElementById('unit').style.display = '';
			document.getElementById('unit').innerHTML = "rate/min";
		}else if(option=="patientsWithMoreRESPIRATORYRATE"){
			document.getElementById('unit').style.display = '';
			document.getElementById('unit').innerHTML = "";
		}else if(option=="patientsWithMoreSYSTOLICBLOODPRESSURE"){
			document.getElementById('unit').style.display = '';
			document.getElementById('unit').innerHTML = "mmHg";
		}else{
			document.getElementById('unit').style.display = 'none';
		}
  }