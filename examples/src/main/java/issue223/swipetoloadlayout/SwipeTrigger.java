package issue223.swipetoloadlayout;

/**
 * Created by Aspsine on 2015/8/17.
 */
public interface SwipeTrigger {
    void onPrepare(SwipeToLoadLayout frame, int state);

    void onMove(int y, boolean isComplete, boolean automatic);

    void onRelease();

    void onComplete();

    void onReset();
}
