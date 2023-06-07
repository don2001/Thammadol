package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;
import java.util.*;
import javax.annotation.Nonnull;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.Move.*;
import uk.ac.bris.cs.scotlandyard.model.Piece.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;
import java.util.ArrayList;

import static uk.ac.bris.cs.scotlandyard.model.Piece.MrX.MRX;

/**
 * cw-model
 * Stage 1: Complete this class
 */

public final class MyGameStateFactory implements Factory<GameState> {
	//ArrayList<LogEntry>lg =new ArrayList<LogEntry>();
	//private ImmutableSet<Piece> remaining;
	int index=0;
	private final class MyGameState implements GameState {
		ArrayList<LogEntry>lg =new ArrayList<LogEntry>();
		private GameSetup setup;
		private ImmutableSet<Piece> remaining;
		private ImmutableList<LogEntry> log;
		private Player mrX;
		private List<Player> detectives;
		private ImmutableSet<Move> moves;
		private ImmutableSet<Piece> winner;

       int index=0;


		Set<Move.SingleMove> SingleMoves =new HashSet<>();
		Set<Move.DoubleMove> DoubleMoves=new HashSet<>();
		//List<LogEntry>lg =new ArrayList<LogEntry>();
		private MyGameState(GameSetup setup,ImmutableSet<Piece> remaining,ImmutableList<LogEntry> log,Player mrX,
							List<Player>detectives,int index){
			this.setup = setup;
			this.remaining = remaining;
			this.log = log;
			this.mrX = mrX;
			this.detectives = detectives;
			this.index=index;
		}
        //this.moves=ImmutableSet.copyOf(Iterables.concat(Move.SingleMove,Move.DoubleMove));



		@Override public GameSetup getSetup() {  return setup; }
		@Override  public ImmutableSet<Piece> getPlayers() {
			Set<Piece> s = new HashSet<Piece>();
			s.add(mrX.piece());
			for(Player p:detectives){
				s.add(p.piece());
			}
			ImmutableSet<Piece> is  = ImmutableSet.copyOf(s);
			//remaining=is;
			return is;

		}

		@Nonnull
		@Override
		public Optional<Integer> getDetectiveLocation(Detective detective) {
			if(detective==Detective.BLUE) {
				int ans=-1;
                for(Player p:detectives){
	                 if(p.piece()==Detective.BLUE){
						 ans=p.location();
					 }
				}
				if(ans!=-1)return Optional.of(ans);
				else return Optional.empty();

			}
			else if(detective==Detective.RED) {
				int ans=-1;
				for(Player p:detectives){
					if(p.piece()==Detective.RED){
						ans=p.location();
					}
				}
				if(ans!=-1)return Optional.of(ans);
				else return Optional.empty();
			}
			else if(detective==Detective.GREEN) {
				int ans=-1;
				for(Player p:detectives){
					if(p.piece()==Detective.GREEN){
						ans=p.location();
					}
				}
				if(ans!=-1)return Optional.of(ans);
				else return Optional.empty();
			}
			else if(detective==Detective.WHITE) {
				int ans=-1;
				for(Player p:detectives){
					if(p.piece()==Detective.WHITE){
						ans=p.location();
					}
				}
				if(ans!=-1)return Optional.of(ans);
				else return Optional.empty();
			}
			else if(detective==Detective.YELLOW) {
				int ans=0;
				for(Player p:detectives){
					if(p.piece()==Detective.YELLOW){
						ans=p.location();
					}
				}
				if(ans!=-1)return Optional.of(ans);
				else return Optional.empty();
			}
			return Optional.empty();
		}

		@Nonnull
		@Override
		public Optional<TicketBoard> getPlayerTickets(Piece piece) {
			for (Player player : ImmutableList.<Player>builder().addAll(detectives).add(mrX).build()){
				if(player.piece() == piece){
					return Optional.of(new TicketBoard() {
						@Override
						public int getCount(@Nonnull Ticket ticket) {
							return player.tickets().get(ticket);
						}
					});
				}
			}
			return Optional.empty();
		}


