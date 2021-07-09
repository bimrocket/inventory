//package cat.santfeliu.test.loaders;
//
//import cat.santfeliu.api.components.ConnectorLoader;
//import cat.santfeliu.api.utils.ConfigContainer;
//import cat.santfeliu.api.utils.ConfigManager;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.google.gson.JsonObject;
//import cat.santfeliu.api.utils.ConfigProperty;
//
//public class EmployeeRestControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Autowired
//    private EmployeeRepository repository;
//
//    // write test cases here
//}
///**
// *
// * @author realor
// */
//public class TestLoader extends ConnectorLoader
//{
//  @ConfigProperty(name="url",
//    description="Connection url")
//  String url;
//  
//  @ConfigProperty(name="layer")
//  String layer;
//  
//  @ConfigProperty(name="username")
//  String username;
//
//  @ConfigProperty(name="password")
//  String password;
//
//  @ConfigProperty(name="maxEntities", 
//    description="Max number of entities to return", required = false)
//  int maxEntities;
//
//  @Override
//  public void init(String inventoryName)
//  {
//    super.init(inventoryName); // params not needed inside loader, remove it
//    
//    ConfigManager.inject(this, params); // inject params in loader fields.
//  }
//  
//  @Override
//  public JsonNode load(long timeout)
//  {
//    System.out.println("Connecting to " + url + " with username " + username);
//    return null;
//  }
//
//  @Override
//  public void stop()
//  {
//  } 
//  
//  public static void main(String[] args)
//  {
//    System.out.println(ConfigManager.getProperties(TestLoader.class));
//  }
//}
