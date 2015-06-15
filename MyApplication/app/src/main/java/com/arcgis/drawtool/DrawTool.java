package com.arcgis.drawtool;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.arcgis.activity.MainMap1Activity;
import com.arcgis.activity.MainMap2Activity;
import com.arcgis.activity.MainMap3Activity;
import com.arcgis.activity.MainMap4Activity;
import com.arcgis.activity.MainMap5Activity;
import com.arcgis.activity.Map1FZ;
import com.arcgis.entity.LatLonPoint;
import com.arcgis.entity.MercatorEntity;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.ToastUtil;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.FillSymbol;
import com.esri.core.symbol.LineSymbol;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ropp gispace@yeah.net
 * 画图实现类，支持画点、矩形、线、多边形、圆、手画线、手画多边形，可设置各种图形的symbol。
 */
public class DrawTool extends Subject {

	private MapView mapView;
    //绘制图形的图层
	private GraphicsLayer tempLayer;
	private MarkerSymbol markerSymbol;
	private LineSymbol lineSymbol;
	private FillSymbol fillSymbol;
	private int drawType;
	private boolean active;
	private Point point;
	private Envelope envelope;
	private Polyline polyline;
	private Polygon polygon;
	private Graphic drawGraphic;
	private Point startPoint;
	private int graphicID;

    private double px=0.0;
    private double py=0.0;
    //调用webservice
    private KsoapValidateHttp ksoap;

    private DrawTouchListener drawTouchListener;
    private LatLonListener latLonListener;
	private GetPolylineCoordsListener polylineCoordsListener;
    private GetPolygonCoordsListener polygonCoordsListener;
    private MercatorEntity latLonPoint=null;
    //保存多边形的点
    private List<MercatorEntity> LatLonPoint_List=null;
    //private MapOnTouchListener mapOnTouchListener;

	public static final int POINT = 1;
	public static final int ENVELOPE = 2;
	public static final int POLYLINE = 3;
	public static final int POLYGON = 4;
	public static final int CIRCLE = 5;
	public static final int ELLIPSE = 6;
	public static final int FREEHAND_POLYGON = 7;
	public static final int FREEHAND_POLYLINE = 8;
    private String FLAG=null;


    public DrawTool(MapView mapView,String flag) {
		this.mapView = mapView;
        this.FLAG=flag;
		this.tempLayer = new GraphicsLayer();
		this.mapView.addLayer(this.tempLayer);
		this.markerSymbol = new SimpleMarkerSymbol(Color.RED, 15,SimpleMarkerSymbol.STYLE.TRIANGLE);
		this.lineSymbol = new SimpleLineSymbol(Color.BLUE, 2);
		this.fillSymbol = new SimpleFillSymbol(Color.RED);
		this.fillSymbol.setAlpha(90);
        LatLonPoint_List=new ArrayList<MercatorEntity>();

        drawTouchListener = new DrawTouchListener(this.mapView.getContext(),this.mapView);
        //mapOnTouchListener = new MapOnTouchListener(this.mapView.getContext(),this.mapView);
    }

	public int activate(int drawType) {
		if (this.mapView == null)
			return -1;
		this.deactivate();
		this.mapView.setOnTouchListener(drawTouchListener);
		this.drawType = drawType;
		this.active = true;

		switch (this.drawType) {
            case DrawTool.POINT:
                this.point = new Point();
                drawGraphic = new Graphic(this.point, this.markerSymbol);
                break;
            case DrawTool.ENVELOPE:
                this.envelope = new Envelope();
                drawGraphic = new Graphic(this.envelope, this.fillSymbol);
                break;
            case DrawTool.POLYGON:
                this.polygon = new Polygon();
                drawGraphic = new Graphic(this.polygon, this.fillSymbol);
                break;
            case DrawTool.CIRCLE:
                this.polygon = new Polygon();
                drawGraphic = new Graphic(this.polygon, this.fillSymbol);
                break;
            case DrawTool.FREEHAND_POLYGON:
                this.polygon = new Polygon();
                drawGraphic = new Graphic(this.polygon, this.fillSymbol);
                break;
            case DrawTool.POLYLINE:
                this.polyline = new Polyline();
                drawGraphic = new Graphic(this.polyline, this.lineSymbol);
                break;
            case DrawTool.FREEHAND_POLYLINE:
                this.polyline = new Polyline();
                drawGraphic = new Graphic(this.polyline, this.lineSymbol);
                break;
            default:
                //ToastUtil.show(this.mapView.getContext(),"请选择绘制类型");
                break;
		}
		return graphicID = this.tempLayer.addGraphic(drawGraphic);
	}

