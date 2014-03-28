package com.taobao.top.pacman;

import com.taobao.top.pacman.ActivityUtilities.ProcessActivityCallback;

public class RenderProcessActivityCallback implements ProcessActivityCallback {
	@Override
	public void execute(Activity activity) {
		this.render(activity);
	}

	protected void write(Object input) {
		Trace.write(input + "\n");
	}

	private void render(Activity activity) {
		int depth = getDepth(activity);
		String blank = "";
		for (int i = 0; i < depth; i++)
			blank = "    " + blank;

		String id = getId(activity);
		id = activity.getMemberOf().getOwner() == null ?
				getId(activity) :
				// private memeber
				activity.getMemberOf().getOwner().getId() + "." + activity.getId() + "";

		this.write(String.format("%s%s %s, displayName=%s",
				depth >= 1 ? blank + "- " : blank,
				id,
				activity.getClass().getSimpleName(),
				activity.getDisplayName()));

		for (RuntimeArgument argument : activity.getRuntimeArguments())
			this.write(String.format(
					"%s    * %s#%s argument: %s, direction=%s",
					blank, id, argument.getId(), argument.getName(), argument.getDirection()));

		for (Variable variable : activity.getRuntimeVariables())
			this.write(String.format(
					"%s    * %s#%s variable: %s, isPublic=%s",
					blank, id, variable.getId(), variable.getName(), variable.isPublic()));

		for (Variable variable : activity.getImplementationVariables())
			this.write(String.format(
					"%s    * %s#%s implVariable: %s, isPublic=%s",
					blank, id, variable.getId(), variable.getName(), variable.isPublic()));
	}

	private int getDepth(Activity activity) {
		int depth = 0;
		do {
			if (activity.getParent() == null)
				return depth;
			depth++;
			activity = activity.getParent();
		} while (true);
	}

	private String getId(Activity activity) {
		String id = Integer.toString(activity.getId());
		while (activity.getParent() != null) {
			id = activity.getParent().getId() + "-" + id;
			activity = activity.getParent();
		}
		return id;
	}

}
