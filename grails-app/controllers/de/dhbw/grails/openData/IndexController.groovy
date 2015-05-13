package de.dhbw.grails.openData

import javax.websocket.Session;

import org.springframework.web.servlet.support.RequestContextUtils


/**
 * @author dhammacher
 * UI presenter class for providing data from the GlobalDAO to the UI
 * session scoped bean per default config.
 *
 */
class IndexController {
	
	GlobalDAO globalDAO

	/**
	 * call of this method will render index.gsp
	 * returning values defined within []
	 * main method to provide the view with search results
	 * this is initially called with no parameters on first visit
	 * @return
	 */
	def index() {
		log.info "index() called"
		checkLanguageSet()
		
		String staat = params['staat']
		String ort = params['ort']
		String bildEin = params['bildungseinrichtung']
		String person = params['person']
		String beruf = params['beruf']

		// Look if no search parameters are set
		boolean searchAll = ((staat == null || staat == "")
				&& (ort == null || ort == "")
				&& (bildEin == null || bildEin == "")
				&& (person == null || person == "")
				&& (beruf == null ||beruf == ""))

		List<EducationInstitute> searchResult = []
		boolean rendList = true;
		if(searchAll) {
			rendList = false;
			// user is here for the first time
			List<EducationInstituteBasicInformation> allResults = globalDAO.getAllEducationInstitutes()
			allResults.each { e->
				EducationInstitute ei = globalDAO.getEducationInstituteById(e.id, session.getAttribute("systemLanguage"))
				if(ei == null){
					log.info "could not find ei for " + e + " , language: " + session.getAttribute("systemLanguage")
				}else{
					searchResult.add(ei)
				}
			}
		}
		else {
			// user entered search param(s)
			searchResult = globalDAO.searchEducationInstitutes(
					staat,
					ort,
					bildEin,
					person,
					beruf,
					session.getAttribute("systemLanguage"))
		}

		int foundAmount = searchResult.size()
		log.info "search returned " + foundAmount + " results, printing:"

		// Build a String containing all coordinates with their id in JSON
		String markerString = "", line = ""
		searchResult.eachWithIndex { elem, idx->
			line = "[" + elem.latitude + ", " + elem.longitude + ", \"" + elem.id + "\", \"" + elem.name +"\", \"" + elem.city + "\"]"
			line += ((idx+1)<foundAmount) ? ",\n" : "\n"
			markerString += line
		}

		log.debug "markerString: " + markerString

		[languages: globalDAO.getLanguagesList(),
			labels: globalDAO,
			textId: TextId,
			institutes: searchResult,
			markers: markerString,
			renderList: rendList ]
	}

	/**
	 * AJAX Method to update system language stored in session
	 * trigger = selectbox
	 * @return
	 */
	def updateLanguage() {
		log.info "updateLanguage() called"
		def locale = RequestContextUtils.getLocale(request)
		log.info "spring determined language: " + locale.getLanguage()
		if(params['lang'] == null || params['lang' == ""]) {
			globalDAO.getLanguagesList().each {i->
				log.debug i.getLanguageId()
				if(i.getLanguageId()==locale.getLanguage()){
					session.setAttribute("systemLanguage", i.getLanguageId())
				}
			}
		}
		else{
			session.setAttribute("systemLanguage", params['lang'])
		}
		log.info "session language:" + session.getAttribute("systemLanguage")
	}

	/**
	 * call of this method will render popup.gsp
	 * returning values defined within []
	 * this method provides detailed information about 
	 * an educational institute and its alumni as well as job statistics
	 * @return
	 */
	def popup() {
		log.info "popup() called"
		checkLanguageSet()

		EducationInstitute edu
		String id = params['id']

		// check for url injections
		if (id.charAt(0) >= 'A' && id.charAt(0) <= 'Z'){
			String realId = id.substring(1);
			if(!realId.isNumber()){
				log.error "BAD ID!!! INJECTION?"
				response.sendError(404)
				return
			}
			else {
				edu = globalDAO.getEducationInstituteById(id, session.getAttribute("systemLanguage"))
			}
		}
		else{
			log.error "BAD ID!!! INJECTION?"
			response.sendError(404)
			return
		}

		log.error "params:"
		params.each {i->
			log.info "__"+ i
		}

		// DH 12.05. still no data from DB.. mocking it for ID Q219563
		if(edu?.id == "Q219563")
		{
			log.info "Q219563 MOCKMOCKMOCK!!! BOCK"
			JobStatisticDataset js1 = new JobStatisticDataset("Doenermann", 2)
			JobStatisticDataset js2 = new JobStatisticDataset("Gyrosmann", 3)
			JobStatisticDataset js3 = new JobStatisticDataset("Taxifahrer", 1)

			List<JobStatisticDataset> jsdl = new ArrayList<JobStatisticDataset>()
			jsdl.addAll(js1,js2,js3)

			Alumnus a1 = new Alumnus("Sir Dönalot", "Doenermann", "http://www.doener.de")
			Alumnus a2 = new Alumnus("YUFKALORD", "Doenermann", "http://www.yufka.de")

			Alumnus a3 = new Alumnus("GYROSLAND", "Gyrosmann", "http://www.gyros.de")
			Alumnus a4 = new Alumnus("Sir Taki", "Gyrosmann", "http://www.sir-taki.de")
			Alumnus a5 = new Alumnus("AGGRO-POLICE", "Gyrosmann", "http://www.aggro-police.de")

			Alumnus a6 = new Alumnus("Mahatma Taxi", "Taxifahrer", "http://www.mahatma.de")

			List<Alumnus> al = new ArrayList<Alumnus>()
			al.addAll(a1,a2,a3,a4,a5,a6)

			edu.jobStatisticList = jsdl
			edu.alumnusList = al
		}

		String jobStats = ""
		if(edu != null){
			edu.jobStatisticList?.eachWithIndex { num,idx ->
				jobStats += "['" + num.jobTitle + "'," + num.number + "]"
				if(idx<edu.jobStatisticList.size()-1) jobStats += ",\n"
			}
			log.info "controller popup() call finished. return."
			[educationInstitute: edu,
				jsdString: jobStats,
				labels: globalDAO,
				textId: TextId]
		}

		else {
			log.error "404"
			response.sendError(404)
			return
		}
	}

	@Deprecated
	def fetchName(){
		log.info "fetchName() called"

		def id = params['id']

		log.info "params:"
		params.each {i->
			log.info "__"+ i
		}

		if(id == null){
			id=1;
		}
		[educationInstitute: globalDAO.getEducationInstituteById(id, session.getAttribute("systemLanguage"))]

	}

	def info()
	{

	}

	def impressum()
	{

	}

	def checkLanguageSet(){
		if(session.getAttribute("systemLanguage") == null || session.getAttribute("systemLanguage") == "") {
			log.debug "systemLanguage is null or empty: call updateLanguage to try to determine it"
			updateLanguage()
		}
		log.debug "system language:" + session.getAttribute("systemLanguage")
	}
}
