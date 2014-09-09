package pacman;

import java.util.HashMap;
import java.util.Map;

public class WorkflowExtensionManager {
	private Map<Class<?>, Object> extensions;

	public WorkflowExtensionManager() {
		this.extensions = new HashMap<Class<?>, Object>();
	}

	@SuppressWarnings("unchecked")
	public <T> T getExtension(Class<T> type) {
		return (T) this.extensions.get(type);
	}

	public <T> void addExtension(Class<T> type, T extension) {
		this.extensions.put(type, extension);
	}
}
