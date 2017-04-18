import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.bence.yugioh.*;

/**
 * A Main osztaly.
 * @author Bence
 *
 */
public class Main {
	
	/**
	 * A belepesi pont.
	 */
	public static void main(String[] args){
		//Keszitek egy ablakot
		JFrame f = new JFrame("YuGiOh!");
		
		//es egy JPanel-t ami a jatekot tartalmazza
		GameFrame game = new GameFrame();
		
		//Hozzaadom a jatek komponenst az ablakhoz es beallitom az ablak parametereit
		f.add(game);
		f.setSize(1000, 900);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.setResizable(false);
		
		//Elokeszitem a jatekot, ha nem sikerul kiirom a felhasznalonak es kilepek
		if(!game.PerpareGame()){
			JOptionPane.showMessageDialog(null, "Hiba a betoltes kozben!");
				
			System.exit(-1);
			return;
		}
		
		//Kirajzoltatom a jatekot
		game.Redraw();
	}
}
