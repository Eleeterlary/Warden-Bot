package eleeter.warden.data;

public class WardenData
{
    public int count;
    public java.util.List<String> reasons;

    public WardenData(int count, String firstReason)
    {
        this.count = count;
        this.reasons = new java.util.ArrayList<>();
        this.reasons.add(firstReason);
    }
}
