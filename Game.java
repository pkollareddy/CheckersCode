import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class Game extends JApplet    {
        private CardLayout maincards, smallercards;
        private Container cmain, csmall;
        private FirstPanel intro;
        private MainPanel thegame;
        private LastPanel last;
        private Image image, instr;
        private boolean correct;

        public void init()    {
                cmain = this.getContentPane();
                maincards = new CardLayout ( );
                cmain.setLayout ( maincards );

                intro = new FirstPanel ( );
                thegame = new MainPanel ( );
                last = new LastPanel ( );

                cmain.add ( intro, "First" );
                cmain.add ( thegame, "Second" );
                cmain.add ( last, "Third" );

                image = getImage ( getDocumentBase ( ), "periodic_table.gif" );
                WaitForImage ( this, image );
                instr = getImage ( getDocumentBase ( ), "checkersinstr.gif" );
                WaitForImage ( this, instr );

                getContentPane().setBackground( Color.darkGray );
        }

	public void WaitForImage ( JApplet component, Image image )   {
		MediaTracker tracker = new MediaTracker ( component );
		try  {
			tracker.addImage ( image, 0 );
			tracker.waitForID ( 0 );
		}
		catch ( InterruptedException e )   {
			e.printStackTrace ( );
		}
	}

        class FirstPanel extends JPanel implements MouseListener    {
		int temp = 0;

                public FirstPanel()    {
                        addMouseListener( this );
			setBackground ( Color.blue );
                }

                public void paintComponent( Graphics g )    {
                        super.paintComponent( g );
			Font direct  = new Font ( "Arial", Font.BOLD, 34 );
			g.setFont ( direct );
			if(temp == 0)   {
				g.drawString ( "Welcome to Chequers,", 50, 180 );
				g.drawString ( "the Chemistry game of wit!", 50, 260 );
				temp++;
			}
			if (temp == 2)   {
				g.drawImage(instr, 0, 0, 700, 700, this);
				temp = 3;
			}
                }

                public void mousePressed(MouseEvent evt) {
			if(temp == 3)   {
				maincards.next ( cmain );
				thegame.init();
			}
			else   {
				temp = 2;
				this.repaint();
			}
                }
                public void mouseEntered(MouseEvent evt) { }
                public void mouseExited(MouseEvent evt) { }
                public void mouseClicked(MouseEvent evt) { }
                public void mouseReleased(MouseEvent evt) { }
        }

        class MainPanel extends JPanel    {
                JButton newGameButton;
                JButton resignButton;
		JButton pertab;
                JLabel message;
                boolean correct, attempt;
                Font font;
                private Container content;
                String choice;
		String initword, correctword, badword, theq;
		int count = 1;
                Question qs;
		boolean waitingForAnswer = false,answeredCorrect = false;
		CheckersMove currentMove;
		Board board ;

                public void init() {
                        choice = "";
                        content = this;
                        content.setLayout(null);
                        content.setBackground(new Color(0,150,0));
			board = new Board();
                        Title title = new Title();
                        qs = new Question();
                        content.add(title);
                        content.add(qs);
                        content.add(board);
                        content.add(newGameButton);
                        content.add(resignButton);
			content.add(pertab);
                        content.add(message);
                        qs.setBounds(200,500,800,100);
                        title.setBounds(40,30,500,100);
                        board.setBounds(130,130,244,244);
                        newGameButton.setBounds(400,160, 130, 30);
                        resignButton.setBounds(400, 230, 130, 30);
			pertab.setBounds(900, 30, 150, 30);
                        message.setBounds(100, 400, 350, 30);
                }

                class CheckersMove {
                        int fromRow, fromCol;
                        int toRow, toCol;
                        CheckersMove(int r1, int c1, int r2, int c2) {
                                fromRow = r1;
                                fromCol = c1;
                                toRow = r2;
                                toCol = c2;
                        }
                        boolean isJump() {
                                return (fromRow - toRow == 2 || fromRow - toRow == -2);
                        }
                }
                class Board extends JPanel implements ActionListener, MouseListener {
                        CheckersData board;
                        boolean gameInProgress;
                        int currentPlayer;
                        int selectedRow, selectedCol;
                        CheckersMove[] legalMoves;
                        public Board() {
                                setBackground(Color.black);
                                addMouseListener(this);
                                resignButton = new JButton("Resign");
                                resignButton.addActionListener(this);
                                newGameButton = new JButton("New Game");
                                newGameButton.addActionListener(this);
				pertab = new JButton("Periodic Table");
                                pertab.addActionListener(this);
                                message = new JLabel("",JLabel.CENTER);
                                message.setFont(new  Font("Serif", Font.BOLD, 14));
                                message.setForeground(Color.green);
                                board = new CheckersData();
                                doNewGame();
                        }

                        public void actionPerformed(ActionEvent evt) {
                                Object src = evt.getSource();
                                if (src == newGameButton)
                                        doNewGame();
                                else if (src == resignButton)
                                        doResign();
				else if (src == pertab)
                                        maincards.next(cmain);
                        }

                        void doNewGame() {
                                if (gameInProgress == true) {
                                        message.setText("Finish the current game first!");
                                        return;
                                }
                                board.setUpGame();
                                currentPlayer = CheckersData.RED;
                                legalMoves = board.getLegalMoves(CheckersData.RED);
                                selectedRow = -1;
                                message.setText("Red:  Make your move.");
                                gameInProgress = true;
                                newGameButton.setEnabled(false);
                                resignButton.setEnabled(true);
                                repaint();
                        }

                        void doResign() {
                                if (gameInProgress == false) {
                                        message.setText("There is no game in progress!");
                                        return;
                                }
                                if (currentPlayer == CheckersData.RED)
                                        gameOver("RED resigns.  BLACK wins.");
                                else
                                        gameOver("BLACK resigns.  RED wins.");
                        }

                        void gameOver(String str) {
                                message.setText(str);
                                newGameButton.setEnabled(true);
                                resignButton.setEnabled(false);
                                gameInProgress = false;
                        }

                        void doClickSquare(int row, int col) {
                                for (int i = 0; i < legalMoves.length; i++)
                                if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
                                        selectedRow = row;
                                        selectedCol = col;
                                        if (currentPlayer == CheckersData.RED)
                                                message.setText("RED:  Make your move.");
                                        else
                                                message.setText("BLACK:  Make your move.");
                                        repaint();
                                        return;
                                }
                                if (selectedRow < 0) {
                                        message.setText("Click the piece you want to move.");
                                        return;
                                }
                                for (int i = 0; i < legalMoves.length; i++)
                                if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol && legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
					if(!waitingForAnswer){
						doMakeMove(legalMoves[i]);
					}
                                        return;
                                }
                                message.setText("Click the square you want to move to.");
                        }

                        void doMakeMove(CheckersMove move) {
				currentMove=move;
                                if (move.isJump()) {
					if(!waitingForAnswer){
						qs.GetQnA();
						qs.repaint();
						waitingForAnswer=true;
						return;
					}
					else   {
						if(answeredCorrect)   {
							board.makeMove(move);
						}
						waitingForAnswer=false;
					}
                                }
				else    {
					board.makeMove(move);
				}
                                if (currentPlayer == CheckersData.RED) {
                                        currentPlayer = CheckersData.BLACK;
                                        legalMoves = board.getLegalMoves(currentPlayer);
                                        if (legalMoves == null)
                                                gameOver("BLACK has no moves.  RED wins.");
                                        else if (legalMoves[0].isJump())
                                                message.setText("BLACK:  Make your move.  You must jump.");
                                        else
                                                message.setText("BLACK:  Make your move.");
                                }
                                else    {
                                        currentPlayer = CheckersData.RED;
                                        legalMoves = board.getLegalMoves(currentPlayer);
                                        if (legalMoves == null)
                                                gameOver("RED has no moves.  BLACK wins.");
                                        else if (legalMoves[0].isJump())
                                                message.setText("RED:  Make your move.  You must jump.");
                                        else
                                                message.setText("RED:  Make your move.");
                                }
                                selectedRow = -1;
                                if (legalMoves != null) {
                                        boolean sameStartSquare = true;
                                        for (int i = 1; i < legalMoves.length; i++)
						if (legalMoves[i].fromRow != legalMoves[0].fromRow
					|| legalMoves[i].fromCol != legalMoves[0].fromCol) {
						sameStartSquare = false;
						break;
                                                }
                                                if (sameStartSquare) {
                                                        selectedRow = legalMoves[0].fromRow;
                                                        selectedCol = legalMoves[0].fromCol;
                                                }
                                }
                                repaint();
                        }

                        public void paintComponent(Graphics g) {
                                g.setColor(Color.black);
                                g.drawRect(0,0,getSize().width-1,getSize().height-1);
                                g.drawRect(1,1,getSize().width-3,getSize().height-3);
                                for (int row = 0; row < 8; row++) {
                                        for (int col = 0; col < 8; col++) {
                                                if ( row % 2 == col % 2 )
                                                        g.setColor(Color.lightGray);
                                                else
                                                        g.setColor(Color.gray);
                                                g.fillRect(2 + col*30, 2 + row*30, 30, 30);
                                                switch (board.pieceAt(row,col)) {
							case CheckersData.RED:
                                                        g.setColor(Color.red);
                                                        g.fillOval(4 + col*30, 4 + row*30, 26, 26);
                                                        break;
							case CheckersData.BLACK:
                                                        g.setColor(Color.black);
                                                        g.fillOval(4 + col*30, 4 + row*30, 26, 26);
                                                        break;
							case CheckersData.RED_KING:
                                                        font = new Font("SansSerif", Font.BOLD, 15);
                                                        g.setFont(font);
                                                        g.setColor(Color.red);
                                                        g.fillOval(4 + col*30, 4 + row*30, 26, 26);
                                                        g.setColor(Color.white);
                                                        g.drawString("K", 10 + col*30, 23 + row*30);
                                                        break;
							case CheckersData.BLACK_KING:
                                                        font = new Font("SansSerif", Font.BOLD, 15);
                                                        g.setFont(font);
                                                        g.setColor(Color.black);
                                                        g.fillOval(4 + col*30, 4 + row*30, 26, 26);
                                                        g.setColor(Color.white);
                                                        g.drawString("K", 10 + col*30, 23 + row*30);
                                                        break;
                                                }
                                        }
                                }
                                if (gameInProgress) {
                                        g.setColor(Color.cyan);
                                        for (int i = 0; i < legalMoves.length; i++) {
                                                g.drawRect(2 + legalMoves[i].fromCol*30, 2 + legalMoves[i].fromRow*30, 29, 29);
                                        }
                                        if (selectedRow >= 0) {
                                                g.setColor(Color.white);
                                                g.drawRect(2 + selectedCol*30, 2 + selectedRow*30, 29, 29);
                                                g.drawRect(3 + selectedCol*30, 3 + selectedRow*30, 27, 27);
                                                g.setColor(Color.green);
                                                for (int i = 0; i < legalMoves.length; i++) {
                                                        if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow)
                                                                g.drawRect(2 + legalMoves[i].toCol*30, 2 + legalMoves[i].toRow*30, 29, 29);
                                                }
                                        }
                                }
                        }

                        public Dimension getPreferredSize() {
                                return new Dimension(244, 244);
                        }

                        public Dimension getMinimumSize() {
                                return new Dimension(244, 244);
                        }

                        public Dimension getMaximumSize() {
                                return new Dimension(244, 244);
                        }
                        public void mousePressed(MouseEvent evt) {
                                if (gameInProgress == false)
                                        message.setText("Click \"New Game\" to start a new game.");
				else {
					int col = (evt.getX() - 2) / 30;
					int row = (evt.getY() - 2) / 30;
					if (col >= 0 && col < 8 && row >= 0 && row < 8)
						doClickSquare(row,col);
				}
                        }

                        public void mouseReleased(MouseEvent evt) { }
                        public void mouseClicked(MouseEvent evt) { }
                        public void mouseEntered(MouseEvent evt) { }
                        public void mouseExited(MouseEvent evt) { }
                }
                class CheckersData {
                        public static final int
                        EMPTY = 0,
                        RED = 1,
                        RED_KING = 2,
                        BLACK = 3,
                        BLACK_KING = 4;
                        private int[][] board;
                        public CheckersData() {
                                board = new int[8][8];
                                setUpGame();
                        }

                        public void setUpGame() {
                                for (int row = 0; row < 8; row++) {
                                        for (int col = 0; col < 8; col++) {
                                                if ( row % 2 == col % 2 ) {
                                                        if (row < 3)
                                                                board[row][col] = BLACK;
                                                        else if (row > 4)
                                                                board[row][col] = RED;
                                                        else
                                                                board[row][col] = EMPTY;
                                                }
                                                else {
                                                        board[row][col] = EMPTY;
                                                }
                                        }
                                }
                        }
                        public int pieceAt(int row, int col) {
                                return board[row][col];
                        }
                        public void setPieceAt(int row, int col, int piece) {
                                board[row][col] = piece;
                        }
                        public void makeMove(CheckersMove move) {
                                makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
                        }
                        public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
                                board[toRow][toCol] = board[fromRow][fromCol];
                                board[fromRow][fromCol] = EMPTY;
                                if (fromRow - toRow == 2 || fromRow - toRow == -2) {
                                        int jumpRow = (fromRow + toRow) / 2;
                                        int jumpCol = (fromCol + toCol) / 2;
                                        board[jumpRow][jumpCol] = EMPTY;
                                }
                                if (toRow == 0 && board[toRow][toCol] == RED)
                                        board[toRow][toCol] = RED_KING;
                                if (toRow == 7 && board[toRow][toCol] == BLACK)
                                        board[toRow][toCol] = BLACK_KING;
                        }
                        public CheckersMove[] getLegalMoves(int player) {
                                if (player != RED && player != BLACK)
                                        return null;
                                int playerKing;
                                if (player == RED)
                                        playerKing = RED_KING;
                                else
                                        playerKing = BLACK_KING;
                                ArrayList moves = new ArrayList();
                                for (int row = 0; row < 8; row++) {
                                        for (int col = 0; col < 8; col++) {
                                                if (board[row][col] == player || board[row][col] == playerKing) {
                                                        if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                                                                moves.add(new CheckersMove(row, col, row+2, col+2));
                                                        if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                                                                moves.add(new CheckersMove(row, col, row-2, col+2));
                                                        if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                                                                moves.add(new CheckersMove(row, col, row+2, col-2));
                                                        if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                                                                moves.add(new CheckersMove(row, col, row-2, col-2));
                                                }
                                        }
                                }
                                if (moves.size() == 0) {
                                        for (int row = 0; row < 8; row++) {
                                                for (int col = 0; col < 8; col++) {
                                                        if (board[row][col] == player || board[row][col] == playerKing) {
                                                                if (canMove(player,row,col,row+1,col+1))
                                                                        moves.add(new CheckersMove(row,col,row+1,col+1));
                                                                if (canMove(player,row,col,row-1,col+1))
                                                                        moves.add(new CheckersMove(row,col,row-1,col+1));
                                                                if (canMove(player,row,col,row+1,col-1))
                                                                        moves.add(new CheckersMove(row,col,row+1,col-1));
                                                                if (canMove(player,row,col,row-1,col-1))
                                                                        moves.add(new CheckersMove(row,col,row-1,col-1));
                                                        }
                                                }
                                        }
                                }
                                if (moves.size() == 0)
                                        return null;
				else {
					CheckersMove[] moveArray = new CheckersMove[moves.size()];
					for (int i = 0; i < moves.size(); i++)
						moveArray[i] = (CheckersMove)moves.get(i);
					return moveArray;
				}

                        }

                        public CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
                                if (player != RED && player != BLACK)
                                        return null;
                                int playerKing;
                                if (player == RED)
                                        playerKing = RED_KING;
                                else
                                        playerKing = BLACK_KING;
                                ArrayList moves = new ArrayList();
                                if (board[row][col] == player || board[row][col] == playerKing) {
                                        if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                                                moves.add(new CheckersMove(row, col, row+2, col+2));
                                        if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                                                moves.add(new CheckersMove(row, col, row-2, col+2));
                                        if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                                                moves.add(new CheckersMove(row, col, row+2, col-2));
                                        if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                                                moves.add(new CheckersMove(row, col, row-2, col-2));
                                }
                                if (moves.size() == 0)
                                        return null;
				else {
					CheckersMove[] moveArray = new CheckersMove[moves.size()];
					for (int i = 0; i < moves.size(); i++)
						moveArray[i] = (CheckersMove)moves.get(i);
					return moveArray;
				}
                        }

                        private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {
                                if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
                                        return false;
                                if (board[r3][c3] != EMPTY)
                                        return false;
                                if (player == RED) {
                                        if (board[r1][c1] == RED && r3 > r1)
                                                return false;
                                        if (board[r2][c2] != BLACK && board[r2][c2] != BLACK_KING)
                                                return false;
                                        return true;
                                }
                                else {
                                        if (board[r1][c1] == BLACK && r3 < r1)
                                                return false;
                                        if (board[r2][c2] != RED && board[r2][c2] != RED_KING)
                                                return false;
                                        return true;
                                }
                        }

                        private boolean canMove(int player, int r1, int c1, int r2, int c2) {
                                if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
                                        return false;
                                if (board[r2][c2] != EMPTY)
                                        return false;
                                if (player == RED) {
                                        if (board[r1][c1] == RED && r2 > r1)
                                                return false;
                                        return true;
                                }
                                else {
                                        if (board[r1][c1] == BLACK && r2 < r1)
                                                return false;
                                        return true;
                                }
                        }
                }

                class Title extends JPanel   {
                        public void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                Color color = new Color(0,150,0);
                                setBackground(color);
                                font = new Font("SansSerif", Font.BOLD + Font.ITALIC, 56);
                                g.setFont(font);
                                g.drawString("Chequers", 70, 50);
                        }
                }

                class Question extends JPanel implements ActionListener  {
			JRadioButton cb1, cb2, cb3, cb4, cb5;
			int psp = 0, wii = 0, ds = 0, xbox = 0, ps3 = 0;
			TextReader inFile;
			String fileName;
			int index;
			JLabel q;
			JButton answer;

			public Question()   {
				q = new JLabel("Please Wait");
				this.add(q);
				fileName = "Game.txt";
				inFile = new TextReader(fileName);
				setBackground(Color.blue);
				this.setLayout(new FlowLayout());
				ButtonGroup colorGroup = new ButtonGroup();
				cb1 = new JRadioButton("PSP");
				cb1.addActionListener(this);
				cb2 = new JRadioButton("Wii");
				colorGroup.add(cb2);
				cb2.addActionListener(this);
				this.add(cb2);
				cb3 = new JRadioButton("Nintendo DS");
				colorGroup.add(cb3);
				cb3.addActionListener(this);
				this.add(cb3);
				cb1.setSelected(true);
				answer = new JButton("Final Answer");
				answer.addActionListener(this);
				this.add(answer);
				answer.setEnabled(false);
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				q.setText(theq);
				int i = (int)(Math.random() * 100) % 2;
				if(i == 0)   {
					cb2.setText(correctword);
					cb3.setText(badword);
				}
				else   {
					cb3.setText(correctword);
					cb2.setText(badword);
				}
				if(inFile.eof())   {
					maincards.next ( cmain );
					inFile.close();
				}
			}

			public void GetQnA()   {
				if (inFile.fail())    {
					System.err.println("Can't open " + fileName);
					System.exit(1);
				}
				if (!inFile.eof() )    {
					theq = inFile.readLine();
				}
				initword = inFile.readLine();
				if (!inFile.eof() )    {
					index = initword.indexOf("|");
					correctword =  initword.substring(0, index);
					badword =  initword.substring(index + 1, initword.length());
				}
				answer.setEnabled(true);
			}

			public void actionPerformed(ActionEvent evt) {
				String choice = evt.getActionCommand();
				if(choice.equals("Final Answer")){
					if ((cb2.isSelected()&&cb2.getText().equals(correctword))||
						(cb3.isSelected()&&cb3.getText().equals(correctword))){
						answeredCorrect=true;
					}else{
						answeredCorrect=false;
					}

					board.doMakeMove(currentMove);
					cb2.setText("");
					cb3.setText("");
					q.setText("");
					answer.setEnabled(false);
				}
			}
		}
	}

	class LastPanel extends JPanel implements ActionListener   {
		JButton back;

		public LastPanel()    {
			back = new JButton("Back");
			back.addActionListener(this);
			this.add(back);
			setBackground ( Color.red );
		}

		public void actionPerformed(ActionEvent evt) {
			Object src = evt.getSource();
                        if (src == back)
				maincards.previous ( cmain );
		}

		public void paintComponent( Graphics g )    {
			super.paintComponent( g );
			g.drawImage( image, 0, 0, 1280, 700, this );
		}
	}
}
