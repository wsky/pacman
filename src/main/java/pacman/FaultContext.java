package pacman;

public class FaultContext {
	private Exception exception;
	private ActivityInstance source;

	public FaultContext(Exception exception, ActivityInstance source) {
		this.exception = exception;
		this.source = source;
	}

	public Exception getException() {
		return this.exception;
	}
	
	public ActivityInstance getSource() {
		return this.source;
	}

}
