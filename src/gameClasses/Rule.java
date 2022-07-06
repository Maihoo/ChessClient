package gameClasses;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import api.PlaySound;
import api.Assets;
import api.Game;

public class Rule {

	//Bools
	boolean isMine, whiteTemp, blackTemp, extra, willChoose;
	boolean[] moveable = new boolean[64];
	//Ints
	int checks, step, r, u, n, it, x, y, xTemp, yTemp, activeField, activeFieldTemp, pattCounter;
	//Strings
	String characterTemp = "";
	ArrayList<String> boards = new ArrayList<String>();
	//Others
	Game game;
	Point p = MouseInfo.getPointerInfo().getLocation();
	Point b = MouseInfo.getPointerInfo().getLocation();
	Field[] field = new Field[64];
	Extra rahmen = new Extra(game, Assets.rahmen, p.x, p.y, true);
	Extra rot = new Extra(game, Assets.rot, p.x, p.y, false);
	Extra from, to, checkFrom, checkTo;
	Extra[] gruen = new Extra[64];
	PlaySound ps = new PlaySound();

	public Rule (Game game) {
		System.out.println("Game created.");
		this.game = game;
	}
		
	public void calcAmount() {
		pattCounter = 0;
		String stringToTest = game.fieldToString(field).substring(0, game.fieldToString(field).length()-8);
		
		for(int i = 0; i < boards.size(); i++) {
			if(stringToTest.equalsIgnoreCase(boards.get(i))) pattCounter++;			
		}
		
		boards.add(game.fieldToString(field).substring(0, game.fieldToString(field).length()-8));
		game.giveFieldToReviewMemory(field);
		
		step++;

		if(pattCounter >= 3) game.patt();
	}
	
	public void tick() {
		checks++;
		
		if(game.figureChoosen.length() > 2) {
			
			field[activeField].character = game.figureChoosen;
			if(!game.iAmWhite) game.field[63-activeField].character = game.figureChoosen;
			else game.field[activeField].character = game.figureChoosen;
			if(willChoose) {
				willChoose = false;
				try {
					if(!game.iAmWhite) reverseField();
					
					game.setField(field);
					calcAmount();
					
					if(!game.iAmWhite) reverseField();
				} catch (IOException e) { }
			}
			game.giveToPC();
			game.figureChoosen = "";
			update();
		}
		
		if(game.willHaveToUpdate) {
			if(!game.iAmWhite) reverseField();
			this.field = game.field;
			if(!game.iAmWhite) reverseField();
		}
		
		if(n == 0) {
			u = 1;
			init();
		}
		
		if(u == 0 || game.willHaveToUpdate) {
			if(checks%20 == 0) {
				update();
			}
		}
		
		try {
			p = game.getMP();
			if(p != null) {
				b.x = game.getMP().x;
				b.y = game.getMP().y;
			}
		} catch (Exception e) {
			b.x = 0;
			b.y = 0;
		}
		
		x = (b.x-55)/120;
		y = (b.y-75)/120;
		
		if(x > 7) x = 7;
		if(x < 0) x = 0;
		if(y > 7) y = 7;
		if(y < 0) y = 0;
		
		fieldscan();
		
		try {
			if(game.ableToRedo && !game.isOffline && game.isMyTurn && (checks%20) == 0) {
				if(game.cm.askForEnemyRedo()) {
					game.eWantsRedo = true;
				}
			}
		} catch (IOException e1) {}
		
		if(game.ableToRedo && game.mouseManager.leftPressed && !game.isOffline && b.x > 1060 && b.x < 1160 && b.y > 1060-100 && b.y < 1060) {
			if(!game.isMyTurn) {
				game.redo();
				System.out.println("Redo clicked1");
				game.resetMM();
			}
			
			if(game.eWantsRedo && game.isMyTurn) {
				game.redo();
				try {
					game.cm.tcpclient.giveUpTurn();
				} catch (IOException e) { }
				System.out.println("Redo clicked2");
				game.resetMM();
			}
		}
		
		
		if(game.isMyTurn && game.mouseManager.leftPressed && isMine && r == 0) {
			
			r++;
			
			game.resetMM();
			activeFieldTemp = activeField;
			characterTemp = field[activeField].character;
			xTemp = x;
			yTemp = y;
			whiteTemp = field[activeField].white;
			blackTemp = field[activeField].black;
			
			it = 0;
			moveable = makeMoveable(field, activeField, game.iAmWhite);
			
			rot.x = x;
			rot.y = y;
			rot.render = true;
			
			for(int i = 0; i < 64; i++) {
				if(moveable[i]) {
					gruen[i].render = true;
				}
			}
		}
		
		if(game.mouseManager.leftPressed && r != 0 && moveable[activeField]) {
			
			game.willHaveToUpdate 	= false;
			game.eWantsRedo 		= false;
			game.waitingForRedo 	= false;
			game.ableToRedo 		= true;
			
			ps.playSound("res/sounds/moveSound.wav");
			
			r--;
			game.resetMM();

			if(field[activeFieldTemp].character.equalsIgnoreCase("bauer") || !field[activeField].character.equalsIgnoreCase("0")) {
				pattCounter = 0;
				boards.clear();
			}
			
			u = 0;		
			
			field = makeStep(field, activeField, activeFieldTemp, true);
			
			try {
				if(!game.iAmWhite) reverseField();
				
					checkFrom.render = false;
					checkTo.render 	 = false;
					from.render 	 = false;
					to.render 		 = false;
				
					if(game.iAmWhite) {
						game.from 	= activeFieldTemp;
						game.to 	= activeField;
					}
					else {
						game.from 	= 63-activeFieldTemp;
						game.to 	= 63-activeField;
					}
				
					if(!willChoose) game.setField(field);
					game.calcPoints();
					if(!game.isOffline) calcAmount();
			
				if(!game.iAmWhite) reverseField();
			}
			catch(Exception e) {
				System.out.println("setField- couldn't be executed in OnlineRule");
			}
			
			rot.render = false;
			for(int i = 0; i < 64; i++) {
				gruen[i].render = false;
			}
		}
		
		if(game.mouseManager.leftPressed && r != 0 && (field[activeFieldTemp].white == game.iAmWhite || field[activeFieldTemp].black == !game.iAmWhite)) {
			game.resetMM();
			r--;
			rot.render = false;
			for(int i = 0; i < 64; i++) {
				gruen[i].render = false;
			}
		}

		rahmen.x = x;
		rahmen.y = y;
	}
	
