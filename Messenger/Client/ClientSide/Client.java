package ClientSide;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Client class, handles all incoming messages from the server
 * 
 * @author darag
 *
 */
public class Client
{
	// params
	private final String serverName;
	private final int serverPort;
	public Socket socket;
	private InputStream serverIn;
	private OutputStream serverOut;
	public BufferedReader bufferedIn;
	private String user_name;

	public String getUserName()
	{
		return user_name;
	}

	private ArrayList<Listener> listeners = new ArrayList<>();

	/**
	 * Start client with server and port
	 * 
	 * @param serverName
	 * @param serverPort
	 */
	public Client(String serverName, int serverPort)
	{
		this.serverName = serverName;
		this.serverPort = serverPort;
	}

	/**
	 * Driver for the client class, handles parsing all the communication and shows
	 * it on the gui using the listener interface.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		Client c = new Client("localhost", 18524);
		c.addListener(new Listener()
		{
			@Override
			public void message(String fromLogin, String msgBody)
			{
				System.out.println("MESSAGE " + fromLogin + " " + msgBody);
			}

			@Override
			public void online(String Login)
			{
				System.out.println("ONLINE " + Login);

			}

			@Override
			public void offline(String Login)
			{
				System.out.println("OFFLINE " + Login);

			}

			@Override
			public void command(String cmd)
			{
				System.out.println(cmd);
			}

			@Override
			public void users(String user)
			{
				System.out.println(user);
			}
		});

		// Start the client gui
		MessagePane mp = new MessagePane(c);
		mp.setVisible(true);
	}

	/**
	 * Handle the login after the button is clicked
	 * 
	 * @param login
	 * @return
	 * @throws IOException
	 */
	public boolean login(String login) throws IOException
	{
		this.user_name = login;
		String cmd = "login " + user_name + "\n";
		serverOut.write(cmd.getBytes());

		String response = bufferedIn.readLine();
		if (("LOGGED " + this.user_name).equalsIgnoreCase(response))
		{
			
			startMessageReader();
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Start a new Thread that accepts messages from the server
	 */
	private void startMessageReader()
	{
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					readFromServer();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	/**
	 * Read from the server using new thread
	 * 
	 * @throws IOException
	 */
	private void readFromServer() throws IOException
	{

		while (true)
		{
			String line = bufferedIn.readLine();
			String[] input = line.split(" ", 3);
			String command = input[0];
			System.out.println("Command from the server:" + command);

			if (command.equals("FETCHING"))
			{
				handleAllUsers(line);
			}
			else if (command.equals("MESSAGE"))
			{
				String[] message = line.split(" ", 3);
				handleMessage(message);
			}
			else if (command.equals("ONLINE"))
			{
				handleOnline(input);
			}
			else if (command.equals("DISCONNECTING"))
			{
				handleDisconnect();
				break;
			}
			else if (command.equals("OFFLINE"))
			{
				handleOffline(input);
			}
			else
			{
				handleCommand(line);
			}
		}
	}

	/**
	 * If other commands
	 * 
	 * @param other
	 */
	private void handleCommand(String other)
	{
		for (Listener listener : listeners)
		{
			listener.command(other);
		}
	}

	/**
	 * If get_user command
	 * 
	 * @param other
	 */
	private void handleAllUsers(String other)
	{
		for (Listener listener : listeners)
		{
			listener.users(other);
		}
	}

	/**
	 * Handle quit command, close socket
	 */
	public void handleDisconnect()
	{
		listeners.clear();
		this.user_name = null;
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Handle reading out messages
	 */
	private void handleMessage(String[] message)
	{
		String login = message[1];
		String message_body = message[2];

		for (Listener listener : listeners)
		{
			listener.message(login, message_body);
		}
	}

	/**
	 * Handle online status and command
	 * 
	 * @param tokens
	 */
	private void handleOffline(String[] tokens)
	{
		String login = tokens[1];
		for (Listener listener : listeners)
		{
			listener.offline(login);
		}
	}

	/**
	 * Handle offline status and command
	 * 
	 * @param tokens
	 */
	private void handleOnline(String[] tokens)
	{
		String login = tokens[1];
		for (Listener listener : listeners)
		{
			listener.online(login);
		}
	}

	/**
	 * Handle sending commands to the server
	 * 
	 * @param msg
	 */
	public void sendToServer(String msg)
	{
		try
		{
			serverOut.write(msg.getBytes());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Connect after button is pressed, init connection to host
	 */
	public boolean connect()
	{
		try
		{
			this.socket = new Socket(serverName, serverPort);
			this.serverOut = socket.getOutputStream();
			this.serverIn = socket.getInputStream();
			this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public void addListener(Listener listener)
	{
		listeners.add(listener);
	}

}
