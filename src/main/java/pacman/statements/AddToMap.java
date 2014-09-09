package pacman.statements;

import pacman.ActivityMetadata;
import pacman.InArgument;
import pacman.NativeActivity;
import pacman.NativeActivityContext;
import pacman.RuntimeArgument;
import pacman.RuntimeArgument.ArgumentDirection;

public class AddToMap extends NativeActivity {
	public InArgument Map;
	public InArgument Key;
	public InArgument Value;
	
	@Override
	protected void cacheMetadata(ActivityMetadata metadata) throws Exception {
		metadata.bindAndAddArgument(this.Map, new RuntimeArgument("Map", java.util.Map.class, ArgumentDirection.In));
		metadata.bindAndAddArgument(this.Key, new RuntimeArgument("Key", Object.class, ArgumentDirection.In));
		metadata.bindAndAddArgument(this.Value, new RuntimeArgument("Value", Object.class, ArgumentDirection.In));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void execute(NativeActivityContext context) throws Exception {
		((java.util.Map) this.Map.get(context)).put(this.Key.get(context), this.Value.get(context));
	}
	
}
