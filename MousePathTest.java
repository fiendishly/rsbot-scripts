import java.awt.Point;
import java.util.Map;

import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;

@ScriptManifest(authors = { "Sweed Raver" }, name = "Sweed Mouse Spline Tester", version = 1.0, category = "Development")
public class MousePathTest extends Script {

	@Override
	public int loop() {
		moveMouseByPath(new Point(random(0, 765), random(0, 503)), 10, 10);
		log("Moved mouse to: (" + getMouseLocation().getX() + ", "
				+ getMouseLocation().getY() + ")");
		return random(1000, 2000);
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		return true;
	}
}
