package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.util.*;

/**
 * cw-model
 * Stage 2: Complete this class
 */
public final class MyModelFactory implements Factory<Model> {
	private GameSetup setup;
	private Player mrX;
	private List<Player> detectives;
	private Set<Model.Observer>ob=new HashSet<>();

	private final class MyModel implements Model{
		private GameSetup setup;
		private Player mrX;
		private List<Player> detectives;
		private Set<Model.Observer>ob=new HashSet<>();
		private Board.GameState board;

		MyModel(GameSetup setup,
				Player mrX,
				ImmutableList<Player> detectives){
			this.setup=setup;
			this.mrX=mrX;
			this.detectives=detectives;
			this.board = new MyGameStateFactory().build(setup, mrX, detectives);

		}

		@Nonnull
		@Override
		public Board getCurrentBoard() {
			return this.board;
		}

		@Override
		public void registerObserver(@Nonnull Observer observer) {
			if(observer==null){
				throw new NullPointerException();
			}
			//System.out.println(observer);
			if(ob.contains(observer)){
				throw new IllegalArgumentException();
			}
			ob.add(observer);
		}

		@Override
		public void unregisterObserver(@Nonnull Observer observer) {
			if(ob==null && observer!=null){
				throw new IllegalArgumentException();
			}
			if(observer==null){
				throw new NullPointerException();
			}
			if(!ob.contains(observer)){
				throw new IllegalArgumentException();
			}
			ob.remove(observer);
		}

		@Nonnull
		@Override
		public ImmutableSet<Observer> getObservers() {
			ImmutableSet<Observer>ans= ImmutableSet.copyOf(ob);
			System.out.println(ans.size());
			return ans;
		}

		@Override
		public void chooseMove(@Nonnull Move move) {
            board.advance(move);
			if(board.getWinner().isEmpty()){
				//System.out.println(1);
				for(Observer o : ob){
					o.onModelChanged(board, Observer.Event.MOVE_MADE);
					//System.out.println(board.getAvailableMoves());
				}
			}
			else{
				for(Observer o : ob){
					o.onModelChanged(board, Observer.Event.GAME_OVER);
				}
			}
		}
	}



	@Nonnull @Override public Model build(GameSetup setup,
	                                      Player mrX,
	                                      ImmutableList<Player> detectives) {
		// TODO

		return new MyModel(setup,mrX,detectives);
	}

}
