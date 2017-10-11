package demolition;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

/**
 * Window to contain canvas to draw on.
 * 
 * @author Alex Braithwaite
 *
 */
public class Window extends JFrame {

	private static final long serialVersionUID = -9089191999704171643L;

	/**
	 * Used for key listening.
	 */
	public boolean pressW, pressA, pressS, pressD, pressRight, pressLeft, pressSpace, mousePress;
	public int mouseX, mouseY;
	/**
	 * Used for key listening.
	 */
	public int pressInt = 0;

	public Window(DemolitionGame g) {
		super("Demolition");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);

		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mousePress = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mousePress = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});

		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}

		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// Determine which key was pressed.
				switch (e.getKeyCode()) {
				case KeyEvent.VK_W:
					pressW = true;
					break;
				case KeyEvent.VK_A:
					pressA = true;
					break;
				case KeyEvent.VK_S:
					pressS = true;
					break;
				case KeyEvent.VK_D:
					pressD = true;
					break;
				case KeyEvent.VK_RIGHT:
					pressRight = true;
					break;
				case KeyEvent.VK_LEFT:
					pressLeft = true;
					break;
				case KeyEvent.VK_SPACE:
					pressSpace = true;
					break;
				case KeyEvent.VK_0:
					pressInt = 0;
					break;
				case KeyEvent.VK_1:
					pressInt = 1;
					break;
				case KeyEvent.VK_2:
					pressInt = 2;
					break;
				case KeyEvent.VK_3:
					pressInt = 3;
					break;
				case KeyEvent.VK_4:
					pressInt = 4;
					break;
				case KeyEvent.VK_5:
					pressInt = 5;
					break;
				case KeyEvent.VK_6:
					pressInt = 6;
					break;
				case KeyEvent.VK_7:
					pressInt = 7;
					break;
				case KeyEvent.VK_8:
					pressInt = 8;
					break;
				case KeyEvent.VK_9:
					pressInt = 9;
					break;
				default:
					break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_W:
					pressW = false;
					break;
				case KeyEvent.VK_A:
					pressA = false;
					break;
				case KeyEvent.VK_S:
					pressS = false;
					break;
				case KeyEvent.VK_D:
					pressD = false;
					break;
				case KeyEvent.VK_RIGHT:
					pressRight = false;
					break;
				case KeyEvent.VK_LEFT:
					pressLeft = false;
					break;
				case KeyEvent.VK_SPACE:
					pressSpace = false;
					break;
				default:
					break;
				}
			}
		});

		add(g.world = new Canvas(g, DemolitionGame.worldX, DemolitionGame.worldY), BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
