package pacman.statements;

import java.io.PrintStream;

import pacman.ActivityContext;
import pacman.ActivityMetadata;
import pacman.InArgument;
import pacman.NativeActivity;
import pacman.NativeActivityContext;
import pacman.RuntimeArgument;
import pacman.Trace;
import pacman.RuntimeArgument.ArgumentDirection;

public class WriteLine extends NativeActivity {
	public InArgument Text;
	public InArgument TextWriter;

	public WriteLine() {
	}

	public WriteLine(Object text) {
		this.Text = new InArgument(text);
	}

	@Override
	protected void cacheMetadata(ActivityMetadata metadata) {
		if (this.Text == null)
			this.Text = new InArgument();
		metadata.bindAndAddArgument(this.Text, new RuntimeArgument("Text", String.class, ArgumentDirection.In));

		if (this.TextWriter == null)
			this.TextWriter = new InArgument();
		metadata.bindAndAddArgument(this.TextWriter, new RuntimeArgument("TextWriter", PrintStream.class, ArgumentDirection.In));
	}

	@Override
	protected void execute(NativeActivityContext context) {
		if (this.Text != null)
			this.println(context, this.Text.get(context));
	}

	private void println(ActivityContext context, Object input) {
		if (this.TextWriter.get(context) != null)
			((PrintStream) this.TextWriter.get(context)).println(input);
		else
			Trace.writeLine(input);
	}
}
