package eleeter.warden.utils;

public class WardenCase {
    public long targetId;
    public String targetName;
    public String status;
    public String reason;
    public long timestamp;
    public int totalReports;

    public WardenCase(long targetId, String targetName, String status, String reason) {
        this.targetId = targetId;
        this.targetName = targetName;
        this.status = status;
        this.reason = reason;
        this.timestamp = System.currentTimeMillis();
    }
}