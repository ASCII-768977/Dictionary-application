

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class ServerGUI {

	private JTextArea textArea;
	private JFrame frame;

	public ServerGUI(ServerSocket server) {
		initialize(server);
	}

	public void initialize(ServerSocket server) {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(18, 6, 414, 255);
		frame.getContentPane().add(scrollPane);

		setTextArea(new JTextArea());
		scrollPane.setViewportView(getTextArea());
		getTextArea().setLineWrap(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					server.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});

		frame.setVisible(true);

	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

}
