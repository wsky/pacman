package pacman.statements;

import pacman.ActivityMetadata;
import pacman.InArgument;
import pacman.NativeActivity;
import pacman.NativeActivityContext;
import pacman.OutArgument;
import pacman.RuntimeArgument;
import pacman.RuntimeArgument.ArgumentDirection;

public class Assign extends NativeActivity {
	public InArgument Value;
	public OutArgument To;

	@Override
	protected void cacheMetadata(ActivityMetadata metadata) {
		metadata.bindAndAddArgument(this.Value, new RuntimeArgument("Value", Object.class, ArgumentDirection.In));
		metadata.bindAndAddArgument(this.To, new RuntimeArgument("To", Object.class, ArgumentDirection.Out));
	}

	@Override
	protected void execute(NativeActivityContext context) {
		this.To.set(context, this.Value.get(context));
	}
}
