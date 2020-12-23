
package ServerSide;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * This is a basic server gui taken from MOODLE
 * 
 * @author darag
 *
 */
public class ServerGUI extends javax.swing.JFrame
{
	private static final long serialVersionUID = 1L;
	public javax.swing.JButton jButton1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JScrollPane jScrollPane1;
	public javax.swing.JTextArea jTextArea1;
	private Server server = new Server(18524, this);

	/**
	 * Creates new form server
	 */

	public ServerGUI()
	{
		setAlwaysOnTop(true);
		setResizable(false);
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form..
	 */

	private void initComponents()
	{

		jButton1 = new javax.swing.JButton();
		jButton1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				jTextArea1.setText("> Connecting...\n");
				server.start();
			}
		});
		jScrollPane1 = new javax.swing.JScrollPane();
		jTextArea1 = new javax.swing.JTextArea("");
		jTextArea1.setEditable(false);
		jLabel1 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jButton1.setText("Start");
		jButton1.addMouseListener(new java.awt.event.MouseAdapter()
		{
			public void mouseReleased(java.awt.event.MouseEvent evt)
			{
				startServer(evt);
			}
		});

		jTextArea1.setColumns(20);
		jTextArea1.setRows(5);
		jScrollPane1.setViewportView(jTextArea1);

		jLabel1.setText("Server");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
		      .addGroup(layout.createSequentialGroup().addGap(18)
		            .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
		                  .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 360, GroupLayout.PREFERRED_SIZE)
		                  .addGroup(layout.createSequentialGroup().addComponent(jButton1)
		                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                        .addComponent(jLabel1).addGap(166)))
		            .addContainerGap(22, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
		      .addGroup(layout.createSequentialGroup().addContainerGap()
		            .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(jButton1).addComponent(jLabel1))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE).addContainerGap()));
		getContentPane().setLayout(layout);

		pack();
	}

	/**
	 * Update Gui when the server starts
	 * 
	 * @param evt
	 */
	private void startServer(java.awt.event.MouseEvent evt)
	{
		jButton1.setEnabled(false);
	}
}
