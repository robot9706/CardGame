import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.bence.yugioh.*;

/**
 * A Main oszt�ly.
 * @author Bence
 *
 */
public class Main {
	
	/**
	 * A bel�p�si pont.
	 */
	public static void main(String[] args){
		//K�sz�tek egy ablakot
		JFrame f = new JFrame("YuGiOh!");
		
		//�s egy JPanel-t ami a j�t�kot tartalmazza
		GameFrame game = new GameFrame();
		
		//Hozz�adom a j�t�k komponenst az ablakhoz �s be�ll�tom az ablak param�tereit
		f.add(game);
		f.setSize(1000, 900);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.setResizable(false);
		
		//El�k�sz�tem a j�t�kot, ha nem siker�l ki�rom a felhaszn�l�nak �s kil�pek
		if(!game.PerpareGame()){
			JOptionPane.showMessageDialog(null, "Hiba a bet�lt�s k�zben!");
				
			System.exit(-1);
			return;
		}
		
		//Kirajzoltatom a j�t�kot
		game.Redraw();
	}
}
