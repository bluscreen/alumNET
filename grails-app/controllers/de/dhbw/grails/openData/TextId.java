package de.dhbw.grails.openData;

public enum TextId {
	TEXTID_State("P17"),
	TEXTID_City("Q515"),
	TEXTID_Person("Q215627"),
	TEXTID_Name("Q82799"),
	TEXTID_Education_Institute("Q2385804"),
	TEXTID_Job("P106"),
	TEXTID_Year_Of_Foundation("P571"),
	TEXTID_Phone("P1329"),
	TEXTID_Alumni("Q508719"),
	TEXTID_Address("Q319608"),
	TEXTID_EMail("P968"),
	TEXTID_Description("Q829624"), 
	TEXTID_AboutUs("Q4668607"),
	TEXTID_Imprint("Q1075810"), 
	TEXTID_Error("Q29485");
	
	private final String textid;
	
	TextId(String textid){
		this.textid = textid;
	}
	
	public String getTextid() {
		return textid;
	}
}
