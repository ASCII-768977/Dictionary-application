

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class HandleAClient implements Runnable {
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;

	private MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
	private String path;
	private ServerGUI ui;

	public HandleAClient(Socket socket, String path, ServerGUI ui) {
		this.socket = socket;
		this.path = path;
		this.ui = ui;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			while (true) {
				String input = dis.readUTF();
				control(input, dos, path);
			}
		} catch (EOFException e3) {
			try {
				socket.close();
				Server.clientNum -= 1;
				ui.getTextArea().append("There are " + Server.clientNum + " client(s)." + "\n");
				ui.getTextArea().append(socket.getInetAddress() + " has leaved" + "\n" + "\n");
				System.out.println("There are " + Server.clientNum + " client(s).");
				System.out.println(socket.getInetAddress() + " has leaved" + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dis != null)
					dis.close();
				if (dos != null)
					dos.close();
				if (socket != null) {
					socket.close();
					socket = null;
				}
			} catch (IOException io) {
				io.printStackTrace();
			}
		}

	}

	public void control(String input, DataOutputStream dos, String path) {
		Set<String> keySet = map.keySet();
		StringTokenizer st = new StringTokenizer(input, ",");
		String command = st.nextToken();
		if (command.equals("addMean")) {
			read(path);
			if (!st.hasMoreElements()) {
				try {
					dos.writeUTF("Please input a word or an explaination");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				String word = st.nextToken();
				if (!st.hasMoreElements()) {
					try {
						dos.writeUTF("Please input a word or an explaination");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					String mean = st.nextToken();
					ui.getTextArea().append(command + ": " + word + "\n");
					ui.getTextArea().append("mean: " + mean + "\n" + "\n");
					System.out.println(command + ": " + word);
					System.out.println("mean: " + mean);
					addMean(word, mean, dos);
					write(map, keySet, path);
				}
			}
		} else if (command.equals("find")) {
			read(path);
			if (!st.hasMoreElements()) {
				try {
					dos.writeUTF("Please input a word");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				String word = st.nextToken();
				ui.getTextArea().append(command + ": " + word + "\n");
				System.out.println(command + ": " + word);
				find(word, dos);
			}
		} else if (command.equals("delete")) {
			read(path);
			if (!st.hasMoreElements()) {
				try {
					dos.writeUTF("Please input a word");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				String word = st.nextToken();
				ui.getTextArea().append(command + " " + word + "\n" + "\n");
				System.out.println(command + " " + word);
				delete(word, dos);
				write(map, keySet, path);
			}
		} else if (command.equals("addWord")) {
			read(path);
			if (!st.hasMoreElements()) {
				try {
					dos.writeUTF("Please input a word or an explaination");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				String word = st.nextToken();
				if (!st.hasMoreElements()) {
					try {
						dos.writeUTF("Please input a word or an explaination");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					String mean = st.nextToken();
					ui.getTextArea().append(command + " " + word + "\n");
					ui.getTextArea().append("mean: " + mean + "\n" + "\n");
					System.out.println(command + ": " + word);
					System.out.println("mean: " + mean);
					addWord(word, mean, dos);
					write(map, keySet, path);
				}
			}
		}
	}

	public void addWord(String word, String mean, DataOutputStream dos) {
		if (map.containsKey(word)) {
			try {
				dos.writeUTF("The word already exist");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			map.add(word, mean);
			List<String> values = map.getValues(word);
			String meaning = String.join(",", values);
			try {
				dos.writeUTF("Add word Successful");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addMean(String word, String mean, DataOutputStream dos) {
		if (!map.containsKey(word)) {
			try {
				dos.writeUTF("The word does not exist");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			map.add(word, mean);
			List<String> values = map.getValues(word);
			String meaning = String.join(",", values);
			try {
				dos.writeUTF("Add mean Successful");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void find(String word, DataOutputStream dos) {
		if (map.isEmpty()) {
			try {
				dos.writeUTF("The dictionary is empty");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (map.containsKey(word)) {
				List<String> values = map.getValues(word);
				String meaning = String.join(",", values);
				ui.getTextArea().append("mean: " + meaning + "\n" + "\n");
				System.out.println(meaning);
				try {
					dos.writeUTF(meaning);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					dos.writeUTF("This word is not in the dictionary");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void delete(String word, DataOutputStream dos) {
		if (map.isEmpty()) {
			try {
				dos.writeUTF("The dictionary is empty");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (map.containsKey(word)) {
				map.remove(word);
				Set<String> keySet = map.keySet();
				for (String key : keySet) {
					List<String> values = map.getValues(key);
					for (String value : values) {
						System.out.println(key + ": " + value);
					}
				}
				try {
					dos.writeUTF("Delete \"" + word + "\" Successful");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					dos.writeUTF("This word is not in the dictionary");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void read(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				map.clear();
				BufferedReader br = new BufferedReader(new FileReader(path));
				String temp = br.readLine();
				if (temp != null) {
					int index = Integer.parseInt(temp);
					for (int read = 0; read < index; read++) {
						String temp2 = br.readLine();
						StringTokenizer st = new StringTokenizer(temp2, ":");
						String words = st.nextToken();
						String means = st.nextToken();
						map.add(words, means);
					}
				}
				br.close();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
		}

	}

	public synchronized void write(MultiValueMap<String, String> map, Set<String> keySet, String path) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileOutputStream(path), true);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int number = 0;
		for (String key : keySet) {
			List<String> value3 = map.getValues(key);
			for (int i = 0; i < value3.size(); i++) {
				number += 1;
			}
		}
		pw.println(number);
		System.out.println(number);
		for (String key : keySet) {
			List<String> value3 = map.getValues(key);
			for (String value : value3) {
				pw.println(key + ":" + value);
			}
		}
		pw.close();
	}
}
