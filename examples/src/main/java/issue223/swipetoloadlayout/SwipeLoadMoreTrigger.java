package issue223.swipetoloadlayout;

/**
 * Created by Aspsine on 2015/8/17.
 */
public interface SwipeLoadMoreTrigger extends SwipeTrigger {
    void onLoadMore();

    boolean canLoadMore();
}
