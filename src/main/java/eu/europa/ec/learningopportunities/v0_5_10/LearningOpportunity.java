//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.05.20 at 09:04:12 AM EEST 
//


package eu.europa.ec.learningopportunities.v0_5_10;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}LearningOpportunityId"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}CountryCode"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}Title" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}NonPreferredTerm" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}MoreInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}Description" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}Url" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}EducationLevel"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}ThematicAreas" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}InformationLanguage"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}EqfLevel" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}NqfLevel" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}SpecialTargetGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}TeachingLanguage" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}StudyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}DurationCode" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}DurationInformation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}StartDate" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}Qualifications" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}AccessRequirements" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}AdmissionProcedure" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}Costs" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}Grants" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}Credits" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}ProviderName" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}ProviderContactInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}ProviderType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://ec.europa.eu/LearningOpportunities/v0_5_10}CourseLocation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "learningOpportunityId",
    "countryCode",
    "title",
    "nonPreferredTerm",
    "moreInfo",
    "description",
    "url",
    "educationLevel",
    "thematicAreas",
    "informationLanguage",
    "eqfLevel",
    "nqfLevel",
    "specialTargetGroup",
    "teachingLanguage",
    "studyType",
    "durationCode",
    "durationInformation",
    "startDate",
    "qualifications",
    "accessRequirements",
    "admissionProcedure",
    "costs",
    "grants",
    "credits",
    "providerName",
    "providerContactInfo",
    "providerType",
    "courseLocation"
})
@XmlRootElement(name = "LearningOpportunity")
public class LearningOpportunity {

    @XmlElement(name = "LearningOpportunityId", required = true)
    protected String learningOpportunityId;
    @XmlElement(name = "CountryCode", required = true)
    protected String countryCode;
    @XmlElement(name = "Title", required = true)
    protected List<I18NNonEmptyString> title;
    @XmlElement(name = "NonPreferredTerm")
    protected List<I18NNonEmptyString> nonPreferredTerm;
    @XmlElement(name = "MoreInfo")
    protected List<I18NNonEmptyString> moreInfo;
    @XmlElement(name = "Description", required = true)
    protected List<I18NNonEmptyString> description;
    @XmlElement(name = "Url", required = true)
    protected List<I18NUrl> url;
    @XmlElement(name = "EducationLevel", required = true)
    protected String educationLevel;
    @XmlElement(name = "ThematicAreas", required = true)
    protected List<ThematicAreas> thematicAreas;
    @XmlElement(name = "InformationLanguage", required = true)
    protected LanguageCode informationLanguage;
    @XmlElement(name = "EqfLevel")
    protected BigInteger eqfLevel;
    @XmlElement(name = "NqfLevel")
    protected String nqfLevel;
    @XmlElement(name = "SpecialTargetGroup")
    protected List<SpecialTargetGroupType> specialTargetGroup;
    @XmlElement(name = "TeachingLanguage", required = true)
    protected List<LanguageCode> teachingLanguage;
    @XmlElement(name = "StudyType")
    protected List<StudyTypeType> studyType;
    @XmlElement(name = "DurationCode")
    protected String durationCode;
    @XmlElement(name = "DurationInformation")
    protected List<I18NString> durationInformation;
    @XmlElement(name = "StartDate")
    protected List<I18NString> startDate;
    @XmlElement(name = "Qualifications")
    protected List<Qualifications> qualifications;
    @XmlElement(name = "AccessRequirements")
    protected List<I18NString> accessRequirements;
    @XmlElement(name = "AdmissionProcedure")
    protected List<I18NString> admissionProcedure;
    @XmlElement(name = "Costs")
    protected List<I18NString> costs;
    @XmlElement(name = "Grants")
    protected List<I18NString> grants;
    @XmlElement(name = "Credits")
    protected List<I18NString> credits;
    @XmlElement(name = "ProviderName", required = true)
    protected List<I18NNonEmptyString> providerName;
    @XmlElement(name = "ProviderContactInfo")
    protected List<I18NString> providerContactInfo;
    @XmlElement(name = "ProviderType")
    protected List<I18NString> providerType;
    @XmlElement(name = "CourseLocation")
    protected List<CourseLocation> courseLocation;

