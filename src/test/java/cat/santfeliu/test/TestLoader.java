package cat.santfeliu.test;

import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.utils.ConfigContainer;
import cat.santfeliu.api.utils.ConfigManager;
import com.google.gson.JsonObject;
import cat.santfeliu.api.utils.ConfigProperty;

/**
 *
 * @author realor
 */
public class TestLoader extends ConnectorLoader
{
  @ConfigProperty(name="url",
    description="Connection url")
  String url;
  
  @ConfigProperty(name="layer")
  String layer;
  
  @ConfigProperty(name="username")
  String username;

  @ConfigProperty(name="password")
  String password;

  @ConfigProperty(name="maxEntities", 
    description="Max number of entities to return", required = false)
  int maxEntities;

  @Override
  public void init(String inventoryName, ConfigContainer params)
  {
    super.init(inventoryName, params); // params not needed inside loader, remove it
    
    ConfigManager.inject(this, params); // inject params in loader fields.
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
    ConfigManager.getProperties(loader);
    System.out.println(ConfigManager.getProperties(loader));
  }
}
