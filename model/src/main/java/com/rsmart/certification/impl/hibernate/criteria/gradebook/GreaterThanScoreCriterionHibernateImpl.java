package com.rsmart.certification.impl.hibernate.criteria.gradebook;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rsmart.certification.api.criteria.UnknownCriterionTypeException;

/**
 * User: duffy
 * Date: Jul 5, 2011
 * Time: 9:59:47 AM
 */
public class GreaterThanScoreCriterionHibernateImpl
    extends GradebookItemCriterionHibernateImpl
{
	private final String MESSAGE_REPORT_TABLE_INCOMPLETE = "report.table.incomplete";
	
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

	@Override
	public List<String> getReportData(String userId, String siteId, Date issueDate) 
	{
		List<String> reportData = new ArrayList<String>();
		
		Double score = getCriteriaFactory().getScore(getItemId(), userId, siteId);
		String datum = "";
		if (score == null)
		{
			datum = getCertificateService().getString(MESSAGE_REPORT_TABLE_INCOMPLETE);
		}
		else
		{
			NumberFormat numberFormat = NumberFormat.getInstance();
			datum = numberFormat.format(score);
		}
		
		reportData.add(datum);
		return reportData;
	}
	
	@Override
	public Date getDateMet(String userId, String siteId)
	{
		try 
		{
			if (!getCriteriaFactory().isCriterionMet(this, userId, siteId))
			{
				return null;
			}
		} 
		catch (UnknownCriterionTypeException e) 
		{
			return null;
		}
		
		return getCriteriaFactory().getDateRecorded(getItemId(), userId, siteId);
	}
}
