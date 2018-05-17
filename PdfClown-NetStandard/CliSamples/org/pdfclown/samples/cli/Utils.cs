using System;

namespace org.pdfclown.samples.cli
{
  internal static class Utils
  {
    /**
      <summary>Gets the value of the given property.</summary>
      <param name="propertyName">Property name whose value has to be retrieved.</param>
    */
    public static object Get(
      this object obj,
      string propertyName
      )
    {return obj.GetType().GetProperty(propertyName).GetValue(obj,null);}

    /**
      <summary>Gets whether the object's definition is compatible with the given type's.</summary>
      <remarks>This extension method represents a workaround to the lack of type covariance support in C#.
      You may consider it equivalent to a (forbidden) overloading of the 'is' operator.</remarks>
      <param name="type">Type to verify against the object's definition.</param>
    */
    public static bool Is(
      this object obj,
      Type type
      )
    {
      Type objType = obj.GetType();
      Type typeDefinition = GetDefinition(type);
      while(objType != null)
      {
        if(typeDefinition == GetDefinition(objType))
          return true;

        objType = objType.BaseType;
      }
      return false;
    }

    public static void Prompt(
      string message
      )
    {
      Console.WriteLine("\n" + message);
      Console.WriteLine("Press ENTER to continue");
      Console.ReadLine();
    }

    private static Type GetDefinition(
      Type type
      )
    {return type.IsGenericType ? type.GetGenericTypeDefinition() : type;}
  }
}