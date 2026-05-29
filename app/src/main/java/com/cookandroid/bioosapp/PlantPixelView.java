package com.cookandroid.bioosapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

public class PlantPixelView extends View {

    private Paint paint;

    private Set<String> activeStates = new HashSet<>();
    private String lastAction = "Stable";
    private double totalEnergy = 100.0;

    private final int GRID_WIDTH = 32;
    private final int GRID_HEIGHT = 32;

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
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(false);
    }

    public void updatePlantState(Set<String> activeStates, String lastAction, double totalEnergy) {
        this.activeStates.clear();

        if (activeStates != null) {
            this.activeStates.addAll(activeStates);
        }

        this.lastAction = lastAction != null ? lastAction : "Stable";
        this.totalEnergy = totalEnergy;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float pixelSize = Math.min(
                getWidth() / (float) GRID_WIDTH,
                getHeight() / (float) GRID_HEIGHT
        );

        float offsetX = (getWidth() - GRID_WIDTH * pixelSize) / 2f;
        float offsetY = (getHeight() - GRID_HEIGHT * pixelSize) / 2f;

        drawBackground(canvas, pixelSize, offsetX, offsetY);
        drawSoil(canvas, pixelSize, offsetX, offsetY);
        drawRoots(canvas, pixelSize, offsetX, offsetY);
        drawStem(canvas, pixelSize, offsetX, offsetY);
        drawLeaves(canvas, pixelSize, offsetX, offsetY);
        drawStateEffects(canvas, pixelSize, offsetX, offsetY);
    }

    private void drawBackground(Canvas canvas, float pixelSize, float offsetX, float offsetY) {
        int bgColor = Color.rgb(18, 24, 22);

        if (activeStates.contains("HeatStress")) {
            bgColor = Color.rgb(35, 22, 20);
        } else if (activeStates.contains("DroughtMode")) {
            bgColor = Color.rgb(32, 28, 20);
        } else if (activeStates.contains("RecoveryMode")) {
            bgColor = Color.rgb(18, 30, 24);
        }

        canvas.drawColor(bgColor);
    }

    private void drawSoil(Canvas canvas, float pixelSize, float offsetX, float offsetY) {
        int soilColor = Color.rgb(92, 61, 36);

        if (activeStates.contains("DroughtMode")) {
            soilColor = Color.rgb(120, 92, 50);
        }

        for (int y = 27; y < 30; y++) {
            for (int x = 4; x < 28; x++) {
                drawPixel(canvas, x, y, soilColor, pixelSize, offsetX, offsetY);
            }
        }

        // 작은 흙 입자
        drawPixel(canvas, 6, 26, soilColor, pixelSize, offsetX, offsetY);
        drawPixel(canvas, 11, 26, soilColor, pixelSize, offsetX, offsetY);
        drawPixel(canvas, 21, 26, soilColor, pixelSize, offsetX, offsetY);
        drawPixel(canvas, 26, 26, soilColor, pixelSize, offsetX, offsetY);
    }

    private void drawRoots(Canvas canvas, float pixelSize, float offsetX, float offsetY) {
        int rootColor = Color.rgb(145, 95, 55);

        if (activeStates.contains("DroughtMode")) {
            rootColor = Color.rgb(110, 78, 45);
        }

        drawPixel(canvas, 16, 26, rootColor, pixelSize, offsetX, offsetY);
        drawPixel(canvas, 16, 27, rootColor, pixelSize, offsetX, offsetY);
        drawPixel(canvas, 15, 28, rootColor, pixelSize, offsetX, offsetY);
        drawPixel(canvas, 17, 28, rootColor, pixelSize, offsetX, offsetY);
        drawPixel(canvas, 14, 29, rootColor, pixelSize, offsetX, offsetY);
        drawPixel(canvas, 18, 29, rootColor, pixelSize, offsetX, offsetY);
    }

    private void drawStem(Canvas canvas, float pixelSize, float offsetX, float offsetY) {
        int stemColor = Color.rgb(67, 150, 85);

        if (activeStates.contains("DroughtMode")) {
            stemColor = Color.rgb(125, 120, 55);
        } else if (activeStates.contains("HeatStress")) {
            stemColor = Color.rgb(130, 90, 60);
        } else if (activeStates.contains("RecoveryMode")) {
            stemColor = Color.rgb(80, 190, 100);
        }

        if (totalEnergy < 40) {
            stemColor = Color.rgb(90, 105, 70);
        }

        for (int y = 13; y <= 26; y++) {
            drawPixel(canvas, 16, y, stemColor, pixelSize, offsetX, offsetY);
        }

        // 줄기 두께감
        if (totalEnergy >= 60) {
            for (int y = 18; y <= 25; y++) {
                drawPixel(canvas, 15, y, stemColor, pixelSize, offsetX, offsetY);
            }
        }
    }

    private void drawLeaves(Canvas canvas, float pixelSize, float offsetX, float offsetY) {
        int leafColor = Color.rgb(65, 200, 95);
        int leafDarkColor = Color.rgb(40, 150, 70);

        if (activeStates.contains("DroughtMode")) {
            leafColor = Color.rgb(190, 155, 55);
            leafDarkColor = Color.rgb(125, 95, 45);
        } else if (activeStates.contains("HeatStress")) {
            leafColor = Color.rgb(160, 115, 65);
            leafDarkColor = Color.rgb(180, 65, 55);
        } else if (activeStates.contains("RecoveryMode")) {
            leafColor = Color.rgb(95, 235, 120);
            leafDarkColor = Color.rgb(55, 180, 85);
        }

        boolean pruned = lastAction.contains("Pruning");

        // 왼쪽 잎
        if (!pruned || lastAction.equals("PruningAlreadyExecuted")) {
            drawLeafCluster(canvas, 11, 15, leafColor, leafDarkColor, pixelSize, offsetX, offsetY, false);
            drawLeafCluster(canvas, 10, 20, leafColor, leafDarkColor, pixelSize, offsetX, offsetY, false);
        }

        // 오른쪽 잎
        if (!pruned) {
            drawLeafCluster(canvas, 21, 14, leafColor, leafDarkColor, pixelSize, offsetX, offsetY, true);
            drawLeafCluster(canvas, 22, 19, leafColor, leafDarkColor, pixelSize, offsetX, offsetY, true);
        } else {
            // 가지치기 흔적
            int cutColor = Color.rgb(180, 80, 65);
            drawPixel(canvas, 18, 17, cutColor, pixelSize, offsetX, offsetY);
            drawPixel(canvas, 19, 17, cutColor, pixelSize, offsetX, offsetY);
        }

        // 꼭대기 새싹
        if (activeStates.contains("RecoveryMode")) {
            int sproutColor = Color.rgb(120, 255, 140);
            drawPixel(canvas, 16, 11, sproutColor, pixelSize, offsetX, offsetY);
            drawPixel(canvas, 15, 12, sproutColor, pixelSize, offsetX, offsetY);
            drawPixel(canvas, 17, 12, sproutColor, pixelSize, offsetX, offsetY);
        }
    }

    private void drawLeafCluster(
            Canvas canvas,
            int centerX,
            int centerY,
            int leafColor,
            int leafDarkColor,
            float pixelSize,
            float offsetX,
            float offsetY,
            boolean rightSide
    ) {
        int droop = activeStates.contains("DroughtMode") ? 1 : 0;

        drawPixel(canvas, centerX, centerY + droop, leafColor, pixelSize, offsetX, offsetY);
        drawPixel(canvas, centerX - 1, centerY + droop, leafColor, pixelSize, offsetX, offsetY);
        drawPixel(canvas, centerX + 1, centerY + droop, leafColor, pixelSize, offsetX, offsetY);
        drawPixel(canvas, centerX, centerY - 1 + droop, leafColor, pixelSize, offsetX, offsetY);
        drawPixel(canvas, centerX, centerY + 1 + droop, leafDarkColor, pixelSize, offsetX, offsetY);

        if (rightSide) {
            drawPixel(canvas, centerX + 2, centerY + droop, leafDarkColor, pixelSize, offsetX, offsetY);
        } else {
            drawPixel(canvas, centerX - 2, centerY + droop, leafDarkColor, pixelSize, offsetX, offsetY);
        }
    }

    private void drawStateEffects(Canvas canvas, float pixelSize, float offsetX, float offsetY) {
        if (activeStates.contains("HeatStress")) {
            int heatColor = Color.rgb(230, 80, 55);

            drawPixel(canvas, 8, 12, heatColor, pixelSize, offsetX, offsetY);
            drawPixel(canvas, 24, 11, heatColor, pixelSize, offsetX, offsetY);
            drawPixel(canvas, 25, 18, heatColor, pixelSize, offsetX, offsetY);
            drawPixel(canvas, 7, 21, heatColor, pixelSize, offsetX, offsetY);
        }

        if (activeStates.contains("DroughtMode")) {
            int dryColor = Color.rgb(170, 130, 55);

            drawPixel(canvas, 5, 25, dryColor, pixelSize, offsetX, offsetY);
            drawPixel(canvas, 9, 24, dryColor, pixelSize, offsetX, offsetY);
            drawPixel(canvas, 23, 25, dryColor, pixelSize, offsetX, offsetY);
            drawPixel(canvas, 27, 24, dryColor, pixelSize, offsetX, offsetY);
        }
    }

    private void drawPixel(
            Canvas canvas,
            int gridX,
            int gridY,
            int color,
            float pixelSize,
            float offsetX,
            float offsetY
    ) {
        paint.setColor(color);

        float left = offsetX + gridX * pixelSize;
        float top = offsetY + gridY * pixelSize;
        float right = left + pixelSize;
        float bottom = top + pixelSize;

        canvas.drawRect(left, top, right, bottom, paint);
    }
}