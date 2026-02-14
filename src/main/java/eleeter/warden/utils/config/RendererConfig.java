package eleeter.warden.utils.config;

public class RendererConfig
{

    /** ArchiveRenderer */
    public static int A_WIDTH = 1920;
    public static int A_HEIGHT = 1080;
    public static final String AR_HEADER_TITLE = "ARCHIVE";
    public static final String AR_VERDICT_PREFIX = "Verdict: ";
    public static final String AR_REASON_PREFIX = "Reason: ";
    public static final String AR_TIMESTAMP_PREFIX = "Timestamp: ";
    public static final String AR_FILE_NAME = "archive.png";
    public static final String AR_SUSPECT_LABEL = "SUSPECT";
    public static final String AR_ADJUDICATOR_LABEL = "ADJUDICATOR";
    public static final String AR_REPORTERS_LABEL = "REPORTERS";
    public static final String AR_REPORTER_LABEL = "REPORTER";



    /**
     * CaseStatusRenderer
     * UNI MEANS "UNIVERSAL" We can use the same value More than one Method.
     * */
    public static int C_WIDTH = 1920;
    public static int C_HEIGHT = 1000;
    public static final float C_ARC = 3f;
    public static final int C_UNI = 35;
    public static final int C_UNI_2 = 80;
    public static final int C_UNI_3 = 65;
    public static final int C_UNI_4 = 42;
    public static final int C_UNI_5 = 95;
    public static final int C_UNI_6 = 60;
    public static final String CSR_FILE_NAME = "warden_case_resolution.png";
    public static final String CSR_HEADER_TITLE = "WARDEN CENTRAL INTELLIGENCE // CASE RESOLVED";
    public static final String CSR_SERVER_ENTRY = "Server Entry: ";
    public static final String CSR_SUBJECT_ID = "Subject ID:   ";
    public static final String CSR_RESOLVED_AT = "Resolved At:  ";
    public static final String CSR_FINAL_VERDICT = "FINAL VERDICT";
    public static final String CSR_VERDICT_PREFIX = "> ";







    /** Report Renderer */
    public static final String RR_FONT_SEGOE_UI = "Segoe UI";
    public static final String RR_FONT_CONSOLAS = "Consolas";
    public static final int ARC_WIDTH = 35;
    public static final int ARC_HEIGHT = 35;
    public static final float GLASS_STROKE = 3f;
    public static final float DANGER_STROKE = 2f;
    public static final String RR_FILE_NAME = "warden_case_report.png";
    public static final String RR_HEADER_TITLE = "WARDEN CENTRAL INTELLIGENCE // CASE FILE";
    public static final String RR_SERVER_ENTRY = "Server Entry: ";
    public static final String RR_REPORTERS_PLURAL = "REPORTERS:";
    public static final String RR_REPORTERS_SINGULAR = "REPORTER:";
    public static final String RR_REPORTED_AT = "Reported At:  ";
    public static final String RR_UNIQUE_REPORTERS = "UNIQUE REPORTERS";
    public static final String RR_TOTAL_ATTEMPTS = "TOTAL ATTEMPTS";
    public static final String RR_SUBMITTED_EVIDENCE = "SUBMITTED EVIDENCE / REASON";
    public static final String RR_EVIDENCE_PREFIX = "> ";


}
