package cat.santfeliu.api.utils;

import java.io.InputStream;

import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Service;

@Service
public class InventoryUtils {

	public String getGuid() {
		PythonInterpreter pythonInterpreter = new PythonInterpreter();
		InputStream is = InventoryUtils.class.getResourceAsStream("python/guid.py");
		pythonInterpreter.execfile(is);
		pythonInterpreter.exec("x = new()");
		return pythonInterpreter.get("x").asString();
	}
}