	public Field[] makeStep(Field[] input, int aF, int aFT, boolean canChoose) {
		Field[] output = new Field[64];

		for(int j = 0; j < 64; j++) {
			output[j] = new Field(input[j].game, input[j].character, input[j].white, input[j].black, input[j].x, input[j].y);
			output[j].wasMoved		= input[j].wasMoved;
			output[j].enPassanteable= input[j].enPassanteable;
		}
		
		
		//enPessant
		if(output[aFT].character.equalsIgnoreCase("bauer") && (output[aF].character.length() < 2)) {
			output[aF+8].character 	= "0";
			output[aF+8].white 		= false;
			output[aF+8].black 		= false;
			output[aF+8].wasMoved 	= false;
		}
		
		//rochade
		if(output[aFT].character.equalsIgnoreCase("koenig") && (aFT - aF == -2)) {
			System.out.println("ROCHADE1");
			for(int i = 2; i <= 7; i++) {
				if(aFT+i <= 63) {
					if(output[aFT+i].character.equalsIgnoreCase("turm")) {
						output[aFT+1].character = "turm";
						output[aFT+1].wasMoved 	= true;
						output[aFT+1].white		= output[aFT].white;
						output[aFT+1].black 	= output[aFT].black;
						output[aFT+i].character = "0";
						output[aFT+i].black 	= false;
						output[aFT+i].white		= false;
						output[aFT+i].wasMoved 	= false;
						break;
					}
				}
			}
		}
		if(output[aFT].character.equalsIgnoreCase("koenig") && (aFT - aF == 2)) {
			System.out.println("ROCHADE2");
			for(int i = 2; i <= 7; i++) {
				if(aFT-i >= 0) {
					if(output[aFT-i].character.equalsIgnoreCase("turm")) {
						output[aFT-1].character = "turm";
						output[aFT-1].wasMoved 	= true;
						output[aFT-1].white 		= output[aFT].white;
						output[aFT-1].black 	= output[aFT].black;
						output[aFT-i].character = "0";
						output[aFT-i].black		= false;
						output[aFT-i].white		= false;
						output[aFT-i].wasMoved 	= false;
						break;
					}
				}
			}
		}		
		
		output[aF].character 	= input[aFT].character;
		output[aF].wasMoved 	= true;
		output[aF].white 		= input[aFT].white;
		output[aF].black 		= input[aFT].black;
		output[aFT].character 	= "0";
		output[aFT].black 		= false;
		output[aFT].white	 	= false;
		output[aFT].wasMoved 	= false;
		
		//set enPessant
		if(output[aF].character.equalsIgnoreCase("bauer")) {
			if((Math.abs(aF - aFT) == 16) && isEnPassantable(output)) {
				output[aF].enPassanteable = true;
			}
			else output[aF].enPassanteable = false;
		}
		
		
		if(!canChoose) return output;
		
		//choose figure
		if(output[aF].character.equalsIgnoreCase("bauer") && aF < 8) {
			//output[aF].character = game.chooseFigure();
			willChoose = true;
			game.chooseFigure();
		}
		
		return output;
	}
	
