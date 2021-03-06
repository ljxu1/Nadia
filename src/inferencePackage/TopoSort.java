package inferencePackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import nodePackage.*;


public class TopoSort {

	/*
     *this topological sort method uses "Kahn's algorithm which is based on BFS(Breadth First Search)
     *within this method, the original 'dependencyMatrix' will lose information of dependency 
     *due to the reason that the algorithm itself uses the dependency information and delete it while topological sorting
     *Hence, this method needs to create copy of dependencyMatrix.
     */
	public static List<Node> bfsTopoSort(HashMap<String, Node> nodeMap, HashMap<Integer, String> nodeIdMap, int[][] dependencyMatrix)
	{
		List<Node> sortedNodeList = new ArrayList<>();
		int sizeOfMatrix = dependencyMatrix[0].length;
		int[][] copyOfDependencyMatrix = createCopyOfDependencyMatrix(dependencyMatrix, sizeOfMatrix);	

		List<Node> tempList = new ArrayList<>();
		List<Node> SList = fillingSList(nodeMap, nodeIdMap, tempList, copyOfDependencyMatrix);
		
		
		while(!SList.isEmpty())
		{
			Node node = SList.remove(0);
			sortedNodeList.add(node);
			int nodeId = node.getNodeId();
			
			for(int i = 0; i < sizeOfMatrix; i++)
			{
				if(nodeId != i && copyOfDependencyMatrix[nodeId][i] != 0)
				{
					copyOfDependencyMatrix[nodeId][i] = 0; // this is to remove dependency from 'node' to child node with nodeId == 'i'
					
					int numberOfIncomingEdge = sizeOfMatrix-1; // from this line, it is process to check whether or not the child node with nodeId == 'i' has any other incoming dependencies from other nodes, and the reason for subtracting 1 from matrixSize is to exclude node itself count from the matrix size.
					for(int j = 0; j < sizeOfMatrix; j++)
					{
						if(j != i && copyOfDependencyMatrix[j][i] == 0)
						{
							numberOfIncomingEdge--;
						}
					}
					if(numberOfIncomingEdge == 0) // there is no incoming dependencies for the node with nodeId == 'i'
					{
						SList.add(nodeMap.get(nodeIdMap.get(i)));
					}
				}
				
			}
//			tempList.clear(); // tempList needs to be cleared because it is used for filling SList, and it is used to avoid creating List instance over and over again.
//			SList = fillingSList(nodeMap, nodeIdMap, tempList, copyOfDependencyMatrix);
		}
		
		boolean checkDAG = false;
		for(int i = 0; i< sizeOfMatrix; i++)
		{
			for(int j = 0; j < sizeOfMatrix; j++)
			{
				if(i != j && copyOfDependencyMatrix[i][j] != 0)
				{
					checkDAG = true;
					break;
				}
			}
		}
																		
		if(checkDAG)
		{
			sortedNodeList.clear();
		}
		
		/*
		 * if size of sortedNodeList is '0' then the graph is cyclic so that RuleSet needs rewriting due to it is in incorrect format
		 */
		return sortedNodeList;

		
	}
	
	public static List<Node> fillingSList(HashMap<String, Node> nodeMap, HashMap<Integer, String> nodeIdMap, List<Node> tempList, int[][] dependencyMatrix)
	{
		int sizeOfMatrix = dependencyMatrix[0].length;
		
		for(int childRow = 0 ; childRow < sizeOfMatrix; childRow++)
		{
			int count = 0;
			for(int parentCol = 0; parentCol < sizeOfMatrix; parentCol++)
			{
				
				if(dependencyMatrix[parentCol][childRow] == 0 && parentCol != childRow) // don't count when parentCol == childRow due to it is where nodeOption (KNOWN or NOT) is stored. 
				{
					count++;
				}
				else
				{
					continue;
				}
			}
			if(count == sizeOfMatrix-1) //exclude its own dependency due to it is nodeOption
			{
				String tempNodeName = nodeIdMap.get(childRow);
				if(tempNodeName != null)
				{
					tempList.add(nodeMap.get(tempNodeName));
				}
			}
		}//initial 'S' List for Kahn's topological algorithm.
		
		return tempList;
	}
	public static int[][] createCopyOfDependencyMatrix( int[][] dependencyMatrix , int sizeOfMatrix)
	{

		int[][] copyOfDependencyMatrix = new int[sizeOfMatrix][sizeOfMatrix];
		for(int i = 0 ; i < sizeOfMatrix; i++)
		{
			for(int j = 0; j < sizeOfMatrix; j++)
			{
				copyOfDependencyMatrix[i][j] = dependencyMatrix[i][j];
			}
		}
		
		return copyOfDependencyMatrix;
	}
	
