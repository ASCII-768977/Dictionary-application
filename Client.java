

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client mc = new Client();
		int port = Integer.parseInt(args[1]);
		mc.initClient(args[0], port);
	}

	private JFrame frame;
	private JTextField textField;
	private JTextArea textArea;
	private JTextField textField_1;
	private DataOutputStream dos;
	private Socket socket;
	private DataInputStream dis;
	
	// public static boolean bConnect = false;

	public void initGUI(DataOutputStream dos, DataInputStream dis) {
		frame = new JFrame();
		frame.setTitle("Dictionary Query");
		frame.setBounds(100, 100, 680, 485);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblInputAWord = new JLabel("Input a word");
		lblInputAWord.setBounds(55, 22, 87, 16);
		frame.getContentPane().add(lblInputAWord);

		JLabel lblInputAnExplaination = new JLabel("Input an explaination");
		lblInputAnExplaination.setBounds(43, 80, 144, 16);
		frame.getContentPane().add(lblInputAnExplaination);

		textField = new JTextField();
		textField.setBounds(35, 40, 130, 25);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String inputWord = textField.getText();
			}
		});

		textField_1 = new JTextField();
		textField_1.setBounds(35, 93, 630, 25);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		textField_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String inputMean = textField_1.getText();
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(35, 145, 630, 285);
		frame.getContentPane().add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setLineWrap(true);

		JButton btnAddWord = new JButton("AddWord");
		btnAddWord.setBounds(210, 15, 115, 30);
		frame.getContentPane().add(btnAddWord);
		btnAddWord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = textField.getText();
				String word = "addWord" + "," + text;
				String mean = textField_1.getText();
				textArea.setText("");
				textField.setText("");
				textField_1.setText("");
				textArea.append("Add word " + text + "\n");
				textArea.append("Eaplain:" + mean);
				System.out.println("AddWord Button clicked!");
				sendAddWord(word, mean, dos);
				receive(dis);
			}
		});
		JButton btnAddMean = new JButton("AddMean");
		btnAddMean.setBounds(210, 50, 115, 30);
		frame.getContentPane().add(btnAddMean);
		btnAddMean.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = textField.getText();
				String word = "addMean" + "," + text;
				String mean = textField_1.getText();
				textArea.setText("");
				textField.setText("");
				textField_1.setText("");
				textArea.append("Add mean " + text + "\n");
				textArea.append("Eaplain:" + mean);
				System.out.println("AddMean Button clicked!");
				sendAddMean(word, mean, dos);
				receive(dis);
			}

		});

		JButton btnFind = new JButton("Find");
		btnFind.setBounds(385, 35, 115, 30);
		frame.getContentPane().add(btnFind);
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = textField.getText();
				String word = "find" + "," + text;
				textArea.setText("");
				textField.setText("");
				textField_1.setText("");
				textArea.append("Find \"" + text + "\" meaning" + "\n");
				System.out.println("Find Button clicked!");
				sendFindOrDelete(word, dos);
				receiveFindResult(dis);

			}
		});

		JButton btnDelete = new JButton("Delete");
		btnDelete.setBounds(545, 35, 115, 30);
		frame.getContentPane().add(btnDelete);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = textField.getText();
				String word = "delete" + "," + text;
				textArea.setText("");
				textField.setText("");
				textField_1.setText("");
				textArea.append("Delete \"" + text + "\" word");
				System.out.println("Delete Button clicked!");
				sendFindOrDelete(word, dos);
				receive(dis);
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					dos.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}

	
	public void initClient(String host, int port) {

		try {
			socket = new Socket(host, port);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			initGUI(dos, dis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendAddWord(String word, String mean, DataOutputStream dos) {
		try {
			dos.writeUTF(word + "," + mean);
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendAddMean(String word, String mean, DataOutputStream dos) {
		try {
			dos.writeUTF(word + "," + mean);
			dos.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void sendFindOrDelete(String word, DataOutputStream dos) {
		try {
			dos.writeUTF(word);
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void receive(DataInputStream dis) {
		try {
			String result = dis.readUTF();
			textArea.append("\n" + result + "\n");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void receiveFindResult(DataInputStream dis) {
		try {
			String result = dis.readUTF();
			if (result.equals("This word is not in the dictionary")) {
				textArea.append(result);
			} else if (result.equals("The dictionary is empty")) {
				textArea.append(result);
			} else {
				StringTokenizer st = new StringTokenizer(result, ",");
				List<String> templist = new ArrayList<String>();
				while (st.hasMoreElements()) {
					String temp = st.nextToken();
					templist.add(temp);
				}
				for (int i = 0; i < templist.size(); i++) {
					textArea.append(i + 1 + ": " + templist.get(i) + "\n");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
