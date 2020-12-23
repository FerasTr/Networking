package ServerSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class handles connections per client, it supports features such as
 * sending messages, getting all users, disconnecting.
 * 
 * @author darag
 *
 */
public class ServerThread extends Thread
{

	// params
	private final Socket client_socket;
	private Server server;
	private String login = null; // current login
	private OutputStream output_stream;

	public ServerThread(Server server, Socket client_socket)
	{
		this.client_socket = client_socket;
		this.server = server;
	}

	public String getLogin()
	{
		return login;
	}

	public void run()
	{
		try
		{
			this.handleClient();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Heavy work is done in this function, it handles all connections and errors
	 * from the client
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void handleClient() throws IOException, InterruptedException
	{
		this.output_stream = client_socket.getOutputStream();
		InputStream input_stream = client_socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input_stream));
		String line;

		// HANDLE EACH COMMAND
		while ((line = reader.readLine()) != null)
		{
			String[] msg = line.split(" ", 3);
			if (msg != null && msg.length > 0)
			{
				// HANDLE LOGIN, THROW ERROR IF WENT WRONG
				String cmd = msg[0];
				if ("login".equalsIgnoreCase(cmd))
				{
					if (this.login == null)
					{
						boolean check = handleLogin(msg);
						if (!check)
						{
							server.gui.jTextArea1.append("login conflict, " + this.client_socket.getLocalPort()
							      + " tried to login using a used name.\n");
							handleLogout();
							break;
						}
					}
					else
					{
						server.gui.jTextArea1.append(
						      "login conflict, " + this.client_socket.getLocalPort() + " tried to login while logged in.\n");
						this.send("DENIED\n");
					}
				}
				// HANDLE GET USERS, THROW ERROR IF WENT WRONG
				else if ("get_users".equalsIgnoreCase(cmd))
				{
					if (this.login != null)
					{
						server.gui.jTextArea1
						      .append("Sending connected users list to: " + this.client_socket.getLocalPort() + '\n');
						getUsers();
					}
					else
					{
						server.gui.jTextArea1
						      .append(this.client_socket.getLocalPort() + " tried to fetch the list of users.\n");
						this.send("DENIED\n");
					}
				}
				// HANDLE SENDING MESSAGES, THROW ERROR IF WENT WRONG
				else if ("send".equalsIgnoreCase(cmd))
				{
					if (this.login != null)
					{
						server.gui.jTextArea1.append("Sending a messeage from: " + this.login + " to " + msg[1] + '\n'
						      + "Context: " + msg[2] + '\n');
						sendMessage(msg);
					}
					else
					{
						server.gui.jTextArea1.append(
						      this.client_socket.getLocalPort() + " tried to send a message while not being logged in.\n");
						this.send("DENIED\n");
					}
				}
				// HANDLE LOGGING OFF
				else if ("disconnect".equalsIgnoreCase(cmd) || "quit".equalsIgnoreCase(cmd)
				      || "logoff".equalsIgnoreCase(cmd) || "logout".equalsIgnoreCase(cmd))
				{
					server.gui.jTextArea1.append(
					      this.client_socket.getLocalPort() + " tried to send a message while not being logged in.\n");
					if (this.login != null)
					{
						handleLogout();
					}
					else
					{
						this.server.removeAllNull();
					}
					break;

				}
				else
				{
					String error = "UNKOWN " + cmd + '\n';
					this.send(error);
				}
			}
		}
		client_socket.close();
	}

	/**
	 * Takes command and splits it into an array with 3 cells, one for sender , rec
	 * , message body
	 * 
	 * @param msg
	 * @return
	 */
	private boolean sendMessage(String[] msg)
	{
		if (msg.length == 3)
		{
			String receiver = msg[1];
			String message = msg[2];
			if (receiver.equalsIgnoreCase("all"))
			{
				boolean check = sendAll(message);
				if (check)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			ArrayList<ServerThread> clients = server.getConnections();
			ArrayList<String> users = server.getUsers();
			if (users.contains(receiver))
			{
				for (ServerThread client : clients)
				{
					if (client.login.equals(receiver))
					{
						String message_ready = "MESSAGE " + this.login + " " + message + '\n';
						try
						{
							client.send(message_ready);
							return true;
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * If message includes all for rec then send for all.
	 * 
	 * @param message
	 * @return
	 */
	private boolean sendAll(String message)
	{
		ArrayList<ServerThread> clients = server.getConnections();
		String message_ready = "MESSAGE " + this.login + " " + message + '\n';
		for (ServerThread client : clients)
		{
			if ((client.login != null) && !client.login.equals(this.login))
			{
				try
				{
					client.send(message_ready);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * Handle all users command
	 * 
	 * @throws IOException
	 */
	private void getUsers() throws IOException
	{
		ArrayList<ServerThread> clients = server.getConnections();
		this.send("FETCHING\n");
		if (clients.size() <= 1)
		{
			this.send("ALONE\n"); // if only you in server
		}
		else
		{
			String message_ready = null;
			for (ServerThread client : clients)
			{
				message_ready = "CONNECTED " + client.login + '\n';
				if (!this.login.equals(client.login))
				{
					this.send(message_ready); // send all except you
				}
			}
		}
	}

	/**
	 * Handle logout, close socket at end
	 * 
	 * @throws IOException
	 */
	private void handleLogout() throws IOException
	{
		ArrayList<ServerThread> clients = server.getConnections();

		String logout_message = "OFFLINE " + this.login + '\n';
		for (ServerThread client : clients)
		{
			if (!this.login.equals(client.login))
			{
				client.send(logout_message);
			}
		}
		server.getUsers().remove(login);
		server.gui.jTextArea1.append("User disconnected succesfully: " + client_socket.getPort() + '\n');
		this.login = null;
		this.send("DISCONNECTING\n");
		this.server.removeAllNull();
		client_socket.close();
	}

	/**
	 * Handle login, tell everyone youre online
	 * 
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	private boolean handleLogin(String[] msg) throws IOException
	{
		if (msg.length == 2)
		{
			String login = msg[1];
			this.login = login;
			if (!(server.getUsers().contains(login)))
			{
				String message_ready = "LOGGED " + login + '\n';
				this.output_stream.write(message_ready.getBytes());
				server.getUsers().add(login);
				server.gui.jTextArea1.append("User logged in succesfully: " + login + '\n');
				ArrayList<ServerThread> clients = server.getConnections();

				String login_message = "ONLINE " + login + '\n';
				for (ServerThread client : clients)
				{
					if (!login.equals(client.login))
					{
						client.send(login_message);
					}
				}
				return true;
			}
			return false;
		}
		else
		{
			return false;
		}
	}

	private void send(String msg) throws IOException
	{
		this.output_stream.write(msg.getBytes());
	}
}