	public void deactivate() {
		//this.mapView.setOnTouchListener(mapOnTouchListener);
        this.mapView.setOnTouchListener(drawTouchListener);
		this.tempLayer.removeAll();
		this.active = false;
		//this.drawType = -1;
		this.point = null;
		this.envelope = null;
		this.polygon = null;
		this.polyline = null;
		this.drawGraphic = null;
		this.startPoint=null;
	}

	public MarkerSymbol getMarkerSymbol() {
		return markerSymbol;
	}

	public void setMarkerSymbol(MarkerSymbol markerSymbol) {
		this.markerSymbol = markerSymbol;
	}

	public LineSymbol getLineSymbol() {
		return lineSymbol;
	}

	public void setLineSymbol(LineSymbol lineSymbol) {
		this.lineSymbol = lineSymbol;
	}

	public FillSymbol getFillSymbol() {
		return fillSymbol;
	}

	public void setFillSymbol(FillSymbol fillSymbol) {
		this.fillSymbol = fillSymbol;
	}


    //绘制完成后调用
    private void sendDrawEndEvent() {
		DrawEvent e = new DrawEvent(this, DrawEvent.DRAW_END,DrawTool.this.drawGraphic);
		DrawTool.this.notifyEvent(e);
		int type = this.drawType;

        //提交点
        if(type==POINT){
            latLonListener=MainMap1Activity.getInstance();
            if(latLonListener!=null){
                latLonListener.handleLatLon(new LatLonPoint(DrawTool.this.px,DrawTool.this.py));
            }

        }
        //提交多边形
        if(type==POLYGON){
            if(FLAG.equals("Map1FZ")){
                polygonCoordsListener= Map1FZ.getInstance();
                if(LatLonPoint_List!=null && LatLonPoint_List.size()>2 && polygonCoordsListener!=null){
                    polygonCoordsListener.handleLatLon(LatLonPoint_List);
                }else{
                    ToastUtil.show(this.mapView.getContext(),"地块不完整,请重新绘制");
                }
            }
            if(FLAG.equals("MainMap2Activity")){
                polygonCoordsListener= MainMap2Activity.getInstance();
                if(LatLonPoint_List!=null && LatLonPoint_List.size()>2 && polygonCoordsListener!=null){
                    polygonCoordsListener.handleLatLon(LatLonPoint_List);
                }else{
                    ToastUtil.show(this.mapView.getContext(),"地块不完整,请重新绘制");
                }
            }
            if(FLAG.equals("MainMap3Activity")){
                polygonCoordsListener= MainMap3Activity.getInstance();
                if(LatLonPoint_List!=null && LatLonPoint_List.size()>2 && polygonCoordsListener!=null){
                    polygonCoordsListener.handleLatLon(LatLonPoint_List);
                }else{
                    ToastUtil.show(this.mapView.getContext(), "地块不完整,请重新绘制");
                }
            }
            if(FLAG.equals("MainMap4Activity")){
                polygonCoordsListener= MainMap4Activity.getInstance();
                if(LatLonPoint_List!=null && LatLonPoint_List.size()>2 && polygonCoordsListener!=null){
                    polygonCoordsListener.handleLatLon(LatLonPoint_List);
                }else{
                    ToastUtil.show(this.mapView.getContext(),"地块不完整,请重新绘制");
                }
            }
            if(FLAG.equals("MainMap5Activity")){
                polygonCoordsListener= MainMap5Activity.getInstance();
                if(LatLonPoint_List!=null && LatLonPoint_List.size()>2 && polygonCoordsListener!=null){
                    polygonCoordsListener.handleLatLon(LatLonPoint_List);
                }else{
                    ToastUtil.show(this.mapView.getContext(),"地块不完整,请重新绘制");
                }
            }
        }
		//
		if(type==POLYLINE){
			if(FLAG.equals("Map1FZ")){
				polylineCoordsListener= Map1FZ.getInstance();
				if(LatLonPoint_List!=null && LatLonPoint_List.size()>2 && polylineCoordsListener!=null){
					polylineCoordsListener.handleLatLon(LatLonPoint_List);
				}else{
					ToastUtil.show(this.mapView.getContext(),"线绘制不正确,请重新绘制");
				}
			}
//			if(FLAG.equals("MainMap2Activity")){
//				polygonCoordsListener= MainMap2Activity.getInstance();
//				if(LatLonPoint_List!=null && LatLonPoint_List.size()>2 && polygonCoordsListener!=null){
//					polygonCoordsListener.handleLatLon(LatLonPoint_List);
//				}else{
//					ToastUtil.show(this.mapView.getContext(),"地块不完整,请重新绘制");
//				}
//			}
//			if(FLAG.equals("MainMap3Activity")){
//				polygonCoordsListener= MainMap3Activity.getInstance();
//				if(LatLonPoint_List!=null && LatLonPoint_List.size()>2 && polygonCoordsListener!=null){
//					polygonCoordsListener.handleLatLon(LatLonPoint_List);
//				}else{
//					ToastUtil.show(this.mapView.getContext(), "地块不完整,请重新绘制");
//				}
//			}
//			if(FLAG.equals("MainMap4Activity")){
//				polygonCoordsListener= MainMap4Activity.getInstance();
//				if(LatLonPoint_List!=null && LatLonPoint_List.size()>2 && polygonCoordsListener!=null){
//					polygonCoordsListener.handleLatLon(LatLonPoint_List);
//				}else{
//					ToastUtil.show(this.mapView.getContext(),"地块不完整,请重新绘制");
//				}
//			}
//			if(FLAG.equals("MainMap5Activity")){
//				polygonCoordsListener= MainMap5Activity.getInstance();
//				if(LatLonPoint_List!=null && LatLonPoint_List.size()>2 && polygonCoordsListener!=null){
//					polygonCoordsListener.handleLatLon(LatLonPoint_List);
//				}else{
//					ToastUtil.show(this.mapView.getContext(),"地块不完整,请重新绘制");
//				}
//			}
		}
		//
        if(LatLonPoint_List!=null && LatLonPoint_List.size()>0){
            LatLonPoint_List.clear();
        }
		this.deactivate();
		this.activate(-1);
	}


