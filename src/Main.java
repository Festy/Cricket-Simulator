import java.io.IOException;
import java.util.ArrayList;


public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Parser parser = Parser.getParser();
		ArrayList<Match> allMatches = parser.getAllMatches("/Users/utsavpatel/Desktop/Cricket Simulator/data2");
		double wideBalls=0;
		double noBalls=0;
		double totalDeliveries=0;
		double otherExtras=0;
		
		for(Match match: allMatches){
			//if(match.teams[1].equals("India")||match.teams[1].equals("India")) counter++;
			for(Inning inning: match.allInings){
				for(Over over: inning.allOvers){
					for(Delivery delivery: over.allDeliveries){
						if(delivery.extra_type!=null && delivery.extra_type.equals("wides")) wideBalls++;
						else if(delivery.extra_type!=null && delivery.extra_type.equals("noballs")) noBalls++;
						else if(delivery.extra_type!=null) otherExtras++;
						totalDeliveries++;
					}
				}
			}
		}
		
		System.out.println("Wide balls: "+wideBalls);
		System.out.println("No Balls: "+noBalls);
		System.out.println("Other extra balls: "+otherExtras);
		System.out.println("Total Deliveries: "+totalDeliveries);
		System.out.println("Probability of an extra delivery per delivery: "+((wideBalls+otherExtras+noBalls)/totalDeliveries));

	}

}
