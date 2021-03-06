package de.dhbw.grails.openData

import javax.websocket.Session;

import org.springframework.web.servlet.support.RequestContextUtils


/**
 * @author dhammacher
 * UI presenter class for providing data from the GlobalDAO to the UI
 * session scoped bean per config
 * 
 * TODO currently no valid concurrency behaviour is implemented:
 * see branch: https://github.com/bluscreen/alumNET/tree/concurrency
 * for a first approach (not working yet)
 *
 */
class IndexController {

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

		List<EducationInstitute> searchResult = []

		String staat = params['staat']
		String ort = params['ort']
		String bildEin = params['bildungseinrichtung']
		String person = params['person']
		String beruf = params['beruf']

		/**
		 *  Look if no search parameters are set
		 */
		boolean searchAll = ((staat == null || staat == "")
				&& (ort == null || ort == "")
				&& (bildEin == null || bildEin == "")
				&& (person == null || person == "")
				&& (beruf == null ||beruf == ""))

		/**
		 *  this flag will determine if the resultlist in left bottom corner is to be rendered.
		 */
		boolean rendList = true;

		if(searchAll) {
			rendList = false;
			/**
			 * user is here for the first time or has not enteres search criteria
			 */
			List<EducationInstituteBasicInformation> allResults = GlobalDAO.instance.getAllEducationInstitutes()
			allResults.each { e->
				/**
				 *  here we retrieve additional information for the bubbles 
				 *  attached to the markers, as no action listener could be 
				 *  implemented in short term as onclick for the bubble.
				 *  
				 *  TODO : remove this and implement action listener for 
				 *  marker.onclick to retrieve additional information per ajax request
				 *  --> performance bottleneck
				 */
				EducationInstitute ei = GlobalDAO.instance.getEducationInstituteById(e.id, session.getAttribute("systemLanguage"))
				if(ei == null){
					log.info "could not find ei for " + e + " , language: " + session.getAttribute("systemLanguage")
				}else{
					searchResult.add(ei)
				}
			}
		}
		else {
			/**
			 *  user entered search param(s)
			 */
			searchResult = GlobalDAO.instance.searchEducationInstitutes(
					staat,
					ort,
					bildEin,
					person,
					beruf,
					session.getAttribute("systemLanguage"))
		}

		int foundAmount = searchResult.size()
		log.info "search returned " + foundAmount + " results, printing:"

		/**
		 *  Build a String containing all coordinates with their id in a js array
		 */
		String markerString = "", line = ""
		def badItems = []
		searchResult.eachWithIndex { elem, idx->
			if(elem != null && (elem.name != null && !elem.name.isEmpty() && elem.city != null && !elem.city.isEmpty())) {
				line = "[" + elem?.latitude + ", " + elem?.longitude + ", \"" + elem?.id + "\", \"" + elem?.name +"\", \"" + elem?.city + "\"]"
				line += ((idx+1)<foundAmount) ? ",\n" : "\n"
				markerString += line
			}
			else {
				log.error "NPE in resultset at index " + idx + " elem: " + elem
				badItems << elem
			}
		}
		/**
		 * remove null etc objects from searchresult
		 */
		searchResult.removeAll(badItems)

		log.info "markerString: " + markerString

		/**
		 *  TODO future task:
		 *  labels should not return the whole dao instance, 
		 *  the gettext method should be implemented in a dedicated bean
		 */
		[languages: GlobalDAO.instance.getLanguagesList(),
			labels: GlobalDAO.instance,
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
			GlobalDAO.instance.getLanguagesList().each {i->
				log.info i.getLanguageId()
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
				edu = GlobalDAO.instance.getEducationInstituteById(id, session.getAttribute("systemLanguage"))
			}
		}
		else{
			log.error "BAD ID!!! INJECTION?"
			response.sendError(404)
			return
		}

		log.info "params:"
		params.each {i->
			log.info "__"+ i
		}

		//		// DH 12.05. still no data from DB.. mocking it for ID Q219563
		//		if(edu?.id == "Q219563")
		//		{
		//			log.info "Q219563 MOCKMOCKMOCK!!! BOCK"
		//			JobStatisticDataset js1 = new JobStatisticDataset("Doenermann", 2)
		//			JobStatisticDataset js2 = new JobStatisticDataset("Gyrosmann", 3)
		//			JobStatisticDataset js3 = new JobStatisticDataset("Taxifahrer", 1)
		//
		//			List<JobStatisticDataset> jsdl = new ArrayList<JobStatisticDataset>()
		//			jsdl.addAll(js1,js2,js3)
		//
		//			Alumnus a1 = new Alumnus("Sir D�nalot", "Doenermann", "http://www.doener.de")
		//			Alumnus a2 = new Alumnus("YUFKALORD", "Doenermann", "http://www.yufka.de")
		//
		//			Alumnus a3 = new Alumnus("GYROSLAND", "Gyrosmann", "http://www.gyros.de")
		//			Alumnus a4 = new Alumnus("Sir Taki", "Gyrosmann", "http://www.sir-taki.de")
		//			Alumnus a5 = new Alumnus("AGGRO-POLICE", "Gyrosmann", "http://www.aggro-police.de")
		//
		//			Alumnus a6 = new Alumnus("Mahatma Taxi", "Taxifahrer", "http://www.mahatma.de")
		//
		//			List<Alumnus> al = new ArrayList<Alumnus>()
		//			al.addAll(a1,a2,a3,a4,a5,a6)
		//
		//			edu.jobStatisticList = jsdl
		//			edu.alumnusList = al
		//		}

		String jobStats = ""
		if(edu != null){
			edu.jobStatisticList?.eachWithIndex { num,idx ->
				jobStats += "['" + num.jobTitle + "'," + num.number + "]"
				if(idx<edu.jobStatisticList.size()-1) jobStats += ",\n"
			}
			log.info "controller popup() call finished. return."
			[educationInstitute: edu,
				jsdString: jobStats,
				labels: GlobalDAO.instance,
				textId: TextId]
		}

		else {
			log.error "404"
			response.sendError(404)
			return
		}
	}

	def checkLanguageSet(){
		if(session.getAttribute("systemLanguage") == null || session.getAttribute("systemLanguage") == "") {
			log.info "systemLanguage is null or empty: call updateLanguage to try to determine it"
			updateLanguage()
		}
		log.info "system language:" + session.getAttribute("systemLanguage")
	}

	def info()
	{

	}

}