package it.stefanochizzolini.reflex;

import java.io.File;
import java.net.URL;

public class Class
{
  /**
    Retrieves the primary available location of a class.

    @param className The name of the class whose location has to be retrieved.
  */
  public static String getLocation(
    String className
    )
  {
    String location;
    {
      String classResourcePath = className.replace('.', '/') + ".class";
      // Get class position!
      URL classUrl = Thread.currentThread().getContextClassLoader().getResource(classResourcePath);
      if(classUrl == null)
        return null;

      // Get class location!
      try
      {location =  new File(classUrl.getFile()).getPath();}
      catch(Exception e)
      {throw new RuntimeException(e);}
      try
      {location = java.net.URLDecoder.decode(location,"UTF-8");}
      catch(Exception e)
      {/* NOOP. */}

      // Eliminate the inner path!
      int index = location.indexOf("!");
      if(index >= 0) // JAR file.
      {location = location.substring(0, index);}
      else // Directory.
      {
        String osDependentClassPath = classResourcePath.replace('/',File.separatorChar);
        location = location.substring(0, location.indexOf(osDependentClassPath)-1);
      }
      // Eliminate the leading protocol identifier!
      index = location.indexOf(":");
      if(index >= 0)
      {location = location.substring(++index);}
    }
    return location;
  }
}
