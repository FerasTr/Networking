package ServerSide;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class creates ServerThread objects for Achieving multi-user chat room.
 */
public class Server extends Thread
{

	// Server variables and lists for managing users.
	private int port; // default = 18524
	private ArrayList<ServerThread> connections = new ArrayList<ServerThread>();
	private ArrayList<String> users = new ArrayList<String>();
	public ServerGUI gui;

	public Server(int port, ServerGUI serverGUI)
	{
		this.gui = serverGUI;
		this.port = port;
	}
	
	public Server(int port)
	{
		this.port = port;
	}

	public ArrayList<ServerThread> getConnections()
	{
		return connections;
	}

	public ArrayList<String> getUsers()
	{
		return users;
	}

	/** Start a new Thread. */
	@Override
	public void run()
	{
		try
		{
			ServerSocket server_socket = new ServerSocket(port);
			while (true)
			{
				Socket client_socket = server_socket.accept();
				System.out.println("Connected to: " + client_socket);
				ServerThread worker = new ServerThread(this, client_socket);
				connections.add(worker); // Add connection to the array
				worker.start();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/** Removes null connections */
	public void removeAllNull()
	{
		Iterator<String> iter = this.users.iterator();
		while (iter.hasNext())
		{
			String user = (String) iter.next();
			if (user == null)
			{
				iter.remove();
			}
		}
		removeClient();
	}

	/** Removes client from the database */
	private void removeClient()
	{
		Iterator<ServerThread> iter = this.connections.iterator();
		while (iter.hasNext())
		{
			ServerThread client = (ServerThread) iter.next();
			if (client.getLogin() == null)
			{
				iter.remove();
			}
		}
	}
}
