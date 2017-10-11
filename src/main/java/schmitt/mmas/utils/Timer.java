package schmitt.mmas.utils;

public class Timer {

    private Long startedTime;

    public void startTimer() {
        startedTime = System.currentTimeMillis();
    }

    public long elapsedTime() {
        return System.currentTimeMillis() - startedTime;
    }

}