	public void fieldscan(){
		
		if(8*y+x >= 0 && 8*y+x <= 63) activeField = 8*y+x;
		
		if(activeField < 0 || activeField > 63) return;
		
		if((game.iAmWhite && field[activeField].white) || (!game.iAmWhite && field[activeField].black) && field[activeField].character != "0") {
			isMine = true;
		}
		else isMine = false;
	}
	
	public boolean[] makeMoveable(Field[] checkField, int position, boolean iAmWhite) {
		
		it++;
		int row 	= position / 8;
		int column 	= position % 8;
		
		boolean[] toBeMoveable = new boolean[64];
		for(int i = 0; i < 64; i++) {
			toBeMoveable[i] = false;
		}
		
		switch (checkField[position].character) {
		case "turm":
			for(int i = 1; i <= 7; i++) {
				if(position + i <= 63 && (position + i)/8 == row) {
					if(checkField[position+i].character.length() > 2) {
						if((checkField[position+i].black && iAmWhite) || (checkField[position+i].white && !iAmWhite)) toBeMoveable[position + i] = true;
						break;
					}
					
					toBeMoveable[position + i] = true;
				}
			}
			
			for(int i = -1; i >= -7; i--) {
				if(position + i >= 0 && (position + i)/8 == row) {
					if(checkField[position+i].character.length() > 2) {
						if((checkField[position+i].black && iAmWhite) || (checkField[position+i].white && !iAmWhite)) toBeMoveable[position + i] = true;
						break;
					}
					
					toBeMoveable[position + i] = true;
				}
			}
			
			for(int i = 1; i <= 7; i++) {
				if(position + 8*i <= 63 && (position + 8*i)%8 == column) {
					if(checkField[position+8*i].character.length() > 2) {
						if((checkField[position+8*i].black && iAmWhite) || (checkField[position+8*i].white && !iAmWhite)) toBeMoveable[position + 8*i] = true;
						break;
					}
					toBeMoveable[position + 8*i] = true;
				}
			}
			
			for(int i = -1; i >= -7; i--) {
				if(position + 8*i >= 0 && (position + 8*i)%8 == column) {
					if(checkField[position+8*i].character.length() > 2) {
						if((checkField[position+8*i].black && iAmWhite) || (checkField[position+8*i].white && !iAmWhite)) toBeMoveable[position + 8*i] = true;
						break;
					}
					toBeMoveable[position + 8*i] = true;
				}
			}
			break;
			
			
		case "springer":
			if(position-17 >= 0  && column>0)							toBeMoveable[position-17] = true;
			if(position-15 >= 0  && column<7)							toBeMoveable[position-15] = true;
			if(position-10 >= 0  && column>1)							toBeMoveable[position-10] = true;
			if(position-6  >= 0  && column<6 && (position-6)/8 != row)	toBeMoveable[position-6 ] = true;
			if(position+6  <= 63 && column>1 && (position+6)/8 != row)	toBeMoveable[position+6 ] = true;
			if(position+10 <= 63 && column<6) 							toBeMoveable[position+10] = true;
			if(position+15 <= 63 && column>0) 							toBeMoveable[position+15] = true;
			if(position+17 <= 63 && column<7) 							toBeMoveable[position+17] = true;
			break;
			
			
		case "laeufer":
			for(int i = 1; i <= 7; i++) {
				if(position + 7*i <= 63 && column > (position + 7*i)%8) {
					if(checkField[position+7*i].character.length() > 2) {
						if((checkField[position+7*i].black && iAmWhite) || (checkField[position+7*i].white && !iAmWhite)) toBeMoveable[position + 7*i] = true;
						break;
					}
					if((position + 7*i)/8 != row) {
						toBeMoveable[position + 7*i] = true;
					}
				}
			}
			
			for(int i = -1; i >= -7; i--) {
				if(position + 7*i >= 0 && column < (position + 7*i)%8) {
					if(checkField[position+7*i].character.length() > 2) {
						if((checkField[position+7*i].black && iAmWhite) || (checkField[position+7*i].white && !iAmWhite)) toBeMoveable[position + 7*i] = true;
						break;
					}
					if((position + 7*i)/8 != row) {
						toBeMoveable[position + 7*i] = true;
					}
				}
			}
			
			for(int i = 1; i <= 7; i++) {
				if(position + 9*i <= 63 && column < (position + 9*i)%8) {
					if(checkField[position+9*i].character.length() > 2) {
						if((checkField[position+9*i].black && iAmWhite) || (checkField[position+9*i].white && !iAmWhite)) toBeMoveable[position + 9*i] = true;
						break;
					}
				
					toBeMoveable[position + 9*i] = true;
				}
			}
			
			for(int i = -1; i >= -7; i--) {
				if(position + 9*i >= 0 && column > (position + 9*i)%8) {
					if(checkField[position+9*i].character.length() > 2) {
						if((checkField[position+9*i].black && iAmWhite) || (checkField[position+9*i].white && !iAmWhite)) toBeMoveable[position + 9*i] = true;
						break;
					}

					toBeMoveable[position + 9*i] = true;
				}
			}
			break;
			
			
		case "koenig":
			//rochade
			//rechts
			for(int i = 1; i < 8; i++) {
				if(position+i <= 63) {
					if((checkField[position+i].character.equalsIgnoreCase("turm")) && i > 1 &&
							(checkField[position+i].white && iAmWhite || checkField[position+i].black && !iAmWhite) &&
							( (int) ((position+i)/8) == row ) &&
							(!checkField[position].wasMoved && !checkField[position+i].wasMoved)
							) toBeMoveable[position+2] = true; 
					if(checkField[position+i].character.length() > 2) i = 10;
				}
			}
			//links
			for(int i = -1; i > -8; i--) {
				if(position+i >= 0) {
					if((checkField[position+i].character.equalsIgnoreCase("turm")) && i < -1 &&
							(checkField[position+i].white && iAmWhite || checkField[position+i].black && !iAmWhite) &&
							( (int) ((position+i+1)/8) == row) &&
							(!checkField[position].wasMoved && !checkField[position+i].wasMoved)
							) toBeMoveable[position-2] = true; 
					if(checkField[position+i].character.length() > 2) i = -10; 
				}
			}
			
			//normale Züge
			if(position-9 >=  0 && (position -9)/8 == row-1) 	toBeMoveable[position-9] = true;	//upper left
			if(position-8 >=  0 && (position -8)/8 != row) 		toBeMoveable[position-8] = true;	//upper mid
			if(position-7 >=  0 && (position -7)/8 != row) 		toBeMoveable[position-7] = true;	//upper right
			if(position-1 >=  0 && (position -1)/8 == row) 		toBeMoveable[position-1] = true;	//mid 	left
			if(position+1 <= 63 && (position +1)/8 == row) 		toBeMoveable[position+1] = true;	//mid	right
			if(position+7 <= 63 && (position +7)/8 != row) 		toBeMoveable[position+7] = true;	//lower	left
			if(position+8 <= 63 && (position +8)/8 != row) 		toBeMoveable[position+8] = true;	//lower mid
			if(position+9 <= 63 && (position +9)/8 == row+1)	toBeMoveable[position+9] = true;	//lower right
			break;
			
			
		case "dame":
			for(int i = 1; i <= 7; i++) {
				if(position + i <= 63 && row == (position+i)/8) {
					if(checkField[position+i].character.length() > 2) {
						if((checkField[position+i].black && iAmWhite) || (checkField[position+i].white && !iAmWhite)) toBeMoveable[position + i] = true;
						break;
					}
					toBeMoveable[position + i] = true;
				}
				else break;
			}
			
			for(int i = -1; i >= -7; i--) {
				if(position + i >= 0 && row == (position+i)/8) {
					if(checkField[position+i].character.length() > 2) {
						if((checkField[position+i].black && iAmWhite) || (checkField[position+i].white && !iAmWhite)) toBeMoveable[position + i] = true;
						break;
					}
					toBeMoveable[position + i] = true;
				}
				else break;
			}
			
			for(int i = 1; i <= 7; i++) {
				if(position + 8*i <= 63) {
					if(checkField[position+8*i].character.length() > 2) {
						if((checkField[position+8*i].black && iAmWhite) || (checkField[position+8*i].white && !iAmWhite)) toBeMoveable[position + 8*i] = true;
						break;
					}
					toBeMoveable[position + 8*i] = true;
				}
			}
			
			for(int i = -1; i >= -7; i--) {
				if(position + 8*i >= 0) {
					if(checkField[position+8*i].character.length() > 2) {
						if((checkField[position+8*i].black && iAmWhite) || (checkField[position+8*i].white && !iAmWhite)) toBeMoveable[position + 8*i] = true;
						break;
					}
					toBeMoveable[position + 8*i] = true;
				}
			}
			for(int i = 1; i <= 7; i++) {
				if(position + 7*i <= 63 && column > (position + 7*i)%8 && (position + 7*i)/8 != row) {
					if(checkField[position+7*i].character.length() > 2) {
						if((checkField[position+7*i].black && iAmWhite) || (checkField[position+7*i].white && !iAmWhite)) toBeMoveable[position + 7*i] = true;
						break;
					}
					toBeMoveable[position + 7*i] = true;
				}
			}
			
			for(int i = -1; i >= -7; i--) {
				if(position + 7*i >= 0 && column < (position + 7*i)%8 && (position + 7*i)/8 != row) {
					if(checkField[position+7*i].character.length() > 2) {
						if((checkField[position+7*i].black && iAmWhite) || (checkField[position+7*i].white && !iAmWhite)) toBeMoveable[position + 7*i] = true;
						break;
					}
					toBeMoveable[position + 7*i] = true;
				}
			}
			
			for(int i = 1; i <= 7; i++) {
				if(position + 9*i <= 63 && column < (position + 9*i)%8) {
					if(checkField[position+9*i].character.length() > 2) {
						if((checkField[position+9*i].black && iAmWhite) || (checkField[position+9*i].white && !iAmWhite)) toBeMoveable[position + 9*i] = true;
						break;
					}
					toBeMoveable[position + 9*i] = true;
				}
			}
			
			for(int i = -1; i >= -7; i--) {
				if(position + 9*i >= 0 && column > (position + 9*i)%8) {
					if(checkField[position+9*i].character.length() > 2) {
						if((checkField[position+9*i].black && iAmWhite) || (checkField[position+9*i].white && !iAmWhite)) toBeMoveable[position + 9*i] = true;
						break;
					}
					toBeMoveable[position + 9*i] = true;
				}
			}
			break;
			
			
		case "bauer":
			int ta = 1;
			if(it > 1) ta = -1;
			
			//normal move
			if(position - ta*8 >= 0 && position - ta*8 < 64 && checkField[position - ta*8].character.length() < 2) toBeMoveable[position - ta*8] = true;
			//double move
			if(!checkField[position].wasMoved && position - ta*16 >= 0 && position - ta*16 < 64 && checkField[position - ta*8].character.length() < 2 && checkField[position - ta*16].character.length() < 2) {
				toBeMoveable[position - ta*16] = true;
			}
			//capture
			if( (position - ta*7)/8 == row-ta*1 && position - ta*7 >= 0 && position - ta*7 < 64 && ((checkField[position-ta*7].white && !iAmWhite) || (checkField[position-ta*7].black && iAmWhite) )) toBeMoveable[position - ta*7] = true;
			if( (position - ta*9)/8 == row-ta*1 && position - ta*9 >= 0 && position - ta*9 < 64 && ((checkField[position-ta*9].white && !iAmWhite) || (checkField[position-ta*9].black && iAmWhite) )) toBeMoveable[position - ta*9] = true;
			//en Passant
			if(	( checkField[position+ta*1].character.equalsIgnoreCase("bauer")									)  &&	//if it's a pawn
				((checkField[position+ta*1].white && !iAmWhite) || (checkField[position+ta*1].black && iAmWhite))  &&	//if it's an enemy piece
				( checkField[position+ta*1].enPassanteable														)  &&	//if the pawn is en-passantable
				( (int) ((position+ta*1)/8) == row																) ) {	//and it's the same row +1
					toBeMoveable[position + ta*1 - ta*8] = true;
					
			}
			if(	( checkField[position-ta*1].character.equalsIgnoreCase("bauer")									) &&	//if it's a pawn
				((checkField[position-ta*1].white && !iAmWhite) || (checkField[position-ta*1].black && iAmWhite)) &&	//if it's an enemy piece
				( checkField[position-ta*1].enPassanteable														) &&	//if the pawn is en-passantable
				( (int) ((position-ta*1)/8) == row																) ) {	//and it's the same row -1
					toBeMoveable[position - ta*1 - ta*8] = true;
			}
			break;		
			
			
		default:
			break;
		}
		
		//check if own player
		for(int i = 0; i < 64; i++) {
			if( (checkField[i].white && iAmWhite ) || (checkField[i].black && !iAmWhite)) toBeMoveable[i] = false;
		}
		
		toBeMoveable[position] = false;
		
		
		if(it > 1)  return toBeMoveable;
				
		//check if move causes check
		for(int i = 0; i < 64; i++) {
			if(toBeMoveable[i]) toBeMoveable[i] = !checkCheck(i, position);	
		}
		
		return toBeMoveable;
	}
	
