package ClientSide;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Clinet gui, suppoers logging in and connecting and sending to all or spec
 * user.
 * 
 * @author darag
 *
 */
public class MessagePane extends JFrame implements Listener
{

	private static final long serialVersionUID = 3970976847258960979L;
	private Client client;

	public DefaultListModel<String> listModel = new DefaultListModel<>();
	private JTextField input_field = new JTextField();;
	private JButton connect_button = new JButton("Connect");
	private JButton login_button = new JButton("Login");
	private JScrollPane messageList_container = new JScrollPane();
	private JList<String> message_list = new JList<String>(listModel);
	private final JTextField login_name = new JTextField();

	/*
	 * 
	 * Build the gui, give functions to each elenment
	 */
	public MessagePane(Client client)
	{
		setResizable(false);
		setAlwaysOnTop(true);
		this.client = client;
		client.addListener(this);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login_name.setHorizontalAlignment(SwingConstants.CENTER);
		login_name.setText("name");
		login_name.setColumns(10);

		input_field.setColumns(10);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout
		      .createSequentialGroup()
		      .addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup()
		            .addGap(37).addComponent(connect_button, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)
		            .addGap(18).addComponent(login_button, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)
		            .addGap(18).addComponent(login_name, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE))
		            .addGroup(groupLayout.createSequentialGroup().addContainerGap()
		                  .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
		                        .addComponent(input_field, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 707,
		                              Short.MAX_VALUE)
		                        .addComponent(messageList_container, GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE))))
		      .addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
		      .addGroup(groupLayout.createSequentialGroup().addContainerGap()
		            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
		                  .addComponent(connect_button, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
		                  .addComponent(login_button, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
		                  .addComponent(login_name, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
		            .addPreferredGap(ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
		            .addComponent(messageList_container, GroupLayout.PREFERRED_SIZE, 398, GroupLayout.PREFERRED_SIZE)
		            .addGap(15).addComponent(input_field, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
		            .addContainerGap()));

		messageList_container.setViewportView(message_list);
		getContentPane().setLayout(groupLayout);
		this.setSize(777, 559);
		String help_commands = "Available commands: get_users, send <to> <body>, disconnect/logoff/quit";
		listModel.addElement(help_commands);
		input_field.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String text = input_field.getText();
				if (!client.socket.isClosed())
				{
					client.sendToServer(text + '\n');
				}
				listModel.addElement(("> " + text));
				input_field.setText("");
			}
		});

		connect_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (client.connect())
				{
					connect_button.setEnabled(false);
					listModel.addElement("Connected");
				}
				else
				{
					listModel.addElement("No Connection");
				}
			}
		});

		login_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (client.login(login_name.getText()))
					{

						login_button.setEnabled(false);
						login_name.setEnabled(false);
						listModel.addElement("LOGGED " + login_name.getText());
					}
					else
					{
						listModel.addElement("FAILED TO LOGIN");
					}
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}

			}
		});
	}

	/**
	 * Read from the listner, read incoming messages
	 */
	@Override
	public void message(String fromLogin, String msgBody)
	{
		String line = fromLogin + ": " + msgBody;
		listModel.addElement(line);
	}

	/**
	 * Read from the listner, read incoming online status
	 */
	@Override
	public void online(String Login)
	{
		String line = "ONLINE " + Login;
		listModel.addElement(line);

	}

	/**
	 * Read from the listner, read incoming offline status
	 */
	@Override
	public void offline(String Login)
	{
		String line = "OFFLINE " + Login;
		listModel.addElement(line);

	}

	/**
	 * Read from the listner, read incoming command
	 */
	@Override
	public void command(String cmd)
	{
		listModel.addElement(cmd);
	}

	/**
	 * Read from the listner, read incoming user list
	 */
	@Override
	public void users(String user)
	{
		listModel.addElement(user);

	}
}
