package cat.santfeliu.api.components;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import cat.santfeliu.api.beans.KafkaMessageDelete;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.repo.GlobalIdRepo;

public abstract class ConnectorTransformer extends ConnectorComponent {
	
	@Autowired
	protected GlobalIdRepo globalIdRepo;
	
	public abstract JsonObject transform(JsonObject object);
	
}