    //

    // 扩展MapOnTouchListener，实现画图功能
	class DrawTouchListener extends MapOnTouchListener{

		public DrawTouchListener(Context context, MapView view) {
			super(context, view);
		}



        @Override
		public boolean onTouch(View view, MotionEvent event) {
			if (active	&& (drawType == POINT || drawType == ENVELOPE
						|| drawType == CIRCLE || drawType == FREEHAND_POLYLINE
                        || drawType == FREEHAND_POLYGON)
					&& event.getAction() == MotionEvent.ACTION_DOWN) {
				Point point = mapView.toMapPoint(event.getX(), event.getY());
				switch (drawType) {
                    case DrawTool.POINT:
                        DrawTool.this.point.setXY(point.getX(), point.getY());
                        Point WGS84Point = (Point) GeometryEngine.project(point, SpatialReference.create(102100), SpatialReference.create(4326));
                        DrawTool.this.px=WGS84Point.getX();
                        DrawTool.this.py=WGS84Point.getY();

                        sendDrawEndEvent();
                        break;
                    case DrawTool.ENVELOPE:
                        startPoint = point;
                        envelope.setCoords(point.getX(), point.getY(), point.getX(), point.getY());
                        break;
                    case DrawTool.CIRCLE:
                        startPoint = point;
                        break;
                    case DrawTool.FREEHAND_POLYGON:
                        polygon.startPath(point);
                        break;
                    case DrawTool.FREEHAND_POLYLINE:
                        polyline.startPath(point);
                        break;
				}
			}
			return super.onTouch(view, event);
		}

		@Override
		public boolean onDragPointerMove(MotionEvent from, MotionEvent to) {
			if (active && (drawType == ENVELOPE || drawType == FREEHAND_POLYGON
				|| drawType == FREEHAND_POLYLINE || drawType == CIRCLE)) {
				Point point = mapView.toMapPoint(to.getX(), to.getY());
				switch (drawType) {
                    case DrawTool.ENVELOPE:
                        envelope.setXMin(startPoint.getX() > point.getX() ? point
                                .getX() : startPoint.getX());
                        envelope.setYMin(startPoint.getY() > point.getY() ? point
								.getY() : startPoint.getY());
                        envelope.setXMax(startPoint.getX() < point.getX() ? point
								.getX() : startPoint.getX());
                        envelope.setYMax(startPoint.getY() < point.getY() ? point
								.getY() : startPoint.getY());
                        DrawTool.this.tempLayer.updateGraphic(graphicID, envelope.copy());
                        break;
                    case DrawTool.FREEHAND_POLYGON:
                        polygon.lineTo(point);
                        DrawTool.this.tempLayer.updateGraphic(graphicID, polygon);
                        break;
                    case DrawTool.FREEHAND_POLYLINE:
                        polyline.lineTo(point);
                        DrawTool.this.tempLayer.updateGraphic(graphicID, polyline);
                        break;
                    case DrawTool.CIRCLE:
                        double radius = Math.sqrt(Math.pow(startPoint.getX()
                                - point.getX(), 2)
                                + Math.pow(startPoint.getY() - point.getY(), 2));
                        getCircle(startPoint, radius, polygon);
                        DrawTool.this.tempLayer.updateGraphic(graphicID, polygon);
                        break;
				}
				return true;
			}
			return super.onDragPointerMove(from, to);
		}

