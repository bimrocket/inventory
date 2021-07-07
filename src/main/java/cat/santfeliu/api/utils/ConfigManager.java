package cat.santfeliu.api.utils;

import cat.santfeliu.api.components.ConnectorComponent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author realor
 */
public class ConfigManager
{
  public static List<PropertyInfo> getProperties(
    ConnectorComponent component)
  {
    List<PropertyInfo> properties = new ArrayList<>();
  
    Class cls = component.getClass();
    Field[] fields = cls.getDeclaredFields();
    for (Field field : fields)
    {
      ConfigProperty property = field.getAnnotation(ConfigProperty.class);
      if (property != null)
      {
        PropertyInfo info = new PropertyInfo(property, field);        
        properties.add(info);
      }
    }
    return properties;    
  }
  
  public static void inject(ConnectorComponent component, 
    ConfigContainer config)
  {
    Class cls = component.getClass();
    Field[] fields = cls.getDeclaredFields();
    for (Field field : fields)
    {
      ConfigProperty property = field.getAnnotation(ConfigProperty.class);
      if (property != null)
      {
        String name = property.name();
        String textValue = config.getParamValue(name);
        Object value = null;
        if (field.getType() == String.class)
        {
          value = textValue;
        }
        else if (field.getType() == boolean.class 
          || field.getType() == Boolean.class)
        {
          value = Integer.parseInt(textValue);          
        }
        else if (field.getType() == int.class 
          || field.getType() == Integer.class)
        {
          value = Integer.parseInt(textValue);          
        }
        else if (field.getType() == double.class 
          || field.getType() == Double.class)
        {
          value = Double.parseDouble(textValue);          
        }
        // ... other conversions (arrays, etc...)
        try
        {
          field.setAccessible(true);
          field.set(component, value);
        }
        catch (Exception ex)
        {
          // warn
        }
      }
    }    
  }
  
  public static class PropertyInfo
  {
    private final String name;
    private final boolean required;
    private final String description;
    private final Field field;

    PropertyInfo(ConfigProperty property, Field field)
    {
      this.name = property.name();
      this.required = property.required();
      this.description = property.description();
      this.field = field;
    }
    
    public String getName()
    {
      return name;
    }

    public boolean isRequired()
    {
      return required;
    }

    public String getDescription()
    {
      return description;
    }

    public Field getField()
    {
      return field;
    }
    
    @Override
    public String toString()
    {
      StringBuilder buffer = new StringBuilder();
      buffer.append(name);
      if (required) buffer.append("*");
      buffer.append(" : ").append(field.getType().getSimpleName());
      return buffer.toString();
    }
  }
}
