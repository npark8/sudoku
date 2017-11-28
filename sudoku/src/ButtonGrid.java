/**source code from: https://github.com/SomeKittens/Sudoku-Project/blob/master/SudokuGenerator.java#L174
 * 2017/10/24 Tuesday - 2017/10/27 Friday
 * 2017/11/27 Monday - 2017/11/28 Tuesday
 * */

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
 
public class ButtonGrid {
	/*enums*/
	private static final int EASY = 30;
	private static final int MEDIUM = 45;
	private static final int HARD = 60;
	private static final int GAME_IPG = 1;
	private static final int GAME_END = 0;
	/*frame and panels*/
	private JFrame frame;
	private JPanel startPage;
	private JPanel playPageGrid;
	private JPanel playPageButton;
	private JSplitPane playPage;
    private JButton[][] grid;
    /*flags*/
    private int difficulty;
	private int flag = GAME_IPG;

    /*counters*/
    private int [] numCounter = {0,9,9,9,9,9,9,9,9,9};
    private int [] emptyCellCounter = {30,45,60};
    private int [][] answer = new int[9][9];
    private int [][] initialCells = new int[9][9];
    
    /*condition variables*/
    private boolean clear,prevInputExist,sameInput;
    private int counterCurr,counterPrev,userInput,x,y;
    private String prevInputValue;
    
