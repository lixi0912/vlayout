package bugfix.issue282;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;

import java.util.List;

/**
 * @author 陈晓辉
 * @description <>
 * @date 2018/5/14
 */
public class DelegateItemAnimator extends RecyclerView.ItemAnimator {
    public interface ItemAnimatorListener {
        void onAnimationFinished(RecyclerView.ViewHolder item);
    }

    private RecyclerView.ItemAnimator delegate;
    private ItemAnimatorListener listener;


    public DelegateItemAnimator(RecyclerView.ItemAnimator delegate, ItemAnimatorListener listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    public void setListener(ItemAnimatorListener listener) {
        this.listener = listener;
    }

    public void setItemAnimator(RecyclerView.ItemAnimator delegate) {
        this.delegate = delegate;
    }


    @Override
    public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder,
                                        @NonNull ItemHolderInfo preLayoutInfo,
                                        @Nullable ItemHolderInfo postLayoutInfo) {
        return null != delegate && delegate.animateDisappearance(viewHolder, preLayoutInfo, postLayoutInfo);
    }

    @Override
    public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder,
                                     @Nullable ItemHolderInfo preLayoutInfo,
                                     @NonNull ItemHolderInfo postLayoutInfo) {
        return null != delegate && delegate.animateAppearance(viewHolder, preLayoutInfo, postLayoutInfo);
    }

    @Override
    public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull ItemHolderInfo preLayoutInfo,
                                      @NonNull ItemHolderInfo postLayoutInfo) {
        return null != delegate && delegate.animatePersistence(viewHolder, preLayoutInfo, postLayoutInfo);
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder,
                                 @NonNull RecyclerView.ViewHolder newHolder,
                                 @NonNull ItemHolderInfo preLayoutInfo,
                                 @NonNull ItemHolderInfo postLayoutInfo) {
        return null != delegate && delegate.animateChange(oldHolder, newHolder, preLayoutInfo, postLayoutInfo);
    }


    @Override
    public void runPendingAnimations() {
        if (null != delegate) {
            delegate.runPendingAnimations();
        }
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        if (null != delegate) {
            delegate.endAnimation(item);
        }
        if (null != listener) {
            listener.onAnimationFinished(item);
        }
    }


    @Override
    public long getMoveDuration() {
        if (null != delegate) {
            return delegate.getMoveDuration();
        }
        return super.getMoveDuration();
    }

    @Override
    public void setMoveDuration(long moveDuration) {
        super.setMoveDuration(moveDuration);
        if (null != delegate) {
            delegate.setMoveDuration(moveDuration);
        }
    }

    @Override
    public long getAddDuration() {
        if (null != delegate) {
            return delegate.getAddDuration();
        }
        return super.getAddDuration();
    }

    @Override
    public void setAddDuration(long addDuration) {
        super.setAddDuration(addDuration);
        if (null != delegate) {
            delegate.setAddDuration(addDuration);
        }
    }

    @Override
    public long getRemoveDuration() {
        if (null != delegate) {
            return delegate.getRemoveDuration();
        }
        return super.getRemoveDuration();
    }

    @Override
    public void setRemoveDuration(long removeDuration) {
        super.setRemoveDuration(removeDuration);
        if (null != delegate) {
            delegate.setRemoveDuration(removeDuration);
        }
    }

    @Override
    public long getChangeDuration() {
        if (null != delegate) {
            return delegate.getChangeDuration();
        }
        return super.getChangeDuration();
    }

    @Override
    public void setChangeDuration(long changeDuration) {
        super.setChangeDuration(changeDuration);
        if (null != delegate) {
            delegate.setChangeDuration(changeDuration);
        }
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {
        if (null != delegate) {
            return delegate.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
        }
        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (null != delegate) {
            return delegate.recordPostLayoutInformation(state, viewHolder);
        }
        return super.recordPostLayoutInformation(state, viewHolder);
    }

    @Override
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        if (null != delegate) {
            return delegate.canReuseUpdatedViewHolder(viewHolder);
        }
        return super.canReuseUpdatedViewHolder(viewHolder);
    }

    @Override
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull List<Object> payloads) {
        if (null != delegate) {
            return delegate.canReuseUpdatedViewHolder(viewHolder, payloads);
        }
        return super.canReuseUpdatedViewHolder(viewHolder, payloads);
    }

    @Override
    public ItemHolderInfo obtainHolderInfo() {
        if (null != delegate) {
            return delegate.obtainHolderInfo();
        }
        return super.obtainHolderInfo();
    }

    @Override
    public void onAnimationStarted(RecyclerView.ViewHolder viewHolder) {
        super.onAnimationStarted(viewHolder);
        if (null != delegate) {
            delegate.onAnimationStarted(viewHolder);
        }
    }

    @Override
    public void onAnimationFinished(RecyclerView.ViewHolder item) {
        super.onAnimationFinished(item);
        if (null != delegate) {
            delegate.onAnimationFinished(item);
        }
        if (null != listener) {
            listener.onAnimationFinished(item);
        }
    }

    @Override
    public void endAnimations() {
        if (null != delegate) {
            delegate.endAnimations();
        }
    }

    @Override
    public boolean isRunning() {
        return null != delegate && delegate.isRunning();
    }
}
