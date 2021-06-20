package cat.santfeliu.api.utils;

import java.io.InputStream;

import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Service;

@Service
public class InventoryUtils {

	private PythonInterpreter pythonInterpreter = new PythonInterpreter();

	public String getGuid() {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("python/guid.py");
		pythonInterpreter.execfile(is);
		pythonInterpreter.exec("x = new()");
		return pythonInterpreter.get("x").asString();
	}
}
