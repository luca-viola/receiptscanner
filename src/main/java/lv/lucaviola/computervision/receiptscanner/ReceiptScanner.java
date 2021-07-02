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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReceiptScanner
{
  private static final boolean DEBUG_FLAG = false;
  private static final int RESIZED_PROP_HEIGHT=500;
  private Logger log= Logger.getLogger(Main.class);

  public ReceiptScanner() {}

  public Mat scan(Mat image)
  {
    MatOfPoint realVertices = getTheTargetImageVertices(image);
    Mat warpedImage = new SmartDeskew(realVertices, image).deskew();
    Mat enhancedImage=adaptiveThreshold(warpedImage);
    return enhancedImage;
  }

  public Mat scanContours(Mat image)
  {
    MatOfPoint realVertices = getTheTargetImageVertices(image);
    drawContours(image, realVertices);
    return image;
  }


  private MatOfPoint getTheTargetImageVertices(Mat image)
  {
    int RESIZED_PROP_WIDTH = (int)((double)(RESIZED_PROP_HEIGHT*((double)image.width()/(double) image.height())));
    double ratio=(double)image.height()/RESIZED_PROP_HEIGHT;

    Mat resizedImage = resizeImage(image, RESIZED_PROP_WIDTH, RESIZED_PROP_HEIGHT);
    Mat greyImage = colorImageToGrayScale(resizedImage);
    Mat blurredImage = blurImage(greyImage);
    Mat cannyEdgeImage = cannyEdgeDetect(blurredImage);
    MatOfPoint vertices = findContours(cannyEdgeImage);
    return calculateOriginalImageVertices(vertices,ratio);
  }

  private Mat adaptiveThreshold(Mat image)
  {
    Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
    Imgproc.adaptiveThreshold(image, image, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15, 7);
    return image;
  }

  private MatOfPoint calculateOriginalImageVertices(MatOfPoint vertices, double ratio)
  {
    Point [] points=vertices.toArray();
    for(int i=0; i<4; i++)
    {
      points[i].x=points[i].x*ratio;
      points[i].y=points[i].y*ratio;
    }
    MatOfPoint result=new MatOfPoint();
    result.fromArray(points);
    return result;
  }

  private void drawContours(Mat image, MatOfPoint contour)
  {
    List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
    contourList.add(contour);
    Imgproc.drawContours(image, contourList, -1, new Scalar(0, 255, 0), 2);
  }

  private List<MatOfPoint> getContoursSortedFromBiggestToSmallestArea(Mat image)
  {
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
    Collections.sort(contours, (o1, o2) -> {
      if (Imgproc.contourArea(o1) == Imgproc.contourArea(o2)) return 0;
      if (Imgproc.contourArea(o1) < Imgproc.contourArea(o2)) return 1;
      return -1;
    });
    return contours;
  }

  private MatOfPoint2f getBiggestRectangleByArea(List<MatOfPoint> contours)
  {
    MatOfPoint2f biggestContourArea = null;
    for (MatOfPoint contour : contours)
    {
      MatOfPoint2f c = new MatOfPoint2f();
      c.fromList(contour.toList());
      double perimeter = Imgproc.arcLength(c, true);
      MatOfPoint2f douglasPeuckerApproxCurve = new MatOfPoint2f();
      Imgproc.approxPolyDP(c, douglasPeuckerApproxCurve, 0.02 * perimeter, true);
      List<Point> points = douglasPeuckerApproxCurve.toList();
      showPointsForDebug(points);

      if (douglasPeuckerApproxCurve.toList().size() == 4)
      {
        biggestContourArea = douglasPeuckerApproxCurve;
        log.debug("CHOSEN");
        break;
      }
    }
    return biggestContourArea;
  }

  private MatOfPoint findContours(Mat image)
  {
    List<MatOfPoint> contours = getContoursSortedFromBiggestToSmallestArea(image);
    MatOfPoint2f biggestContourArea = getBiggestRectangleByArea(contours);
    MatOfPoint vertices = new MatOfPoint();
    vertices.fromList(biggestContourArea.toList());
    return vertices;
  }

  private Mat cannyEdgeDetect(Mat blur)
  {
    Mat canny = new Mat(blur.height(), blur.width(), CvType.CV_8UC1);
    Imgproc.Canny(blur, canny, 75, 200);
    return canny;
  }

  private Mat blurImage(Mat grey)
  {
    Mat blur = new Mat(grey.height(), grey.width(), CvType.CV_8UC1);
    Imgproc.GaussianBlur(grey, blur, new Size(5, 5), 0);
    return blur;
  }

  private Mat colorImageToGrayScale(Mat resizeimage)
  {
    Mat grey = new Mat(resizeimage.height(), resizeimage.width(), CvType.CV_8UC1);
    Imgproc.cvtColor(resizeimage, grey, Imgproc.COLOR_RGB2GRAY);
    return grey;
  }

  private Mat resizeImage(Mat image, int width,int height)
  {
    Mat resizeimage = new Mat();
    Size sz = new Size(width, height);
    Imgproc.resize(image, resizeimage, sz, 0, 0, Imgproc.INTER_CUBIC);
    return resizeimage;
  }

  private void showPointsForDebug(List<Point> points)
  {
    for (Point point : points)
      log.debug("[ X:" + point.x + ",Y: " + point.y + "]");
    log.debug("*****");
  }
}
