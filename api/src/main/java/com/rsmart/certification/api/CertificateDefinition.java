package com.rsmart.certification.api;

import com.rsmart.certification.api.criteria.Criterion;
import com.rsmart.certification.api.criteria.UnknownCriterionTypeException;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * CertificateDefinition represents the context and criteria for a certificate and maintains a DocumentTemplate that
 * can be used to render a printable version of the certificate.
 *
 * User: duffy
 * Date: Jun 7, 2011
 * Time: 4:41:15 PM
 */
public interface CertificateDefinition
{
    public String getId();
    
    public String getCreatorUserId();
    
    public Date getCreateDate();

    public String getName();

    public String getDescription();

    /**
     * @return the Sakai site identifier to which this certificate is bound
     */
    public String getSiteId();
    
    /**
     * The status of a CertificateDefinition is one of:
     *
     *      UNPUBLISHED - The CertificateDefinition has not yet been fully defined
     *      ACTIVE      - The CertificateDefinition is in use and can be used for awards
     *      INACTIVE    - The CertificateDefinition is not presently available for awards
     *
     * @return the current status
     */
    public CertificateDefinitionStatus getStatus();

    /**
     * @return the template for rendering printable certificates
     */
    public DocumentTemplate getDocumentTemplate();

    /**
     * @return a Map of field names to field values for populating the template when rendering
     */
    public Map<String, String> getFieldValues();

    public Set<Criterion> getAwardCriteria();
    
    /**
     * Returns the date of issue for the given user
     * @param userId
     * @return
     */
    public Date getIssueDate(String userId);
    
    /**
     * Determines whether the the certificate was awarded to the given user
     * @param userId
     * @return
     * @throws UnknownCriterionTypeException
     */
    public boolean isAwarded(String userId)
    	throws UnknownCriterionTypeException;
}
