package com.cookandroid.bioosapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PlantTreeView extends View {
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint legendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final ArrayList<NodeInfo> nodes = new ArrayList<>();
    private final HashMap<Integer, NodeInfo> nodeMap = new HashMap<>();

    private static final float NODE_RADIUS = 36f;

    public PlantTreeView(Context context) {
        super(context);

        linePaint.setStrokeWidth(5f);
        linePaint.setColor(0xFF777777);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4f);
        borderPaint.setColor(0xFF333333);

        textPaint.setColor(0xFF222222);
        textPaint.setTextSize(24f);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);

        legendPaint.setColor(0xFF333333);
        legendPaint.setTextSize(22f);
        legendPaint.setTypeface(Typeface.DEFAULT);

        setMinimumHeight(560);
    }

    public void updateFromJson(JSONObject root) {
        nodes.clear();
        nodeMap.clear();

        JSONArray nodeArray = findNodeArray(root);

        if (nodeArray == null) {
            invalidate();
            return;
        }

        for (int i = 0; i < nodeArray.length(); i++) {
            JSONObject obj = nodeArray.optJSONObject(i);

            if (obj == null) {
                continue;
            }

            NodeInfo node = new NodeInfo();
            node.id = obj.optInt("id", -1);
            node.parentId = obj.optInt("parentId", obj.optInt("parent", -1));
            node.type = obj.optString("type", "Unknown");
            node.status = obj.optString("status", "Unknown");

            nodes.add(node);
            nodeMap.put(node.id, node);
        }

        calculatePositions();
        invalidate();
    }

    private JSONArray findNodeArray(JSONObject root) {
        JSONArray nodesArray = root.optJSONArray("nodes");
        if (nodesArray != null) {
            return nodesArray;
        }

        JSONArray plantNodes = root.optJSONArray("plantNodes");
        if (plantNodes != null) {
            return plantNodes;
        }

        JSONObject plantObject = root.optJSONObject("plant");
        if (plantObject != null) {
            JSONArray innerNodes = plantObject.optJSONArray("nodes");
            if (innerNodes != null) {
                return innerNodes;
            }

            JSONArray innerPlantNodes = plantObject.optJSONArray("plantNodes");
            if (innerPlantNodes != null) {
                return innerPlantNodes;
            }
        }

        return root.optJSONArray("plant");
    }

    private void calculatePositions() {
        int rootCount = 0;
        int stemCount = 0;
        int leafCount = 0;
        int rootTipCount = 0;
        int unknownCount = 0;

        for (NodeInfo node : nodes) {
            if (node.type.equalsIgnoreCase("Root")) {
                node.level = 3;
                node.order = rootCount++;
            } else if (node.type.equalsIgnoreCase("Stem")) {
                node.level = 2;
                node.order = stemCount++;
            } else if (node.type.equalsIgnoreCase("Leaf")) {
                node.level = 1;
                node.order = leafCount++;
            } else if (node.type.equalsIgnoreCase("RootTip")) {
                node.level = 4;
                node.order = rootTipCount++;
            } else {
                node.level = 0;
                node.order = unknownCount++;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (nodes.size() == 0) {
            textPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("No plant structure data.", 30, 80, textPaint);
            textPaint.setTextAlign(Paint.Align.CENTER);
            return;
        }

        int width = getWidth();

        for (NodeInfo node : nodes) {
            setScreenPosition(node, width);
        }

        drawLegend(canvas);
        drawEdges(canvas);
        drawNodes(canvas);
    }

    private void setScreenPosition(NodeInfo node, int width) {
        int centerX = width / 2;

        if (node.type.equalsIgnoreCase("Root")) {
            node.x = centerX;
            node.y = 310;
        } else if (node.type.equalsIgnoreCase("Stem")) {
            node.x = centerX;
            node.y = 205;
        } else if (node.type.equalsIgnoreCase("Leaf")) {
            int spacing = 145;
            int leafTotal = countType("Leaf");
            int startX = centerX - ((leafTotal - 1) * spacing / 2);
            node.x = startX + node.order * spacing;
            node.y = 95;
        } else if (node.type.equalsIgnoreCase("RootTip")) {
            int spacing = 145;
            int rootTipTotal = countType("RootTip");
            int startX = centerX - ((rootTipTotal - 1) * spacing / 2);
            node.x = startX + node.order * spacing;
            node.y = 425;
        } else {
            node.x = 80 + node.order * 110;
            node.y = 90;
        }
    }

    private int countType(String type) {
        int count = 0;

        for (NodeInfo node : nodes) {
            if (node.type.equalsIgnoreCase(type)) {
                count++;
            }
        }

        return count;
    }

    private void drawLegend(Canvas canvas) {
        float y = 35f;

        legendPaint.setTextSize(22f);
        legendPaint.setColor(0xFF333333);

        canvas.drawText("Alive", 20, y, legendPaint);
        nodePaint.setColor(0xFF4CAF50);
        canvas.drawCircle(95, y - 7, 11, nodePaint);

        canvas.drawText("Pruned", 125, y, legendPaint);
        nodePaint.setColor(0xFFE57373);
        canvas.drawCircle(225, y - 7, 11, nodePaint);

        canvas.drawText("Wilted", 255, y, legendPaint);
        nodePaint.setColor(0xFFFFB74D);
        canvas.drawCircle(345, y - 7, 11, nodePaint);
    }

    private void drawEdges(Canvas canvas) {
        for (NodeInfo node : nodes) {
            NodeInfo parent = nodeMap.get(node.parentId);

            if (parent == null) {
                continue;
            }

            linePaint.setColor(0xFF888888);
            canvas.drawLine(parent.x, parent.y, node.x, node.y, linePaint);
        }
    }

    private void drawNodes(Canvas canvas) {
        for (NodeInfo node : nodes) {
            nodePaint.setStyle(Paint.Style.FILL);
            nodePaint.setColor(getStatusColor(node.status));

            canvas.drawCircle(node.x, node.y, NODE_RADIUS, nodePaint);
            canvas.drawCircle(node.x, node.y, NODE_RADIUS, borderPaint);

            textPaint.setColor(0xFFFFFFFF);
            textPaint.setTextSize(24f);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            textPaint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText(shortType(node.type), node.x, node.y + 8, textPaint);

            textPaint.setColor(0xFF222222);
            textPaint.setTextSize(20f);
            canvas.drawText("#" + node.id, node.x, node.y + 62, textPaint);
        }
    }

    private int getStatusColor(String status) {
        if (status.equalsIgnoreCase("Alive")) {
            return 0xFF4CAF50;
        }

        if (status.equalsIgnoreCase("Pruned")) {
            return 0xFFE57373;
        }

        if (status.equalsIgnoreCase("Wilted")) {
            return 0xFFFFB74D;
        }

        return 0xFF9E9E9E;
    }

    private String shortType(String type) {
        if (type.equalsIgnoreCase("Root")) {
            return "R";
        }

        if (type.equalsIgnoreCase("Stem")) {
            return "S";
        }

        if (type.equalsIgnoreCase("Leaf")) {
            return "L";
        }

        if (type.equalsIgnoreCase("RootTip")) {
            return "RT";
        }

        return "?";
    }

    private static class NodeInfo {
        int id;
        int parentId;
        String type;
        String status;

        int level;
        int order;

        float x;
        float y;
    }
}