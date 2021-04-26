package cat.santfeliu.api.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cat.santfeliu.api.enumerator.JobSourcesEnum;
import cat.santfeliu.api.model.SourceModel;
import cat.santfeliu.api.repo.SourceRepo;

@Service
public class SourceParamsUtils {

	@Autowired
	private SourceRepo sourceRepo;

	private String SOURCE_GEOSERVER = JobSourcesEnum.GEOSERVER.getValue();
	
	
	public Map<String, String> getSourceParams(String source) {
HashMap<String, String> sourceParams = new HashMap<>();
if (source.equals(SOURCE_GEOSERVER)) {
	List<SourceModel> params = sourceRepo.findBy(SOURCE_GEOSERVER);
	for (SourceModel param: params) {
sourceParams.put(param.getSourceParamName(), param.getSourceParamValue());
	}
}
return sourceParams;
	}
}
