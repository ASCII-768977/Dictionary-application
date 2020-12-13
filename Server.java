

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {

	public static int clientNum;
	private ServerSocket server;
	private Socket socket;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Server ms = new Server();
		int port = Integer.parseInt(args[0]);
		String path = args[1];
		ms.initServer(port, path);
	}

	public void initServer(int port, String path) {

		try {
			server = new ServerSocket(port);
			clientNum = 0;
			ServerGUI ui = new ServerGUI(server);
			ui.getTextArea().append("wait for 8000 connection" + "\n");
			System.out.println("wait for 8000 connection");
			while (true) {
				socket = server.accept();
				ui.getTextArea().append("someone connected" + "\n");
				System.out.println("someone connected");
				new Thread(new HandleAClient(socket, path, ui)).start();
				clientNum++;
				ui.getTextArea().append("There are " + clientNum + " client(s)." + "\n");
				System.out.println("There are " + clientNum + " client(s).");
				InetAddress address = socket.getInetAddress();
				ui.getTextArea().append("The ip address of client:" + address.getHostAddress() + "\n" + "\n");
				System.out.println("The ip address of client:" + address.getHostAddress() + "\n");
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
