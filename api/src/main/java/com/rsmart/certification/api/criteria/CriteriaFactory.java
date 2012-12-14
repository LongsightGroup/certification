package com.rsmart.certification.api.criteria;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.rsmart.certification.api.CertificateDefinition;

/**
 * User: duffy
 * Date: Jun 23, 2011
 * Time: 11:43:32 AM
 */
public interface CriteriaFactory
{
    public Set<CriteriaTemplate> getCriteriaTemplates();

    public CriteriaTemplate getCriteriaTemplate(String id)
        throws UnknownCriterionTypeException;
    
    public CriteriaTemplate getCriteriaTemplate(Criterion criterion)
        throws UnknownCriterionTypeException;

    public Set<Class <? extends Criterion>> getCriterionTypes();

    public boolean isCriterionMet (Criterion criterion)
        throws UnknownCriterionTypeException;

    public boolean isCriterionMet (Criterion criterion, String userId, String contextId)
        throws UnknownCriterionTypeException;

    public Criterion createCriterion (CriteriaTemplate template, Map<String, String> bindings)
            throws InvalidBindingException, CriterionCreationException, UnknownCriterionTypeException;
    
    /** 
     * @param itemId the gradebook item's id
     * @param userId the user's id
     * @return the score on a gradebook item (if not applicable, returns null)
     */
    public Double getScore(Long itemId, String userId, String contextId);
    
    /**
     * @param userId the user's id
     * @return the final score for the given user
     */
    public Double getFinalScore(String userId, String contextId);
    
    /**
     * @param itemId
     * @param userId
     * @return the date that the gradebook item's score was entered (if applicable)
     */
    public Date getDateRecorded(Long itemId, String userId, String contextId);
    
    /**
     * The date of issue is the moment in time where this user has become eligible to download their certificate.
     * Returns null if the certificate is not awarded to this user
     * 
     * For example, on a GreaterThanScore criterion, the date they met that criteria is the result of getDateRecorded()
     * on the criterion's gradebook item, whereas on a DueDatePassed criterion, the date at which this criterion is met 
     * is the gradebook item's due date. To get the date of issue we evaluate the date that each criterion was met and 
     * select the last one in chronological order
     * 
     * @param userId
     * @param contextId
     * @return
     */
    public Date getDateIssued(String userId, String contextId, CertificateDefinition certDef);
    
}
