import de.dhbw.grails.openData.DatabaseInterface
import de.dhbw.grails.openData.GlobalDAO


// Place your Spring DSL code here
beans = {
	dbi(DatabaseInterface)
	globalDAO(GlobalDAO)
}
