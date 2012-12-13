package com.rsmart.certification.impl.hibernate.criteria.gradebook;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: duffy
 * Date: Jul 5, 2011
 * Time: 9:59:47 AM
 */
public class FinalGradeScoreCriterionHibernateImpl
    extends GradebookItemCriterionHibernateImpl
{
	private final String MESSAGE_REPORT_TABLE_HEADER_FCG = "report.table.header.fcg";
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
}
