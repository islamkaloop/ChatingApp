package M3;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
	
	static Socket clientSocket;
	static DataInputStream inFromServer;
	static DataOutputStream outToServer;
	static BufferedReader inFromUser=new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws IOException 
	{
        System.out.println("Enter your name to be identified with");
        String id=inFromUser.readLine();
        join(id);
        if(clientSocket!=null) 
        {
        	System.out.println("If you want to get the memeber list type getMemberList."+'\n'+"Send MSG Format is: Name MSG");
        	ExecutorService pool = Executors.newFixedThreadPool(20);
        	pool.execute(new liveMsgRead(inFromServer));
        	while(true)
        	{
        		String message = inFromUser.readLine();
        		if (message.toLowerCase().equals("bye") || message.toLowerCase().equals("quit") || message.toLowerCase().equals("exit")) 
        		{
                	quit();
        			break;
    			}
        		else if(message.equals("getMemberList"))
            	{
            		getMemberList();
            	}
            	else
            	{
            		ArrayList<String> list= new ArrayList<>(Arrays.asList(message.split(" ")));
            		if(list.size()>1) 
            		{
            			String x[]=new String[2];
            			x=message.split(" ", 2);
                		String destination=x[0];
                		chat(clientSocket,destination,2,x[1]);
            		}
            		else
            		{
            			System.out.println("Incorrect Message Format");
            		}
            	}
        	}
        	pool.shutdown();
        }
    }
	public static class liveMsgRead implements Runnable
	{
		private DataInputStream inFromServer;
		public liveMsgRead (DataInputStream inFromServer) {
			this.inFromServer=inFromServer;
		}
		@Override
		public void run() {
			try
			{
				while(true)
				{
					String msg = inFromServer.readUTF();
					char c = msg.charAt(0);
					if(c=='*') 
					{
						System.out.println(msg.substring(1));
					}
					else if(c=='#') 
					{
						inFromServer.close();
				        outToServer.close();
				        clientSocket.close();
						break;
					}
					else if(msg.equals("true")) 
					{
						System.out.println("Message sent Successfully");
					}
					else if(msg.equals("false"))
					{
						System.out.println("Entered username is not online or incorrect or it is yourself");
					}
					else
					{
						System.out.println(msg);
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	public static void join(String name) throws IOException 
	{
		System.out.println("enter port of server you want to connect to 2018 for sever A, 2019 for server B");
		int Port =Integer.parseInt(inFromUser.readLine());
		Socket socket = new Socket("J6578", Port);
		inFromServer = new DataInputStream(socket.getInputStream());
		outToServer = new DataOutputStream(socket.getOutputStream());
		outToServer.writeUTF(name);
		if(inFromServer.readUTF().equals("true"))
        {
            System.out.println("I am connected at " + socket +" with unique name "+name);
    		clientSocket=socket;
        }
        else 
        {
        	System.out.println("Name is already used");
        	socket.close();
        	clientSocket=null;
        	inFromServer.close();
        	inFromServer=null;
        	outToServer.close();
        	outToServer=null;
        }
	}
	public static void getMemberList() throws UnknownHostException, IOException
	{
		System.out.println("Online Users");
		outToServer.writeUTF("getMemberList");
		outToServer.flush();
	}
	public static void quit() throws UnknownHostException, IOException 
	{
		outToServer.writeUTF("quit");
        System.out.println("bye");
	}
	public static void chat (Socket Source, String Destination, int TTL, String Message) throws UnknownHostException, IOException 
	{
		outToServer.writeUTF(Destination+" "+Message);
		outToServer.flush();
		/*for(int i=0;i<999999999;i++)
		{
			if(inFromServerFlag != null)
			{
				if(inFromServerFlag.equals("false"))
				{
					inFromServerFlag=null;
					return false;
				}
				else
				{
					inFromServerFlag=null;
					return true;
				}
			}
		}
		
		System.out.println("out");
		inFromServerFlag=null;
		return false;
		*/
		/*boolean flag = false;
		while(inFromServerFlag==null)
		{
			if(inFromServerFlag!=null)
			{
				if(inFromServerFlag.equals("false"))
				{
					inFromServerFlag=null;
					flag=false;
				}
				else
				{
					inFromServerFlag=null;
					flag=true;
				}
			}
		}
		inFromServerFlag=null;
		if(flag)
			return true;
		else
			return false;
		*/
	}
}