		public boolean onDragPointerUp(MotionEvent from, MotionEvent to) {
			if (active && (drawType == ENVELOPE || drawType == FREEHAND_POLYGON
					|| drawType == FREEHAND_POLYLINE || drawType == CIRCLE)) {
				Point point = mapView.toMapPoint(to.getX(), to.getY());
				switch (drawType) {
				case DrawTool.ENVELOPE:
					envelope.setXMin(startPoint.getX() > point.getX() ? point
							.getX() : startPoint.getX());
					envelope.setYMin(startPoint.getY() > point.getY() ? point
							.getY() : startPoint.getY());
					envelope.setXMax(startPoint.getX() < point.getX() ? point
							.getX() : startPoint.getX());
					envelope.setYMax(startPoint.getY() < point.getY() ? point
							.getY() : startPoint.getY());
					break;
				case DrawTool.FREEHAND_POLYGON:
					polygon.lineTo(point);
					break;
				case DrawTool.FREEHAND_POLYLINE:
					polyline.lineTo(point);
					break;
				case DrawTool.CIRCLE:
					double radius = Math.sqrt(Math.pow(startPoint.getX()
							- point.getX(), 2)
							+ Math.pow(startPoint.getY() - point.getY(), 2));
					getCircle(startPoint, radius, polygon);
					break;
				}
				sendDrawEndEvent();
				startPoint = null;
				return true;
			}
			return super.onDragPointerUp(from, to);
		}

		public boolean onSingleTap(MotionEvent event) {
			Point point = mapView.toMapPoint(event.getX(), event.getY());
			if (active && (drawType==POLYGON || drawType==POLYLINE)) {
				switch (drawType) {
				case DrawTool.POLYGON:
                    latLonPoint=new MercatorEntity();
					if (startPoint == null) {
						startPoint = point;
						polygon.startPath(point);
                        latLonPoint.setX(point.getX());
                        latLonPoint.setY(point.getY());
                        LatLonPoint_List.add(latLonPoint);
					} else{
                        polygon.lineTo(point);
                        latLonPoint.setX(point.getX());
                        latLonPoint.setY(point.getY());
                        LatLonPoint_List.add(latLonPoint);
                    }

					DrawTool.this.tempLayer.updateGraphic(graphicID, polygon);
					break;
				case DrawTool.POLYLINE:
					latLonPoint=new MercatorEntity();
					if (startPoint == null) {
						startPoint = point;
						polyline.startPath(point);
						latLonPoint.setX(point.getX());
						latLonPoint.setY(point.getY());
						LatLonPoint_List.add(latLonPoint);
					} else {
						polyline.lineTo(point);
						latLonPoint.setX(point.getX());
						latLonPoint.setY(point.getY());
						LatLonPoint_List.add(latLonPoint);
					}
					Log.i("a", drawGraphic.getUid()+"");
					DrawTool.this.tempLayer.updateGraphic(graphicID, polyline);
					break;
				}
				return true;
			}
			return false;
		}

		public boolean onDoubleTap(MotionEvent event) {
			if (active &&(drawType==POLYGON || drawType==POLYLINE)) {
				Point point = mapView.toMapPoint(event.getX(), event.getY());
				switch (drawType) {
                    case DrawTool.POLYGON:
                        latLonPoint=new MercatorEntity();
                        polygon.lineTo(point);
                        latLonPoint.setX(point.getX());
                        latLonPoint.setY(point.getY());
                        LatLonPoint_List.add(latLonPoint);
                        break;
                    case DrawTool.POLYLINE:
						latLonPoint=new MercatorEntity();
                        polyline.lineTo(point);
						latLonPoint.setX(point.getX());
						latLonPoint.setY(point.getY());
						LatLonPoint_List.add(latLonPoint);
                        break;
				}
				sendDrawEndEvent();
				startPoint = null;
				return true;
			}
			return super.onDoubleTap(event);
		}

		private void getCircle(Point center, double radius, Polygon circle) {
			circle.setEmpty();
			Point[] points = getPoints(center, radius);
			circle.startPath(points[0]);
			for (int i = 1; i < points.length; i++)
				circle.lineTo(points[i]);
		}

		private Point[] getPoints(Point center, double radius) {
			Point[] points = new Point[50];
			double sin;
			double cos;
			double x;
			double y;
			for (double i = 0; i < 50; i++) {
				sin = Math.sin(Math.PI * 2 * i / 50);
				cos = Math.cos(Math.PI * 2 * i / 50);
				x = center.getX() + radius * sin;
				y = center.getY() + radius * cos;
				points[(int) i] = new Point(x, y);
			}
			return points;
		}
	}

    //

}
