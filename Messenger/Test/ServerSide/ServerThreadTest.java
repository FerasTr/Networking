package ServerSide;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ClientSide.Client;

/**
 * This JUint tests basic functions of a server thread
 * 
 * @author darag
 *
 */
public class ServerThreadTest
{
	private static ServerGUI server = new ServerGUI();
	private static Client client2 = new Client("localhost", 18524);
	private static Client client1 = new Client("localhost", 18524);

	/**
	 * Tear down everything, close sockets, close server and GUI
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		client1.handleDisconnect();
		if (client1.socket.isClosed())
		{
			System.out.println("Disconnecting user1");
		}
		client2.handleDisconnect();
		if (client2.socket.isClosed())
		{
			System.out.println("Disconnecting user2");
		}
		server.setVisible(false);
		server.dispose();
	}

	/**
	 * Build the server GUI
	 */
	@BeforeClass
	public static void BuildBeforeClass()
	{
		server.setVisible(true);
		server.jButton1.doClick();
	}

	/**
	 * Test all operations
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void connect() throws IOException, InterruptedException
	{
		assertTrue(client1.connect());
		String line = "";
		System.out.println("connected test pass");

		client1.sendToServer("login user\n");
		line = client1.bufferedIn.readLine();
		System.out.println("Should be LOGGED user: " + line);
		assertTrue("Should be LOGGED user", "LOGGED user".equals(line));

		client1.sendToServer("nothing\n");
		line = client1.bufferedIn.readLine();
		System.out.println("Should be UNKOWN nothing: " + line);
		assertTrue("Should be UNKOWN nothing", "UNKOWN nothing".equals(line));

		client1.sendToServer("login other\n");
		line = client1.bufferedIn.readLine();
		System.out.println("Should be DENIED: " + line);
		assertTrue("Should be DENIED", "DENIED".equals(line));

		assertTrue(client2.connect());
		client2.sendToServer("login user2\n");
		line = client1.bufferedIn.readLine();
		System.out.println("Should be ONLINE user2: " + line);
		assertTrue("Should be ONLINE user2", "ONLINE user2".equals(line));

		client2.sendToServer("send user this is a test\n");
		line = client1.bufferedIn.readLine();
		System.out.println("Should be MESSAGE user2 this is a test: " + line);
		assertTrue("Should be MESSAGE user2 this is a test", "MESSAGE user2 this is a test".equals(line));

	}

}
