package com.edbrix.enterprise.Utils;

public class Constants {

	private final static String BASE_URL = "http://enterpriseservices.edbrix.net/app/student/";
	// http://enterpriseservices.edbrix.net/app/student/authenticatestudent

	public final static String userLogin = BASE_URL.concat("authenticateuser");  // authenticatestudent
	public final static String userRegister = BASE_URL.concat("");
	public final static String getSchoolList = BASE_URL.concat("getschoollist");
	public final static String changePassword = BASE_URL.concat("changepassword");
	public final static String getMeetings = BASE_URL.concat("getmeetings");
	public final static String forgotPassword = BASE_URL.concat("forgotpassword");
	public final static String getMeetingDetails = BASE_URL.concat("getmeetingdetails");
	public final static String getDashboardCourseSchedules = BASE_URL.concat("getdashboardcourseandschedules");
	public final static String playCourseContent = BASE_URL.concat("playcoursecontent");
	public final static String playCourseContentSubmit = BASE_URL.concat("playcoursecontentsubmit");

	public final static String getInstructorResources = BASE_URL.concat("getinstructorresources");
	public final static String getCourseSections = BASE_URL.concat("getcoursesections");
	public final static String getConnectivityTypes = BASE_URL.concat("getconnectivitytypes");

	public final static String setCreateCourse = BASE_URL.concat("createcourse");
	public final static String setCreateCourseContent = BASE_URL.concat("createcoursecontent");
	public final static String getCourseContent = BASE_URL.concat("getcoursecontents");

	public final static String contentType_C ="C";
	public final static String contentType_WC ="WC";
	public final static String contentType_IMG ="IMG";
	public final static String contentType_Audio ="AC";
	public final static String contentType_Video ="VC";
	public final static String contentType_Doc ="DC";
	public final static String contentType_Iframe ="IC";
	public final static String contentType_Survey ="SV";
	public final static String contentType_Test ="TEST";

	public final static String submitType_Check ="check";
	public final static String submitType_Timer ="timer";
	public final static String submitType_Question ="question";

	public final static String submitDataType_TrueFalse ="truefalse";
	public final static String submitDataType_SingleChoice ="singlechoice";
	public final static String submitDataType_MultiChoice ="multichoice";
	public final static String submitDataType_ImageChoice ="imagechoice";
	public final static String submitDataType_LongAnswer ="longanswer";
	public final static String submitDataType_FillInBlanks ="fillinblanks";

//	truefalse, singlechoice, multichoice ,imagechoice ,longanswer ,fillinblanks


	// TODO Change it to your web domain
	public final static String WEB_DOMAIN = "zoom.us";

	// TODO Change it to your APP Key
	public final static String APP_KEY__ = "UTiw4W3on3YdNzjU1dD3dxFIa9mCVu16JOop";
	public final static String APP_KEY_ = "zAJLGT0f3NjDeThGV15pwjVzDACqYf4VDkH7";
	public final static String APP_KEY = "peoY0DllTnoO7yOiF9gbbc3K0SHoh4QRHMba";

	// TODO Change it to your APP Secret
	public final static String APP_SECRET__ = "JC37LqI5LssCXYCXytIxNc2hyyHghHSX7CKo";
	public final static String APP_SECRET_ = "HZOsMSYDYLBvtRESSpSvXW7tULBFJo2T3JYb";
	public final static String APP_SECRET = "p2Khhd7mya7bQ2u8NFBh2hYTDskCT58yR57V";

	// TODO change it to your user ID
	final static String USER_ID = "604813"; // 604813  ijTMthVYQzSy06ba9Elxdw
	final static String USER_ID_ = "606345"; // 604813  ijTMthVYQzSy06ba9Elxdw

	// TODO change it to your token
	final static String ZOOM_TOKEN_ = "";
	// -TyvZsQXmG3U-FXc8iQF9u0LUCg8iCmFihMb1FGf5_c.BgMgY01hTnU5d0F0QkdyRDVMMmVyOUNLV1ZOdTVqQTdmajZAZmVkZDA2ZGM1YTQxNjExNThjNTYwMDE3YTA0M2NkM2RiNTBiMjVjZjBiZTQ4NDRiNDAyYmU5MzY0MzAyNWI3NgAMM0NCQXVvaVlTM3M9
	final static String ZOOM_TOKEN = "SvUqT2iJ1q2j25AGC6EVtwkSwZWDuLoPdGSx";

	// TODO Change it to your exist meeting ID to start meeting
	final static String MEETING_ID_JOIN = "329184643"; // 329184643
	final static String MEETING_ID_START = "329184643"; // 7073082323  mine - 775-879-3264

}
