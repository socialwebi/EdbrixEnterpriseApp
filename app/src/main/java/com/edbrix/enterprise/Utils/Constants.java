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


	// TODO Change it to your web domain
	final static String WEB_DOMAIN = "zoom.us";

	// TODO Change it to your APP Key
	final static String APP_KEY_ = "UTiw4W3on3YdNzjU1dD3dxFIa9mCVu16JOop";
	final static String APP_KEY = "zAJLGT0f3NjDeThGV15pwjVzDACqYf4VDkH7";

	// TODO Change it to your APP Secret
	final static String APP_SECRET_ = "JC37LqI5LssCXYCXytIxNc2hyyHghHSX7CKo";
	final static String APP_SECRET = "HZOsMSYDYLBvtRESSpSvXW7tULBFJo2T3JYb";

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