    /**
     * constructor
     * @param width  - total grid rows
     * @param length - total grid columns
     */
    public ButtonGrid(int width, int length){
		frame = new JFrame();
		grid = new JButton[width][length];
		final JPopupMenu nums = new JPopupMenu();
		startPage = new JPanel();
		startPage.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 105));
		initStartPage(this);
		playPageGrid = new JPanel();
		playPageGrid.setLayout(new GridLayout(width, length));
		playPageGrid.setSize(width*30, length*32);
		playPageButton = new JPanel();
		initNumPalette(nums);
		initGridCell(this, nums, width, length);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(width * 40, length * 40);
		frame.setResizable(false);
		frame.setVisible(true);
    }
    /**
     * initializes condition variables
     * @param itm  - input from 0 - 9
     * @param nums - drop down input menu
     **/
    public void initNumPaletteValues(JMenuItem itm, JPopupMenu nums){
    	/*condition for clear input*/
		clear = (itm.getText()!=null && itm.getText().equals("clear"))?true:false;
		counterCurr = (!clear)?numCounter[Integer.valueOf(itm.getText())]:-1;
		
		/*condition for previously entered input update*/
		prevInputValue = ((JButton) nums.getInvoker()).getText();
		prevInputExist = (prevInputValue != null && !prevInputValue.isEmpty())?true:false;
		counterPrev = (prevInputExist)?numCounter[Integer.valueOf(prevInputValue)]:-1;        			
		
		/*condition for same input*/
		sameInput = (prevInputExist && prevInputValue.equals(itm.getText()))?true:false;
		
		/*condition for input validate*/
		userInput = (!clear)?Integer.valueOf(itm.getText()):-1;
		x = Integer.valueOf(((JButton) nums.getInvoker()).getName().split(" ")[0]);
		y = Integer.valueOf(((JButton) nums.getInvoker()).getName().split(" ")[1]);
    }
    
    /**
     * initializes drop down menu bar & appropriate mouse click action
     * @param nums - drop down input menu
     **/
    public void initNumPalette(final JPopupMenu nums){
    	for(int i = 0; i<10; i++){
    		JMenuItem item;
    		if(i==0) item = new JMenuItem("clear");
    		else item = new JMenuItem(String.valueOf(i));
        	item.setEnabled(true);
        	/*add input action for drop down number palette*/
        	ActionListener actionListener = new ActionListener(){
				@Override
        		public void actionPerformed(ActionEvent actionEvent){
        			JMenuItem itm = (JMenuItem) actionEvent.getSource();
        			initNumPaletteValues(itm, nums);
        			if(itm.isEnabled()){
        				if(!legalMove(userInput, x, y)){
        					if(userInput>0 && !sameInput) 
        						JOptionPane.showMessageDialog(frame.getComponentAt(frame.getHeight()/2,frame.getWidth()/2),
        								"WARNING: "+userInput+" is illegal");
        				}
        				if(checkAnswer(userInput,x,y)>=0){
	        				if(!sameInput){
	        					if(!clear){
			        				((JButton) nums.getInvoker()).setText(itm.getText());
			        				((JButton) nums.getInvoker()).setActionCommand("fixedCell");
			        				counterCurr--;
			        				numCounter[Integer.valueOf(itm.getText())] = counterCurr;
			        				/*increment the previously entered input's counter & enable it again*/			        				if(counterPrev>=0){
			        					int index = Integer.valueOf(prevInputValue);
			        					counterPrev++;
			        					numCounter[index] = counterPrev;
			        					((JMenuItem) nums.getComponent(index)).setEnabled(true);
			        				}
	        					}
	        					else{/*clear previous input if exist; do not update if cell empty*/
	        						if(prevInputExist){
	        							int index = Integer.valueOf(prevInputValue);
			        					counterPrev++;
			        					numCounter[index] = counterPrev;
			        					((JButton) nums.getInvoker()).setText(null);
			        					((JMenuItem) nums.getComponent(index)).setEnabled(true);
	        						}
	        					}
	        				}
	        				else{
	        					if(!prevInputExist){
		        					((JButton) nums.getInvoker()).setText(itm.getText());
			        				((JButton) nums.getInvoker()).setActionCommand("fixedCell");
			        				counterCurr--;
			        				numCounter[Integer.valueOf(itm.getText())] = counterCurr;
	        					}
	        					
	        				}
        				}
        				else{
        					if(userInput>0 && legalMove(userInput, x, y)){
        						JOptionPane.showMessageDialog(frame.getComponentAt(frame.getHeight()/2,frame.getWidth()/2),
        								"WARNING: "+userInput+" is incorrect");
        					}
        				}
        				/*decrement cell counter & TODO add scores*/
        				decrCellCounter(difficulty);
        			}
        			if(counterCurr==0) itm.setEnabled(false);
        			if(getCellCounter(difficulty)==0) flag = GAME_END;
        			if(flag == GAME_END){
        				JOptionPane.showMessageDialog(frame.getComponentAt(frame.getHeight()/2,frame.getWidth()/2),
								"CONGRATURATION! You solved the puzzle.");
        			}
        		}
        	};
            item.addActionListener(actionListener);
            nums.add(item);
        }
    }
    
    /**
     * initializes grid buttons with action button 
     * @param nums   - user input drop down menu
     * @param length - total grid columns
     * @param width  - total grid rows
     **/
    public void initGridCell(final ButtonGrid bg, final JPopupMenu nums, int length, int width){
    	for(int y=0; y<length; y++){
			for (int x = 0; x < width; x++) {
				grid[x][y] = new JButton();
				grid[x][y].setActionCommand("emptyCell");
				grid[x][y].setName(x+" "+y);
				/*add drop down number palette action for each grid*/
				ActionListener a = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent actionEvent) {
						JButton btn = (JButton) actionEvent.getSource();
						if (btn.getActionCommand().equals("emptyCell")) {
							nums.show(btn, 0, 0);/*menu offset*/
						}
						else{ /*highlight components on the grid*/
							toggleHighlightAll(btn);
						}
					}
				};
				grid[x][y].addActionListener(a);
				grid[x][y].setContentAreaFilled(false);
				/*create usual SUDOKU grid look*/
				grid[x][y].setBorder(BorderFactory.createMatteBorder(1, 1, 1,
						1, Color.gray));
				if (x == 2 || x == 5 || y == 2 || y == 5) {
					if (x == 2 || x == 5) setCompoundBorder(x,y,0,0,0,2,1,1,1,0);
					if (y == 2 || y == 5) setCompoundBorder(x,y,0,0,2,0,1,1,0,1);
					if ((x == 2 && y == 2) || (x == 5 && y == 2)
						|| (x == 2 && y == 5) || (x == 5 && y == 5)) 
						setCompoundBorder(x,y,0,0,2,2,1,1,0,0);
				}
				playPageGrid.add(grid[x][y]);
			}
        }
    	JButton reset = resetButton(nums);
    	reset.setSize(width/2, length/9);
    	JButton regenerate = regenerateButton(bg);
    	JButton changeMode = changeModeButton(bg); 
    	playPageButton.setLayout(new FlowLayout());
    	playPageButton.add(reset);
    	playPageButton.add(regenerate);
    	playPageButton.add(changeMode);
    	playPage = new JSplitPane(JSplitPane.VERTICAL_SPLIT, playPageGrid, playPageButton);
    	playPage.setDividerLocation(286);
    }
    
    /**
     * helper function to empty every cell
     * @param bg - main ButtonGrid object 
     **/
    private void resetAll(final ButtonGrid bg){
    	for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				grid[i][j].setActionCommand("emptyCell");
				grid[i][j].setText(null);
			}
		}
		numCounter[0] = 0;
		for(int i=1; i<10; i++){
			numCounter[i] = 9;
		}
    }
    
    /**
     * creates and returns a button with regenerate action
     * @param bg - main ButtonGrid object to layout the puzzle
     * @return   - new JButton with regenerate function
     **/
    private JButton regenerateButton(final ButtonGrid bg) {
    	JButton regenerate = new JButton("REGEN");
    	regenerate.addActionListener(new ActionListener(){
    		@Override
    		public void actionPerformed(ActionEvent ev){
    			resetAll(bg);
    			new SudokuGenerator(bg);
    		}
    	});
		return regenerate;
	}
    
    /**
     * creates and returns a button with switch mode action
     * @param bg - main ButtonGrid object to layout the puzzle
     * @return   - new JButton with switch mode function
     **/
	private JButton changeModeButton(final ButtonGrid bg) {
    	JButton changeMode = new JButton("MODE");
    	changeMode.addActionListener(new ActionListener(){
    		@Override
    		public void actionPerformed(ActionEvent ev){
    			resetAll(bg);
    			playPage.setVisible(false);
    			startPage.setVisible(true);
    		}
    	});
		return changeMode;
	}
	/**
     * creates and returns a button with reset action
     * @param nums - drop down input menu
     * @return     - new JButton with reset function
     **/
    public JButton resetButton(final JPopupMenu nums){
    	JButton reset = new JButton("RESET");
    	reset.addActionListener(new ActionListener(){
    		@Override
    		public void actionPerformed(ActionEvent ev){
    			for(int i=0; i<9; i++){
    				for(int j=0; j<9; j++){/*empty cell and increment counter*/
    					if(initialCells[i][j]==0){
    						if(isCellEmpty(i,j)){
    							numCounter[Integer.valueOf(grid[i][j].getText())] += 1;
    							nums.getComponent(Integer.valueOf(grid[i][j].getText())).setEnabled(true);
    							grid[i][j].setActionCommand("emptyCell");
    							grid[i][j].setText("");
    						}
    					}
    					else{/*turn off all highlighted elements*/
    						if(grid[i][j].getActionCommand().equals("highlighted")){
    							toggleHighlightAll(grid[i][j]);
    						}
    					}
    				}
    			}
    		}
    	});
		return reset;
    }
    
    /**
     * sets compounded boarder for buttons
     * @param x/y  - row/column respectively
     * @param gx   - boarder pixel gray
     * @param dgx  - boarder pixel dark gray
     **/
    public void setCompoundBorder(int x, int y, int g1, int g2, int g3, int g4, 
    		int dg1, int dg2, int dg3, int dg4){
    	grid[x][y].setBorder(new CompoundBorder(
    			BorderFactory.createMatteBorder(g1, g2, g3, g4, Color.darkGray),
				BorderFactory.createMatteBorder(dg1, dg2, dg3, dg4, Color.gray)));
    }
    
    /**
     * initializes start page with difficulty buttons
     * @param bg - main ButtonGrid object
     **/
    public void initStartPage(final ButtonGrid bg){
    	JButton easyButton = new JButton("EASY");
    	JButton medButton = new JButton("MEDIUM");
    	JButton hardButton = new JButton("HARD");
    	
    	easyButton.addActionListener(generateSudoku(bg,EASY));
    	medButton.addActionListener(generateSudoku(bg,MEDIUM));
    	hardButton.addActionListener(generateSudoku(bg,HARD));
    	
    	startPage.add(easyButton);
    	startPage.add(medButton);
    	startPage.add(hardButton);
    	frame.add(startPage);
    }
    
    /**
     * generates a sudoku puzzle based on the difficulty
     * @param bg main ButtonGrid object to layout puzzle
     * @param difficulty - difficulty of puzzle 
     **/
    public ActionListener generateSudoku(final ButtonGrid bg, final int difficulty){
    	return new ActionListener(){
    		@Override
    		public void actionPerformed(ActionEvent ev){
    			setDifficulty(difficulty);
    			new SudokuGenerator(bg);
    			playPage.setVisible(true);
    			startPage.setVisible(false);
    			frame.getContentPane().add(playPage);
    		}
    	};
    }
    
    /**
     * validate input action
     * @param value - user input
     * @param x - row to search
     * @param y - column to search
     * @return true if input can be placed in the given position (x,y)
     **/
    public boolean legalMove(int value, int x, int y){
    	if(value<0) return false;
    	int valueExist = 0;
    	/*checks if value exists within the same row*/
		for(int i=0; i<9; i++){
			if(isCellEmpty(x,i))
				valueExist = Integer.valueOf(grid[x][i].getText());
			if(value==valueExist) return false;
		}
		/*checks if value exists within the same column*/
		for(int i=0; i<9; i++){
			if(isCellEmpty(i,y))
				valueExist = Integer.valueOf(grid[i][y].getText());
			if(value==valueExist) return false;
		}
		int cornerX = setCornerXY(x);
		int cornerY = setCornerXY(y);
		/*checks if value exists within the same quadrant*/
		for(int i=cornerX; i<(cornerX+3); i++){
			for(int j=cornerY; j<(cornerY+3); j++){
				if(isCellEmpty(i,j)) 
					valueExist = Integer.valueOf(grid[i][j].getText());
				if(value==valueExist) return false;
			}
		}
		return true;
    }
    
    /** 
     * finds appropriate col/row position for searching
     * @param  i - current col/row position
     * @return x - starting row/col of the corresponding quadrant
     **/
	public int setCornerXY(int i){
		if(0<=i && i<3) return 0;
		if(3<=i && i<6) return 3;
		else return 6;
	}
    /**
     * checks if user input matches existing answer
     * @param x/y - row/column to search respectively
     * @return  0 - correct answer
     * @return -1 - incorrect answer
     * @return  1 - user input is clear cell
     **/
    public int checkAnswer(int userInput, int x, int y){
		if(answer[x][y] == userInput) return 0;
		if(userInput < 0) return 1;
    	return -1;
	}
    
    /**
     * tracks emptyCells
     * @param difficulty - represents difficulty of puzzle
     **/
    public void decrCellCounter(int difficulty){
    	if(difficulty == EASY) emptyCellCounter[0] -=1;
    	else if(difficulty == MEDIUM) emptyCellCounter[1] -=1;
    	else emptyCellCounter[2] -=1;
    }
    
	/**
	 * toggles highlight for all buttons with the same value
	 * @param btn - initially clicked button
	 **/
	public void toggleHighlightAll(JButton btn){
		Color c;
		int value = Integer.valueOf(btn.getText());
		boolean highlight = true;
		if(btn.getActionCommand().equals("highlighted")){
			highlight = false;
			c= new Color(0, 89, 179);
		}
		else{
			c = Color.RED;
		}
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				if(isCellEmpty(i,j)){
					int valueExist = Integer.valueOf(grid[i][j].getText());
					if(value == valueExist) {
						/*toggle highlight keyword*/
						if(grid[i][j].getActionCommand().equals("fixedCell"))
							grid[i][j].setActionCommand("highlighted");
						
						else if(grid[i][j].getActionCommand().equals("highlighted"))
							grid[i][j].setActionCommand("fixedCell");
						
						/*set different color for input cells*/
						if(grid[i][j].getActionCommand().equals("emptyCell")&&!highlight)
							grid[i][j].setForeground(Color.BLACK);
						
						else grid[i][j].setForeground(c);
					}
					else{/*toggle highlight for previously highlighted cells*/
						if(grid[i][j].getActionCommand().equals("highlighted")){
							grid[i][j].setActionCommand("fixedCell");
							grid[i][j].setForeground(new Color(0, 89, 179));
						}
					}
				}
			}
		}
	}
	
	/**
	 * checks for empty cell 
	 * @param  r, c - cell position 
	 * @return true if empty, false otherwise
	 */
	public boolean isCellEmpty(int r, int c){
		if(grid[r][c].getText()!=null && !grid[r][c].getText().isEmpty())
			return true;
		return false;
	}
	
    /**
     * getters and setters
     **/
    public JButton [][] getGrid(){
    	return grid;
    }
    public int [] getCounters(){
    	return numCounter;
    }
    public JFrame getFrame(){
    	return frame;
    }
    public int [][] getAnswer(){
    	return answer;
    }
	public int getDifficulty() {
		return difficulty;
	}
	public int getCellCounter(int difficulty){
		if(difficulty == EASY) return emptyCellCounter[0];
		else if (difficulty == MEDIUM) return emptyCellCounter[1];
		else return emptyCellCounter[2];
	}
	public int [][] getInitialCells() {
		return initialCells;
	}
    public void setCounters(int i, int value){
    	numCounter[i] = value;
    }
    public void setAnswer(int r, int c, int value){
    	answer[r][c] = value;
    }
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public void setInitialCells(int r, int c, int value) {
		initialCells[r][c] = value;
	}
}