	public boolean checkCheck(int position, int whereFrom){
		
		Field[] tempField = new Field[64];

		for(int i = 0; i < 64; i++) {
			tempField[i] = new Field(field[i].game, field[i].character, field[i].white, field[i].black, field[i].x, field[i].y);
			tempField[i].character		= field[i].character;
			tempField[i].white			= field[i].white;
			tempField[i].black			= field[i].black;
			tempField[i].x				= field[i].x;
			tempField[i].y				= field[i].y;
			
			tempField[i].wasMoved		= field[i].wasMoved;
			tempField[i].enPassanteable	= field[i].enPassanteable;							//tempField = field KLAPPT SAFE
		}
		
		tempField = makeStep(tempField, position, whereFrom, false);						//Figur zieht KLAPPT SAFE
		
		boolean[] results = new boolean[64];
		int kingPosition = -100;
		
		for(int i = 0; i < 64; i++) {
			
			if((tempField[i].white && !game.iAmWhite) || (tempField[i].black && game.iAmWhite)) {
				kingPosition = findKing(tempField, game.iAmWhite);
				it++;
				results = makeMoveable(tempField, i, !game.iAmWhite);						//für jede gegnerische Figur wird makeMoveable bestimmt
				if(results[kingPosition]) {
					return true;															//falls es auf Position des eigenen Königs true ist, return schach
				}
			}
		}
		return false;
	}
	