    /**
     * Gets the value of the learningOpportunityId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLearningOpportunityId() {
        return learningOpportunityId;
    }

    /**
     * Sets the value of the learningOpportunityId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLearningOpportunityId(String value) {
        this.learningOpportunityId = value;
    }

    /**
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the title property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTitle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NNonEmptyString }
     * 
     * 
     */
    public List<I18NNonEmptyString> getTitle() {
        if (title == null) {
            title = new ArrayList<I18NNonEmptyString>();
        }
        return this.title;
    }

    /**
     * Gets the value of the nonPreferredTerm property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nonPreferredTerm property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNonPreferredTerm().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NNonEmptyString }
     * 
     * 
     */
    public List<I18NNonEmptyString> getNonPreferredTerm() {
        if (nonPreferredTerm == null) {
            nonPreferredTerm = new ArrayList<I18NNonEmptyString>();
        }
        return this.nonPreferredTerm;
    }

    /**
     * Gets the value of the moreInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the moreInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMoreInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NNonEmptyString }
     * 
     * 
     */
    public List<I18NNonEmptyString> getMoreInfo() {
        if (moreInfo == null) {
            moreInfo = new ArrayList<I18NNonEmptyString>();
        }
        return this.moreInfo;
    }

    /**
     * Gets the value of the description property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the description property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NNonEmptyString }
     * 
     * 
     */
    public List<I18NNonEmptyString> getDescription() {
        if (description == null) {
            description = new ArrayList<I18NNonEmptyString>();
        }
        return this.description;
    }

    /**
     * Gets the value of the url property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the url property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUrl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NUrl }
     * 
     * 
     */
    public List<I18NUrl> getUrl() {
        if (url == null) {
            url = new ArrayList<I18NUrl>();
        }
        return this.url;
    }

    /**
     * Gets the value of the educationLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEducationLevel() {
        return educationLevel;
    }

    /**
     * Sets the value of the educationLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEducationLevel(String value) {
        this.educationLevel = value;
    }

    /**
     * Gets the value of the thematicAreas property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the thematicAreas property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getThematicAreas().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ThematicAreas }
     * 
     * 
     */
    public List<ThematicAreas> getThematicAreas() {
        if (thematicAreas == null) {
            thematicAreas = new ArrayList<ThematicAreas>();
        }
        return this.thematicAreas;
    }

    /**
     * Gets the value of the informationLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link LanguageCode }
     *     
     */
    public LanguageCode getInformationLanguage() {
        return informationLanguage;
    }

    /**
     * Sets the value of the informationLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link LanguageCode }
     *     
     */
    public void setInformationLanguage(LanguageCode value) {
        this.informationLanguage = value;
    }

    /**
     * Gets the value of the eqfLevel property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getEqfLevel() {
        return eqfLevel;
    }

    /**
     * Sets the value of the eqfLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setEqfLevel(BigInteger value) {
        this.eqfLevel = value;
    }

    /**
     * Gets the value of the nqfLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNqfLevel() {
        return nqfLevel;
    }

    /**
     * Sets the value of the nqfLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNqfLevel(String value) {
        this.nqfLevel = value;
    }

    /**
     * Gets the value of the specialTargetGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the specialTargetGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecialTargetGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpecialTargetGroupType }
     * 
     * 
     */
    public List<SpecialTargetGroupType> getSpecialTargetGroup() {
        if (specialTargetGroup == null) {
            specialTargetGroup = new ArrayList<SpecialTargetGroupType>();
        }
        return this.specialTargetGroup;
    }

    /**
     * Gets the value of the teachingLanguage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the teachingLanguage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTeachingLanguage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LanguageCode }
     * 
     * 
     */
    public List<LanguageCode> getTeachingLanguage() {
        if (teachingLanguage == null) {
            teachingLanguage = new ArrayList<LanguageCode>();
        }
        return this.teachingLanguage;
    }

