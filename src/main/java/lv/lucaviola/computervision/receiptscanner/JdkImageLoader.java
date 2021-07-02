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

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class JdkImageLoader implements PlatformImageLoader
{
  @Override
  public Mat loadFromResources(URL resourceUrl) throws IOException
  {
    String filepath = resourceUrl.getPath();
    Mat image= Highgui.imread(filepath);
    return image;
  }

  @Override
  public Mat loadFromFile(String fileName) throws IOException
  {
    BufferedImage bi=ImageIO.read(new File(fileName));
    Mat mat = bufferedImageToMat(bi);
    return mat;
  }

  @Override
  public Mat loadFromUrl(URL onlineUrl) throws IOException
  {
    BufferedImage bi=ImageIO.read(onlineUrl);
    Mat image = bufferedImageToMat(bi);
    return image;
  }

  private Mat bufferedImageToMat(BufferedImage bi)
  {
    Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
    byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
    mat.put(0, 0, data);
    return mat;
  }
}