	public void update() {
		u = 0;
		if(game.askIfIsMyTurn()) {
			try {
				if(!game.isOffline) ps.playSound("res/sounds/moveSound.wav");
				
				u++;
				game.willHaveToUpdate 	= false;
				game.eWantsRedo 		= false;
				game.waitingForRedo 	= false;
				
				if(!game.iAmWhite && !extra) {
					reverseField();
					calcAmount();
					step--;
					reverseField();
				}
				
				this.field = game.getField();
				game.calcPoints();
				
				//look if king is in check
				if(kingAttacked(field, game.iAmWhite)) {
					int attackedFrom;
					int king;
					if(game.iAmWhite) {
						attackedFrom = kingAttackedFrom(field, game.iAmWhite);
						king = findKing(field, game.iAmWhite);
					}
					else {
						attackedFrom 	= 63 - kingAttackedFrom(field, game.iAmWhite);
						king 			= 63 - findKing(field, game.iAmWhite);
					}
					
					if(attackedFrom >= 0 && attackedFrom < 64) {
						checkFrom.x 		= (attackedFrom)%8;
						checkFrom.y 		= (attackedFrom)/8;
						checkFrom.render 	= true;
					}
					
					if(game.to >= 0) {
						checkTo.x 			= (king)%8;
						checkTo.y 			= (king)/8;
						checkTo.render 		= true;
					}
				}
				
				//get enemies last turn
				if(extra) {
					if(game.iAmWhite) {
						
						if(game.from >= 0) {
							from.x = (game.from)%8;
							from.y = (game.from)/8;
							from.render = true;
						}
						if(game.to >= 0) {
							to.x = (game.to)%8;
							to.y = (game.to)/8;
							to.render = true;
						}
					}
					else {
						if(game.from >= 0) {
							from.x = (63-game.from)%8;
							from.y = (63-game.from)/8;
							from.render = true;
						}
						if(game.to >= 0) {
							to.x = (63-game.to)%8;
							to.y = (63-game.to)/8;
							to.render = true;
						}
					}
				}
				
				calcAmount();
				if(!extra && game.iAmWhite) step--; 
				extra = true;
		
				
				if(!game.iAmWhite) reverseField();
			}
			catch(Exception e) {
				System.out.println("Field couldn't be updatet in OnlineRule");
			}
			
			boolean willSurrender = true;
			boolean[] results;
			for(int i = 0; i < 64; i++) {
				if((field[i].white && game.iAmWhite) || (field[i].black && !game.iAmWhite)) {
					it = 0;
					results = makeMoveable(field, i, game.iAmWhite);
					it = 0;
					for(int j = 0; j < 64; j++) {
						if(results[j]) {
							willSurrender = false; 
						}	
					}
				}
			}
			if(willSurrender) {
				if(!kingAttacked(field, game.iAmWhite)) game.patt();
				else surrender();
			}
		}
	}
	
