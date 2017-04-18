import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.bence.yugioh.*;

/**
 * A Main osztály.
 * @author Bence
 *
 */
public class Main {
	
	/**
	 * A belépési pont.
	 */
	public static void main(String[] args){
		//Készítek egy ablakot
		JFrame f = new JFrame("YuGiOh!");
		
		//és egy JPanel-t ami a játékot tartalmazza
		GameFrame game = new GameFrame();
		
		//Hozzáadom a játék komponenst az ablakhoz és beállítom az ablak paramétereit
		f.add(game);
		f.setSize(1000, 900);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.setResizable(false);
		
		//Elõkészítem a játékot, ha nem sikerül kiírom a felhasználónak és kilépek
		if(!game.PerpareGame()){
			JOptionPane.showMessageDialog(null, "Hiba a betöltés közben!");
				
			System.exit(-1);
			return;
		}
		
		//Kirajzoltatom a játékot
		game.Redraw();
	}
}
