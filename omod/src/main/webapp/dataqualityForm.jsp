<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:htmlInclude file="/moduleResources/tracdataquality/javaScriptControl.js" />
<openmrs:htmlInclude file="/moduleResources/tracdataquality/jquery-1.3.2.min.js" />

<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/scripts/jquery.bigframe.js" />
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/scripts/ui/ui.core.js" />
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/scripts/ui/ui.dialog.js" />
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/scripts/ui/ui.draggable.js" />
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/scripts/ui/ui.resizable.js" />

<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/theme/ui.all.css" />
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/theme/demo.css" />

<openmrs:require privilege="View Data Quality" redirect="/module/@MODULE_ID@/DataQualityAlerts.form" otherwise="/login.htm" />

<script type="text/javascript">
	var $j = jQuery.noConflict();
</script>

<a href="${pageContext.request.contextPath}/admin/index.htm"><spring:message code="admin.title.short"/></a>
<br />

<h2><b><spring:message code="@MODULE_ID@.subTitle" /></b></h2>

<b class="boxHeader"><spring:message code="@MODULE_ID@.criteria.current"/></b>
<div class="box">
	<table width="100%">
		<tr>
			<td >
				<table cellspacing="1" cellpadding="2" border="1" style="border-width: 5px;   border-style: solid; border-color: #8fabc7"; ">
          			<tr>
          				<td><h3><spring:message code="@MODULE_ID@.criteria"/></h3> </td>
          				<td><h3><spring:message code="@MODULE_ID@.patient.number"/></h3></td>
          			</tr>

					<c:forEach var="patientNumber" items="${patientNumbers}" varStatus="status">				
						<tr >
							<td ><spring:message code="tracdataquality.indicator.${patientNumber.key}"/></td>
							<td><center><a href="dataqualityLink.form?checkType=${patientNumber.key}" onmouseover=hideForm();>${patientNumber.value}</a></center></td>
						</tr>
						<c:if test="${status.last}">
							<tr>
								<td><spring:message code="@MODULE_ID@.patient.withMoreThanXValue"/></td>
								<td><a href="#" id="ddd"><center><spring:message code="@MODULE_ID@.clickHere"/></center></a></td>
							</tr>
							<tr>
								<td><spring:message code="@MODULE_ID@.patient.withoutTracnetOrNid"/></td>
								<td><a href="#" id="ddd1"><center><spring:message code="@MODULE_ID@.clickHere"/></center></a></td>
							</tr>

						</c:if>
					</c:forEach>
				</table>
			</td>
		</tr>
	</table>
</div>

<!-- <div id="mydiv">
	<b class="boxHeader"><spring:message code="@MODULE_ID@.search.specification"/></b>
	<div class="box">
		<br /><br />
		
		
	</div>
</div>

<div id="mydiv1">
	<b class="boxHeader"><spring:message code="@MODULE_ID@.search.specification.identifier"/></b>
	<div class="box">
		<br /><br />
		
	</div>
</div> -->

<div id="divDlg"></div>
<div id="dlgCtnt" style="display: none;"></div>

