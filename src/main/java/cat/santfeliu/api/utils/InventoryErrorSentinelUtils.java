package cat.santfeliu.api.utils;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cat.santfeliu.api.beans.SentinelError;
import cat.santfeliu.api.model.ConnectorExecutionErrorsDb;
import cat.santfeliu.api.model.ConnectorExecutionErrorsDbId;
import cat.santfeliu.api.repo.ConnectorExecutionErrorsRepo;

@Service
public class InventoryErrorSentinelUtils {
	
	public static final HashMap<String, HashMap<SentinelError, ConnectorExecutionErrorsDb>> actualErrorStack = new HashMap<>();
	
	@Autowired
	private ConnectorExecutionErrorsRepo repo;
	
	public SentinelError senError(String connectorName, Integer executionId, String errorKey) {
		return this.senError(connectorName, executionId, errorKey, 4);
	}
	
	public SentinelError senError(String connectorName, Integer executionId, String errorKey, int place) {
		SentinelError toReturn = null;
		String executionPlace = this.getPlace(place);
		SentinelError error = new SentinelError().executionId(executionId).errorKey(errorKey).errorPlace(executionPlace).conName(connectorName);
		if (actualErrorStack.containsKey(connectorName)) {
			if (!actualErrorStack.get(connectorName).containsKey(error)) {
				actualErrorStack.get(connectorName).put(error, this.errToDb(error));
				toReturn = error;
			} else {
				for (SentinelError err : actualErrorStack.get(connectorName).keySet()) {
					if (err.equals(error)) {
						toReturn = error;
					}
				};
			}
		} else {
			actualErrorStack.put(connectorName, new HashMap<>());
			actualErrorStack.get(connectorName).put(error, this.errToDb(error));
			toReturn = error;
		}
		
		return toReturn;
	}
	
	private ConnectorExecutionErrorsDb errToDb(SentinelError err, ConnectorExecutionErrorsDb errDb) {
		Optional<ConnectorExecutionErrorsDb> errDbFound = this.repo.findById(new ConnectorExecutionErrorsDbId().errorKey(err.getErrorKey()).errorPlace(err.getErrorPlace()).executionId(err.getExecutionId()));
		if (errDbFound.isPresent()) {
			errDb = errDbFound.get();
		}
		errDb.setExecutionId(err.getExecutionId());
		errDb.setExecutionConnectorName(err.getConnectorName());
		errDb.setErrorKey(err.getErrorKey());
		errDb.setErrorPlace(err.getErrorPlace());
		errDb.setErrorDescription(err.getErrorDescription());
		errDb.setErrorGlobalIds(err.getErrorGlobalIds());
		errDb.setErrorOccurrences(err.getErrorOccurrences());
		errDb.setErrorExceptionName(err.getErrorException());
		return errDb;
	}
	
	private ConnectorExecutionErrorsDb errToDb(SentinelError err) {
		ConnectorExecutionErrorsDb errDb = new ConnectorExecutionErrorsDb();
		return this.errToDb(err, errDb);
	}
	
	public boolean saveAllAndFlush(String connectorName) {
		if (actualErrorStack.containsKey(connectorName)) {
			for (Entry<SentinelError, ConnectorExecutionErrorsDb> e : actualErrorStack.get(connectorName).entrySet()) {
				this.repo.save(this.errToDb(e.getKey(), e.getValue()));
			}
			actualErrorStack.get(connectorName).clear();
		}
		return true;
	}
	
	
	
	public String getPlace(int stackTraceId) {
		StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[stackTraceId];
		return stackTrace.getMethodName() + "@" + stackTrace.getClassName() + "(line:" +stackTrace.getLineNumber() + ")";
	}
}
