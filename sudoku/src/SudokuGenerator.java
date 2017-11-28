/**source code from: https://github.com/SomeKittens/Sudoku-Project/blob/master/SudokuGenerator.java#L174
 * 2017/10/24 Tuesday - 2017/10/27 Friday
 * 2017/11/27 Monday - 2017/11/28 Tuesday
 * */

import java.awt.*;
import java.util.*;

import javax.swing.SwingUtilities;


public class SudokuGenerator {
	/**
	 * constructor
	 * @param bg - main ButtonGrid object to layout the puzzle
	 */
	public SudokuGenerator(ButtonGrid bg){
		nextCell(bg,0,0);
		saveGrid(bg,0);
		makeHoles(bg);
		saveGrid(bg,1);
		//printAnswer(bg);
	}
	
	/**
	 * generates a random complete sudoku puzzle
	 * @param bg - main ButtonGrid object to layout the puzzle
	 * @param x  - row to search
	 * @param y  - column to search
	 * @return   - true to stop recursion, false to proceed with backtracking algorithm
	 **/
	public boolean nextCell(ButtonGrid bg, int x, int y){
		int nextX = x;
		int nextY = y;
		int [] toCheck = {1,2,3,4,5,6,7,8,9};
		int top = toCheck.length;
		Random r = new Random();
		int tmp = 0;
		int current = 0;
		/*suffle check list; put the used input at the head*/
		for(int i=top-1;i>0;i--){
			/*get random number from 0 to i*/
		    current = r.nextInt(i);
		    tmp = toCheck[current];
		    toCheck[current] = toCheck[i];
		    toCheck[i] = tmp;
    	}
		for(int i=0;i<toCheck.length;i++){
			if(bg.legalMove(toCheck[i], x, y)){
				Font boldText=new Font(bg.getGrid()[x][y].getFont().getName(),Font.BOLD,bg.getGrid()[x][y].getFont().getSize());
				bg.getGrid()[x][y].setOpaque(true);
				bg.getGrid()[x][y].setBackground(new Color(204, 204, 204));
				bg.getGrid()[x][y].setForeground(new Color(0, 89, 179));
				/*once cell is filled, disable button click*/
				bg.getGrid()[x][y].setActionCommand("fixedCell");
				bg.getGrid()[x][y].setText(String.valueOf(toCheck[i]));
				bg.getGrid()[x][y].setFont(boldText);
				
				/*when reached at the end of row/col, reset back to zero*/
				if(x==8){
					if(y==8){
						return true;
					}
					else{/*go to next row*/
						nextX = 0;
						nextY = y + 1;
					}
				}
				else{/*go to next col*/
					nextX = x + 1;
				}
				if(nextCell(bg,nextX,nextY)) return true;
			}
		}
		/*check for different input*/
		bg.getGrid()[x][y].setText(null);
		return false;
	}
	
	/**
	 * empty fixed grid at random and update numCounter
	 * @param bg - main ButtonGrid object to layout the puzzle
	 **/
	private void makeHoles(ButtonGrid bg) {
		double remainingSquares = 81;
		double remainingHoles = (double)bg.getDifficulty();
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				double holeChance = remainingHoles/remainingSquares;
				if(Math.random() <= holeChance){
					bg.getCounters()[Integer.valueOf(bg.getGrid()[i][j].getText())] -= 1;
					bg.getGrid()[i][j].setText("");
					bg.getGrid()[i][j].setActionCommand("emptyCell");
					bg.getGrid()[i][j].setOpaque(false);
					bg.getGrid()[i][j].setForeground(Color.BLACK);
					remainingHoles--;
				}
				remainingSquares--;
			}
		}
		for(int i=1;i<10;i++){
			bg.setCounters(i, 9-bg.getCounters()[i]);
		}
	}
	/**
	 * saves generated puzzle answer 
	 * @param bg - main ButtonGrid object to layout the puzzle
	 **/
	public void saveGrid(ButtonGrid bg, int mode){
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				int value = 0;
				if(bg.isCellEmpty(i, j)){
					value = Integer.valueOf(bg.getGrid()[i][j].getText());
				}
				if(mode==0){
					bg.setAnswer(i, j, value);
				}
				else bg.setInitialCells(i,j,value);
			}
		}
	}
	public void printAnswer(ButtonGrid bg){
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				int value = bg.getAnswer()[j][i];
				System.out.print(value+" ");
			}
			System.out.println();
		}
		System.out.println("printing initial cells: ");
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				int value = bg.getInitialCells()[j][i];
				System.out.print(value+" ");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		// TODO set timer & score 
		new ButtonGrid(9,9);
	}
}