<script type="text/javascript">
	function showDialog(title){
		$j("#divDlg").html("<div id='dialog' style='font-size: 0.9em;' title='"+title+"'><p><div id='forms'></div></p></div>");
		$j("#dialog").dialog({
			zIndex: 980,
			bgiframe: true,
			height: 150,
			width: 808,
			modal: true
		});	
	}

	function distroyResultDiv(){
		while(document.getElementById("dialog")){
			var DIVtoRemove = document.getElementById("dialog");
			DIVtoRemove.parentNode.removeChild(DIVtoRemove);
		}
	}

	function populateForm(){
		distroyResultDiv();
		showDialog("<spring:message code='@MODULE_ID@.criteria.other'/> : <spring:message code='@MODULE_ID@.patient.withoutTracnetOrNid'/>");
		$j("#forms").html("<form method='post' action='dataqualityLink.form'><table><tr><td><spring:message code='@MODULE_ID@.type'/></td><td><select name='checkType'><option value='patientWithNoTRACnet'><spring:message code='@MODULE_ID@.identifiertype.tracnet'/></option><option value='patientWithNoNID'><spring:message code='@MODULE_ID@.identifiertype.nid'/></option></select></td></tr><tr><td></td><td><input type='submit' value='<spring:message code='@MODULE_ID@.search'/>'/></td></tr></table></form>");
	}

	function populateForm1(){
		distroyResultDiv("<spring:message code='@MODULE_ID@.criteria.other'/> : <spring:message code='@MODULE_ID@.patient.withMoreThanXValue'/>");
		showDialog("<spring:message code='@MODULE_ID@.criteria.other'/> : <spring:message code='@MODULE_ID@.patient.withMoreThanXValue'/>");
		$j("#forms").html("<form method='post' name='Form' action='dataqualityLink.form' onSubmit='return validateNumber()'><table><tr><td><spring:message code='@MODULE_ID@.type'/></td><td><select name='checkType' onchange='displayUnit(this.value)'><option value='choose'><spring:message code='@MODULE_ID@.criteria.choose'/></option><option value='patientWithMoreKgs'><spring:message code='@MODULE_ID@.criteria.patientWithMoreThanXKglWeight'/></option><option value='patientsWithMoreHeight'><spring:message code='@MODULE_ID@.criteria.patientWithMoreXCmsHeight'/></option><option value='patientsWithMoreBMI'><spring:message code='@MODULE_ID@.criteria.patientWithMoreXBMI'/></option><option value='patientsWithMoreBLOODOXYGENSATURATION'><spring:message code='@MODULE_ID@.criteria.patientWithMoreXBloodOxygenSaturation'/></option><option value='patientsWithMoreDIASTOLICBLOODPRESSURE'><spring:message code='@MODULE_ID@.criteria.patientWithMoreXDiastolicBloodPressure'/></option><option value='patientsWithMoreHEADCIRCUMFERENCE'><spring:message code='@MODULE_ID@.criteria.patientWithMoreXHeadCircumference'/></option><option value='patientsWithMoreKARNOFSKYPERFORMANCESCORE'><spring:message code='@MODULE_ID@.criteria.patientWithMoreXKarnosfkyPerformanceScore'/></option><option value='patientsWithMorePULSE'><spring:message code='@MODULE_ID@.criteria.patientWithMoreXPulse'/></option><option value='patientsWithMoreRESPIRATORYRATE'><spring:message code='@MODULE_ID@.criteria.patientWithMoreXRespiratoryRate'/></option><option value='patientsWithMoreSYSTOLICBLOODPRESSURE'><spring:message code='@MODULE_ID@.criteria.patientWithMoreXSystolicBloodPressure'/></option><option value='patientsWithMoreTEMPERATURE'><spring:message code='@MODULE_ID@.criteria.patientWithMoreXTemparature'/></option></select></td></tr><tr><td><spring:message code='@MODULE_ID@.value.x'/></td><td><input type='text' name='createriaValue'/> <b id='unit' style='display: none; color: blue;'>kg</b></td></tr><tr><td></td><td><input type='submit' value='<spring:message code='@MODULE_ID@.search'/>'/></td></tr></table></form>");
	}

	$j(document).ready( function() {
		//$j("#mydiv").hide();
		//$j("#mydiv1").hide();
		$j("#ddd").click( function() {
			populateForm1();
		});
		$j("#ddd1").click( function() {
			//$j("#mydiv").hide();
			//$j("#mydiv1").show("slow");
			populateForm();
		});
		//$j("#ddd2").click( function() {
			//$j("#mydiv").hide();
			//$j("#mydiv1").hide();
			//showDialog();
		//});
	});
</script>

<%@ include file="/WEB-INF/template/footer.jsp"%>