	public int findKing(Field[] input, boolean iAmWhite) {
		int output = -100;
		if(iAmWhite) {
			for(int i = 0; i < 64; i++) {
				if(input[i].character.equalsIgnoreCase("koenig") && input[i].white) output = i; 
			}
		}
		else {
			for(int i = 0; i < 64; i++) {
				if(input[i].character.equalsIgnoreCase("koenig") && input[i].black) output = i; 
			}
		}
		return output;
	}
	
	public boolean kingAttacked(Field[] input, boolean iAmWhite) {
		
		int kingPosition = findKing(input, iAmWhite);
		boolean[] results = new boolean[64];
		for(int i = 0; i < 64; i++) {
			if((input[i].white && !iAmWhite) || (input[i].black && iAmWhite)) {
				results = makeMoveable(input, i, !iAmWhite);
				if(results[kingPosition]) return true;
			}
		}
		return false;
	}
	
	public int kingAttackedFrom(Field[] input, boolean iAmWhite) {
		
		int kingPosition = findKing(input, iAmWhite);
		boolean[] results = new boolean[64];
		for(int i = 0; i < 64; i++) {
			if((input[i].white && !iAmWhite) || (input[i].black && iAmWhite)) {
				results = makeMoveable(input, i, !iAmWhite);
				if(results[kingPosition]) return i;
			}
		}
		return -1;
	}
	
