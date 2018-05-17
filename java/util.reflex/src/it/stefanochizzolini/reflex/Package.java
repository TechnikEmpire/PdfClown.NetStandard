package it.stefanochizzolini.reflex;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Package
{
  /**
    Retrieves all the statically-available local classes
    belonging to the specified package.

    @param packageName The package name to search.
  */
  public static List<java.lang.Class<?>> getClasses(
    String packageName
    )
  {
    return getClasses(
      packageName,
      (System.getProperty("sun.boot.class.path") + File.pathSeparator // Bootstrap classes.
          + System.getProperty("java.ext.dirs") + File.pathSeparator // Extension classes.
          + System.getProperty("java.class.path") // User classes.
        ).split(File.pathSeparator)
      );
  }

  /**
    Retrieves all the statically-available classes
    belonging to the specified package
    and located in any specified location.

    @param packageName The package name to search.
    @param locations The local paths to search.
  */
  public static List<java.lang.Class<?>> getClasses(
    String packageName,
    String[] locations
    )
  {
    try
    {
      ArrayList<java.lang.Class<?>> classes = new ArrayList<java.lang.Class<?>>();
      {
        String packagePath = packageName.replace('.','/') + '/';
        String osDependentPackagePath = packagePath.replace('/',File.separatorChar);
        for(String location : locations)
        {
          File locationFile = new File(location);
          if(!locationFile.exists())
            continue;

          if(locationFile.isDirectory()) // Directory.
          {
            locationFile = new File(location + File.separator + osDependentPackagePath);
            if(!locationFile.exists())
              continue;

            // Get the list of the files contained in the package!
            String[] filePaths = locationFile.list();
            for(String filePath : filePaths)
            {
              // Is it a class?
              if(filePath.endsWith(".class"))
              {
                classes.add(
                  java.lang.Class.forName(
                    packageName + '.' + filePath.substring(0, filePath.length() - 6)
                    )
                  );
              }
            }
          }
          else // JAR file.
          {
            JarFile jarFile = new JarFile(locationFile);
            for(
              Enumeration<JarEntry> entries = jarFile.entries();
              entries.hasMoreElements();
              )
            {
              String jarEntryPath = ((JarEntry)entries.nextElement()).getName();
              if(
                jarEntryPath.startsWith(packagePath)
                  && jarEntryPath.endsWith(".class")
                )
              {
                classes.add(
                  java.lang.Class.forName(
                    jarEntryPath.substring(0, jarEntryPath.length() - 6).replaceAll("/",".")
                    )
                  );
              }
            }
          }
        }
      }
      return classes;
    }
    catch(Exception e)
    {throw new RuntimeException(e);}
  }
}