    /**
     * Gets the value of the studyType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the studyType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStudyType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StudyTypeType }
     * 
     * 
     */
    public List<StudyTypeType> getStudyType() {
        if (studyType == null) {
            studyType = new ArrayList<StudyTypeType>();
        }
        return this.studyType;
    }

    /**
     * Gets the value of the durationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDurationCode() {
        return durationCode;
    }

    /**
     * Sets the value of the durationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDurationCode(String value) {
        this.durationCode = value;
    }

    /**
     * Gets the value of the durationInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the durationInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDurationInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NString }
     * 
     * 
     */
    public List<I18NString> getDurationInformation() {
        if (durationInformation == null) {
            durationInformation = new ArrayList<I18NString>();
        }
        return this.durationInformation;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the startDate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStartDate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NString }
     * 
     * 
     */
    public List<I18NString> getStartDate() {
        if (startDate == null) {
            startDate = new ArrayList<I18NString>();
        }
        return this.startDate;
    }

    /**
     * Gets the value of the qualifications property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qualifications property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQualifications().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Qualifications }
     * 
     * 
     */
    public List<Qualifications> getQualifications() {
        if (qualifications == null) {
            qualifications = new ArrayList<Qualifications>();
        }
        return this.qualifications;
    }

    /**
     * Gets the value of the accessRequirements property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accessRequirements property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccessRequirements().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NString }
     * 
     * 
     */
    public List<I18NString> getAccessRequirements() {
        if (accessRequirements == null) {
            accessRequirements = new ArrayList<I18NString>();
        }
        return this.accessRequirements;
    }

    /**
     * Gets the value of the admissionProcedure property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the admissionProcedure property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdmissionProcedure().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NString }
     * 
     * 
     */
    public List<I18NString> getAdmissionProcedure() {
        if (admissionProcedure == null) {
            admissionProcedure = new ArrayList<I18NString>();
        }
        return this.admissionProcedure;
    }

    /**
     * Gets the value of the costs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the costs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCosts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NString }
     * 
     * 
     */
    public List<I18NString> getCosts() {
        if (costs == null) {
            costs = new ArrayList<I18NString>();
        }
        return this.costs;
    }

    /**
     * Gets the value of the grants property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the grants property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGrants().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NString }
     * 
     * 
     */
    public List<I18NString> getGrants() {
        if (grants == null) {
            grants = new ArrayList<I18NString>();
        }
        return this.grants;
    }

    /**
     * Gets the value of the credits property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the credits property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCredits().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NString }
     * 
     * 
     */
    public List<I18NString> getCredits() {
        if (credits == null) {
            credits = new ArrayList<I18NString>();
        }
        return this.credits;
    }

    /**
     * Gets the value of the providerName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the providerName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProviderName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NNonEmptyString }
     * 
     * 
     */
    public List<I18NNonEmptyString> getProviderName() {
        if (providerName == null) {
            providerName = new ArrayList<I18NNonEmptyString>();
        }
        return this.providerName;
    }

    /**
     * Gets the value of the providerContactInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the providerContactInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProviderContactInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NString }
     * 
     * 
     */
    public List<I18NString> getProviderContactInfo() {
        if (providerContactInfo == null) {
            providerContactInfo = new ArrayList<I18NString>();
        }
        return this.providerContactInfo;
    }

    /**
     * Gets the value of the providerType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the providerType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProviderType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NString }
     * 
     * 
     */
    public List<I18NString> getProviderType() {
        if (providerType == null) {
            providerType = new ArrayList<I18NString>();
        }
        return this.providerType;
    }

    /**
     * Gets the value of the courseLocation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the courseLocation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCourseLocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CourseLocation }
     * 
     * 
     */
    public List<CourseLocation> getCourseLocation() {
        if (courseLocation == null) {
            courseLocation = new ArrayList<CourseLocation>();
        }
        return this.courseLocation;
    }

}
