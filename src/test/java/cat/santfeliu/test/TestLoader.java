package cat.santfeliu.test;

import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.utils.ComponentProperty;
import cat.santfeliu.api.utils.ConfigContainer;
import cat.santfeliu.api.utils.PropertyManager;
import com.google.gson.JsonObject;

/**
 *
 * @author realor
 */
public class TestLoader extends ConnectorLoader
{
  @ComponentProperty(name="url",
    description="Connection url")
  String url;
  
  @ComponentProperty(name="layer")
  String layer;
  
  @ComponentProperty(name="username")
  String username;

  @ComponentProperty(name="password")
  String password;

  @ComponentProperty(name="maxEntities", 
    description="Max number of entities to return", required = false)
  int maxEntities;

  @Override
  public void init(String inventoryName, ConfigContainer params)
  {
    super.init(inventoryName, params); // params not needed inside loader, remove it
    
    PropertyManager.inject(this, params); // inject params in loader fields.
  }
  
  @Override
  public JsonObject load(long timeout)
  {
    System.out.println("Connecting to " + url + " with username " + username);
    return null;
  }

  @Override
  public void stop()
  {
  } 
  
  public static void main(String[] args)
  {
    TestLoader loader = new TestLoader();
    System.out.println(PropertyManager.getProperties(loader));
  }
}
