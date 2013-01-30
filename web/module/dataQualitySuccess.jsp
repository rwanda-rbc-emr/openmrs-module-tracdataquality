<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:htmlInclude file="/moduleResources/@MODULE_ID@/styles/listingstyle.css" />

<a href="${pageContext.request.contextPath}/admin/index.htm"><spring:message code="admin.title.short"/></a>&nbsp;|&nbsp;<a href="DataQualityAlerts.form"><spring:message code="@MODULE_ID@.perform"/></a>

<h2><b><spring:message code="@MODULE_ID@.subTitle" /></b></h2>
<br/>

<h3><b><spring:message code="@MODULE_ID@.result" arguments="65" /></b></h3>
<br/>

<div style="width: 99%; border: 1px solid #8FABC7; margin: auto; -moz-border-radius: 3px; padding: 3px;">
	<c:set var="columns" value="6" scope="page"/>
	<div id="list_container" style="width: 99%">
	<div id="list_title">
		<div class="list_title_msg"><spring:message code="${msgToDisplay}" arguments="${msgArguments}" /></div>
		<div class="list_title_bts">
			<openmrs:hasPrivilege privilege="Export Collective Patient Data">
				<a title="Export this list into CSV format" href="downloadController.form?id=2&checkType=${checkType}&createriaValue=${valueRangeType}">Export</a>
			</openmrs:hasPrivilege>					
		</div>
		<div style="clear:both;"></div>
	</div>
	
	<table id="list_data">
		<tr>
			<th class="columnHeader">#.</th>
			<th class="columnHeader"><spring:message code="Patient.identifier"/></th>
			<openmrs:hasPrivilege privilege="View Patient Names">
				<c:set var="columns" value="9" scope="page"/>
				<th class="columnHeader"><spring:message code="PersonName.givenName"/></th>
				<th class="columnHeader"><spring:message code="PersonName.middleName"/></th>
				<th class="columnHeader"><spring:message code="PersonName.familyName"/></th>
			</openmrs:hasPrivilege>
			<th class="columnHeader"><spring:message code="Person.age"/></th>
			<th class="columnHeader"><spring:message code="Person.gender"/></th>		
			<th class="columnHeader"><spring:message code="general.creator"/></th>
			<th class="columnHeader"><spring:message code="general.edit"/></th>
		</tr>
		
		<c:if test="${empty thePatientList}">
			<tr>
				<td colspan="${columns}" style="text-align: center;"><spring:message code="@MODULE_ID@.noResultFound"/></td>
			</tr>
		</c:if>
		
		<c:forEach var="patient" items="${thePatientList}" varStatus="status">

			<tr>
				<td class="rowValue ${status.count%2!=0?'even':''}">${status.count}.</td>
				<td class="rowValue ${status.count%2!=0?'even':''}">${patient.patientIdentifier}</td>
				<openmrs:hasPrivilege privilege="View Patient Names">
					<td class="rowValue ${status.count%2!=0?'even':''}">${patient.givenName}</td>
					<td class="rowValue ${status.count%2!=0?'even':''}">${patient.middleName}</td>
					<td class="rowValue ${status.count%2!=0?'even':''}">${patient.familyName}</td>
				</openmrs:hasPrivilege>
				<td class="rowValue ${status.count%2!=0?'even':''}">${(patient.age<1)?'<1':patient.age}</td>
				<td class="rowValue ${status.count%2!=0?'even':''}">
					<img src="${pageContext.request.contextPath}/images/${patient.gender == 'M' ? 'male' : 'female'}.gif" /></td>
				<td class="rowValue ${status.count%2!=0?'even':''}">${patient.personCreator}</td>
				<td class="rowValue ${status.count%2!=0?'even':''}">
					<a href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${patient.patientId}">
						<spring:message code="patientDashboard.viewDashboard"/>
						<!-- <img src="${pageContext.request.contextPath}/images/edit.gif" title="Edit" border="0" align="top" /> -->
					</a>
				</td>
			</tr>
			
			<c:if test="${status.last}">
				<c:set var="numberOfPatients" value="${status.count}"/>
			</c:if>

		</c:forEach>
		
	</table>
	
	<div id="list_footer">
		<div class="list_footer_info">Total number of patients : <u>${numberOfPatients}</u></div>
		<div class="list_footer_pages">		
			&nbsp;&nbsp;		
		</div>
		<div style="clear: both"></div>
	</div>
	
	</div>	
	
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>