	public void surrender() {
		game.field[findKing(field, game.iAmWhite)].character = "0";
		game.calcPoints();
		
		try {
			game.setField(field);
			game.calcPoints();
		} catch(Exception e) { }		
	}
	
	public void render(Graphics g) {
		if(n == 0) {init(); n++;}
		
		if(game.ableToRedo && ((game.eWantsRedo && game.isMyTurn) || game.waitingForRedo)) g.drawImage(Assets.sideBarRedoHL, 1060, 0, null); 
		
		//render enemies last turn
		from.render(g);
		to.render(g);
		
		//render check
		checkFrom.render(g);
		checkTo.render(g);
		
		//render Steps
		g.setFont(new Font("Impact", Font.PLAIN, 64)); 
		g.setColor(new Color(36, 7, 7));
		
		byte[] data = ("Step " + step).getBytes();
	    g.drawBytes(data, 0, data.length, 1100, 932);
		
		//render possible moves and field
		for(int i = 0; i < 64; i++) {
			gruen[i].render(g);
			field[i].render(g);
		}
		
		
		
		rahmen.render(g);
		rot.render(g);
	}

	public void init() {
		n++;
		
		System.out.println("Online Game initiated.");
		
		b.x = 0;
		b.y = 0;		
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				field[(8*i)+j] = new Field(game, "0", false, false, j, i);
			}
		}
		
		field[ 0] = new Field(game, "turm"		, false, true, 0, 0);
		field[ 1] = new Field(game, "springer"	, false, true, 1, 0);
		field[ 2] = new Field(game, "laeufer"	, false, true, 2, 0);
		field[ 3] = new Field(game, "dame"		, false, true, 3, 0);
		field[ 4] = new Field(game, "koenig"	, false, true, 4, 0);
		field[ 5] = new Field(game, "laeufer"	, false, true, 5, 0);
		field[ 6] = new Field(game, "springer"	, false, true, 6, 0);
		field[ 7] = new Field(game, "turm"		, false, true, 7, 0);
		field[ 8] = new Field(game, "bauer"		, false, true, 0, 1);
		field[ 9] = new Field(game, "bauer"		, false, true, 1, 1);
		field[10] = new Field(game, "bauer"		, false, true, 2, 1);
		field[11] = new Field(game, "bauer"		, false, true, 3, 1);
		field[12] = new Field(game, "bauer"		, false, true, 4, 1);
		field[13] = new Field(game, "bauer"		, false, true, 5, 1);
		field[14] = new Field(game, "bauer"		, false, true, 6, 1);
		field[15] = new Field(game, "bauer"		, false, true, 7, 1);
		
		for(int i = 0; i < 8; i++) {
			field[i+16] = new Field(game, "b", false, false, i, 2);
			field[i+24] = new Field(game, "0", false, false, i, 3);
			field[i+32] = new Field(game, "0", false, false, i, 4);
			field[i+40] = new Field(game, "0", false, false, i, 5);
		}
		
		field[48] = new Field(game, "bauer"		, true, false, 0, 6);
		field[49] = new Field(game, "bauer"		, true, false, 1, 6);
		field[50] = new Field(game, "bauer"		, true, false, 2, 6);
		field[51] = new Field(game, "bauer"		, true, false, 3, 6);
		field[52] = new Field(game, "bauer"		, true, false, 4, 6);
		field[53] = new Field(game, "bauer"		, true, false, 5, 6);
		field[54] = new Field(game, "bauer"		, true, false, 6, 6);
		field[55] = new Field(game, "bauer"		, true, false, 7, 6);
		field[56] = new Field(game, "turm"		, true, false, 0, 7);
		field[57] = new Field(game, "springer"	, true, false, 1, 7);
		field[58] = new Field(game, "laeufer"	, true, false, 2, 7);
		field[59] = new Field(game, "dame"		, true, false, 3, 7);
		field[60] = new Field(game, "koenig"	, true, false, 4, 7);
		field[61] = new Field(game, "laeufer"	, true, false, 5, 7);
		field[62] = new Field(game, "springer"	, true, false, 6, 7);
		field[63] = new Field(game, "turm"		, true, false, 7, 7);
		
		for(int j = 0; j < 64; j++) {
			gruen[j] = new Extra(game, Assets.gruen, j%8, (j-j%8)/8, false);
		}
		
		from 		= new Extra(game, Assets.yellow, 0, 0, false);
		to 			= new Extra(game, Assets.yellow, 0, 0, false);
		
		checkFrom 	= new Extra(game, Assets.rot, 	 0, 0, false);
		checkTo 	= new Extra(game, Assets.rot, 	 0, 0, false);
		
		game.initField(field);
		
		this.field = game.getField();
		
		if(!game.iAmWhite) reverseField();
	}
	
	public boolean isEnPassantable(Field[] input) {
		if(activeField+1 < 64) {
			if(	(input[activeField+1].character.equalsIgnoreCase("bauer")) && 												//falls rechts ein Bauer ist
				( (input[activeField+1].black && game.iAmWhite) || (input[activeField+1].white && !game.iAmWhite) ) &&		//vom Gegner
				( (int) (activeField/8) == (int) ((activeField+1)/8) ) ) {													//in der selben Zeile
				return true;
			}
		}
			
		if(activeField-1 >= 0) {
			if(	(input[activeField-1].character.equalsIgnoreCase("bauer")) && 												//falls links ein Bauer ist
				( (input[activeField-1].black && game.iAmWhite) || (input[activeField-1].white && !game.iAmWhite) ) &&		//vom Gegner
				( (int) (activeField/8) == (int) ((activeField-1)/8) ) ) {													//in der selben Zeile
				return true;
			}
		}
		return false;
	}
	
	public void reverseField() {
		Field[] tempField = new Field[64];

		for(int i = 0; i < 64; i++) {
			tempField[i] = new Field(field[i].game, field[i].character, field[i].white, field[i].black, field[i].x, field[i].y);
			tempField[i].wasMoved		= field[i].wasMoved;
			tempField[i].enPassanteable	= field[i].enPassanteable;					//tempField = field KLAPPT SAFE
		}
		
		for(int i = 0; i < 64; i++) {
			field[i].character 		= tempField[63-i].character;
			field[i].white 			= tempField[63-i].white;
			field[i].black 			= tempField[63-i].black;
			field[i].wasMoved		= tempField[63-i].wasMoved;
			field[i].enPassanteable	= tempField[63-i].enPassanteable;	
		}
	}
}