	/*
	 * this class is another version of topological sort.
	 * the first version of topological sort used Kahn's algorithm which is based on Breadth First Search(BFS)
	 * Topological sorted list is a fundamental part to get an order list of all questions.
	 * However, it always provide same order at all times which might not be shortest path for a certain individual case therefore,
	 * this topological sort based on historical record of each medical condition is suggested.
	 * 
	 * logic for the sorting is as follows; 
	 * note: topological sort logic contains a recursive method 
	 * 1. set 'S' and 'sortedList'
	 * 2. get all data for each rules from database as a HashMap<String, Record>
	 * 3. find rules don't have any parent rules, and add them into 'S' list
	 * 4. if there is an element in the 'S' list
	 * 5. visit the element
	 *    5.1 if the element has any child rules
	 *        5.1.1 get a list of all child rules, and keep visiting until there are no non-visited rules
	 *        5.1.2 if there is not any 'OR' rules ( there are only 'AND' rules)
	 *              5.1.2.1 find the most negative rule, and add the rule into the 'sortedList'
	 *        5.1.3 if there is not any 'AND' rules ( there are only 'OR' rules)
	 *        		5.1.3.1 find the most positive rule, and add the rule into the 'sortedList'
	 * 
	 */

    public static List<Node> dfsTopoSort(HashMap<String, Node> nodeMap, HashMap<Integer, String> nodeIdMap, int[][] dependencyMatrix, HashMap<String, Record> recordMapOfNodes)
    {

    	 List<Node> sortedList = new ArrayList<>();

         if(recordMapOfNodes == null || recordMapOfNodes.isEmpty())
         {
         	sortedList = bfsTopoSort(nodeMap, nodeIdMap, dependencyMatrix);
         }
         else
         {
     		List<Node> visitedNodeList = new ArrayList<>();
     		int[][] copyOfDependencyMatrix = createCopyOfDependencyMatrix(dependencyMatrix, dependencyMatrix[0].length);
     		
     		List<Node> S = fillingSList(nodeMap, nodeIdMap, new ArrayList<Node>(), copyOfDependencyMatrix);
     		
     		while(!S.isEmpty())
             {
             	Node node = S.remove(0);
             	visitedNodeList.add(node);
                 visit(node, sortedList, recordMapOfNodes, nodeMap, nodeIdMap, visitedNodeList, dependencyMatrix); 
             }
         }
         
         return sortedList;    
    }

