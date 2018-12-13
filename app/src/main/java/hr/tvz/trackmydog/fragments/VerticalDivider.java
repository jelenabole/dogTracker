package hr.tvz.trackmydog.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class VerticalDivider extends DividerItemDecoration {

    private static String TAG = "Vertical Divider";
    private int offset;

    VerticalDivider(Context context, int orientation, int offset) {
        super(context, orientation);
        this.offset = offset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        Log.d(TAG, "getItemOffsets");
        // TODO - called for each view as it is being put inside
        outRect.set(offset, offset, 0, offset);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        Log.d(TAG, "onDraw");

        int bothY = 0;
        int startX = 0;
        int endX = 0;

        // this will iterate over every visible view
        for (int i = 0; i < parent.getChildCount(); i++) {
            // item view:
            final View itemView = parent.getChildAt(i);
            bothY = itemView.getBottom() + offset;

            if (i == 0) {
                // find layout with texts:
                final View layout = ((LinearLayout) parent.getChildAt(i)).getChildAt(1);

                // start (with text padding) and end of text layout:
                startX = layout.getLeft() + layout.getPaddingLeft();
                endX = layout.getRight();
            }

            // draw the separator
            canvas.drawLine(startX, bothY, endX,
                    itemView.getBottom() + offset, new Paint(Color.BLACK));
        }
    }
}