package ir;

public class SlotTracker {
    private int count = 0;

    private static SlotTracker instance = new SlotTracker();
    public static SlotTracker getInstance() {
        return instance;
    }

    public void reset() {
        count = 0;
    }

    public String alloc() {
        String name = Integer.toString(count++);
        return name;
    }
}
