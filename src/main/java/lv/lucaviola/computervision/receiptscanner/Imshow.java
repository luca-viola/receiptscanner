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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.*;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Imshow
{
  private JPanel topPanel;
  private JScrollPane scrollPanel;
  public JFrame Window;
  private ImageIcon image;
  private JLabel label;

  public Imshow(String title)
  {
    Window = new JFrame();
    image = new ImageIcon();
    label = new JLabel();
    scrollPanel=new JScrollPane();
    scrollPanel.getViewport().add(label);
    label.setIcon(image);
    Window.getContentPane().add(scrollPanel);
    Window.setResizable(true);
    Window.setTitle(title);
    Window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  public void showResizedImage(Mat img, int newImageWidth, int newImageHeight)
  {
    Imgproc.resize(img, img, new Size(newImageWidth, newImageHeight));
    showImage(img);
  }

  public void showImage(Mat img)
  {
    BufferedImage bufImage = null;
    try
    {
      bufImage = toBufferedImage(img);
      image.setImage(bufImage);
      Window.pack();
      label.updateUI();
      Window.setVisible(true);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private BufferedImage toBufferedImage(Mat m)
  {
    int type = BufferedImage.TYPE_BYTE_GRAY;
    if (m.channels() > 1)
      type = BufferedImage.TYPE_3BYTE_BGR;
    int bufferSize = m.channels() * m.cols() * m.rows();
    byte[] b = new byte[bufferSize];
    m.get(0, 0, b);
    BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    System.arraycopy(b, 0, targetPixels, 0, b.length);
    return image;
  }
}