    /*
     * The idea of this method is to visit a rule that could get a result of parent rule of the rule as quick as it can be
     * for instance, if a 'OR' child rule is 'TRUE' then the parent rule is 'TRUE', 
     * and if a 'AND' child rule is 'FALSE' then the parent rule is 'FALSE'. 
     * AS result, visit more likely true 'OR' rule or more likely false 'AND' rule to determine a parent rule as fast as we can
     */
    public static List<Node> visit(Node node, List<Node> sortedList, HashMap<String, Record> recordMapOfNodes, HashMap<String,Node> nodeMap, HashMap<Integer, String> nodeIdMap, List<Node> visitedNodeList, int[][] dependencyMatrix)
    {
        sortedList.add(node);
        int nodeId = node.getNodeId();
        int orDependencyType = DependencyType.getOr();
        int andDependencyType = DependencyType.getAnd();
        List<Integer> orOutDependency = Arrays.stream(dependencyMatrix[nodeId]).filter(item -> (item & orDependencyType) == orDependencyType).boxed().collect(Collectors.toList());
        List<Integer> andOutDependency = Arrays.stream(dependencyMatrix[nodeId]).filter(item -> (item & andDependencyType) == andDependencyType).boxed().collect(Collectors.toList());

        if(!orOutDependency.isEmpty() && !andOutDependency.isEmpty())
        {
            List<Node> childRuleList = new ArrayList<>();
            for(int i = 0; i < dependencyMatrix[nodeId].length; i++)
            {
            	if(dependencyMatrix[nodeId][i] != 0)
            	{
            		childRuleList.add((nodeMap.get(nodeIdMap.get(i))));
            	}
            }
            
            if(!orOutDependency.isEmpty() && andOutDependency.isEmpty())
            {
                while(!childRuleList.isEmpty())
                {
                	/* 
                	 * the reason for selecting an option having more number of 'yes' is as follows
                	 * if it is 'OR' rule and it is 'TRUE' then it is the shortest path, and ignore other 'OR' rules
                	 * Therefore, looking for more likely 'TRUE' rule would be the shortest one rather than
                	 * looking for more likely 'FALSE' rule in terms of processing time
                	 */
                	Node theMostPositive = findTheMostPositive(childRuleList, recordMapOfNodes);
                    if(!visitedNodeList.contains(theMostPositive))
                    {
                    	visitedNodeList.add(theMostPositive);
                        sortedList = visit(theMostPositive, sortedList, recordMapOfNodes, nodeMap, nodeIdMap, visitedNodeList, dependencyMatrix);
                    }
                }

            }
            else
            {
                if(orOutDependency.isEmpty() && !andOutDependency.isEmpty())
                {
                	/* 
                	 * the reason for selecting an option having more number of 'yes' is as follows
                	 * if it is 'AND' rule and it is 'FALSE' then it is the shortest path, and ignore other 'AND' rules
                	 * Therefore, looking for more likely 'FALSE' rule would be the shortest one rather than
                	 * looking for more likely 'TRUE' rule in terms of processing time
                	 */
                    while(!childRuleList.isEmpty())
                    {
                    	Node theMostNegative = findTheMostNegative(childRuleList, recordMapOfNodes);
                        if(!visitedNodeList.contains(theMostNegative))
                        {
                        	visitedNodeList.add(theMostNegative);
                            sortedList = visit(theMostNegative, sortedList, recordMapOfNodes, nodeMap, nodeIdMap, visitedNodeList, dependencyMatrix);
                        }
                    }
                }
            }
        }
        
        return sortedList;
    }
    
    public static Node findTheMostPositive(List<Node> childNodeList, HashMap<String, Record> recordListOfNodes)
    {
    	Node theMostPositive = null;
        int yesCount = 0;
        int noCount = 0;
        float theMostPossibility = 0;
        int sum = 0;
        float result = 0;
        for(Node node: childNodeList)
        {
            yesCount = recordListOfNodes.get(node.getNodeName()).getTrueCount();
            noCount = recordListOfNodes.get(node.getNodeName()).getFalseCount();
            
            result = (float)yesCount/(yesCount + noCount);
            if(analysis(result, theMostPossibility, yesCount + noCount, sum))
            {
                theMostPossibility = result;
                sum =  yesCount + noCount;
                theMostPositive = node;
            }
        }
        childNodeList.remove(theMostPositive);
        return theMostPositive;
        
    }
    
    public static Node findTheMostNegative(List<Node> childNodeList, HashMap<String, Record> recordListOfNodes)
    {
    	Node theMostNegative = null;
        int yesCount = 0;
        int noCount = 0;
        float theMostPossibility = 0;
        int sum = 0;
        float result = 0;
        for(Node node: childNodeList)
        {
            yesCount = recordListOfNodes.get(node.getNodeName()).getTrueCount();
            noCount = recordListOfNodes.get(node.getNodeName()).getFalseCount();

            result = (float)noCount/(yesCount+noCount);

            if(analysis(result, theMostPossibility, yesCount + noCount, sum))
            {
                theMostPossibility = result;
                sum =  yesCount + noCount;
                theMostNegative = node;
            }
        }
        childNodeList.remove(theMostNegative);
        return theMostNegative;
    }
    
    public static boolean analysis(float result, float theMostPossibility, int yesCountNoCount, int sum)
    {
        boolean highlyPossible = false;
        /*
         * firstly select an option having more cases and high possibility
         */
        if(result > theMostPossibility && yesCountNoCount > sum)
        {
            highlyPossible = true;
        }
        else
        {
        	/*
        	 * secondly, even though the number of being used case is fewer, if it has high possibility
        	 * then still select the option
        	 */
            if(result > theMostPossibility )
            {
                highlyPossible = true;
            }
        }
        
        return highlyPossible;
    }

}