		@Nonnull
		@Override
		public ImmutableList<LogEntry> getMrXTravelLog() {
			//System.out.println("1 "+lg);
			index=log.size();
			return log;
		}
		@Nonnull
		@Override
		public ImmutableSet<Piece> getWinner() {
			//System.out.println("1" +remaining);
			//System.out.println(mrX.location());
			winner=ImmutableSet.of();
			List<Piece>l= new ArrayList<>();
			for(Player p: detectives){
				l.add(p.piece());
			}

			// detectives win
			for(Player p:detectives){
				//System.out.println(p.location());
				if(p.location()== mrX.location()){
					winner=ImmutableSet.<Piece>builder().addAll(l).build();
					return ImmutableSet.<Piece>builder().addAll(l).build();
				}
			}

			if(remaining.contains(mrX.piece())){
				boolean ok=false;
				for(int des: setup.graph.adjacentNodes(mrX.location())){
					//System.out.println(des);
					boolean work=true;
					for(Transport t:setup.graph.edgeValueOrDefault(mrX.location(),des,ImmutableSet.of())){
						if(!mrX.has(t.requiredTicket())&& !mrX.has(Ticket.SECRET)){
							work=false;
						}
					}

					for(Player p:detectives){
						if(p.location()==des){
							work=false;
						}
					}
					ok=ok||work;
				}
				if(!ok){
					winner=ImmutableSet.<Piece>builder().addAll(l).build();
					return ImmutableSet.<Piece>builder().addAll(l).build();
				}
			}

			//mrx win
			boolean have=true;
			for(Player p:detectives){
				if(p.has(Ticket.BUS) ||p.has(Ticket.TAXI) || p.has(Ticket.UNDERGROUND)){
					have=false;
				}
			}
			//System.out.println(have);
			if(getSetup().moves.size()==1){
				have =false;
			}
			//System.out.println(have);
			if(log.size()==setup.moves.size() && remaining.contains(mrX.piece())){
                have=true;
			}
			//System.out.println(have);
			if(have)winner=ImmutableSet.<Piece>builder().add(mrX.piece()).build();
			return winner;
		}

