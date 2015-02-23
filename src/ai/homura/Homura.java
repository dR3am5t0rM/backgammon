package ai.homura;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import ai.AI;
import game.Column;
import game.Game;
import game.Move;
import game.PossibleMove;

public class Homura implements AI {
	
	private final String timelinePath = "src/ai/homura/timeline.bgdata";
	
	Random generator;
	Timeline timeline;
	ArrayList<TimelineMove> gameMoves;
	
	public Homura(){
		generator = new Random();
		gameMoves = new ArrayList<TimelineMove>();
		try{
			FileInputStream fin = new FileInputStream(timelinePath);
			ObjectInputStream in = new ObjectInputStream(fin);
			timeline = (Timeline) in.readObject();
			in.close();
			fin.close();
		} catch (IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (timeline == null){
				timeline = new Timeline();
			}
		}
		
		for (TimelineMove t : timeline.timelineMoves){
//			System.out.println(t.getFrom() + " > " + t.getTo() + " BS:" + t.getBoardState() + " W: " + t.getWins() + " L: " + t.getLoses() );
		}
//		System.exit(0);
	}
	

	public void makeMove(){

		ArrayList<TimelineMove> knownMoves = new ArrayList<>();
		for (TimelineMove m : this.timeline.timelineMoves){
			for (PossibleMove move : Move.possibleMoves){
				if (Arrays.equals(m.getBoardState(), TimelineMove.makeBoardState(Column.getAll())) && m.getFrom() == move.getFrom() && m.getTo() == move.getTo()){
					knownMoves.add(m);
				}
			}
		}
		
		if (!knownMoves.isEmpty()){

			TimelineMove bestMove = null;
			double bestValue = -10000000.0;
			for (TimelineMove m : knownMoves){

				if (m.getValue() > bestValue){
//					System.out.println(m.getValue());
					bestValue = m.getValue();
					bestMove = m;
				}
			}
			
//			System.out.println(bestMove.getTurn());
			PossibleMove chosenKnownMove = Move.find(bestMove.getFrom(),bestMove.getTo());
			Move.executeMove(chosenKnownMove,true);
			System.out.println("using known move");
		} else {
			int chosenMove = generator.nextInt(Move.possibleMoves.size());
			PossibleMove move = Move.possibleMoves.get(chosenMove);
			gameMoves.add(new TimelineMove(Column.getAll(),move.getFrom(),move.getTo()));
			Move.executeMove(move,true);			
		}
	}

	public void addWinData() {
		timeline.pushWinData(gameMoves);
		this.saveTimeline();
	}

	public void addLoseData() {
		timeline.pushLoseData(gameMoves);
		this.saveTimeline();		
	}

	public void saveTimeline() {
		try {
			FileOutputStream fout = new FileOutputStream(timelinePath);
			ObjectOutputStream out = new ObjectOutputStream(fout);
			out.writeObject(timeline);
			out.close();
			fout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.gameMoves = new ArrayList<>();
		}
	}
	

}
