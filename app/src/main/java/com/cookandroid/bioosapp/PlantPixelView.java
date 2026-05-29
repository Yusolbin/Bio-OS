package com.cookandroid.bioosapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

public class PlantPixelView extends View {

    private Paint paint;

    private Set<String> activeStates = new HashSet<>();
    private String lastAction = "Stable";
    private double totalEnergy = 100.0;

    private Bitmap currentBitmap;

    public PlantPixelView(Context context) {
        super(context);
        init();
    }

    public PlantPixelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlantPixelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(false);
        paint.setFilterBitmap(false);
        paint.setDither(false);

        currentBitmap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.stable
        );
    }

    public void updatePlantState(Set<String> activeStates, String lastAction, double totalEnergy) {
        this.activeStates.clear();

        if (activeStates != null) {
            this.activeStates.addAll(activeStates);
        }

        this.lastAction = lastAction != null ? lastAction : "Stable";
        this.totalEnergy = totalEnergy;

        updateBitmapByState();
        invalidate();
    }

    private void updateBitmapByState() {
        int resId = R.drawable.stable;

        if (totalEnergy <= 5.0) {
            resId = R.drawable.dead_critical;
        } else if (lastAction != null && lastAction.equalsIgnoreCase("PruningAlreadyExecuted")) {
            resId = R.drawable.pruning_already_executed;
        } else if (lastAction != null &&
                (lastAction.equalsIgnoreCase("Pruning")
                        || lastAction.equalsIgnoreCase("PruningFailed"))) {
            resId = R.drawable.pruned;
        } else if (totalEnergy < 40.0) {
            resId = R.drawable.low_energy;
        } else if (activeStates.contains("RecoveryMode")) {
            resId = R.drawable.recovery_mode;
        } else if (activeStates.contains("HeatStress")) {
            resId = R.drawable.heat_stress;
        } else if (activeStates.contains("DroughtMode")) {
            resId = R.drawable.drought_mode;
        } else if (activeStates.contains("PhotosynthesisBoost")) {
            resId = R.drawable.photosynthesis_boost;
        } else if (activeStates.contains("ColdStress")) {
            resId = R.drawable.cold_stress;
        } else {
            resId = R.drawable.stable;
        }

        currentBitmap = BitmapFactory.decodeResource(getResources(), resId);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(0xFF101816);

        if (currentBitmap == null) {
            return;
        }

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        int targetSize = Math.min(viewWidth, viewHeight) - 40;

        if (targetSize < 64) {
            targetSize = Math.min(viewWidth, viewHeight);
        }

        int left = (viewWidth - targetSize) / 2;
        int top = (viewHeight - targetSize) / 2;
        int right = left + targetSize;
        int bottom = top + targetSize;

        Rect src = new Rect(
                0,
                0,
                currentBitmap.getWidth(),
                currentBitmap.getHeight()
        );

        Rect dst = new Rect(left, top, right, bottom);

        canvas.drawBitmap(currentBitmap, src, dst, paint);
    }
}