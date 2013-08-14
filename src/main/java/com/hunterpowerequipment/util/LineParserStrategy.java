package com.hunterpowerequipment.util;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;

public class LineParserStrategy implements AggregationStrategy {
	
	Logger logger = Logger.getLogger(LineParserStrategy.class);
	
	private static String LPT1_HEADER = "C LPT1                                         S" ;
	private static String LPT1_FOOTER = "C LPT1                                         E" ;
	
	public static String SPLIT_TOKEN = "<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>" ;
	
	public String handleLine( String line )
	{
		if( line.length() > 6 )
			line = line.substring(6) ;
		
		return line ;
	}


	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        // put order together in old exchange by adding the order from new exchange
 
        if (oldExchange == null) {
            // the first time we aggregate we only have the new exchange,
            // so we just return it
            return newExchange;
        }

        String orders = oldExchange.getIn().getBody(String.class);
        String newLine = newExchange.getIn().getBody(String.class);

        if( orders.contains( LPT1_HEADER ))
        {
        	// this is the first line in the file, and we don't want it
        	return newExchange ;
        }
        
        if( newLine.contains( LPT1_FOOTER ))
        {
        	// this is the last line in the job, and we don't want it.
        	return oldExchange ;
        }
        
        if( newLine.contains( LPT1_HEADER ))
        {
        	// this is a page header, except for the first one, which we already removed.
        	newLine = SPLIT_TOKEN ;
        }
        
        // put orders together separating by semi colon
        orders = orders + "\n" + newLine;
        // put combined order back on old to preserve it
        oldExchange.getIn().setBody(orders);
 
        // return old as this is the one that has all the orders gathered until now
        return oldExchange;
    }	

}
