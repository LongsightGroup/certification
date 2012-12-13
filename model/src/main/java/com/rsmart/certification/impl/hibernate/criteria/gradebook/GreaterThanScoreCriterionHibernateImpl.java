package com.rsmart.certification.impl.hibernate.criteria.gradebook;

import java.util.ArrayList;
import java.util.List;

/**
 * User: duffy
 * Date: Jul 5, 2011
 * Time: 9:59:47 AM
 */
public class GreaterThanScoreCriterionHibernateImpl
    extends GradebookItemCriterionHibernateImpl
{
    public String getScore()
    {
        return getVariableBindings().get("score");
    }

    public void setScore(String score)
    {
        getVariableBindings().put("score", score);
    }

	@Override
	public List<String> getReportHeaders() 
	{
		List<String> reportHeaders = new ArrayList<String>();
		
		String header = getItemName();
		
		reportHeaders.add(header);
		return reportHeaders;
	}

	//OWLTODO: Implement
	@Override
	public List<String> getReportData() 
	{
		// TODO Auto-generated method stub
		return null;
	}
}
