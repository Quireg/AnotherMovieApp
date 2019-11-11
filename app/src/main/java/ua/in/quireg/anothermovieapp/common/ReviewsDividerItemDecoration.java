package ua.in.quireg.anothermovieapp.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ua.in.quireg.anothermovieapp.R;

/**
 * Created by Arcturus Mengsk on 2/15/2018, 10:41 AM.
 * 2ch-Browser
 */

public class ReviewsDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;

    public ReviewsDividerItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.shadowline);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, RecyclerView parent,
                           @NonNull RecyclerView.State state) {

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {

            View view = parent.getChildAt(i);

            if (parent.getChildAt(i + 1) == null) {
                //do not draw if it is last item
                continue;
            }

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

            View reviewText = view.findViewById(R.id.review_content);

            if (reviewText == null) {
                return;
            }

            int left = reviewText.getPaddingLeft();
            int right = parent.getWidth() - reviewText.getPaddingRight();

            int top = view.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}