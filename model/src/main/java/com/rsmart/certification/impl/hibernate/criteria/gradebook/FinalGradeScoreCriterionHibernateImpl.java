package com.rsmart.certification.impl.hibernate.criteria.gradebook;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rsmart.certification.api.CertificateService;
import com.rsmart.certification.api.criteria.UnknownCriterionTypeException;

/**
 * User: duffy
 * Date: Jul 5, 2011
 * Time: 9:59:47 AM
 */
public class FinalGradeScoreCriterionHibernateImpl
    extends GradebookItemCriterionHibernateImpl
{
	private static final String MESSAGE_REPORT_TABLE_HEADER_FCG = "report.table.header.fcg";
	private static final String MESSAGE_REPORT_TABLE_INCOMPLETE = "report.table.incomplete";
	private static final String MESSAGE_ITEM_INCOMPLETE = "item.incomplete";
	private static final String MESSAGE_POINT = "point";
	private static final String MESSAGE_POINTS = "points";
	
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
		
		String fcg = getCertificateService().getString(MESSAGE_REPORT_TABLE_HEADER_FCG);
		
		reportHeaders.add(fcg);
		return reportHeaders;
	}

	@Override
	public List<String> getReportData(String userId, String siteId, Date issueDate) 
	{
		List<String> reportHeaders = new ArrayList<String>();
		
		Double grade = getCriteriaFactory().getFinalScore(userId, siteId);
		String datum = "";
		if (grade == null)
		{
			datum = getCertificateService().getString(MESSAGE_REPORT_TABLE_INCOMPLETE);
		}
		else
		{
			NumberFormat numberFormat = NumberFormat.getInstance();
			datum = numberFormat.format(grade);
		}
		
		reportHeaders.add(datum);
		return reportHeaders;
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
			//impossible
			return null;
		}
		
		return getCriteriaFactory().getFinalGradeDateRecorded(userId, siteId);
	}
	
	public String getProgress(String userId, String siteId)
	{
		CertificateService certServ = getCertificateService();
		
		NumberFormat numberFormat = NumberFormat.getInstance();
		
		Double dblScore = getCriteriaFactory().getFinalScore(userId, siteId);
		if (dblScore == null)
		{
			return certServ.getString(MESSAGE_ITEM_INCOMPLETE);
		}
		else
		{
			StringBuilder score = new StringBuilder(numberFormat.format(dblScore));
			if (dblScore == 1)
			{
				score.append(" ").append(certServ.getString(MESSAGE_POINT));
			}
			else
			{
				score.append(" ").append(certServ.getString(MESSAGE_POINTS));
			}
			return certServ.getFormattedMessage("item.complete", new String[]{ score.toString() });
		}
	}
}
