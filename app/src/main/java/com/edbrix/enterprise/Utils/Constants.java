package com.edbrix.enterprise.Utils;

public class Constants {

    public final static String contentType_C = "C";
    // http://enterpriseservices.edbrix.net/app/student/authenticatestudent
    public final static String contentType_WC = "WC";
    public final static String contentType_IMG = "IMG";
    public final static String contentType_Audio = "AC";
    public final static String contentType_Video = "VC";
    public final static String contentType_Doc = "DC";
    public final static String contentType_Iframe = "IC";
    public final static String contentType_Survey = "SV";
    public final static String contentType_Test = "TEST";
    public final static String contentType_Section = "sec";
    public final static String contentType_TrainingSession = "TS";
    public final static String contentType_Assignment = "AS";
    public final static String contentType_Event = "E";
    public final static String docContentType_File = "file";
    public final static String docContentType_Img = "img";
    public final static String submitType_Check = "check";
    public final static String submitType_Timer = "timer";
    public final static String submitType_Question = "question";
    public final static String submitDataType_TrueFalse = "truefalse";
    public final static String submitDataType_SingleChoice = "singlechoice";
    public final static String submitDataType_MultiChoice = "multichoice";
    public final static String submitDataType_ImageChoice = "imagechoice";
    public final static String submitDataType_LongAnswer = "longanswer";
    public final static String availabilityType_TrainingSession = "TS";
    public final static String availabilityType_ZOOM = "ZOOM";
    public final static String TolkBox_APIKey= "45467242";
    public final static String TolkBox_Token= "Token";
    public final static String TolkBox_SessionId= "SessionId";
    public final static String TolkBox_MeetingId= "MeetingId";
    public final static String TolkBox_MeetingName= "MeetingName";
    public final static String FileType_Playwire= "playwire";
    public final static String FileType_Document= "document";


    // TOKBOX
//    'TOKBOX_API_KEY' => '45467242',
//            'TOKBOX_SECRETE_KEY' => 'e3d961f33cf0747d87b449d39c1c5c6d543b61df',

    public final static String submitDataType_FillInBlanks = "fillinblanks";
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



    private final static String BASE_URL = "http://enterpriseservices.edbrix.net/app/student/";
    public final static String userLogin = BASE_URL.concat("authenticateuser.php");  // authenticatestudent
    public final static String userRegister = BASE_URL.concat("");
    public final static String getSchoolList = BASE_URL.concat("getschoollist.php");
    public final static String changePassword = BASE_URL.concat("changepassword.php");
    public final static String getMeetings = BASE_URL.concat("getmeetings.php");
    public final static String forgotPassword = BASE_URL.concat("forgotpassword.php");
    public final static String getMeetingDetails = BASE_URL.concat("getmeetingdetails.php");
    public final static String getDashboardCourseSchedules = BASE_URL.concat("getdashboardcourseandschedules.php");
    public final static String playCourseContent = BASE_URL.concat("playcoursecontent.php");
    public final static String getCourseContentList = BASE_URL.concat("getcoursecontentslist.php");

//	truefalse, singlechoice, multichoice ,imagechoice ,longanswer ,fillinblanks
    public final static String playCourseContentSubmit = BASE_URL.concat("playcoursecontentsubmit.php");
    public final static String getInstructorResources = BASE_URL.concat("getinstructorresources.php");
    public final static String getCourseSections = BASE_URL.concat("getcoursesections.php");
    public final static String getConnectivityTypes = BASE_URL.concat("getconnectivitytypes.php");
    public final static String createAvailability = BASE_URL.concat("createavailibility.php");
    public final static String assignAvailabilityLearnersList = BASE_URL.concat("assignavailabilitylearnerslist.php");
    public final static String assignLearnerToAvailability = BASE_URL.concat("assignlearnertoavailability.php");
    public final static String setCreateCourse = BASE_URL.concat("createcourse.php");
    public final static String setCreateCourseContent = BASE_URL.concat("createcoursecontent.php");
    public final static String getCourseContent = BASE_URL.concat("getcoursecontents.php");
    //
    public final static String setDeleteCourseContent = BASE_URL.concat("deletecoursecontent.php");
    public final static String getCourseDetails = BASE_URL.concat("getcourseeditdetails.php");
    public final static String getSalutations = BASE_URL.concat("getsalutations.php");
    public final static String getTimezoneList = BASE_URL.concat("gettimezones.php");
    public final static String getUserDetails = BASE_URL.concat("editprofile.php");
    public final static String updateUserProfile = BASE_URL.concat("updateprofile.php");
    public final static String sendMeetingNotification = BASE_URL.concat("sendmeetingpushnotification.php");
    public final static String updateUserProfilePic = "https://enterprise.edbrix.net/ntservices/updateUserProfileImage";
    public final static String updateCoursePic = "https://enterprise.edbrix.net/ntservices/updateCourseImage";
    public final static String uploadVideoToMyFiles = "http://enterpriseservices.edbrix.net/app/uploadvideotomyfiles.php";
    public static final String SESSION_INFO_ENDPOINT = BASE_URL + "/session";
    public static final String ARCHIVE_START_ENDPOINT = BASE_URL + "/archive/start";
    public static final String ARCHIVE_STOP_ENDPOINT = BASE_URL + "/archive/:archiveId/stop";
    public static final String ARCHIVE_PLAY_ENDPOINT = BASE_URL + "/archive/:archiveId/view";

}
