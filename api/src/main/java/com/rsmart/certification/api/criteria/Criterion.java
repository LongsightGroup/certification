package com.rsmart.certification.api.criteria;

import java.util.List;
import java.util.Map;

/**
 * User: duffy
 * Date: Jun 21, 2011
 * Time: 10:19:22 AM
 */
public interface Criterion
{
    public String getId();

    public CriteriaFactory getCriteriaFactory();

    public String getExpression();
    
    public Map<String, String> getVariableBindings();
    
    /**
     * @author bbailla2
     * 
     * Returns all the headers that should be displayed on the reporting interface for this criterion.
     * For example, if this is a Final Course Grade criterion, it will return ["Final Course Grade"]
     * @return
     */
    public List<String> getReportHeaders();
    
    /**
     * @author bbailla2
     * 
     * Returns all the cell data that should be displayed on the reporting interface for this criterion.
     * For example if this is an expiry date criterion, it will return [<the date of expiry>]
     * @return
     */
    public List<String> getReportData();
}
