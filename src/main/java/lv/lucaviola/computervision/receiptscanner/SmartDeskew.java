/*
  This file is part of MyReceipts - (C) 2016 by "Luca Viola" <luca@3am.it>

  MyReceipts is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  MyReceipts is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.

  http://www.gnu.org/licenses/gpl.html
*/
package lv.lucaviola.computervision.receiptscanner;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SmartDeskew
{
  private final Mat image;
  private Point [] points;

  public SmartDeskew(MatOfPoint vertices, Mat image)
  {
    this.points=vertices.toArray();
    this.image=image;
  }

  private Point[] reorderVerticesCounterClockWiseFromTopLeft()
  {
    Point[] rect=new Point[4];
    double [] temp=new double[4];

    for(int i=0; i<points.length; i++)
      temp[i]=points[i].x+points[i].y;
    rect[0]=points[indexOfMin(temp)];
    rect[2]=points[indexOfMax(temp)];
    Arrays.fill(temp,0.0);
    for(int i=0; i<points.length; i++)
      temp[i]=points[i].x-points[i].y;
    rect[1]=points[indexOfMin(temp)];
    rect[3]=points[indexOfMax(temp)];
    return rect;
  }

  private int indexOfMax(double[] s)
  {
    List list=Arrays.asList(s[0],s[1],s[2],s[3]);
    int maxIndex = list.indexOf(Collections.max(list));
    return maxIndex;
  }

  private int indexOfMin(double[] s)
  {
    List list=Arrays.asList(s[0],s[1],s[2],s[3]);
    int minIndex = list.indexOf(Collections.min(list));
    return minIndex;
  }

  private int getHeightOfSkewedRectangle(Point topLeft, Point bottomLeft, Point bottomRight, Point topRight) {
    double heightA = Math.sqrt(Math.pow((topRight.x - bottomRight.x), 2) + Math.pow((topRight.y - bottomRight.y), 2));
    double heightB = Math.sqrt(Math.pow((topLeft.x - bottomLeft.x), 2) + Math.pow((topLeft.y - bottomLeft.y), 2));
    return Math.max((int) heightA, (int) (heightB));
  }

  private int getWidthOfSkewedRectangle(Point topLeft, Point bottomLeft, Point bottomRight, Point topRight) {
    double widthA = Math.sqrt(Math.pow((bottomRight.x - bottomLeft.x),2) + Math.pow((bottomRight.y - bottomLeft.y),2));
    double widthB = Math.sqrt(Math.pow((topRight.x - topLeft.x), 2) + Math.pow((topRight.y - topLeft.y), 2));
    return Math.max((int) widthA, (int) widthB);
  }

  public Mat deskew()
  {
    Point [] rect=reorderVerticesCounterClockWiseFromTopLeft();
    Point topLeft=rect[0];
    Point bottomLeft=rect[1];
    Point bottomRight=rect[2];
    Point topRight=rect[3];

    int maxWidth = getWidthOfSkewedRectangle(topLeft, bottomLeft, bottomRight, topRight);
    int maxHeight = getHeightOfSkewedRectangle(topLeft, bottomLeft, bottomRight, topRight);

    List<Point> source = new ArrayList<Point>();
    source.add(topLeft);
    source.add(topRight);
    source.add(bottomRight);
    source.add(bottomLeft);
    Mat src = Converters.vector_Point2f_to_Mat(source);

    List<Point> destination = new ArrayList<Point>();
    destination.add(new Point(0,0));
    destination.add(new Point(maxWidth-1,0));
    destination.add(new Point(maxWidth-1,maxHeight - 1));
    destination.add(new Point(0, maxHeight - 1));
    Mat dst = Converters.vector_Point2f_to_Mat(destination);

    Mat transformationMatrix=Imgproc.getPerspectiveTransform(src, dst);
    Mat warped=new Mat();
    Imgproc.warpPerspective(image, warped,transformationMatrix, new Size(maxWidth, maxHeight));

    return warped;
  }
}
