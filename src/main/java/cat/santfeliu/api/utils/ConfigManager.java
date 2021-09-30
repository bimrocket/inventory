package cat.santfeliu.api.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cat.santfeliu.api.components.ConnectorComponent;

/**
 *
 * @author realor && kfiertke
 */
public class ConfigManager {

	private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

	public static List<PropertyInfo> getProperties(Class componentClass) {
		List<PropertyInfo> properties = new ArrayList<>();

		Field[] fields = componentClass.getDeclaredFields();
		for (Field field : fields) {
			ConfigProperty property = field.getAnnotation(ConfigProperty.class);
			if (property != null) {
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
    Field[] fields = Stream.concat(Arrays.stream(cls.getDeclaredFields()), Arrays.stream(cls.getSuperclass().getDeclaredFields()))
            .toArray(Field[]::new);
    for (Field field : fields)
    {
      ConfigProperty property = field.getAnnotation(ConfigProperty.class);
      if (property != null)
      {
        String name = property.name();
        Object value = null;
        if (name.endsWith("*")) {
        	value = List.copyOf(config.getMultipleParamValues(name));
        } else {
        	String textValue;
        	if (property.required()) {
        		textValue= config.getParamValue(name);
        	} else {
        		textValue = config.getParamValue(name, false);
        	}

            if (field.getType() == String.class)
            {
              value = textValue;
            }
            else if (field.getType() == boolean.class 
              || field.getType() == Boolean.class)
            {
              value = Boolean.parseBoolean(textValue);          
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
            else if (field.getType() == List.class) {
            	log.error("inject@ConfigManager - trying to inject config in field named {} with type List, list types configs should end with * if you want correctly inject list", field.getName());
            }
        }
        
        // ... other conversions (arrays, etc...)
        try
        {
          field.setAccessible(true);
          if (value == null) {
              field.set(component, property.defaultValue());         	  
          } else {
        	  field.set(component, value);
          }

        }
        catch (Exception ex)
        {
          // log warn
        	log.error("inject@ConfigManager - couldn't inject config property named {} in class {}, ", property.name(), field.getClass().getSimpleName());
        }
      }
    }    
  }

	public static class PropertyInfo {
		private final String name;
		private final boolean required;
		private final String description;
		private final Field field;

		PropertyInfo(ConfigProperty property, Field field) {
			this.name = property.name();
			this.required = property.required();
			this.description = property.description();
			this.field = field;
		}

		public String getName() {
			return name;
		}

		public boolean isRequired() {
			return required;
		}

		public String getDescription() {
			return description;
		}

		public Field getField() {
			return field;
		}

		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			buffer.append(name);
			if (required)
				buffer.append("*");
			buffer.append(" : ").append(field.getType().getSimpleName());
			return buffer.toString();
		}
	}
}
