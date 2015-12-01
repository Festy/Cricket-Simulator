import java.util.ArrayList;


public class Match {

	public enum WinType{
		RUNS, WICKETS, TIE, NO_RESULT;
	}
	public enum TossDecisionType{
		FIELD, BAT;
	}

	public String winner;
	public WinType winType;
	public int winValue;
	public String[] teams = new String[2];
	public String tossDecision;
	public String tossWinner;
	public ArrayList<Inning> allInings = new ArrayList<Inning>();
}
