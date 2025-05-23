package app.tuxguitar.android.view.dialog.bend;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import app.tuxguitar.android.graphics.TGColorImpl;
import app.tuxguitar.android.graphics.TGPainterImpl;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.effects.TGEffectBend;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIPosition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TGBendEditor extends View {

	private static final int X_LENGTH = TGEffectBend.MAX_POSITION_LENGTH + 1;
	private static final int Y_LENGTH = TGEffectBend.MAX_VALUE_LENGTH + 1;

	private float xSpacing;
	private float ySpacing;
	private float[] x;
	private float[] y;
	private List<UIPosition> points;
	private TGBendEditorListener listener;
	private TGBendEditorGestureDetector gestureDetector;

	public TGBendEditor(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.x = new float[X_LENGTH];
		this.y = new float[Y_LENGTH];
		this.points = new ArrayList<UIPosition>();
		this.gestureDetector = new TGBendEditorGestureDetector(context, this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float width = MeasureSpec.getSize(widthMeasureSpec);

		this.updateDimensions( width );

		float measuredWidth = width;
		float measuredHeight = (getPaddingTop() + (this.ySpacing * Y_LENGTH) + getPaddingBottom());

		this.setMeasuredDimension(Math.round(measuredWidth), Math.round(measuredHeight));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		UIPainter painter = createPainter(canvas);

		this.paintEditor(painter);

	    painter.dispose();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return (this.gestureDetector.processTouchEvent(event) || super.onTouchEvent(event));
	}

	public void updateDimensions(float expectedWidth){
		this.xSpacing = (expectedWidth / X_LENGTH);
		this.ySpacing = (this.xSpacing / 2);

		float xMargin = (this.xSpacing / 2);
		float yMargin = (this.ySpacing / 2);

		for(int i = 0; i < this.x.length; i++){
			this.x[i] = xMargin + (i * this.xSpacing);
		}
		for(int i = 0; i < this.y.length; i++){
			this.y[i] = yMargin + (i * this.ySpacing);
		}
	}

	public UIPainter createPainter(Canvas canvas){
		return new TGPainterImpl(canvas);
	}

	public void paintEditor(UIPainter painter){
		for(int i = 0;i < this.x.length;i++){
			this.setStyleX(painter,i);
			painter.initPath();
			painter.setAntialias(false);
			painter.moveTo(this.x[i], this.y[0]);
			painter.lineTo(this.x[i], this.y[Y_LENGTH - 1]);
			painter.closePath();
		}
		for(int i = 0;i < this.y.length;i++){
			this.setStyleY(painter,i);
			painter.initPath();
			painter.setAntialias(false);
			painter.moveTo(this.x[0] , this.y[i]);
			painter.lineTo(this.x[X_LENGTH - 1], this.y[i]);
			painter.closePath();
		}

		UIPosition prevPoint = null;
		painter.setLineStyleSolid();
		painter.setLineWidth(2);
		painter.setForeground(new TGColorImpl(new UIColorModel(0x99, 0x99, 0x99)));

		for(UIPosition point : this.points){
			if( prevPoint != null ){
				painter.initPath();
				painter.moveTo(prevPoint.getX(), prevPoint.getY());
				painter.lineTo(point.getX(), point.getY());
				painter.closePath();
			}
			prevPoint = point;
		}

		painter.setLineWidth(5);
		painter.setForeground(new TGColorImpl(new UIColorModel(0, 0, 0)));

		for(UIPosition point : this.points){
			painter.initPath();
			painter.setAntialias(false);
			painter.addRectangle(point.getX() - 2,point.getY() - 2,5,5);
			painter.closePath();
		}
		painter.setLineWidth(1);
	}

	public void setStyleX(UIPainter painter, int i){
		painter.setLineStyleSolid();
		if( i == 0 || i == (X_LENGTH - 1) ){
			painter.setForeground(new TGColorImpl(new UIColorModel(0, 0, 0)));
		} else {
			painter.setForeground(new TGColorImpl(new UIColorModel(0, 0, 0xff)));
			if((i % 3) > 0){
				painter.setLineStyleDot();
			}
		}
	}

	public void setStyleY(UIPainter painter, int i){
		painter.setLineStyleSolid();
		if( i == 0 || i == (Y_LENGTH - 1) ){
			painter.setForeground(new TGColorImpl(new UIColorModel(0, 0, 0)));
		} else {
			painter.setForeground(new TGColorImpl(new UIColorModel(0xff, 0, 0)));

			if((i % 2) > 0){
				painter.setLineStyleDot();
				painter.setForeground(new TGColorImpl(new UIColorModel(0x99, 0x99, 0x99)));
			}else if((i % 4) > 0){
				painter.setLineStyleDot();
			}
		}
	}

	public void checkPoint(float x, float y){
		UIPosition point = new UIPosition(this.getX(x),this.getY(y));
		if(!this.removePoint(point)){
			this.removePointsAtXLine(point.getX());
			this.addPoint(point);
			this.orderPoints();
		}
	}

	public boolean removePoint(UIPosition point){
		UIPosition pointToRemove = null;
		for(UIPosition currPoint : this.points){
			if( currPoint.getX() == point.getX() && currPoint.getY() == point.getY() ){
				pointToRemove = currPoint;
				break;
			}
		}

		if( pointToRemove != null ) {
			this.points.remove(pointToRemove);
			return true;
		}
		return false;
	}

	public void orderPoints(){
		for(int i = 0;i < this.points.size();i++){
			UIPosition minPoint = null;
			for(int noteIdx = i;noteIdx < this.points.size();noteIdx++){
				UIPosition point = (UIPosition)this.points.get(noteIdx);
				if( minPoint == null || point.getX() < minPoint.getX() ){
					minPoint = point;
				}
			}
			this.points.remove(minPoint);
			this.points.add(i,minPoint);
		}
	}

	public void removePointsAtXLine(float x){
		List<UIPosition> pointsToRemove = new ArrayList<UIPosition>();
		for(UIPosition point : this.points){
			if( point.getX() == x ){
				pointsToRemove.add(point);
				break;
			}
		}
		this.points.removeAll(pointsToRemove);
	}

	public void addPoint(UIPosition point){
		this.points.add(point);
	}

	public float getX(float pointX){
		float currPointX = -1;
		for(int i = 0;i < this.x.length;i++){
			if( currPointX < 0 ){
				currPointX = this.x[i];
			}else{
				float distanceX = Math.abs(pointX - currPointX);
				float currDistanceX = Math.abs(pointX - this.x[i]);
				if( currDistanceX < distanceX ){
					currPointX = this.x[i];
				}
			}
		}
		return currPointX;
	}

	public float getY(float pointY){
		float currPointY = -1;
		for(int i = 0;i < this.y.length;i++){
			if( currPointY < 0 ){
				currPointY = this.y[i];
			}else{
				float distanceX = Math.abs(pointY - currPointY);
				float currDistanceX = Math.abs(pointY - this.y[i]);
				if( currDistanceX < distanceX ){
					currPointY = this.y[i];
				}
			}
		}
		return currPointY;
	}

	public boolean isEmpty(){
		return this.points.isEmpty();
	}

	public void addPointFromBendPoint(TGEffectBend.BendPoint bendPoint){
		int indexX = bendPoint.getPosition();
		int indexY = (this.y.length - bendPoint.getValue()) - 1;
		if( indexX >= 0 && indexX < this.x.length && indexY >= 0 && indexY < this.y.length ){
			UIPosition point = new UIPosition(0,0);
			point.setX(this.x[indexX]);
			point.setY(this.y[indexY]);
			this.points.add(point);
		}
	}

	public void addBendPointFromPoint(TGEffectBend effect, UIPosition point){
		int position = 0;
		int value = 0;
		for(int i = 0; i < this.x.length; i++){
			if( point.getX() == this.x[i] ){
				position = i;
			}
		}
		for(int i = 0; i < this.y.length; i++){
			if( point.getY() == this.y[i] ){
				value = (this.y.length - i) -1;
			}
		}
		effect.addPoint(position,value);
	}

	public void loadBend(TGEffectBend effect){
		this.points.clear();
		Iterator<?> it = effect.getPoints().iterator();
		while(it.hasNext()){
			TGEffectBend.BendPoint bendPoint = (TGEffectBend.BendPoint)it.next();
			this.addPointFromBendPoint(bendPoint);
		}
		this.postInvalidate();
	}

	public TGEffectBend createBend(TGFactory factory){
		if(this.points != null && !this.points.isEmpty()){
			TGEffectBend bend = factory.newEffectBend();
			for(UIPosition point : this.points){
				addBendPointFromPoint(bend, point);
			}
			return bend;
		}
		return null;
	}

	public void editPoint(float x, float y) {
		this.checkPoint(x, y);
		this.notifyChanged();
		this.postInvalidate();
	}

	public void notifyChanged() {
		if( this.getListener() != null ) {
			this.getListener().onChange();
		}
	}

	public TGBendEditorListener getListener() {
		return listener;
	}

	public void setListener(TGBendEditorListener listener) {
		this.listener = listener;
	}
}