		@Nonnull
		@Override
		public ImmutableSet<Move> getAvailableMoves() {
			for(Player p:detectives){
				//System.out.println(p.location());
			}
			Set<Move> s=new HashSet<Move>();
			boolean isMrXTurn = remaining.contains(mrX.piece());
            if(isMrXTurn){
				for (Move m : makeSingleMoves(setup, detectives, mrX, mrX.location())) {
					s.add(m);
				}
				for (Move m : makeDoubleMoves(setup, detectives, mrX, mrX.location())) {
					if (getSetup().moves.size() > 1) s.add(m);
				}
			}
			else{
				for(Piece pi:remaining){
					for(Player p:detectives){
						if(p.piece()==pi) {
							//System.out.println(p);
							for (Move m : makeSingleMoves(setup, detectives, p, p.location())) {
								s.add(m);
							}
							for (Move m : makeDoubleMoves(setup, detectives, p, p.location())) {
								s.add(m);
							}
						}
					}
				}
			}
			ImmutableSet<Move> move = ImmutableSet.copyOf(s);
			Set<Move>set= new HashSet<Move>();
			ImmutableSet<Move> ans=ImmutableSet.copyOf(set);
			/*
			if(s.size()==0){
				Set<Piece> ss = new HashSet<Piece>();
				ss.add(mrX.piece());
				ImmutableSet<Piece> is  = ImmutableSet.copyOf(ss);
				remaining=is;
				for (Move m : makeSingleMoves(setup, detectives, mrX, mrX.location())) {
					s.add(m);
				}
				for (Move m : makeDoubleMoves(setup, detectives, mrX, mrX.location())) {
					if (getSetup().moves.size() > 1) s.add(m);
				}

			}*/

			if(getWinner().size()>0){
				move=ans;
			}
			return move;

		}
		@Override public GameState advance(Move move) {
			//System.out.println("1 " +getAvailableMoves());//System.out.println(remaining);
			//if(moves.contains(move)) throw new IllegalArgumentException("Illegal move: "+move);
			List<Player> detective_new= new ArrayList<>(detectives);
			//System.out.println(getAvailableMoves().size());
			if(!getAvailableMoves().contains(move)){
				throw new IllegalArgumentException("Illegal move: "+move);
			}


			Integer dest = move.accept(new Visitor<Integer>(){
				@Override public Integer visit(Move.SingleMove singleMove){
					return singleMove.destination;
				}
				@Override public Integer visit(Move.DoubleMove doubleMove){
					return doubleMove.destination2;
				}
			});
			Integer source = move.accept(new Visitor<Integer>(){
				@Override public Integer visit(Move.SingleMove singleMove){
					return singleMove.source();
				}
				@Override public Integer visit(Move.DoubleMove doubleMove){
					return doubleMove.source();
				}
			});
			List<LogEntry>neww =new ArrayList<>();
			if(move.commencedBy()==mrX.piece()){
				for (Ticket tic : move.tickets()) {
					//System.out.println("1 "+index);
					if (getSetup().moves.get(index)) {
						//System.out.println(1);
						neww.add(LogEntry.reveal(tic, dest));
					} else {
						//System.out.println(2);
						neww.add(LogEntry.hidden(tic));
					}

					mrX = mrX.use(tic);
					mrX = mrX.at(dest);
					index++;
				}

				Set<Piece> now=new HashSet<Piece>();
				for(Player p:detectives){
					now.add(p.piece());
				}
				remaining=ImmutableSet.copyOf(now);
				//System.out.println(remaining);
			}
			else{
				Player current=new Player(mrX.piece(), mrX.tickets(),mrX.location());
				int index = 0;
				for(Player p:detectives){
					if(move.commencedBy()==p.piece()){
						current=p;
						index = detective_new.indexOf(current);
					}
				}

				for (Ticket tic : move.tickets()) {
					//System.out.println(dest);
					current = current.use(tic);
					mrX = mrX.give(tic);
					current = current.at(dest);
					//System.out.println(current.location());
				}

				Set<Piece> now=new HashSet<>();
				//now.add(mrX.piece());
				if(remaining.size()==1 ) {
					//System.out.println(1);
					now.add(mrX.piece());
					remaining = ImmutableSet.copyOf(now);
				}
				else{
					for(Piece p:remaining){
						now.add(p);
					}
					now.remove(move.commencedBy());
					remaining=ImmutableSet.copyOf(now);
					if(getAvailableMoves().size()==0){
						now.removeAll(now);
						now.add(mrX.piece());
						remaining = ImmutableSet.copyOf(now);
					}
				}
				detective_new.set(index,current);
			}
			//System.out.println(remaining);
			if(neww.size()>2){
				neww.remove(neww.size()-1);
			}
			List<LogEntry>l= new ArrayList<LogEntry>();
			for(LogEntry le:log){
				l.add(le);
			}
			for(LogEntry le:neww){
				l.add(le);
			}
			mrX.tickets();
			log=ImmutableList.copyOf(l);
			//System.out.println(detective_new);
			//System.out.println("2 " +getWinner());
			detectives=detective_new;
			return new MyGameState(setup,remaining,log,mrX,detective_new,index);

		}


	}
	@Nonnull @Override public GameState build(
			GameSetup setup,
			Player mrX,
			ImmutableList<Player> detectives) {

		// test 1-8
		int cnt = 0;
		if (!mrX.isMrX()) {
			throw new IllegalArgumentException();
		} else {
			cnt = 1;
		}
		for (Player x : detectives) {
			if (x.isMrX()) {
				cnt++;
				throw new IllegalArgumentException();
			}
		}
		if (cnt != 1) {
			throw new IllegalArgumentException();
		}
		Set<Player> s = new HashSet<Player>();
		Set<Integer> l = new HashSet<Integer>();
		for (Player p : detectives) {
			s.add(p);
			l.add(p.location());
		}
		if (s.size() != detectives.size()) {
			throw new IllegalArgumentException();
		}
		if (l.size() != detectives.size()) {
			throw new IllegalArgumentException();
		}
		//'test 9-12
		for (Player p : detectives) {
			if (p.hasAtLeast(ScotlandYard.Ticket.DOUBLE, 1) || p.hasAtLeast(ScotlandYard.Ticket.SECRET, 1)) {
				throw new IllegalArgumentException();
			}
		}
		if (setup.moves.isEmpty()) throw new IllegalArgumentException();
		if (setup.graph.edges().isEmpty()) throw new IllegalArgumentException();

		//throw new RuntimeException("Implement me!");
		//test 13-16
		return new MyGameState(setup, ImmutableSet.of(MrX.MRX), ImmutableList.of(), mrX, detectives,index);

	}
	private static Set<SingleMove> makeSingleMoves(GameSetup setup, List<Player> detectives, Player player, int source){

		Set<SingleMove>s= new HashSet<SingleMove>();

		for(int destination : setup.graph.adjacentNodes(source)) {
			// TODO find out if destination is occupied by a detective
			//  if the location is occupied, don't add to the collection of moves to return
			int ok=1;
			for(Player p:detectives){
				if(p.location()==destination){
					ok=0;
				}
			}
			if(ok==0){
				continue;
			}
			for(Transport t : setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of()) ) {
				// TODO find out if the player has the required tickets
				//  if it does, construct a SingleMove and add it the collection of moves to return
				SingleMove e = new SingleMove(player.piece(), source, t.requiredTicket(), destination);
				if (player.hasAtLeast(t.requiredTicket(), 1)) {
					s.add(e);
				}
			}
			// TODO consider the rules of secret moves here
			//  add moves to the destination via a secret ticket if there are any left with the player
			SingleMove e = new SingleMove(player.piece(), source, Ticket.SECRET, destination);
            if(player.hasAtLeast(Ticket.SECRET,1)){
                s.add(e);
			}
		}

