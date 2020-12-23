package ServerSide;

import java.io.IOException;

/**
 * This is the driver method for launching server GUI.
 * @author darag
 *
 */
public class Driver
{

	public static void main(String[] args) throws IOException
	{
		
		ServerGUI gui = new ServerGUI();
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				gui.setVisible(true);
			}
		});
	}
}
