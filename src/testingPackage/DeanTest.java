package testingPackage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.*;

import factValuePackage.FactBooleanValue;
import factValuePackage.FactValue;
import nodePackage.DependencyType;
import ruleParser.RuleSetParser;
import ruleParser.RuleSetReader;
import ruleParser.RuleSetScanner;
import ruleParser.Tokenizer;
import ruleParser.Tokens;

public class DeanTest {

	public static void main(String[] args) {
		FactBooleanValue fbv = FactValue.parse(true);
		System.out.println("fbv: "+fbv.getValue());
	
		System.out.println("fbv negation: "+fbv.negatingValue());
		System.out.println("fbv: "+fbv.getValue());

		int dp = 3;
		System.out.println((dp&(DependencyType.getNot()|DependencyType.getKnown())) == (DependencyType.getNot()|DependencyType.getKnown()));
		
		String str = "\"double quoted\"";
		Pattern p = Pattern.compile("(\")(.*)(\")");
		Matcher m = p.matcher(str);
		m.find();
		System.out.println(m.group(2));
		
		Pattern pt = Pattern.compile("^[\\d\\-()\\s\\+\\\\]*$");
		String phone = "(+23)423\\12312";
		Matcher ma= pt.matcher(phone);
		String test = "^[\\d\\-()\\s\\+]*$";
		System.out.println(ma.find());
		System.out.println(phone.replaceAll("[\\-\\(\\)\\s\\+\\\\]", ""));
		
		String s = "OR NOT we have person's name and dob";
		System.out.println(s.trim().replaceAll("^(OR\\s?|AND\\s?)(MANDATORY|OPTIONALLY|POSSIBLY)?(\\s?NOT|\\s?KNOWN)*", ""));
		String sDiff = s.replace(s.trim().replaceAll("^(OR\\s?|AND\\s?)(MANDATORY|OPTIONALLY|POSSIBLY)?(\\s?NOT|\\s?KNOWN)*", "").trim(), "").trim();
		System.out.println("sDiff: "+sDiff);
		
		String ss = "person's number is 12312213213123124";
		Tokens st = Tokenizer.getTokens(ss);
													
		System.out.println("last token: "+st.tokensList.get(st.tokensList.size()-1));
		HashMap<String, String> hm = new HashMap<>();
		hm.put("h1", "h1");
		hm.put("h2", "h2");
		hm.put("h3", "h3");
		hm.put("h4", "h4");
		hm.put("h5", "h5");
		
		HashMap<String, String> hm2 = hm;
		hm2.put("h6", "h6");
		System.out.println("hm size: "+hm.size());
		
		List<Integer> list = new ArrayList<>(3);
		list.add(1);
		System.out.println("list size:"+list.size());
		IntStream.range(0, list.size()).forEach(i -> System.out.println("haha"+i));
//		RuleSetReader ilr = new RuleSetReader();
//		ilr.setStreamSource(TopoSortingTest.class.getResourceAsStream("testing NOT and KNOWN.txt"));
//		RuleSetParser isf = new RuleSetParser();		
//		RuleSetScanner rsc = new RuleSetScanner(ilr,isf);
//		rsc.scanRuleSet();
		
	}

}