        return s;
		// TODO return the collection of moves
	}
	private static Set<DoubleMove> makeDoubleMoves(GameSetup setup, List<Player> detectives, Player player, int source){
         HashSet<DoubleMove>s= new HashSet<DoubleMove>();
		 for(int middle :setup.graph.adjacentNodes(source)){
			 for(int end: setup.graph.adjacentNodes(middle)){
				 int ok=1;
				 for(Player p:detectives){
					 if(p.location()==middle || p.location()==end){
						 ok=0;
					 }
				 }
				 if(ok==0 || !player.hasAtLeast(Ticket.DOUBLE,1)){
					 continue;
				 }
                  for(Transport t: setup.graph.edgeValueOrDefault(source,middle,ImmutableSet.of())) {
					  for (Transport u : setup.graph.edgeValueOrDefault(middle, end, ImmutableSet.of())) {
						  DoubleMove e=new DoubleMove(player.piece(),source,t.requiredTicket(),middle,u.requiredTicket()
								  ,end);
							   if(t!=u){
								   if(player.hasAtLeast(Ticket.DOUBLE,1)&& player.hasAtLeast(t.requiredTicket(),1)&&
										   player.hasAtLeast(u.requiredTicket(),1)){
									   s.add(e);
								   }
							   }
							   else{
								   if(player.hasAtLeast(Ticket.DOUBLE,1)&& player.hasAtLeast(t.requiredTicket(),2)){
                                       s.add(e);
								   }

							   }
							   if(player.hasAtLeast(Ticket.SECRET,2)){
								   DoubleMove f=new DoubleMove(player.piece(),source,Ticket.SECRET,middle,Ticket.SECRET,end);
								   s.add(f);
							   }
							   if(player.hasAtLeast(Ticket.SECRET,1) && player.hasAtLeast(t.requiredTicket(),1)){
							       DoubleMove f=new DoubleMove(player.piece(),source,t.requiredTicket(),middle,Ticket.SECRET,end);
								   s.add(f);
							   }
							   if(player.hasAtLeast(Ticket.SECRET,1) && player.hasAtLeast(u.requiredTicket(),1)){
								   DoubleMove f=new DoubleMove(player.piece(),source,Ticket.SECRET,middle,u.requiredTicket(),end);
								   s.add(f);
							   }

					  }
				  }

			 }
		 }
         return s;
	}
}
