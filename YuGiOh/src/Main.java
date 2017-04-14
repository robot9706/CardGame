import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.bence.yugioh.*;

public class Main {
	
	
	public static void main(String[] args){
		GameFrame game = new GameFrame();
						
		JFrame f = new JFrame("YuGiOh!");
		f.setContentPane(game);
		f.setSize(1000, 900);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.setResizable(false);
		
		if(!game.PerpareGame()){
			JOptionPane.showMessageDialog(null, "Hiba a bet�lt�s k�zben!");
				
			System.exit(-1);
			return;
		}
		
		game.Redraw();
	}
}
