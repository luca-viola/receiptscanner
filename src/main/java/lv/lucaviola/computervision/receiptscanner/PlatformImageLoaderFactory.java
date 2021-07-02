package lv.lucaviola.computervision.receiptscanner;

import org.apache.log4j.Logger;

/**
 * Created by lviola on 28/01/16.
 */
public class PlatformImageLoaderFactory
{
  private static Logger log= Logger.getLogger(PlatformImageLoaderFactory.class);
  private static final String [] IMAGE_LOADERS = { "JdkImageLoader", "AndroidImageLoader" };

  public static PlatformImageLoader getInstance()
  {
    for(String IMAGE_LOADER : IMAGE_LOADERS )
    {
      Package pckg=PlatformImageLoader.class.getPackage();

      try
      {
        PlatformImageLoader obj=(PlatformImageLoader)Class.forName(pckg.getName()+"."+IMAGE_LOADER).newInstance();
        return obj;
      }
      catch (InstantiationException e)
      {
        e.printStackTrace();
      }
      catch (IllegalAccessException e)
      {
        e.printStackTrace();
      }
      catch (ClassNotFoundException e)
      {
        e.printStackTrace();
      }
    }
    return null;
  }
}
