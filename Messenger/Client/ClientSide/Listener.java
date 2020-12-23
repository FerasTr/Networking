package ClientSide;

/**
 * Interface that handles reading from the server per client
 * @author darag
 *
 */
public interface Listener
{	
	public void message(String fromLogin, String msgBody);

	public void online(String Login);

	public void offline(String Login);
	
	
	public void command(String cmd);
	
	public void users(String user);
}
