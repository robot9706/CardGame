import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.bence.yugioh.*;

public class Main {
	
	
	public static void main(String[] args){
		GameFrame game = new GameFrame();
						
		JFrame f = new JFrame("YuGiOh!");
		f.setContentPane(game);
		f.setSize(900, 1000);	
		f.setVisible(true);
		
		if(!game.PerpareGame()){
			JOptionPane.showMessageDialog(null, "Hiba a betöltés közben!");
				
			System.exit(-1);
			return;
		}
		
		game.invalidate();
		game.validate();
		game.repaint();
	}
}
