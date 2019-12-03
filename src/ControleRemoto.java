import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import rmi.RemoteAccess;

public class ControleRemoto extends javax.swing.JFrame
		implements ActionListener, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

	private static final long serialVersionUID = -1842261777470977698L;

	private javax.swing.JLabel a;
	double heightServidor, widthServidor;

	private String token;

	private JFrame janela;
	private JButton btnEnviar;
	private JLabel ipLabel;
	private JTextField ipField;
	RemoteAccess obj;

	ControleRemoto() {
		criarJanela();
	}

	public void criaTela(byte image[]) {
		btnEnviar.setVisible(false);
		ipLabel.setVisible(false);
		ipField.setVisible(false);

		a = new javax.swing.JLabel();
		a.setIcon(new javax.swing.ImageIcon(image));
		a.addMouseListener(this);
		a.addMouseMotionListener(this);
		a.addKeyListener(this);
		a.addMouseWheelListener(this);
		a.setFocusable(true);
		a.requestFocusInWindow();
		
		janela.add(a);
		janela.pack();
		this.addKeyListener(this);
	}

	public void update(byte image[]) {
		a.setIcon(new javax.swing.ImageIcon(image));
	}

	private void criarJanela() {
		janela = new JFrame("Chat");
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		janela.setSize(500, 500);
		janela.setLayout(new FlowLayout());
		criaElementos();
		janela.setVisible(true);
	}

	private void criaElementos() {
		ipLabel = new JLabel("Ip: ");
		ipField = new JTextField(10);
		btnEnviar = new JButton("Conectar");
		btnEnviar.addActionListener(this);

		janela.add(ipLabel);
		janela.add(ipField);
		janela.add(btnEnviar);

	}

	private void connection(String ip) {
		try {
			obj = (RemoteAccess) Naming.lookup("rmi://" + ip + "/RemoteControler");
			token = obj.logIn("12345");

			System.out.println("Token: " + token);
			byte screenshot[] = obj.getScreenshot(token);

			heightServidor = obj.getHeightResolution(token);
			widthServidor = obj.getWidthResolution(token);
			criaTela(screenshot);

		} catch (Exception e) {
			System.out.println("RemoteControler erro" + e.getMessage());
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getActionCommand().equals("Conectar")) {
				connection(ipField.getText());
			}
		} catch (Exception ex) {
			System.out.println("Connection erro " + ex.getMessage());
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		try {
			obj.pressMouse(token, InputEvent.BUTTON1_DOWN_MASK);
			update(obj.getScreenshot(token));
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		try {

			double x = (e.getX() * widthServidor) / (a.getWidth());
			double y = (e.getY() * heightServidor) / (a.getHeight());

			obj.moveMouse(token, (int) x, (int) y);
			update(obj.getScreenshot(token));
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			obj.pressMouse(token, InputEvent.getMaskForButton(e.getButton()));
			obj.releaseMouse(token, InputEvent.getMaskForButton(e.getButton()));
			update(obj.getScreenshot(token));
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		try {
			obj.pressMouse(token, InputEvent.getMaskForButton(e.getButton()));
			update(obj.getScreenshot(token));
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		try {
			update(obj.getScreenshot(token));
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	// 10.61.5.203

	@Override
	public void keyPressed(KeyEvent e) {
		try {
			System.out.println(e.getKeyChar());
			obj.pressKey(token, e.getKeyCode());
			update(obj.getScreenshot(token));
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		try {
			System.out.println(e.getKeyChar());
			obj.releaseKey(token, e.getKeyCode());
			update(obj.getScreenshot(token));
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		try {
			obj.wheelMouse(token, e.getWheelRotation());
			update(obj.getScreenshot(token));
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}

	}

}
