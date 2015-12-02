import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Parser {

	private static final Parser parser = new Parser();
	private Parser(){
		
	}
	
	public static Parser getParser(){
		return parser;
	}
	
	public ArrayList<Match> getAllMatches(String folderPath){
		String fileName = null;
//		HashSet<String> ignoreFiles = new HashSet<String>(){{
//			add("250670.yaml");
//			add("267710.yaml");
//			add("291346.yaml");
//			add("291360.yaml");
//			add("295785.yaml");
//			add("295788.yaml");
//			add("296918.yaml");
//			add("325576.yaml");
//			add("366626.yaml");
//			
//		}};
		ArrayList<Match> allMatches = new ArrayList<Match>();
		try{
			
			File folder = new File(folderPath);
			FileReader fReader;
			
			int countFiles = 1;
			int countFilesWithResults = 1;
			
			for(File f: folder.listFiles()){

				try{
					if(!f.isDirectory() && f.getName().contains(".yaml")){

						countFiles++;
						fReader = new FileReader(f);
						BufferedReader reader = new BufferedReader(fReader);
						String line = reader.readLine();
						
//						System.out.println("Processing file:"+f.getName());
						fileName = f.getName();
//						if(ignoreFiles.contains(fileName)){
//							continue;
//							// ignore this file as it has error
//							//System.out.println();
//						}
						Match match = new Match();
						
						
						// Outcome processing
						while(line!=null && !line.contains("outcome")) {
							line = reader.readLine();
						}
						if(line!=null && line.contains("outcome")){
							countFilesWithResults++;
							line = reader.readLine(); // by:
							line = reader.readLine(); // "runs or wickets"
							
							if(line.contains("runs")) {
								match.winType = Match.WinType.RUNS;
								match.winValue = Integer.parseInt(line.replace("runs: ", "").trim());
							}
							else if(line.contains("wickets")){
								match.winType = Match.WinType.WICKETS;
								match.winValue = Integer.parseInt(line.replace("wickets: ", "").trim());
							}else{
								match.winType = Match.WinType.TIE;
							}
							
							line = reader.readLine(); // winner
							match.winner = line.replace("winner: ", "");
//							System.out.println(match.winner);
						}
						
						while(line!=null && !line.contains("teams")){
							line = reader.readLine();
						}
						if(line!=null && line.contains("teams")){
							match.teams[0] = reader.readLine().replace("-", "").trim();
							match.teams[1] = reader.readLine().replace("-", "").trim();
						}
						
						while(line!=null && !line.contains("innings")){
							line = reader.readLine();
						}
						
						
						//Ball by ball processing
						//First Innings
						while(line!=null && !line.contains("1st innings")){
							line = reader.readLine();
						}
						if(line!=null && line.contains("1st innings")){
							// This line will contain team name playing the first inning.
							
							line = reader.readLine().replace("team:", "").trim();
							Inning firstInning = new Inning();
							firstInning.inningNumer=1;
							firstInning.team = line;
							
							line = reader.readLine(); // "deliveries"
							
							Over over = null;
							Delivery del = null;
							
							line="";
							ArrayList<String> list = new ArrayList<String>();
							while(line!=null && !line.contains("2nd innings")){
								if(!line.contains("2nd innings")&& !line.isEmpty())
									list.add(line);
								line = reader.readLine();
							}
							
							for(String line2:list){
								
								// start of a new delivery
								if(line2.trim().matches("- \\d+\\.\\d+:")){
									if(del!=null) over.allDeliveries.add(del);
									//System.out.println(line2);
									String[] pair = line2.trim().replace("-", "").replace(":", "").trim().split("\\.");
									int ballNo = Integer.parseInt(pair[1]);
									int overNo = Integer.parseInt(pair[0]);
									if(ballNo==1) {
										// put previous over in the list
										if(over!=null) firstInning.allOvers.add(over);
										over = new Over();
										over.overNumber = overNo;
									}
									
									del = new Delivery();
									del.ballNumer = ballNo;

								}
								else if(line2.contains("batsman")){
									String val = line2.trim().replace("batsman:", "").trim();
									if(val.matches("\\d+")) del.runs_by_batsman = Integer.parseInt(val);
									else del.batsman = val;
								}
								else if(line2.contains("bowler")) {
									over.bowler = line2.trim().replace("bowler:", "").trim();
								}
								else if(line2.contains("byes")) 
									{
									del.extra_type = "byes";
									}
								else if(line2.contains("legbyes")){
									del.extra_type = "legbyes";
								}
								else if(line2.contains("noballs")){
									del.extra_type = "noballs";
								}
								else if(line2.contains("wides")){
									del.extra_type = "wides";
								}
								else if(line2.contains("extras")){
									String val = line2.trim().replace("extras:", "").trim();
									if(val.matches("\\d+")) del.run_by_extras = Integer.parseInt(val);
								}
								else if(line2.contains("total"))
									{
									String val = line2.trim().replace("total:", "").trim();
									if(val.matches("\\d+")) del.total_run = Integer.parseInt(val);
									}
								
							}
							match.allInings.add(firstInning);

							
						}

						
						line = reader.readLine().replace("team:", "").trim();
						Inning secondInning = new Inning();
						secondInning.inningNumer=2;
						secondInning.team = line;
						
						line = reader.readLine(); // "deliveries"
						
						Over over = null;
						Delivery del = null;
						
						line="";
						ArrayList<String> list = new ArrayList<String>();
						while(line!=null){
							if(!line.isEmpty())
								list.add(line);
							line = reader.readLine();
						}
						
						for(String line2:list){
							
							// start of a new delivery
							if(line2.trim().matches("- \\d+\\.\\d+:")){
								if(del!=null) over.allDeliveries.add(del);
								//System.out.println(line2);
								String[] pair = line2.trim().replace("-", "").replace(":", "").trim().split("\\.");
								int ballNo = Integer.parseInt(pair[1]);
								int overNo = Integer.parseInt(pair[0]);
								if(ballNo==1) {
									// put previous over in the list
									if(over!=null) secondInning.allOvers.add(over);
									over = new Over();
									over.overNumber = overNo;
								}
								
								del = new Delivery();
								del.ballNumer = ballNo;

							}
							else if(line2.contains("batsman")){
								String val = line2.trim().replace("batsman:", "").trim();
								if(val.matches("\\d+")) del.runs_by_batsman = Integer.parseInt(val);
								else del.batsman = val;
							}
							else if(line2.contains("bowler")) {
								over.bowler = line2.trim().replace("bowler:", "").trim();
							}
							else if(line2.contains("byes")) 
								{
								del.extra_type = "byes";
								}
							else if(line2.contains("legbyes")){
								del.extra_type = "legbyes";
							}
							else if(line2.contains("noballs")){
								del.extra_type = "noballs";
							}
							else if(line2.contains("wides")){
								del.extra_type = "wides";
							}
							else if(line2.contains("extras")){
								String val = line2.trim().replace("extras:", "").trim();
								if(val.matches("\\d+")) del.run_by_extras = Integer.parseInt(val);
							}
							else if(line2.contains("total"))
								{
								String val = line2.trim().replace("total:", "").trim();
								if(val.matches("\\d+")) del.total_run = Integer.parseInt(val);
								}
							
						}
						match.allInings.add(secondInning);
						allMatches.add(match);
						}
				}
				catch(Exception e){
					countFilesWithResults--;
//					e.printStackTrace();
					System.out.println("Parsing Error in file "+fileName+". Ignoring");
//					System.out.println("Ignoring the file");
				}	
					
				}
			System.out.println("Total files processed:"+ countFiles);
			System.out.println("Total valid files with outcomes:"+ countFilesWithResults);
			
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Error in file "+fileName);
		}
		return allMatches;
		
	}
}
