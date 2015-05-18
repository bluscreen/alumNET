
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>openData</title>
<asset:stylesheet src="popup.css" />
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript">
    	
		// TEST...  STH MUST BE HERE.... SOMEHOW.. IF THERE IS ANYTHING COMING FROM DB!!
		// size of jobstatisticlist: ${educationInstitute?.jobStatisticList?.size()}
		//Anzeigen des Kreisdiagramms
		    
			google.load("visualization", "1", {packages:["corechart"]});	
			google.setOnLoadCallback(drawChart);
			
			    function drawChart() {

			   	var data = google.visualization.arrayToDataTable([
		   			['Beruf', 'Anzahl Absolventen'],
		   			 ${raw(jsdString)}                  		    
		       		 ]);
	       		 
		        var options = {
		          backgroundColor: 'transparent',
		          color: 'white',
		          legend: {textStyle: {color: 'white', fontSize: '14'}},
		          is3D: true,
		        };
	
		        var chart = new google.visualization.PieChart(document.getElementById('statistenBox'));
		        chart.draw(data, options);
		     }
			
		//Nach laden des Fensters werden die Strings angezeigt
		
		// wird das hier noch benötigt? :
<%--		window.onload = function () {--%>
<%--			document.getElementById("anzahlStudiengaenge").innerHTML = "<b>Anzahl der Studiengänge: </b>" + berufeAbsolventen.length;--%>
<%--			document.getElementById("studiengaengeAbsolventen").innerHTML = studiengaengeAbsolventen;		     --%>
<%--		}--%>
   
 	   </script>
</head>
<body>
	<div id="header">alumNET</div>
	<div id="container" class="b">

		<div id="textBox" class="heading box">
			<p>
				${labels.getText(textId.TEXTID_Education_Institute.getTextid(), session.getAttribute("systemLanguage"))}
			</p>
			<table>
				<tr>
					<th>
						${labels.getText(textId.TEXTID_Name.getTextid(), session.getAttribute("systemLanguage"))}:
					</th>
					<td>
						${educationInstitute?.name}
					</td>
				</tr>
				<tr>
					<th>
						${labels.getText(textId.TEXTID_Description.getTextid(), session.getAttribute("systemLanguage"))}:
					</th>
					<td>
						${educationInstitute?.educationInstituteDescription}
					</td>
				</tr>
				<tr>
					<th>
						${labels.getText(textId.TEXTID_Year_Of_Foundation.getTextid(), session.getAttribute("systemLanguage"))}:
					</th>
					<td>
						${educationInstitute?.yearOfFoundation}
					</td>
				</tr>
				<tr>
					<th>Wiki-Link:</th>
					<td><a href="${educationInstitute?.wikipediaHyperlink}"
						target="_blank"> ${educationInstitute?.wikipediaHyperlink}
					</a></td>
				</tr>
				<tr>
					<th>
						${labels.getText(textId.TEXTID_EMail.getTextid(), session.getAttribute("systemLanguage"))}:
					</th>
					<td>
						${educationInstitute?.email}
					</td>
				</tr>
				<tr>
					<th>
						${labels.getText(textId.TEXTID_Address.getTextid(), session.getAttribute("systemLanguage"))}:
					</th>
					<td>
						${educationInstitute?.streetAndHouseNumber}, ${educationInstitute?.zipCode}
						${educationInstitute?.city }
					</td>
				</tr>
			</table>
		</div>

		<div id="alumniBox" class="box">
			<p>
				${labels.getText(textId.TEXTID_Alumni.getTextid(), session.getAttribute("systemLanguage"))}:
				${educationInstitute?.alumnusList?.size()}
			</p>
			<g:if test="${educationInstitute?.alumnusList?.size() > 0}">
				<table>
					<tr>
						<th>
							${labels.getText(textId.TEXTID_Name.getTextid(), session.getAttribute("systemLanguage"))}
						</th>
						<th>
							${labels.getText(textId.TEXTID_Job.getTextid(), session.getAttribute("systemLanguage"))}
						</th>
						<th>Wiki-Link</th>
					</tr>
					<g:each var="i" in="${educationInstitute?.alumnusList}">
						<tr>
							<td>
								${i.alumniName}
							</td>
							<td>
								${i.jobTitle}
							</td>
							<td><g:if
									test="${(i.wikipediaHyperlink != null && !i.wikipediaHyperlink.empty)}">
									<a href="${i.wikipediaHyperlink}" target="_blank">&raquo;
										Link</a>
								</g:if></td>
						</tr>
					</g:each>
				</table>
			</g:if>
		</div>

		<div id="statisticBox" class="box">
			<p>${labels.getText(textId.TEXTID_Statistic.getTextid(), session.getAttribute("systemLanguage"))}</p>
			<div id="statistenBox"></div>
		</div>

	</div>
	<r:layoutResources />
</body>
</html>
