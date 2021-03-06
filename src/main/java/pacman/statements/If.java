package pacman.statements;

import pacman.*;
import pacman.RuntimeArgument.ArgumentDirection;

public class If extends NativeActivity {
	public InArgument Condition;
	public Activity Then;
	public Activity Else;
	
	public If() {
		super();
		this.Condition = new InArgument(true);
	}
	
	@Override
	protected void cacheMetadata(ActivityMetadata metadata) {
		metadata.bindAndAddArgument(this.Condition,
				new RuntimeArgument("Condition", Boolean.class, ArgumentDirection.In));
		metadata.addChild(this.Then);
		metadata.addChild(this.Else);
	}
	
	@Override
	protected void execute(NativeActivityContext context) {
		Boolean condition = (Boolean) this.Condition.get(context);
		if (condition != null && condition) {
			if (this.Then != null)
				context.scheduleActivity(this.Then);
			return;
		}
		if (this.Else != null)
			context.scheduleActivity(this.Else);
	}
}
