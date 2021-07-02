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

import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.io.IOException;
import java.net.URL;

public class Main
{
  private static String outFile="/tmp/output.png";
  static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
  private static Logger log= Logger.getLogger(Main.class);

  public static void main( String args[] ) throws IOException
  {
//    JdkImageLoader jiad=new JdkImageLoader();
    PlatformImageLoader jiad=PlatformImageLoaderFactory.getInstance();
    String resourceName= "scontrino7.png";
    URL url = ReceiptScanner.class.getClassLoader().getResource(resourceName);

    log.info("Receipt scanner 1.0 by Luca Viola");
    ReceiptScanner d=new ReceiptScanner();
    Mat image = jiad.loadFromResources(url);
    log.info("scanning " + resourceName + "..");
    image=d.scan(image);
    Imshow im = new Imshow("Scanned: "+resourceName);
    im.showImage(image);
    Highgui.imwrite(outFile, image);
    log.info("File saved in " + outFile);
  }
}
